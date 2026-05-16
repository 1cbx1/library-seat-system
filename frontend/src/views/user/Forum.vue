<template>
  <div class="page-shell">
    <UserNav />

    <main class="page-container forum-page">
      <div class="page-title">
        <div>
          <h1>交流论坛</h1>
          <p>发布学习交流帖子，支持点赞、收藏和评论。</p>
        </div>
        <el-button type="primary" @click="showPostDialog = true">发布帖子</el-button>
      </div>

      <div v-if="!currentPost" class="post-list">
        <article v-for="post in posts" :key="post.id" class="post-card soft-card" @click="goToDetail(post)">
          <div class="post-header">
            <h2>{{ post.title }}</h2>
            <span>{{ post.userName }}</span>
          </div>
          <p>{{ post.content }}</p>
          <div class="post-footer">
            <span><el-icon><View /></el-icon>{{ post.viewCount || 0 }}</span>
            <span><el-icon><Star /></el-icon>{{ post.likeCount || 0 }}</span>
            <span><el-icon><ChatDotSquare /></el-icon>{{ post.commentCount || 0 }}</span>
            <time>{{ formatTime(post.createTime) }}</time>
          </div>
        </article>
      </div>

      <section v-else class="post-detail">
        <button class="back-button" @click="currentPost = null">
          <el-icon><ArrowLeft /></el-icon>
          返回帖子列表
        </button>
        <article class="post-main soft-card">
          <h2>{{ currentPost.title }}</h2>
          <div class="post-meta">
            <span>作者：{{ currentPost.userName }}</span>
            <span>发布时间：{{ formatTime(currentPost.createTime) }}</span>
          </div>
          <p>{{ currentPost.content }}</p>
          <div class="post-actions">
            <el-button @click="handleLike(currentPost)"><el-icon><Star /></el-icon>{{ currentPost.likeCount || 0 }}</el-button>
            <el-button @click="handleCollect(currentPost)">收藏</el-button>
          </div>
        </article>

        <section class="comments">
          <h3>评论（{{ comments.length }}）</h3>
          <div class="comment-input">
            <el-input v-model="commentContent" type="textarea" placeholder="写下你的评论..." />
            <el-button type="primary" :loading="commentLoading" @click="handleComment">发表评论</el-button>
          </div>
          <article v-for="comment in comments" :key="comment.id" class="comment-card soft-card">
            <strong>{{ comment.userName }}</strong>
            <p>{{ comment.content }}</p>
            <time>{{ formatTime(comment.createTime) }}</time>
          </article>
        </section>
      </section>

      <el-dialog v-model="showPostDialog" title="发布帖子" width="520px">
        <el-form ref="postFormRef" :model="postForm" :rules="rules" label-width="72px">
          <el-form-item label="标题" prop="title"><el-input v-model="postForm.title" /></el-form-item>
          <el-form-item label="内容" prop="content"><el-input v-model="postForm.content" type="textarea" :rows="5" /></el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="showPostDialog = false">取消</el-button>
          <el-button type="primary" :loading="publishLoading" @click="handlePublish">发布</el-button>
        </template>
      </el-dialog>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../../stores/user'
import { ElMessage } from 'element-plus'
import { View, Star, ChatDotSquare, ArrowLeft } from '@element-plus/icons-vue'
import UserNav from '../../components/UserNav.vue'
import request from '../../api'

const router = useRouter()
const userStore = useUserStore()
const posts = ref([])
const currentPost = ref(null)
const comments = ref([])
const showPostDialog = ref(false)
const commentContent = ref('')
const commentLoading = ref(false)
const publishLoading = ref(false)
const postFormRef = ref(null)
const postForm = ref({ title: '', content: '' })

const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }]
}

const formatTime = (value) => value ? new Date(value).toLocaleString() : ''

const loadPosts = async () => {
  const res = await request.get('/post/list')
  posts.value = res.data || []
}

