<template>
  <AdminShell title="座位管理">
    <div class="admin-page-title">
      <div>
        <h2>座位管理</h2>
        <p>按自习室维护座位基础属性。已预约、已占用由预约审核和签到流程自动更新。</p>
      </div>
      <div class="title-actions">
        <el-select v-model="selectedRoomId" placeholder="选择自习室" class="room-select">
          <el-option
            v-for="room in rooms"
            :key="room.id"
            :label="room.roomName"
            :value="room.id"
          />
        </el-select>
        <el-button plain @click="openBatchDialog">批量生成</el-button>
        <el-button type="primary" @click="handleAdd">新增座位</el-button>
      </div>
    </div>

    <section class="seat-stats">
      <article v-for="item in statCards" :key="item.label" class="soft-card" :class="item.type">
        <span>{{ item.label }}</span>
        <strong>{{ item.value }}</strong>
      </article>
    </section>

    <section class="seat-board soft-card">
      <div class="board-head">
        <div>
          <h3>{{ selectedRoomName }}</h3>
          <p>点击座位卡片可维护基础属性。管理员只手动设置“可预约”和“维修/锁定”，业务占用状态由系统自动处理。</p>
        </div>
        <div class="board-actions">
          <div class="legend">
            <span><i class="available"></i>可预约</span>
            <span><i class="occupied"></i>已占用</span>
            <span><i class="reserved"></i>已预约</span>
            <span><i class="locked"></i>维修/锁定</span>
          </div>
          <el-button @click="loadData">刷新</el-button>
        </div>
      </div>

      <div v-if="filteredSeats.length" class="admin-room-map">
        <div class="front-wall">
          <span>讲台 / 前区</span>
          <em>Entrance</em>
        </div>

        <div class="map-body">
          <div class="window-side">靠窗区</div>
          <div class="seat-rows">
            <div v-for="(row, rowIndex) in seatRows" :key="rowIndex" class="seat-row">
              <span class="row-label">{{ rowLabel(rowIndex) }}</span>
              <template v-for="(seat, seatIndex) in row" :key="seat?.id || `empty-${rowIndex}-${seatIndex}`">
                <span v-if="seatIndex === aisleAfter" class="vertical-aisle">过道</span>
                <button
                  v-if="seat"
                  class="seat-card"
                  :class="statusClass(seat.status)"
                  @click="handleEdit(seat)"
                >
                  <strong>{{ shortSeatCode(seat.seatCode) }}</strong>
                  <span>{{ statusText(seat.status) }}</span>
                  <small>
                    <em v-if="isEnabled(seat.hasPower)">电源</em>
                    <em v-if="shouldShowWindow(seat)">靠窗</em>
                  </small>
                </button>
                <span v-else class="seat-placeholder"></span>
              </template>
            </div>
          </div>
          <div class="door-side">
            <span>入口</span>
          </div>
        </div>

        <div class="back-wall">后排安静区</div>
      </div>

      <el-empty v-if="!filteredSeats.length" description="当前自习室暂无座位" />
    </section>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑座位' : '新增座位'" width="520px">
      <el-form :model="form" label-width="92px">
        <el-form-item label="所属自习室">
          <el-select v-model="form.roomId" style="width: 100%">
            <el-option
              v-for="room in rooms"
              :key="room.id"
              :label="room.roomName"
              :value="room.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="座位编号">
          <el-input v-model="form.seatCode" placeholder="例如 ROOM001-01" />
        </el-form-item>
        <el-form-item label="座位状态">
          <el-radio-group v-model="form.status">
            <el-radio-button :label="1">可预约</el-radio-button>
            <el-radio-button :label="4">维修/锁定</el-radio-button>
          </el-radio-group>
          <div class="form-tip">已预约、已占用不是人工维护状态，会由审核通过、签到、签退自动切换。</div>
        </el-form-item>
        <el-form-item label="座位属性">
          <div class="seat-switches">
            <el-switch v-model="form.hasPower" :active-value="1" :inactive-value="0" active-text="有电源" />
            <el-switch v-model="form.hasWindow" :active-value="1" :inactive-value="0" active-text="靠窗" />
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button v-if="form.id" type="danger" plain @click="handleDelete">删除</el-button>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="batchDialogVisible" title="批量生成座位" width="560px">
      <el-form :model="batchForm" label-width="112px">
        <el-form-item label="所属自习室">
          <el-select v-model="batchForm.roomId" style="width: 100%">
            <el-option
              v-for="room in rooms"
              :key="room.id"
              :label="room.roomName"
              :value="room.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="生成数量">
          <el-input-number v-model="batchForm.count" :min="1" :max="120" style="width: 180px" />
          <span class="inline-tip">会从当前最大编号后继续生成，避免覆盖已有座位。</span>
        </el-form-item>
        <el-form-item label="电源规则">
          <el-radio-group v-model="batchForm.powerMode">
            <el-radio-button label="none">不设置</el-radio-button>
            <el-radio-button label="all">全部有电源</el-radio-button>
            <el-radio-button label="desk">每排后四座</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="靠窗规则">
          <el-switch v-model="batchForm.windowColumn" active-text="仅每排第一列标记靠窗" />
          <div class="form-tip">前后台都会只在物理靠窗位置展示“靠窗”，避免不靠窗座位显示窗户。</div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="batchDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="batchLoading" @click="handleBatchGenerate">开始生成</el-button>
      </template>
    </el-dialog>
  </AdminShell>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../../stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import AdminShell from '../../components/AdminShell.vue'
