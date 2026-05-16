<template>
  <AdminShell title="预约审核">
    <div class="admin-page-title">
      <div>
        <h2>预约审核管理</h2>
        <p>审核用户提交的预约申请，拒绝时填写原因，异常预约可由管理员取消。</p>
      </div>
      <el-button type="primary" @click="loadReservations">刷新</el-button>
    </div>

    <el-table :data="reservations" stripe class="soft-card">
      <el-table-column prop="reservationNo" label="预约编号" min-width="170" />
      <el-table-column prop="userName" label="用户" min-width="100" />
      <el-table-column prop="roomName" label="自习室" min-width="130" />
      <el-table-column prop="seatCode" label="座位" width="90" />
      <el-table-column label="预约时间" min-width="250">
        <template #default="{ row }">{{ formatTime(row.startTime) }} - {{ formatTime(row.endTime) }}</template>
      </el-table-column>
      <el-table-column prop="auditStatus" label="审核状态" width="110">
        <template #default="{ row }">
          <el-tag :type="getAuditType(row.auditStatus)">{{ getAuditText(row.auditStatus) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="使用状态" width="110">
        <template #default="{ row }">{{ getStatusText(row.status) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="230" fixed="right">
        <template #default="{ row }">
          <template v-if="row.auditStatus === 'pending' && row.status === 'pending'">
            <el-button type="success" size="small" @click="handleAudit(row, 'approved')">通过</el-button>
            <el-button type="danger" size="small" @click="handleReject(row)">拒绝</el-button>
          </template>
          <el-button v-if="row.status === 'approved' || row.status === 'checked_in'" type="warning" size="small" @click="handleCancel(row)">取消</el-button>
        </template>
      </el-table-column>
    </el-table>
  </AdminShell>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../../stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import AdminShell from '../../components/AdminShell.vue'
import request from '../../api'

const router = useRouter()
const userStore = useUserStore()
const reservations = ref([])

const formatTime = (time) => time ? new Date(time).toLocaleString() : ''
const getAuditType = (status) => ({ pending: 'warning', approved: 'success', rejected: 'danger', cancelled: 'info' }[status] || '')
const getAuditText = (status) => ({ pending: '待审核', approved: '已通过', rejected: '已拒绝', cancelled: '已取消' }[status] || status)
const getStatusText = (status) => ({ pending: '待审核', approved: '待签到', checked_in: '已签到', completed: '已完成', cancelled: '已取消', rejected: '已拒绝' }[status] || status)

const loadReservations = async () => {
  const res = await request.get('/admin/reservation/list')
  reservations.value = res.data || []
}

const handleAudit = async (row, status, reply = '') => {
  await request.post(`/admin/reservation/audit/${row.id}`, { status, reply })
  ElMessage.success('操作成功')
  loadReservations()
}

const handleReject = async (row) => {
  const { value } = await ElMessageBox.prompt('请输入拒绝原因', '拒绝预约', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    inputPlaceholder: '例如：该时间段暂不开放'
  })
  await handleAudit(row, 'rejected', value || '')
}

const handleCancel = async (row) => {
  await ElMessageBox.confirm('确定取消该预约吗？', '提示', { type: 'warning' })
  await request.post(`/admin/reservation/cancel/${row.id}`, null, { params: { reason: '管理员取消' } })
  ElMessage.success('已取消')
  loadReservations()
}

onMounted(() => {
  if (!userStore.isAdmin) {
    router.push('/')
    return
  }
  loadReservations()
})
</script>