const goToDetail = async (post) => {
  const res = await request.get(`/post/${post.id}`)
  currentPost.value = res.data?.post || post
  comments.value = res.data?.comments || []
}

const handleLike = async (post) => {
  await request.post(`/post/like/${post.id}`)
  post.likeCount = (post.likeCount || 0) + 1
}

const handleCollect = async (post) => {
  await request.post(`/post/collect/${post.id}`)
  ElMessage.success('收藏成功')
}

const handleComment = async () => {
  if (!commentContent.value.trim()) return
  commentLoading.value = true
  try {
    await request.post('/post/comment', { postId: currentPost.value.id, content: commentContent.value })
    ElMessage.success('评论成功')
    commentContent.value = ''
    await goToDetail(currentPost.value)
  } finally {
    commentLoading.value = false
  }
}

const handlePublish = async () => {
  await postFormRef.value.validate()
  publishLoading.value = true
  try {
    await request.post('/post', postForm.value)
    ElMessage.success('发布成功')
    showPostDialog.value = false
    postForm.value = { title: '', content: '' }
    loadPosts()
  } finally {
    publishLoading.value = false
  }
}

onMounted(() => {
  if (!userStore.isLoggedIn) {
    router.push('/login')
    return
  }
  loadPosts()
})
</script>

<style scoped>
.forum-page {
  padding: 32px 0 48px;
}

.page-title {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
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

.post-list {
  display: grid;
  gap: 14px;
}

.post-card,
.post-main,
.comment-card {
  padding: 20px;
}

.post-card {
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.post-card:hover {
  transform: translateY(-3px);
}

.post-header,
.post-footer,
.post-meta,
.post-actions {
  display: flex;
  align-items: center;
  gap: 14px;
}

.post-header {
  justify-content: space-between;
}

.post-header h2,
.post-main h2 {
  margin: 0;
  font-size: 20px;
}

.post-header span,
.post-footer,
.post-meta,
.comment-card time {
  color: var(--muted);
  font-size: 13px;
}

.post-card p,
.post-main p,
.comment-card p {
  color: #475569;
  line-height: 1.8;
}

.post-footer span {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.post-footer time {
  margin-left: auto;
}

.post-detail {
  display: grid;
  gap: 16px;
}

.back-button {
  width: fit-content;
  display: inline-flex;
  align-items: center;
  gap: 7px;
  border: 1px solid rgba(31, 111, 91, 0.16);
  border-radius: 7px;
  padding: 9px 13px;
  color: var(--primary);
  background: rgba(255, 255, 255, 0.72);
  box-shadow: 0 10px 24px rgba(20, 43, 35, 0.08);
  font-size: 14px;
  font-weight: 700;
  cursor: pointer;
  transition: transform 0.18s ease, background-color 0.18s ease, box-shadow 0.18s ease;
}

.back-button:hover {
  transform: translateX(-2px);
  background: #f7faf7;
  box-shadow: 0 14px 30px rgba(20, 43, 35, 0.12);
}

.back-button:active {
  transform: translateX(-1px) scale(0.98);
}

.comments h3 {
  margin: 8px 0 12px;
}

.comment-input {
  display: flex;
  gap: 10px;
  margin-bottom: 14px;
}

.comment-input .el-textarea {
  flex: 1;
}

.comment-card {
  margin-bottom: 10px;
}

@media (max-width: 560px) {
  .forum-page {
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

  .post-card,
  .post-main,
  .comment-card {
    padding: 16px;
  }

  .post-header,
  .post-footer,
  .post-meta,
  .post-actions {
    align-items: flex-start;
    flex-wrap: wrap;
  }

  .post-header {
    flex-direction: column;
  }

  .post-footer time {
    width: 100%;
    margin-left: 0;
  }

  .comment-input {
    flex-direction: column;
  }

  .comment-input .el-button,
  .post-actions .el-button {
    width: 100%;
  }
}
</style>
