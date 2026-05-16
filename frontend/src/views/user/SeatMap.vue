<template>
  <div class="page-shell">
    <UserNav />

    <main class="page-container seat-page">
      <div class="seat-header soft-card">
        <el-button @click="router.back()">返回</el-button>
        <div>
          <h1>{{ roomName || '座位选择' }}</h1>
          <p>绿色座位可预约，提交后进入待审核状态，管理员通过后才能签到。</p>
        </div>
      </div>

      <section class="seat-layout">
        <aside class="seat-summary soft-card">
          <h2>状态说明</h2>
          <div class="legend">
            <span><i class="available"></i>可预约</span>
            <span><i class="occupied"></i>已占用</span>
            <span><i class="reserved"></i>已预约</span>
            <span><i class="locked"></i>维修/锁定</span>
          </div>
          <div class="selected-card">
            <span>当前选择</span>
            <strong>{{ selectedSeat?.seatCode || '未选择座位' }}</strong>
            <p>{{ selectedSeat ? seatFeatureText(selectedSeat) : '点击绿色座位后选择预约时间。' }}</p>
          </div>
        </aside>

        <section class="seat-board soft-card">
          <div class="room-map">
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
                      class="seat"
                      :class="[getSeatClass(seat), { selected: selectedSeat?.id === seat.id }]"
                      :disabled="seat.status !== 1"
                      @click="handleSeatClick(seat)"
                    >
                      <strong>{{ displaySeatCode(seat) }}</strong>
                      <small>
                        <span v-if="isEnabled(seat.hasPower)">电源</span>
                        <span v-if="shouldShowWindow(seat)">窗</span>
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

            <div class="back-wall">
              <span>后排安静区</span>
            </div>
          </div>
          <el-empty v-if="!seats.length" description="暂无座位数据" />
        </section>
      </section>

      <el-dialog v-model="dialogVisible" title="提交座位预约" width="460px">
        <el-form :model="reserveForm" :rules="rules" ref="formRef" label-width="88px">
          <el-form-item label="座位">
            <el-tag type="success">{{ selectedSeat?.seatCode }}</el-tag>
          </el-form-item>
          <el-form-item label="快捷时段">
            <div class="quick-time-list">
              <button
                v-for="slot in quickTimeSlots"
                :key="slot.label"
                type="button"
                class="quick-time"
                :class="{ active: selectedQuickSlot === slot.label }"
                @click="applyQuickSlot(slot)"
              >
                {{ slot.label }}
              </button>
            </div>
          </el-form-item>
          <el-form-item label="开始时间" prop="startTime">
            <el-date-picker
              v-model="reserveForm.startTime"
              type="datetime"
              value-format="YYYY-MM-DDTHH:mm:ss"
              placeholder="选择开始时间"
              style="width: 100%"
            />
          </el-form-item>
          <el-form-item label="结束时间" prop="endTime">
            <el-date-picker
              v-model="reserveForm.endTime"
              type="datetime"
              value-format="YYYY-MM-DDTHH:mm:ss"
              placeholder="选择结束时间"
              style="width: 100%"
            />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="loading" @click="handleReserve">确认预约</el-button>
        </template>
      </el-dialog>
    </main>
  </div>
</template>

<script setup>
import { computed, ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '../../stores/user'
import { ElMessage } from 'element-plus'
import UserNav from '../../components/UserNav.vue'
import request from '../../api'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const roomName = ref('')
const seats = ref([])
const dialogVisible = ref(false)
const selectedSeat = ref(null)
const loading = ref(false)
const formRef = ref(null)
const reserveForm = ref({ startTime: '', endTime: '' })
const selectedQuickSlot = ref('')
const seatsPerRow = 8
const aisleAfter = 4
const quickTimeSlots = [
  { label: '08:00-10:00', start: '08:00:00', end: '10:00:00' },
  { label: '10:00-12:00', start: '10:00:00', end: '12:00:00' },
  { label: '14:00-16:00', start: '14:00:00', end: '16:00:00' },
  { label: '16:00-18:00', start: '16:00:00', end: '18:00:00' },
  { label: '19:00-21:00', start: '19:00:00', end: '21:00:00' }
]

const rules = {
  startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
  endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }]
}

