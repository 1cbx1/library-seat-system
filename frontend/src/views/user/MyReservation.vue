<template>
  <div class="page-shell">
    <UserNav />

    <main class="page-container reservation-page">
      <div class="page-title">
        <div>
          <h1>我的预约</h1>
          <p>跟踪审核状态，完成签到、签退和评价。</p>
        </div>
        <el-button type="primary" @click="router.push('/studyrooms')">继续预约</el-button>
      </div>

      <section class="reservation-overview">
        <article class="overview-card soft-card">
          <span>全部预约</span>
          <strong>{{ reservations.length }}</strong>
        </article>
        <article class="overview-card soft-card">
          <span>待审核</span>
          <strong>{{ countByStatus('pending') }}</strong>
        </article>
        <article class="overview-card soft-card">
          <span>待签到</span>
          <strong>{{ countByStatus('approved') }}</strong>
        </article>
        <article class="overview-card soft-card">
          <span>进行中</span>
          <strong>{{ countByStatus('checked_in') }}</strong>
        </article>
      </section>

      <section class="reservation-toolbar soft-card">
        <el-segmented v-model="filterStatus" :options="filterOptions" />
        <span>共 {{ filteredReservations.length }} 条记录</span>
      </section>

      <el-table :data="filteredReservations" class="soft-card reservation-table" stripe>
        <el-table-column prop="reservationNo" label="预约编号" min-width="170" />
        <el-table-column prop="roomName" label="自习室" min-width="130" />
        <el-table-column prop="seatCode" label="座位" width="90" />
        <el-table-column label="预约时间" min-width="250">
          <template #default="{ row }">
            {{ formatTime(row.startTime) }} - {{ formatTime(row.endTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="auditStatus" label="审核状态" width="110">
          <template #default="{ row }">
            <el-tag :type="getAuditStatusType(row.auditStatus)">{{ getAuditStatusText(row.auditStatus) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="使用状态" width="110">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === 'approved'" type="success" size="small" @click="handleCheckIn(row)">签到</el-button>
            <el-button v-if="row.status === 'checked_in'" type="warning" size="small" @click="handleCheckOut(row)">签退</el-button>
            <el-button v-if="row.status === 'completed'" type="primary" size="small" @click="openEval(row)">评价</el-button>
            <el-button v-if="row.status === 'pending' || row.status === 'approved'" type="danger" size="small" @click="handleCancel(row)">取消</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-if="!filteredReservations.length" description="暂无预约记录" />
    </main>

    <el-dialog v-model="evalDialogVisible" title="评价自习室" width="500px">
      <el-form :model="evalForm" label-width="86px">
        <el-form-item label="评分">
          <el-rate v-model="evalForm.score" />
        </el-form-item>
        <el-form-item label="评价内容">
          <el-input v-model="evalForm.content" type="textarea" rows="4" placeholder="请填写本次使用体验" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="evalDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitEval">提交评价</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../../stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import UserNav from '../../components/UserNav.vue'
import request from '../../api'

const router = useRouter()
const userStore = useUserStore()
const reservations = ref([])
const filterStatus = ref('全部')
const evalDialogVisible = ref(false)
const evaluatingReservation = ref(null)
const evalForm = ref({ score: 5, content: '' })

const formatTime = (time) => time ? new Date(time).toLocaleString() : ''
const getStatusType = (status) => ({ pending: 'warning', approved: 'success', checked_in: 'primary', completed: 'info', cancelled: 'info', rejected: 'danger' }[status] || '')
const getStatusText = (status) => ({ pending: '待审核', approved: '待签到', checked_in: '已签到', completed: '已完成', cancelled: '已取消', rejected: '已拒绝' }[status] || status)
const getAuditStatusType = (status) => ({ pending: 'warning', approved: 'success', rejected: 'danger', cancelled: 'info' }[status] || '')
const getAuditStatusText = (status) => ({ pending: '待审核', approved: '已通过', rejected: '已拒绝', cancelled: '已取消' }[status] || status)
const filterOptions = ['全部', '待审核', '待签到', '进行中', '已完成', '已取消']
const filterStatusMap = {
  待审核: 'pending',
  待签到: 'approved',
  进行中: 'checked_in',
  已完成: 'completed',
  已取消: 'cancelled'
}

const filteredReservations = computed(() => {
  if (filterStatus.value === '全部') return reservations.value
  const status = filterStatusMap[filterStatus.value]
  return reservations.value.filter(item => item.status === status)
})

const countByStatus = (status) => reservations.value.filter(item => item.status === status).length

const handleCheckIn = async (row) => {
  await request.post(`/reservation/checkin/${row.id}`)
  ElMessage.success('签到成功')
  loadReservations()
}

const handleCheckOut = async (row) => {
  await request.post(`/reservation/checkout/${row.id}`)
  ElMessage.success('签退成功，请评价本次使用体验')
  openEval(row)
  loadReservations()
}

const openEval = (row) => {
  evaluatingReservation.value = row
  evalForm.value = { score: 5, content: '' }
  evalDialogVisible.value = true
}

const handleSubmitEval = async () => {
  const row = evaluatingReservation.value
  if (!row) return
  await request.post('/evaluation', {
    reservationId: row.id,
    roomId: row.roomId,
    score: evalForm.value.score,
    content: evalForm.value.content
  })
  ElMessage.success('评价提交成功')
  evalDialogVisible.value = false
}

const handleCancel = async (row) => {
  await ElMessageBox.confirm('确定取消该预约吗？', '提示', { type: 'warning' })
  await request.post(`/reservation/cancel/${row.id}`, null, { params: { reason: '用户取消' } })
  ElMessage.success('取消成功')
  loadReservations()
}

const loadReservations = async () => {
  const res = await request.get('/reservation/my')
  reservations.value = res.data || []
}

onMounted(() => {
  if (!userStore.isLoggedIn) {
    router.push('/login')
    return
  }
  loadReservations()
})
</script>

<style scoped>
.reservation-page {
  padding: 32px 0 48px;
}

.page-title {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 18px;
  margin-bottom: 22px;
}

.page-title h1 {
  margin: 0;
  font-size: 34px;
}

.page-title p {
  margin: 8px 0 0;
  color: var(--muted);
}

.reservation-overview {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
  margin-bottom: 18px;
}

.overview-card {
  position: relative;
  overflow: hidden;
  padding: 20px;
}

.overview-card::after {
  content: "";
  position: absolute;
  right: -24px;
  top: -24px;
  width: 82px;
  height: 82px;
  border-radius: 50%;
  background: rgba(31, 111, 91, 0.08);
}

.overview-card span {
  color: var(--muted);
  font-size: 13px;
}

.overview-card strong {
  display: block;
  margin-top: 10px;
  color: var(--primary);
  font-size: 32px;
  line-height: 1;
}

.reservation-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  margin-bottom: 16px;
  padding: 14px 16px;
}

.reservation-toolbar span {
  color: var(--muted);
  font-size: 13px;
}

.reservation-table {
  border-radius: 10px;
}

@media (max-width: 900px) {
  .reservation-overview {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .reservation-toolbar {
    align-items: flex-start;
    flex-direction: column;
  }
}

@media (max-width: 560px) {
  .reservation-page {
    padding: 22px 0 38px;
  }

  .page-title {
    align-items: stretch;
    flex-direction: column;
  }

  .page-title h1 {
    font-size: 28px;
  }

  .page-title .el-button {
    width: 100%;
  }

  .reservation-overview {
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 12px;
  }

  .overview-card {
    padding: 16px;
  }

  .overview-card strong {
    font-size: 28px;
  }

  .reservation-toolbar {
    padding: 12px;
    overflow-x: auto;
  }

  .reservation-table {
    display: block;
    width: 100%;
    overflow-x: auto;
  }
}
</style>
