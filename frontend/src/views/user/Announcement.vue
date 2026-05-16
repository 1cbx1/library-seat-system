<template>
  <div class="page-shell">
    <UserNav />
    <main class="page-container announcement-page">
      <div class="page-title soft-card">
        <div>
          <span>Notice center</span>
          <h1>公告中心</h1>
          <p>查看系统通知、开放安排和座位预约规则。</p>
        </div>
        <div class="notice-summary">
          <strong>{{ announcements.length }}</strong>
          <em>条公告</em>
        </div>
      </div>

      <div v-if="announcements.length" class="announcement-list">
        <article v-for="(item, index) in announcements" :key="item.id" class="announcement-card soft-card">
          <div class="notice-index">{{ String(index + 1).padStart(2, '0') }}</div>
          <div class="notice-body">
            <div class="notice-meta">
              <el-tag v-if="item.top === 1" type="warning">置顶</el-tag>
              <time>{{ formatTime(item.createTime) }}</time>
            </div>
            <h2>{{ item.title }}</h2>
            <p>{{ item.content }}</p>
          </div>
        </article>
      </div>
      <el-empty v-else-if="!loading" description="暂无公告" />
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import UserNav from '../../components/UserNav.vue'
import request from '../../api'

const announcements = ref([])
const loading = ref(true)
const formatTime = (value) => value ? new Date(value).toLocaleString() : ''

onMounted(async () => {
  try {
    const res = await request.get('/announcement/list')
    announcements.value = res.data || []
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.announcement-page {
  padding: 34px 0 48px;
}

.page-title {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 24px;
  padding: 26px;
  background:
    linear-gradient(120deg, rgba(255, 255, 255, 0.92), rgba(232, 240, 235, 0.92)),
    url("https://images.unsplash.com/photo-1497633762265-9d179a990aa6?auto=format&fit=crop&w=1400&q=80") right center/42% 100% no-repeat;
}

.page-title span {
  display: inline-flex;
  margin-bottom: 10px;
  color: var(--accent);
  font-size: 12px;
  font-weight: 800;
  text-transform: uppercase;
}

.page-title h1 {
  margin: 0;
  font-size: 42px;
  line-height: 1.08;
}

.page-title p {
  margin: 10px 0 0;
  color: var(--muted);
}

.notice-summary {
  min-width: 116px;
  padding: 16px;
  border: 1px solid rgba(31, 111, 91, 0.12);
  border-radius: 8px;
  text-align: center;
  background: rgba(255, 255, 255, 0.72);
}

.notice-summary strong {
  display: block;
  color: var(--primary);
  font-size: 34px;
  line-height: 1;
  font-variant-numeric: tabular-nums;
}

.notice-summary em {
  display: block;
  margin-top: 6px;
  color: var(--muted);
  font-size: 12px;
  font-style: normal;
  font-weight: 700;
}

.announcement-list {
  display: grid;
  gap: 18px;
  margin-top: 24px;
}

.announcement-card {
  position: relative;
  display: grid;
  grid-template-columns: 68px minmax(0, 1fr);
  gap: 18px;
  padding: 22px;
  overflow: hidden;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.announcement-card::before {
  content: "";
  position: absolute;
  inset: 0 auto 0 0;
  width: 4px;
  background: var(--primary);
}

.announcement-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 24px 58px rgba(20, 43, 35, 0.14);
}

.notice-index {
  width: 52px;
  height: 52px;
  display: grid;
  place-items: center;
  border-radius: 10px;
  color: #fff;
  background: linear-gradient(135deg, var(--primary), var(--primary-dark));
  font-size: 18px;
  font-weight: 800;
  font-variant-numeric: tabular-nums;
}

.notice-meta {
  display: flex;
  align-items: center;
  gap: 10px;
  color: var(--muted);
  font-size: 13px;
}

.announcement-card h2 {
  margin: 12px 0 10px;
  font-size: 22px;
}

.announcement-card p {
  margin: 0;
  color: #475569;
  line-height: 1.8;
}

@media (max-width: 560px) {
  .announcement-page {
    padding: 22px 0 38px;
  }

  .page-title {
    align-items: flex-start;
    flex-direction: column;
    padding: 20px;
    background:
      linear-gradient(120deg, rgba(255, 255, 255, 0.94), rgba(232, 240, 235, 0.94)),
      url("https://images.unsplash.com/photo-1497633762265-9d179a990aa6?auto=format&fit=crop&w=760&q=80") right bottom/62% auto no-repeat;
  }

  .page-title h1 {
    font-size: 34px;
  }

  .notice-summary {
    width: 100%;
    text-align: left;
  }

  .announcement-card {
    grid-template-columns: 1fr;
    gap: 12px;
    padding: 18px;
  }
}
</style>
