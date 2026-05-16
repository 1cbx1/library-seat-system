<template>
  <div class="page-shell">
    <UserNav />

    <main class="page-container message-page">
      <div class="page-title soft-card">
        <div>
          <span>Message board</span>
          <h1>留言板</h1>
          <p>向管理员提交问题或建议，并查看回复结果。</p>
        </div>
        <div class="message-count">
          <strong>{{ messages.length }}</strong>
          <em>条留言</em>
        </div>
      </div>

      <section class="message-layout">
        <aside class="message-input soft-card">
          <div class="input-title">
            <span>提交留言</span>
            <h2>问题反馈</h2>
            <p>请说明自习室、座位编号或遇到的问题，方便管理员处理。</p>
          </div>
          <el-input v-model="messageContent" type="textarea" :rows="7" placeholder="写下你的留言..." />
          <div class="input-actions">
            <span>回复会显示在右侧留言记录中。</span>
            <el-button type="primary" :loading="loading" @click="handleSubmit">发布留言</el-button>
          </div>
        </aside>

        <section class="message-list">
          <article v-for="item in messages" :key="item.id" class="message-card soft-card">
            <div class="message-status" :class="{ replied: item.replyContent }"></div>
            <div class="message-head">
              <div>
                <strong>{{ item.userName || '用户' }}</strong>
                <time>{{ formatTime(item.createTime) }}</time>
              </div>
              <el-tag :type="item.replyContent ? 'success' : 'warning'" size="small">{{ item.replyContent ? '已回复' : '待回复' }}</el-tag>
            </div>
            <p>{{ item.content }}</p>
            <div v-if="item.replyContent" class="message-reply">
              <strong>管理员回复</strong>
              <span>{{ item.replyContent }}</span>
            </div>
          </article>
        </section>
      </section>

      <el-empty v-if="!messages.length" description="暂无留言" />
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../../stores/user'
import { ElMessage } from 'element-plus'
import UserNav from '../../components/UserNav.vue'
import request from '../../api'

const router = useRouter()
const userStore = useUserStore()
const messages = ref([])
const messageContent = ref('')
const loading = ref(false)

const formatTime = (value) => value ? new Date(value).toLocaleString() : ''

const loadMessages = async () => {
  const res = await request.get('/message/list')
  messages.value = res.data || []
}

const handleSubmit = async () => {
  if (!messageContent.value.trim()) {
    ElMessage.warning('请输入留言内容')
    return
  }
  loading.value = true
  try {
    await request.post('/message', { title: '用户留言', content: messageContent.value })
    ElMessage.success('留言成功')
    messageContent.value = ''
    loadMessages()
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  if (!userStore.isLoggedIn) {
    router.push('/login')
    return
  }
  loadMessages()
})
</script>

<style scoped>
.message-page {
  padding: 32px 0 48px;
}

.page-title {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 24px;
  padding: 26px;
  background:
    linear-gradient(120deg, rgba(255, 255, 255, 0.92), rgba(232, 240, 235, 0.92)),
    url("https://images.unsplash.com/photo-1507842217343-583bb7270b66?auto=format&fit=crop&w=1400&q=80") right center/40% 100% no-repeat;
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
  margin: 8px 0 0;
  color: var(--muted);
}

.message-count {
  min-width: 116px;
  padding: 16px;
  border: 1px solid rgba(31, 111, 91, 0.12);
  border-radius: 8px;
  text-align: center;
  background: rgba(255, 255, 255, 0.72);
}

.message-count strong {
  display: block;
  color: var(--primary);
  font-size: 34px;
  line-height: 1;
  font-variant-numeric: tabular-nums;
}

.message-count em {
  display: block;
  margin-top: 6px;
  color: var(--muted);
  font-size: 12px;
  font-style: normal;
  font-weight: 700;
}

.message-layout {
  display: grid;
  grid-template-columns: 360px minmax(0, 1fr);
  gap: 18px;
  margin-top: 22px;
}

.message-input {
  padding: 22px;
  align-self: start;
  position: sticky;
  top: 86px;
}

.input-title span {
  color: var(--accent);
  font-size: 12px;
  font-weight: 800;
}

.input-title h2 {
  margin: 8px 0;
  font-size: 24px;
}

.input-title p {
  margin: 0 0 16px;
  color: var(--muted);
  line-height: 1.7;
}

.input-actions {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 14px;
  margin-top: 14px;
}

.input-actions span {
  color: var(--muted);
  font-size: 13px;
}

.message-list {
  display: grid;
  gap: 14px;
}

.message-card {
  position: relative;
  padding: 20px 20px 20px 28px;
  overflow: hidden;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.message-status {
  position: absolute;
  inset: 0 auto 0 0;
  width: 5px;
  background: var(--warning);
}

.message-status.replied {
  background: var(--primary);
}

.message-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 24px 58px rgba(20, 43, 35, 0.14);
}

.message-head {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
  color: var(--muted);
}

.message-head div {
  display: grid;
  gap: 4px;
}

.message-head strong {
  color: var(--primary);
}

.message-card p {
  margin: 12px 0 0;
  color: #475569;
  line-height: 1.8;
}

.message-reply {
  margin-top: 14px;
  padding: 14px;
  border-radius: 8px;
  color: #155443;
  background: #eef7f1;
  border: 1px solid #d6eadf;
  display: grid;
  gap: 6px;
}

@media (max-width: 900px) {
  .message-layout {
    grid-template-columns: 1fr;
  }

  .message-input {
    position: static;
  }
}

@media (max-width: 560px) {
  .message-page {
    padding: 22px 0 38px;
  }

  .page-title {
    align-items: flex-start;
    flex-direction: column;
    padding: 20px;
    background:
      linear-gradient(120deg, rgba(255, 255, 255, 0.94), rgba(232, 240, 235, 0.94)),
      url("https://images.unsplash.com/photo-1507842217343-583bb7270b66?auto=format&fit=crop&w=760&q=80") right bottom/62% auto no-repeat;
  }

  .page-title h1 {
    font-size: 34px;
  }

  .message-count {
    width: 100%;
    text-align: left;
  }

  .message-input,
  .message-card {
    padding: 18px;
  }

  .input-actions,
  .message-head {
    align-items: stretch;
    flex-direction: column;
  }

  .input-actions .el-button {
    width: 100%;
  }
}
</style>