import request from '../../api'

const router = useRouter()
const userStore = useUserStore()
const rooms = ref([])
const seats = ref([])
const selectedRoomId = ref(null)
const dialogVisible = ref(false)
const batchDialogVisible = ref(false)
const batchLoading = ref(false)
const form = ref(emptyForm())
const seatsPerRow = 8
const aisleAfter = 4
const batchForm = ref({
  roomId: null,
  count: 32,
  powerMode: 'desk',
  windowColumn: true
})

function emptyForm() {
  return {
    id: null,
    roomId: selectedRoomId.value,
    seatCode: '',
    status: 1,
    hasPower: 0,
    hasWindow: 0
  }
}

const filteredSeats = computed(() => {
  return seats.value
    .filter(seat => !selectedRoomId.value || seat.roomId === selectedRoomId.value)
    .sort((a, b) => extractSeatNumber(a.seatCode) - extractSeatNumber(b.seatCode))
})

const selectedRoomName = computed(() => rooms.value.find(room => room.id === selectedRoomId.value)?.roomName || '全部自习室')
const selectedRoom = computed(() => rooms.value.find(room => room.id === selectedRoomId.value))
const seatRows = computed(() => {
  const rows = []
  for (let index = 0; index < filteredSeats.value.length; index += seatsPerRow) {
    const row = filteredSeats.value.slice(index, index + seatsPerRow)
    while (row.length < seatsPerRow) row.push(null)
    rows.push(row)
  }
  return rows
})
const statCards = computed(() => [
  { label: '座位总数', value: filteredSeats.value.length, type: 'total' },
  { label: '可预约', value: countStatus(1), type: 'available-card' },
  { label: '已占用', value: countStatus(2), type: 'occupied-card' },
  { label: '已预约', value: countStatus(3), type: 'reserved-card' },
  { label: '维修/锁定', value: countStatus(4), type: 'locked-card' }
])

const countStatus = (status) => filteredSeats.value.filter(seat => seat.status === status).length
const rowLabel = (index) => String.fromCharCode(65 + index)
const extractSeatNumber = (code) => {
  const match = String(code || '').match(/(\d+)(?!.*\d)/)
  return match ? Number(match[1]) : 0
}
const shortSeatCode = (code) => {
  const match = String(code || '').match(/(\d+)(?!.*\d)/)
  return match ? match[1].padStart(2, '0') : code
}
const statusText = (status) => ({ 1: '可预约', 2: '已占用', 3: '已预约', 4: '维修/锁定' }[status] || '未知')
const statusClass = (status) => ({ 1: 'available', 2: 'occupied', 3: 'reserved', 4: 'locked' }[status] || 'locked')
const isEnabled = (value) => value === 1 || value === true || value === '1'
const isWindowPosition = (seat) => {
  const position = (extractSeatNumber(seat.seatCode) - 1) % seatsPerRow
  return position === 0
}
const shouldShowWindow = (seat) => isEnabled(seat.hasWindow) && isWindowPosition(seat)
const nextSeatNumber = () => Math.max(0, ...filteredSeats.value.map(seat => extractSeatNumber(seat.seatCode))) + 1
const normalizeSeat = (seat) => ({
  ...seat,
  status: Number(seat.status),
  hasPower: Number(seat.hasPower || 0),
  hasWindow: Number(seat.hasWindow || 0)
})

