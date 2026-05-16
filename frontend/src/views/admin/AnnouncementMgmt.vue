<template>
  <AdminShell title="公告管理">
    <div class="admin-page-title">
      <div>
        <h2>公告管理</h2>
        <p>发布、编辑、删除公告，并支持置顶展示。</p>
      </div>
      <el-button type="primary" @click="handleAdd">发布公告</el-button>
    </div>

    <el-table :data="announcements" stripe class="soft-card">
      <el-table-column prop="title" label="标题" min-width="220" />
      <el-table-column prop="createTime" label="发布时间" width="180">
        <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
      </el-table-column>
      <el-table-column prop="top" label="置顶" width="90">
        <template #default="{ row }">
          <el-tag :type="row.top === 1 ? 'warning' : 'info'">{{ row.top === 1 ? '是' : '否' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="250" fixed="right">
        <template #default="{ row }">
          <el-button :type="row.top === 1 ? 'info' : 'warning'" size="small" @click="handleTop(row)">
            {{ row.top === 1 ? '取消置顶' : '置顶' }}
          </el-button>
          <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="editingAnn.id ? '编辑公告' : '发布公告'" width="560px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="标题"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="内容"><el-input v-model="form.content" type="textarea" :rows="6" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
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
const announcements = ref([])
const dialogVisible = ref(false)
const editingAnn = ref({})
const form = ref({ title: '', content: '' })

const formatTime = (value) => value ? new Date(value).toLocaleString() : ''

const loadAnnouncements = async () => {
  const res = await request.get('/announcement/list')
  announcements.value = res.data || []
}

const handleAdd = () => {
  editingAnn.value = {}
  form.value = { title: '', content: '' }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  editingAnn.value = { ...row }
  form.value = { title: row.title, content: row.content }
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确定删除该公告吗？', '提示', { type: 'warning' })
  await request.delete(`/announcement/${row.id}`)
  ElMessage.success('删除成功')
  loadAnnouncements()
}

const handleTop = async (row) => {
  await request.post(`/announcement/${row.id}/top`, { top: row.top === 1 ? 0 : 1 })
  ElMessage.success('操作成功')
  loadAnnouncements()
}

const handleSave = async () => {
  if (editingAnn.value.id) {
    await request.put(`/announcement/${editingAnn.value.id}`, form.value)
  } else {
    await request.post('/announcement', form.value)
  }
  ElMessage.success('保存成功')
  dialogVisible.value = false
  loadAnnouncements()
}

onMounted(() => {
  if (!userStore.isAdmin) {
    router.push('/')
    return
  }
  loadAnnouncements()
})
</script>
