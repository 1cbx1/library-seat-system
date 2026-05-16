<template>
  <AdminShell title="自习室管理">
    <div class="admin-page-title">
      <div>
        <h2>自习室管理</h2>
        <p>维护自习室图片、位置、容量、开放时间和收费字段。</p>
      </div>
      <el-button type="primary" @click="handleAdd">新增自习室</el-button>
    </div>

    <el-table :data="rooms" stripe class="soft-card">
      <el-table-column prop="roomCode" label="编号" width="110" />
      <el-table-column prop="roomName" label="名称" min-width="140" />
      <el-table-column prop="location" label="位置" min-width="140" />
      <el-table-column prop="floor" label="楼层" width="80" />
      <el-table-column prop="capacity" label="容量" width="90" />
      <el-table-column prop="hourPrice" label="价格/小时" width="110" />
      <el-table-column prop="openTime" label="开放时间" min-width="130" />
      <el-table-column prop="status" label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '开放' : '关闭' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="170" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="editingRoom.id ? '编辑自习室' : '新增自习室'" width="620px">
      <el-form :model="form" label-width="96px">
        <el-form-item label="编号"><el-input v-model="form.roomCode" /></el-form-item>
        <el-form-item label="名称"><el-input v-model="form.roomName" /></el-form-item>
        <el-form-item label="位置"><el-input v-model="form.location" /></el-form-item>
        <el-form-item label="楼层"><el-input v-model="form.floor" /></el-form-item>
        <el-form-item label="容量"><el-input-number v-model="form.capacity" :min="1" /></el-form-item>
        <el-form-item label="价格/小时"><el-input-number v-model="form.hourPrice" :min="0" :precision="2" /></el-form-item>
        <el-form-item label="开放时间"><el-input v-model="form.openTime" placeholder="如 08:00-22:00" /></el-form-item>
        <el-form-item label="图片 URL"><el-input v-model="form.imageUrl" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" type="textarea" rows="3" /></el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">开放</el-radio>
            <el-radio :label="0">关闭</el-radio>
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
const rooms = ref([])
const dialogVisible = ref(false)
const editingRoom = ref({})
const emptyForm = () => ({ roomCode: '', roomName: '', location: '', floor: '', capacity: 50, hourPrice: 0, openTime: '08:00-22:00', imageUrl: '', description: '', status: 1 })
const form = ref(emptyForm())

const loadRooms = async () => {
  const res = await request.get('/room/admin/list')
  rooms.value = res.data || []
}

const handleAdd = () => {
  editingRoom.value = {}
  form.value = emptyForm()
  dialogVisible.value = true
}

const handleEdit = (row) => {
  editingRoom.value = { ...row }
  form.value = { ...row }
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确定删除该自习室吗？', '提示', { type: 'warning' })
  await request.delete(`/room/${row.id}`)
  ElMessage.success('删除成功')
  loadRooms()
}

const handleSave = async () => {
  if (editingRoom.value.id) {
    await request.put(`/room/${editingRoom.value.id}`, form.value)
  } else {
    await request.post('/room', form.value)
  }
  ElMessage.success('保存成功')
  dialogVisible.value = false
  loadRooms()
}

onMounted(() => {
  if (!userStore.isAdmin) {
    router.push('/')
    return
  }
  loadRooms()
})
</script>