const getSeatClass = (seat) => ({ 1: 'available', 2: 'occupied', 3: 'reserved', 4: 'locked' }[seat.status] || 'locked')
const seatRows = computed(() => {
  const sorted = [...seats.value].sort((a, b) => extractSeatNumber(a) - extractSeatNumber(b))
  const rows = []
  for (let index = 0; index < sorted.length; index += seatsPerRow) {
    const row = sorted.slice(index, index + seatsPerRow)
    while (row.length < seatsPerRow) row.push(null)
    rows.push(row)
  }
  return rows
})

const extractSeatNumber = (seat) => {
  const match = String(seat?.seatCode || '').match(/(\d+)(?!.*\d)/)
  return match ? Number(match[1]) : Number(seat?.id || 0)
}

const displaySeatCode = (seat) => {
  const match = String(seat?.seatCode || '').match(/(\d+)(?!.*\d)/)
  return match ? match[1].padStart(2, '0') : seat.seatCode
}

const rowLabel = (index) => String.fromCharCode(65 + index)
const isEnabled = (value) => value === 1 || value === true || value === '1'
const isWindowPosition = (seat) => {
  const position = (extractSeatNumber(seat) - 1) % seatsPerRow
  return position === 0
}
const shouldShowWindow = (seat) => isEnabled(seat.hasWindow) && isWindowPosition(seat)
const seatFeatureText = (seat) => {
  const tags = []
  if (isEnabled(seat.hasPower)) tags.push('电源')
  if (shouldShowWindow(seat)) tags.push('靠窗')
  return tags.join(' · ') || '普通座'
}

const normalizeSeat = (seat) => ({
  ...seat,
  status: Number(seat.status),
  hasPower: Number(seat.hasPower || 0),
  hasWindow: Number(seat.hasWindow || 0)
})

const handleSeatClick = (seat) => {
  if (seat.status !== 1) {
    ElMessage.warning('该座位当前不可预约')
    return
  }
  selectedSeat.value = seat
  reserveForm.value = { startTime: '', endTime: '' }
  selectedQuickSlot.value = ''
  dialogVisible.value = true
}

const todayDate = () => {
  const date = new Date()
  const y = date.getFullYear()
  const m = String(date.getMonth() + 1).padStart(2, '0')
  const d = String(date.getDate()).padStart(2, '0')
  return `${y}-${m}-${d}`
}

const applyQuickSlot = (slot) => {
  selectedQuickSlot.value = slot.label
  const day = todayDate()
  reserveForm.value = {
    startTime: `${day}T${slot.start}`,
    endTime: `${day}T${slot.end}`
  }
}

const handleReserve = async () => {
  await formRef.value.validate()
  if (new Date(reserveForm.value.endTime) <= new Date(reserveForm.value.startTime)) {
    ElMessage.warning('结束时间必须晚于开始时间')
    return
  }
  loading.value = true
  try {
    await request.post('/reservation', {
      seatId: selectedSeat.value.id,
      roomId: Number(route.params.id),
      startTime: reserveForm.value.startTime,
      endTime: reserveForm.value.endTime
    })
    ElMessage.success('预约已提交，请等待管理员审核')
    dialogVisible.value = false
    await loadSeats()
  } finally {
    loading.value = false
  }
}

const loadSeats = async () => {
  const [roomRes, seatRes] = await Promise.all([
    request.get(`/room/${route.params.id}`),
    request.get(`/seat/room/${route.params.id}`)
  ])
  roomName.value = roomRes.data?.roomName || ''
  seats.value = (seatRes.data || []).map(normalizeSeat)
}

onMounted(() => {
  if (!userStore.isLoggedIn) {
    router.push('/login')
    return
  }
  loadSeats()
})
</script>

<style scoped>
.seat-page {
  padding: 32px 0 54px;
}

.seat-header {
  display: flex;
  align-items: center;
  gap: 18px;
  margin-bottom: 22px;
  padding: 22px;
}

.seat-header h1 {
  margin: 0;
  font-size: 34px;
  line-height: 1.12;
}

.seat-header p {
  margin: 8px 0 0;
  color: var(--muted);
}

.seat-layout {
  display: grid;
  grid-template-columns: 300px minmax(0, 1fr);
  gap: 22px;
}

.seat-summary,
.seat-board {
  padding: 22px;
}

.seat-summary h2 {
  margin: 0 0 16px;
  font-size: 20px;
}

.legend {
  display: grid;
  gap: 12px;
}

