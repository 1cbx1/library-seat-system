<template>
  <AdminShell title="论坛管理">
    <div class="admin-page-title">
      <div>
        <h2>论坛管理</h2>
        <p>查看用户帖子，对违规帖子进行删除处理。</p>
      </div>
      <el-button type="primary" @click="loadPosts">刷新</el-button>
    </div>

    <el-table :data="posts" stripe class="soft-card">
      <el-table-column prop="title" label="帖子标题" min-width="220" />
      <el-table-column prop="userName" label="作者" width="130" />
      <el-table-column prop="likeCount" label="点赞" width="90" />
      <el-table-column prop="collectCount" label="收藏" width="90" />
      <el-table-column prop="commentCount" label="评论数" width="100" />
      <el-table-column prop="createTime" label="发布时间" width="180">
        <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="110" fixed="right">
        <template #default="{ row }">
          <el-button type="danger" size="small" @click="handleDeletePost(row)">删除</el-button>
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
const posts = ref([])

const formatTime = (value) => value ? new Date(value).toLocaleString() : ''

const loadPosts = async () => {
  const res = await request.get('/post/list')
  posts.value = res.data || []
}

const handleDeletePost = async (row) => {
  await ElMessageBox.confirm('确定删除该帖子吗？', '提示', { type: 'warning' })
  await request.delete(`/post/${row.id}`)
  ElMessage.success('删除成功')
  loadPosts()
}

onMounted(() => {
  if (!userStore.isAdmin) {
    router.push('/')
    return
  }
  loadPosts()
})
</script>
