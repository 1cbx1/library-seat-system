<template>
  <AdminShell title="数据概览">
    <div class="admin-page-title">
      <div>
        <h2>后台首页</h2>
        <p>查看用户、预约、在座人数和自习室数量。</p>
      </div>
      <div class="action-bar">
        <el-button type="primary" @click="router.push('/admin/stats')">统计图表</el-button>
        <el-button type="success" @click="handleExport">导出 Excel</el-button>
      </div>
    </div>

    <div class="stats-grid">
      <article v-for="item in statCards" :key="item.label" class="stat-card soft-card">
        <span>{{ item.label }}</span>
        <strong>{{ item.value }}</strong>
        <small>{{ item.tip }}</small>
      </article>
    </div>

    <section class="dashboard-panels">
      <div class="soft-card panel">
        <div class="panel-head">
          <h3>预约流程</h3>
          <el-tag type="success">演示闭环</el-tag>
        </div>
        <div class="flow">
          <span>用户选座</span>
          <span>提交预约</span>
          <span>管理员审核</span>
          <span>签到签退</span>
          <span>评价完成</span>
        </div>
      </div>
      <div class="soft-card panel">
        <div class="panel-head">
          <h3>快捷入口</h3>
        </div>
        <div class="quick-actions">
          <el-button @click="router.push('/admin/reservations')">处理预约审核</el-button>
          <el-button @click="router.push('/admin/rooms')">维护自习室</el-button>
          <el-button @click="router.push('/admin/seats')">维护座位状态</el-button>
          <el-button @click="router.push('/admin/messages')">回复用户留言</el-button>
          <el-button @click="router.push('/admin/users')">管理用户</el-button>
        </div>
      </div>
    </section>
  </AdminShell>
</template>

<script setup>
import { computed, ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../../stores/user'
import axios from 'axios'
import AdminShell from '../../components/AdminShell.vue'
import request from '../../api'

const router = useRouter()
const userStore = useUserStore()
const stats = ref({})

const statCards = computed(() => [
  { label: '用户总数', value: stats.value.userCount || 0, tip: '已注册账号' },
  { label: '今日预约', value: stats.value.todayCount || 0, tip: '当天提交记录' },
  { label: '当前在座', value: stats.value.activeCount || 0, tip: '已签到未签退' },
  { label: '自习室数量', value: stats.value.roomCount || 0, tip: '启用和维护中' }
])

const handleExport = async () => {
  const token = localStorage.getItem('token')
  const res = await axios.get('/api/export/reservation', {
    responseType: 'blob',
    headers: token ? { Authorization: `Bearer ${token}` } : {}
  })
  const url = URL.createObjectURL(new Blob([res.data]))
  const link = document.createElement('a')
  link.href = url
  link.download = `预约数据_${Date.now()}.xlsx`
  link.click()
  URL.revokeObjectURL(url)
}

onMounted(async () => {
  if (!userStore.isAdmin) {
    router.push('/')
    return
  }
  const res = await request.get('/admin/stats')
  stats.value = res.data || {}
})
</script>

<style scoped>
.action-bar {
  display: flex;
  gap: 10px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 18px;
}

.stat-card {
  padding: 22px;
  position: relative;
  overflow: hidden;
}

.stat-card::after {
  content: "";
  position: absolute;
  right: -28px;
  top: -28px;
  width: 88px;
  height: 88px;
  border-radius: 50%;
  background: rgba(31, 111, 91, 0.09);
}

.stat-card span,
.stat-card small {
  color: var(--muted);
}

.stat-card strong {
  display: block;
  margin: 12px 0 6px;
  font-size: 34px;
  color: var(--primary);
}

.dashboard-panels {
  display: grid;
  grid-template-columns: 1.2fr 0.8fr;
  gap: 18px;
  margin-top: 20px;
}

.panel {
  padding: 22px;
}

.panel-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.panel h3 {
  margin: 0;
}

.flow {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 10px;
  margin-top: 20px;
}

.flow span {
  padding: 14px 10px;
  text-align: center;
  border-radius: 8px;
  color: var(--primary);
  background: #eef5ef;
  font-weight: 700;
}

.quick-actions {
  display: grid;
  gap: 12px;
  margin-top: 20px;
}

@media (max-width: 1000px) {
  .stats-grid,
  .dashboard-panels,
  .flow {
    grid-template-columns: 1fr;
  }
}
</style>