const loadData = async () => {
  const [roomRes, seatRes] = await Promise.all([
    request.get('/room/admin/list'),
    request.get('/seat/admin/list')
  ])
  rooms.value = roomRes.data || []
  seats.value = (seatRes.data || []).map(normalizeSeat)
  if (!selectedRoomId.value && rooms.value.length) {
    selectedRoomId.value = rooms.value[0].id
  }
}

const handleAdd = () => {
  form.value = emptyForm()
  const room = selectedRoom.value
  const nextNumber = String(nextSeatNumber()).padStart(2, '0')
  form.value.seatCode = `${room?.roomCode || 'ROOM'}-${nextNumber}`
  dialogVisible.value = true
}

const handleEdit = (seat) => {
  form.value = {
    id: seat.id,
    roomId: seat.roomId,
    seatCode: seat.seatCode,
    status: seat.status === 4 ? 4 : 1,
    hasPower: seat.hasPower || 0,
    hasWindow: seat.hasWindow || 0
  }
  dialogVisible.value = true
}

const handleSave = async () => {
  if (!form.value.roomId || !form.value.seatCode) {
    ElMessage.warning('请选择自习室并填写座位编号')
    return
  }
  const payload = {
    roomId: form.value.roomId,
    seatCode: form.value.seatCode,
    status: form.value.status,
    hasPower: form.value.hasPower,
    hasWindow: form.value.hasWindow
  }
  if (form.value.id) {
    await request.put(`/seat/${form.value.id}`, payload)
  } else {
    await request.post('/seat', payload)
  }
  ElMessage.success('保存成功')
  dialogVisible.value = false
  loadData()
}

const openBatchDialog = () => {
  batchForm.value = {
    roomId: selectedRoomId.value,
    count: 32,
    powerMode: 'desk',
    windowColumn: true
  }
  batchDialogVisible.value = true
}

const handleBatchGenerate = async () => {
  if (!batchForm.value.roomId) {
    ElMessage.warning('请选择自习室')
    return
  }
  const room = rooms.value.find(item => item.id === batchForm.value.roomId)
  if (!room) {
    ElMessage.warning('自习室不存在')
    return
  }
  const oldSelectedRoomId = selectedRoomId.value
  selectedRoomId.value = batchForm.value.roomId
  const startNumber = nextSeatNumber()
  selectedRoomId.value = oldSelectedRoomId

  batchLoading.value = true
  try {
    for (let index = 0; index < batchForm.value.count; index += 1) {
      const number = startNumber + index
      const position = (number - 1) % seatsPerRow
      await request.post('/seat', {
        roomId: batchForm.value.roomId,
        seatCode: `${room.roomCode || 'ROOM'}-${String(number).padStart(2, '0')}`,
        status: 1,
        hasPower: batchForm.value.powerMode === 'all' || (batchForm.value.powerMode === 'desk' && position >= aisleAfter) ? 1 : 0,
        hasWindow: batchForm.value.windowColumn && position === 0 ? 1 : 0
      })
    }
    ElMessage.success('座位生成成功')
    selectedRoomId.value = batchForm.value.roomId
    batchDialogVisible.value = false
    loadData()
  } finally {
    batchLoading.value = false
  }
}

const handleDelete = async () => {
  await ElMessageBox.confirm('确定删除该座位吗？', '提示', { type: 'warning' })
  await request.delete(`/seat/${form.value.id}`)
  ElMessage.success('删除成功')
  dialogVisible.value = false
  loadData()
}

onMounted(() => {
  if (!userStore.isAdmin) {
    router.push('/')
    return
  }
  loadData()
})
</script>

<style scoped>
.title-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.room-select {
  width: 220px;
}

.seat-stats {
  display: grid;
  grid-template-columns: repeat(5, minmax(0, 1fr));
  gap: 14px;
  margin-bottom: 18px;
}

.seat-stats article {
  padding: 18px;
  position: relative;
  overflow: hidden;
}

.seat-stats article::after {
  content: "";
  position: absolute;
  right: -24px;
  top: -24px;
  width: 76px;
  height: 76px;
  border-radius: 50%;
  background: rgba(31, 111, 91, 0.08);
}

.seat-stats span {
  color: var(--muted);
  font-size: 13px;
}

.seat-stats strong {
  display: block;
  margin-top: 8px;
  color: var(--primary);
  font-size: 28px;
}

