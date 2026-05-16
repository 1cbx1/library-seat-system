<template>
  <AdminShell title="留言管理">
    <div class="admin-page-title">
      <div>
        <h2>留言管理</h2>
        <p>查看用户留言，回复处理结果，并删除无效留言。</p>
      </div>
      <div class="title-actions">
        <el-segmented v-model="filterStatus" :options="filterOptions" />
        <el-button type="primary" @click="loadMessages">刷新</el-button>
      </div>
    </div>

    <div class="message-stats">
      <article class="soft-card">
        <strong>{{ messages.length }}</strong>
        <span>全部留言</span>
      </article>
      <article class="soft-card">
        <strong>{{ pendingCount }}</strong>
        <span>待回复</span>
      </article>
      <article class="soft-card">
        <strong>{{ repliedCount }}</strong>
        <span>已回复</span>
      </article>
    </div>

    <el-table :data="filteredMessages" stripe class="soft-card">
      <el-table-column label="用户" width="130">
        <template #default="{ row }">{{ row.userName || `用户 #${row.userId || '-'}` }}</template>
      </el-table-column>
      <el-table-column prop="content" label="留言内容" min-width="260" show-overflow-tooltip />
      <el-table-column prop="replyContent" label="回复内容" min-width="260" show-overflow-tooltip>
        <template #default="{ row }">
          <span v-if="row.replyContent">{{ row.replyContent }}</span>
          <span v-else class="muted">暂未回复</span>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'warning'">{{ row.status === 1 ? '已回复' : '待回复' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="提交时间" width="180">
        <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="openReply(row)">回复</el-button>
          <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-empty v-if="!filteredMessages.length" description="暂无留言" />

    <el-dialog v-model="replyDialogVisible" title="回复留言" width="560px">
      <div class="reply-source">
        <span>用户留言</span>
        <p>{{ replyingMessage?.content }}</p>
      </div>
      <el-form :model="replyForm" label-width="80px">
        <el-form-item label="回复内容">
          <el-input v-model="replyForm.replyContent" type="textarea" :rows="5" placeholder="请输入回复内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="replyDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="replyLoading" @click="handleReply">提交回复</el-button>
      </template>
    </el-dialog>
  </AdminShell>
</template>

<script setup>
import { computed, ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../../stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import AdminShell from '../../components/AdminShell.vue'
import request from '../../api'

const router = useRouter()
const userStore = useUserStore()
const messages = ref([])
const filterStatus = ref('全部')
const replyDialogVisible = ref(false)
const replyLoading = ref(false)
const replyingMessage = ref(null)
const replyForm = ref({ replyContent: '' })

const filterOptions = ['全部', '待回复', '已回复']

const pendingCount = computed(() => messages.value.filter(item => item.status !== 1).length)
const repliedCount = computed(() => messages.value.filter(item => item.status === 1).length)
const filteredMessages = computed(() => {
  if (filterStatus.value === '待回复') return messages.value.filter(item => item.status !== 1)
  if (filterStatus.value === '已回复') return messages.value.filter(item => item.status === 1)
  return messages.value
})

const formatTime = (value) => value ? new Date(value).toLocaleString() : ''

const loadMessages = async () => {
  const res = await request.get('/message/list')
  messages.value = res.data || []
}

const openReply = (row) => {
  replyingMessage.value = row
  replyForm.value = { replyContent: row.replyContent || '' }
  replyDialogVisible.value = true
}

const handleReply = async () => {
  if (!replyForm.value.replyContent.trim()) {
    ElMessage.warning('请输入回复内容')
    return
  }
  replyLoading.value = true
  try {
    await request.post(`/message/reply/${replyingMessage.value.id}`, {
      replyContent: replyForm.value.replyContent
    })
    ElMessage.success('回复成功')
    replyDialogVisible.value = false
    loadMessages()
  } finally {
    replyLoading.value = false
  }
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确定删除该留言吗？', '提示', { type: 'warning' })
  await request.delete(`/message/${row.id}`)
  ElMessage.success('删除成功')
  loadMessages()
}

onMounted(() => {
  if (!userStore.isAdmin) {
    router.push('/')
    return
  }
  loadMessages()
})
</script>

<style scoped>
.title-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.message-stats {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
  margin-bottom: 18px;
}

.message-stats article {
  padding: 18px;
}

.message-stats strong {
  display: block;
  color: var(--primary);
  font-size: 28px;
}

.message-stats span,
.muted {
  color: var(--muted);
}

.reply-source {
  padding: 14px;
  margin-bottom: 16px;
  border-radius: 8px;
  background: #f7faff;
}

.reply-source span {
  color: var(--muted);
  font-size: 13px;
}

.reply-source p {
  margin: 8px 0 0;
  color: #334155;
  line-height: 1.7;
}

@media (max-width: 900px) {
  .admin-page-title,
  .title-actions {
    align-items: flex-start;
    flex-direction: column;
  }

  .message-stats {
    grid-template-columns: 1fr;
  }
}
</style>