.legend span {
  display: flex;
  align-items: center;
  gap: 10px;
  color: #4f6259;
}

.legend i {
  width: 18px;
  height: 18px;
  border-radius: 5px;
}

.selected-card {
  margin-top: 24px;
  padding: 16px;
  border-radius: 8px;
  background: #f4f8f5;
  border: 1px solid #dfe9e3;
}

.selected-card span {
  color: var(--muted);
  font-size: 13px;
}

.selected-card strong {
  display: block;
  margin-top: 6px;
  font-size: 24px;
}

.selected-card p {
  margin: 8px 0 0;
  color: var(--muted);
}

.room-map {
  min-width: 680px;
}

.front-wall,
.back-wall {
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
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
  height: 38px;
  margin-top: 18px;
  font-size: 13px;
}

.map-body {
  display: grid;
  grid-template-columns: 42px minmax(0, 1fr) 54px;
  gap: 14px;
  margin-top: 18px;
}

.window-side,
.door-side {
  display: grid;
  place-items: center;
  min-height: 100%;
  border: 1px dashed #c7d6ce;
  border-radius: 8px;
  color: var(--muted);
  background: rgba(244, 248, 245, 0.7);
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
  gap: 14px;
}

.seat-row {
  display: grid;
  grid-template-columns: 24px repeat(4, minmax(60px, 1fr)) 42px repeat(4, minmax(60px, 1fr));
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

.seat {
  min-height: 70px;
  border: 0;
  border-radius: 9px;
  color: #fff;
  cursor: pointer;
  transition: transform 0.18s ease, box-shadow 0.18s ease, filter 0.18s ease;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  gap: 5px;
  box-shadow: inset 0 -10px 18px rgba(0, 0, 0, 0.08);
}

.seat.selected {
  outline: 3px solid rgba(183, 121, 63, 0.38);
  outline-offset: 2px;
}

.seat strong {
  font-size: 18px;
  font-variant-numeric: tabular-nums;
}

.seat small {
  min-height: 18px;
  font-size: 11px;
  opacity: 0.9;
  display: inline-flex;
  gap: 4px;
}

.seat small span {
  min-width: 20px;
  height: 16px;
  display: grid;
  place-items: center;
  border-radius: 4px;
  color: rgba(255, 255, 255, 0.92);
  background: rgba(255, 255, 255, 0.18);
  padding: 0 4px;
}

.seat-placeholder {
  min-height: 70px;
  border: 1px dashed #d7e1dc;
  border-radius: 9px;
  background: rgba(247, 250, 247, 0.58);
}

.quick-time-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.quick-time {
  border: 1px solid #d7e1dc;
  border-radius: 7px;
  padding: 7px 10px;
  color: #4f6259;
  background: #f7faf7;
  font-size: 13px;
  cursor: pointer;
  transition: color 0.18s ease, background-color 0.18s ease, border-color 0.18s ease;
}

.quick-time:hover,
.quick-time.active {
  color: #fff;
  border-color: var(--primary);
  background: var(--primary);
}

.seat:not(:disabled):hover {
  transform: translateY(-3px);
  box-shadow: 0 16px 28px rgba(36, 138, 99, 0.26);
}

.seat:disabled {
  cursor: not-allowed;
  filter: saturate(0.85);
}

.available { background: #248a63; }
.occupied { background: #8d9b94; }
.reserved { background: #2f6f9f; }
.locked { background: #c44949; }

@media (max-width: 900px) {
  .seat-layout {
    grid-template-columns: 1fr;
  }

  .seat-board {
    overflow-x: auto;
  }
}

@media (max-width: 560px) {
  .seat-page {
    padding: 22px 0 38px;
  }

  .seat-header {
    align-items: flex-start;
    flex-direction: column;
    padding: 18px;
  }

  .seat-header .el-button {
    width: 100%;
  }

  .seat-header h1 {
    font-size: 28px;
  }

  .seat-summary,
  .seat-board {
    padding: 18px;
  }

  .legend {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .room-map {
    min-width: 620px;
  }

  .seat-row {
    grid-template-columns: 22px repeat(4, minmax(52px, 1fr)) 38px repeat(4, minmax(52px, 1fr));
    gap: 8px;
  }

  .seat {
    min-height: 62px;
  }

  .seat strong {
    font-size: 16px;
  }

  .quick-time {
    flex: 1 1 calc(50% - 8px);
  }
}
</style>
