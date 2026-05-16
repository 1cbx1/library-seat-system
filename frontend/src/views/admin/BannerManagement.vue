<template>
  <AdminShell title="轮播图管理">
    <div class="admin-page-title">
      <div>
        <h2>轮播图管理</h2>
        <p>维护首页轮播图标题、图片、跳转链接、排序和启用状态。</p>
      </div>
      <el-button type="primary" @click="handleAdd">新增轮播图</el-button>
    </div>

    <el-table :data="banners" stripe class="soft-card">
      <el-table-column prop="title" label="标题" min-width="180" />
      <el-table-column prop="imageUrl" label="图片" width="140">
        <template #default="{ row }">
          <img :src="row.imageUrl" class="banner-thumb" :alt="row.title" />
        </template>
      </el-table-column>
      <el-table-column prop="linkUrl" label="跳转链接" min-width="160" />
      <el-table-column prop="sort" label="排序" width="90" />
      <el-table-column prop="status" label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '显示' : '隐藏' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="170" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="editingBanner.id ? '编辑轮播图' : '新增轮播图'" width="560px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="标题"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="图片 URL"><el-input v-model="form.imageUrl" /></el-form-item>
        <el-form-item label="跳转链接"><el-input v-model="form.linkUrl" placeholder="/studyrooms" /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="form.sort" :min="0" /></el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">显示</el-radio>
            <el-radio :label="0">隐藏</el-radio>
          </el-radio-group>
        </el-form-item>
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
const banners = ref([])
const dialogVisible = ref(false)
const editingBanner = ref({})
const form = ref({ title: '', imageUrl: '', linkUrl: '', sort: 0, status: 1 })

const loadBanners = async () => {
  const res = await request.get('/banner/list')
  banners.value = res.data || []
}

const handleAdd = () => {
  editingBanner.value = {}
  form.value = { title: '', imageUrl: '', linkUrl: '', sort: 0, status: 1 }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  editingBanner.value = { ...row }
  form.value = { ...row }
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确定删除该轮播图吗？', '提示', { type: 'warning' })
  await request.delete(`/banner/${row.id}`)
  ElMessage.success('删除成功')
  loadBanners()
}

const handleSave = async () => {
  if (editingBanner.value.id) {
    await request.put(`/banner/${editingBanner.value.id}`, form.value)
  } else {
    await request.post('/banner', form.value)
  }
  ElMessage.success('保存成功')
  dialogVisible.value = false
  loadBanners()
}

onMounted(() => {
  if (!userStore.isAdmin) {
    router.push('/')
    return
  }
  loadBanners()
})
</script>

<style scoped>
.banner-thumb {
  width: 96px;
  height: 52px;
  object-fit: cover;
  border-radius: 6px;
  background: #eef4ff;
}
</style>
