<template>
  <AdminShell title="统计图表">
    <div class="admin-page-title">
      <div>
        <h2>数据统计图表</h2>
        <p>查看预约趋势和自习室使用分布。</p>
      </div>
      <el-button type="primary" @click="loadData">刷新</el-button>
    </div>

    <div class="stats-cards">
      <article class="stat-card soft-card"><strong>{{ stats.userCount || 0 }}</strong><span>用户总数</span></article>
      <article class="stat-card soft-card"><strong>{{ stats.todayCount || stats.todayReservation || 0 }}</strong><span>今日预约</span></article>
      <article class="stat-card soft-card"><strong>{{ stats.activeCount || stats.activeReservation || 0 }}</strong><span>当前在座</span></article>
      <article class="stat-card soft-card"><strong>{{ stats.roomCount || 0 }}</strong><span>自习室数</span></article>
    </div>

    <div class="charts-grid">
      <section class="chart-card soft-card">
        <div ref="roomChart" class="chart"></div>
      </section>
      <section class="chart-card soft-card">
        <div ref="userChart" class="chart"></div>
      </section>
    </div>
  </AdminShell>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../../stores/user'
import * as echarts from 'echarts'
import AdminShell from '../../components/AdminShell.vue'
import request from '../../api'

const router = useRouter()
const userStore = useUserStore()
const stats = ref({})
const roomChart = ref(null)
const userChart = ref(null)
let roomChartInstance = null
let userChartInstance = null

const loadData = async () => {
  const [statsRes, chartsRes] = await Promise.all([
    request.get('/admin/stats'),
    request.get('/admin/stats/charts')
  ])
  stats.value = statsRes.data || {}
  renderRoomChart(chartsRes.data?.roomStats || [])
  renderUserChart(chartsRes.data?.userStats || [])
}

const renderRoomChart = (data) => {
  if (!roomChart.value) return
  if (roomChartInstance) roomChartInstance.dispose()
  roomChartInstance = echarts.init(roomChart.value)
  roomChartInstance.setOption({
    title: { text: '自习室预约分布', left: 'center', textStyle: { color: '#1e293b', fontSize: 16 } },
    tooltip: {},
    grid: { left: 40, right: 20, bottom: 38, top: 62 },
    xAxis: { type: 'category', data: data.map(d => d.roomName || d.name), axisLabel: { color: '#64748b' } },
    yAxis: { type: 'value', axisLabel: { color: '#64748b' } },
    series: [{ type: 'bar', data: data.map(d => d.count || d.value), itemStyle: { color: '#3157d5', borderRadius: [6, 6, 0, 0] } }]
  })
}

const renderUserChart = (data) => {
  if (!userChart.value) return
  if (userChartInstance) userChartInstance.dispose()
  userChartInstance = echarts.init(userChart.value)
  userChartInstance.setOption({
    title: { text: '近 7 天预约趋势', left: 'center', textStyle: { color: '#1e293b', fontSize: 16 } },
    tooltip: { trigger: 'axis' },
    grid: { left: 40, right: 20, bottom: 38, top: 62 },
    xAxis: { type: 'category', data: data.map(d => d.date || d.name), axisLabel: { color: '#64748b' } },
    yAxis: { type: 'value', axisLabel: { color: '#64748b' } },
    series: [{ type: 'line', smooth: true, data: data.map(d => d.count || d.value), itemStyle: { color: '#22a06b' }, areaStyle: { color: 'rgba(34,160,107,0.12)' } }]
  })
}

onMounted(() => {
  if (!userStore.isAdmin) {
    router.push('/')
    return
  }
  loadData()
})

onUnmounted(() => {
  if (roomChartInstance) roomChartInstance.dispose()
  if (userChartInstance) userChartInstance.dispose()
})
</script>

<style scoped>
.stats-cards {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
  margin-bottom: 18px;
}

.stat-card {
  padding: 20px;
}

.stat-card strong {
  display: block;
  font-size: 30px;
  color: var(--primary);
}

.stat-card span {
  color: var(--muted);
}

.charts-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 18px;
}

.chart-card {
  padding: 18px;
}

.chart {
  width: 100%;
  height: 330px;
}

@media (max-width: 1000px) {
  .stats-cards,
  .charts-grid {
    grid-template-columns: 1fr;
  }
}
</style>