.available-card strong { color: #248a63; }
.occupied-card strong { color: #7d8b84; }
.reserved-card strong { color: #2f6f9f; }
.locked-card strong { color: #c44949; }

.seat-board {
  padding: 22px;
  overflow-x: auto;
}

.board-head {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
  margin-bottom: 18px;
}

.board-head h3 {
  margin: 0;
  font-size: 22px;
}

.board-head p {
  margin: 8px 0 0;
  color: var(--muted);
}

.board-actions {
  display: flex;
  align-items: center;
  gap: 14px;
}

.legend {
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-end;
  gap: 8px;
}

.legend span {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 7px 9px;
  border: 1px solid #dfe8e2;
  border-radius: 7px;
  color: #4f6259;
  background: #f7faf7;
  font-size: 12px;
  font-weight: 700;
}

.legend i {
  width: 10px;
  height: 10px;
  border-radius: 3px;
}

.admin-room-map {
  min-width: 760px;
}

.front-wall,
.back-wall {
  height: 46px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  border-radius: 8px;
  color: #44584f;
  background: linear-gradient(90deg, #dfeae4, #f8faf7, #dfeae4);
  font-weight: 800;
}

.front-wall em {
  color: var(--muted);
  font-style: normal;
  font-size: 12px;
  font-weight: 700;
  text-transform: uppercase;
}

.back-wall {
  height: 36px;
  margin-top: 16px;
  font-size: 13px;
}

.map-body {
  display: grid;
  grid-template-columns: 42px minmax(0, 1fr) 54px;
  gap: 14px;
  margin-top: 16px;
}

.window-side,
.door-side {
  display: grid;
  place-items: center;
  min-height: 100%;
  border: 1px dashed #c7d6ce;
  border-radius: 8px;
  color: var(--muted);
  background: rgba(244, 248, 245, 0.72);
  font-size: 12px;
  font-weight: 800;
  writing-mode: vertical-rl;
  letter-spacing: 0;
}

.door-side span {
  padding: 8px 0;
  color: #fff;
  border-radius: 6px;
  background: var(--accent);
}

.seat-rows {
  display: grid;
  gap: 12px;
}

.seat-row {
  display: grid;
  grid-template-columns: 24px repeat(4, minmax(62px, 1fr)) 42px repeat(4, minmax(62px, 1fr));
  gap: 10px;
  align-items: center;
}

.row-label {
  color: var(--muted);
  font-size: 13px;
  font-weight: 800;
}

.vertical-aisle {
  align-self: stretch;
  display: grid;
  place-items: center;
  border-radius: 6px;
  color: #819187;
  background: repeating-linear-gradient(
    45deg,
    #eef4ef,
    #eef4ef 6px,
    #dfe8e2 6px,
    #dfe8e2 12px
  );
  font-size: 11px;
  font-weight: 800;
}

.seat-card {
  min-height: 76px;
  border: 0;
  border-radius: 9px;
  color: #fff;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 4px;
  padding: 10px 6px;
  box-shadow: inset 0 -10px 18px rgba(0, 0, 0, 0.10);
  transition: transform 0.18s ease, box-shadow 0.18s ease, filter 0.18s ease;
}

.seat-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 16px 28px rgba(20, 43, 35, 0.18);
}

.seat-card strong {
  font-size: 18px;
  font-variant-numeric: tabular-nums;
}

.seat-card span {
  font-size: 12px;
  font-weight: 700;
}

.seat-card small {
  min-height: 18px;
  display: inline-flex;
  gap: 4px;
}

.seat-card em {
  padding: 2px 5px;
  border-radius: 4px;
  background: rgba(255, 255, 255, 0.18);
  font-size: 11px;
  font-style: normal;
}

.seat-placeholder {
  min-height: 76px;
  border: 1px dashed #d7e1dc;
  border-radius: 9px;
  background: rgba(247, 250, 247, 0.58);
}

.available { background: #248a63; }
.occupied { background: #8d9b94; }
.reserved { background: #2f6f9f; }
.locked { background: #c44949; }

.seat-switches {
  display: flex;
  flex-wrap: wrap;
  gap: 18px;
}

.form-tip {
  width: 100%;
  margin-top: 8px;
  color: var(--muted);
  font-size: 12px;
  line-height: 1.6;
}

.inline-tip {
  margin-left: 12px;
  color: var(--muted);
  font-size: 12px;
}

@media (max-width: 1000px) {
  .seat-stats {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .admin-page-title,
  .title-actions,
  .board-head,
  .board-actions {
    align-items: flex-start;
    flex-direction: column;
  }
}
</style>
