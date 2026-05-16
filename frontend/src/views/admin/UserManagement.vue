<template>
  <AdminShell title="用户管理">
    <div class="admin-page-title">
      <div>
        <h2>用户管理</h2>
        <p>查看用户列表，启用或禁用账号，并调整信用分。</p>
      </div>
      <el-button type="primary" @click="loadUsers">刷新</el-button>
    </div>

    <el-table :data="users" stripe class="soft-card">
      <el-table-column prop="username" label="用户名" min-width="130" />
      <el-table-column prop="realName" label="真实姓名" min-width="120" />
      <el-table-column prop="phone" label="手机号" min-width="130" />
      <el-table-column prop="creditScore" label="信用分" width="100" />
      <el-table-column prop="role" label="角色" width="100">
        <template #default="{ row }">
          <el-tag :type="row.role === 'admin' ? 'danger' : 'info'">{{ row.role === 'admin' ? '管理员' : '用户' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'warning'">{{ row.status === 1 ? '正常' : '禁用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="260" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="handleEditCredit(row)">调整信用分</el-button>
          <el-button :type="row.status === 1 ? 'warning' : 'success'" size="small" @click="handleToggleStatus(row)">
            {{ row.status === 1 ? '禁用' : '启用' }}
          </el-button>
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
const users = ref([])

const loadUsers = async () => {
  const res = await request.get('/user/list')
  users.value = res.data || []
}

const handleEditCredit = async (row) => {
  const { value } = await ElMessageBox.prompt('请输入新的信用分（0-100）', '调整信用分', {
    inputValue: String(row.creditScore ?? 100),
    inputPattern: /^(100|[1-9]?\d)$/,
    inputErrorMessage: '请输入 0-100 的整数'
  })
  await request.post(`/user/${row.id}/credit`, { score: Number(value) })
  ElMessage.success('信用分已更新')
  loadUsers()
}

const handleToggleStatus = async (row) => {
  const newStatus = row.status === 1 ? 0 : 1
  await request.post(`/user/${row.id}/status`, { status: newStatus })
  ElMessage.success('用户状态已更新')
  loadUsers()
}

onMounted(() => {
  if (!userStore.isAdmin) {
    router.push('/')
    return
  }
  loadUsers()
})
</script>
