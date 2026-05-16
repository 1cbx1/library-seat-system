<template>
  <div class="page-shell home-page">
    <UserNav />

    <section class="hero">
      <div class="hero-bg"></div>
      <div class="hero-inner page-container">
        <div class="hero-copy">
          <span class="eyebrow">Library seat booking</span>
          <h1>图书馆座位预约系统</h1>
          <p>从自习室浏览、座位选择、预约审核到签到签退，完整覆盖学生日常自习预约流程。</p>
          <div class="hero-actions">
            <el-button type="primary" size="large" @click="router.push('/studyrooms')">预约座位</el-button>
            <button class="text-action" @click="router.push('/my/reservations')">查看我的预约</button>
          </div>
        </div>

        <div class="hero-console">
          <div class="console-head">
            <div>
              <span>座位实时统计</span>
              <strong>{{ selectedRoomName }}</strong>
            </div>
            <el-select v-model="selectedRoomId" size="small" class="room-select" placeholder="选择自习室">
              <el-option
                v-for="room in allRooms"
                :key="room.id"
                :label="room.roomName"
                :value="room.id"
              />
            </el-select>
          </div>
          <div class="quick-stats">
            <div>
              <strong>{{ seatStats.available }}</strong>
              <span>可预约</span>
            </div>
            <div>
              <strong>{{ seatStats.reserved }}</strong>
              <span>已预约</span>
            </div>
            <div>
              <strong>{{ seatStats.occupied }}</strong>
              <span>已占用</span>
            </div>
          </div>
          <p class="console-note">当前展示：{{ selectedRoomName }}，共 {{ currentRoomSeats.length }} 个座位</p>
          <div class="mini-seat-grid">
            <span v-for="seat in previewSeats" :key="seat.key" :class="['mini-seat', seat.className]">{{ seat.label }}</span>
          </div>
          <div class="seat-caption">
            <span><i class="available"></i>可预约</span>
            <span><i class="reserved"></i>已预约</span>
            <span><i class="occupied"></i>已占用</span>
            <span><i class="locked"></i>维修 {{ seatStats.locked }}</span>
          </div>
          <el-button class="console-action" type="primary" @click="goToSeatMap(selectedRoomId)">进入该自习室选座</el-button>
        </div>
      </div>
    </section>

    <main class="page-container home-content">
      <section class="notice-panel soft-card">
        <div class="section-head">
          <div>
            <h2 class="section-title">系统公告</h2>
            <p class="section-subtitle">开放安排、维护通知和预约规则。</p>
          </div>
          <router-link class="text-link" to="/announcements">更多</router-link>
        </div>

        <div v-if="announcements.length" class="notice-list">
          <article v-for="item in announcements" :key="item.id" class="notice-item">
            <time>{{ formatDate(item.createTime) }}</time>
            <h3>{{ item.title }}</h3>
            <p>{{ item.content }}</p>
          </article>
        </div>
        <el-empty v-else description="暂无公告" />
      </section>

      <section class="room-panel">
        <div class="section-head">
          <div>
            <h2 class="section-title">推荐自习室</h2>
            <p class="section-subtitle">选择自习室后进入座位网格。</p>
          </div>
          <router-link class="text-link" to="/studyrooms">全部自习室</router-link>
        </div>

        <div class="room-grid">
          <article v-for="room in studyRooms" :key="room.id" class="room-card soft-card" @click="goToSeatMap(room.id)">
            <img :src="room.imageUrl || fallbackImage(room.id)" :alt="room.roomName" />
            <div class="room-card__body">
              <div class="room-title">
                <h3>{{ room.roomName }}</h3>
                <el-tag size="small" type="success">开放</el-tag>
              </div>
              <p>{{ room.location }} · {{ room.floor || '-' }}层</p>
              <div class="room-meta">
                <span>容量 {{ room.capacity || 0 }} 人</span>
                <span>{{ room.openTime || '08:00-22:00' }}</span>
              </div>
            </div>
          </article>
        </div>
      </section>
    </main>
  </div>
</template>

<script setup>
import { computed, ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../../stores/user'
import UserNav from '../../components/UserNav.vue'
import request from '../../api'

const router = useRouter()
const userStore = useUserStore()
const announcements = ref([])
const allRooms = ref([])
const studyRooms = ref([])
const allSeats = ref([])
const selectedRoomId = ref(null)

const formatDate = (value) => value ? new Date(value).toLocaleDateString() : ''
const fallbackImage = (id) => `https://picsum.photos/seed/library-room-${id}/520/320`
const seatClassMap = { 1: 'available', 2: 'occupied', 3: 'reserved', 4: 'locked' }
const selectedRoomName = computed(() => allRooms.value.find(room => room.id === selectedRoomId.value)?.roomName || '全部自习室')
const currentRoomSeats = computed(() => {
  if (!selectedRoomId.value) return allSeats.value
  return allSeats.value.filter(seat => seat.roomId === selectedRoomId.value)
})
const seatStats = computed(() => currentRoomSeats.value.reduce((stats, seat) => {
  if (seat.status === 1) stats.available += 1
  else if (seat.status === 2) stats.occupied += 1
  else if (seat.status === 3) stats.reserved += 1
  else stats.locked += 1
  return stats
}, { available: 0, reserved: 0, occupied: 0, locked: 0 }))
const previewSeats = computed(() => {
  const source = currentRoomSeats.value.slice(0, 32)
  if (source.length) {
    return source.map((seat, index) => ({
      key: seat.id || `${seat.seatCode}-${index}`,
      label: String(index + 1).padStart(2, '0'),
      className: seatClassMap[seat.status] || 'locked'
    }))
  }
  return Array.from({ length: 16 }, (_, index) => ({
    key: `empty-${index}`,
    label: String(index + 1).padStart(2, '0'),
    className: 'locked'
  }))
})

const goToSeatMap = (roomId) => {
  if (!userStore.isLoggedIn) {
    router.push({ path: '/login', query: { redirect: `/studyrooms/${roomId}/seats` } })
    return
  }
  router.push(`/studyrooms/${roomId}/seats`)
}

onMounted(async () => {
  const [announceRes, roomRes] = await Promise.all([
    request.get('/announcement/list'),
    request.get('/room/list')
  ])
  announcements.value = (announceRes.data || []).slice(0, 4)
  studyRooms.value = (roomRes.data || []).slice(0, 4)
  allRooms.value = roomRes.data || []
  selectedRoomId.value = allRooms.value[0]?.id || null

  const rooms = roomRes.data || []
  const seatResults = await Promise.allSettled(
    rooms.map(room => request.get(`/seat/room/${room.id}`))
  )
  allSeats.value = seatResults.flatMap(result => result.status === 'fulfilled' ? (result.value.data || []) : [])
})
</script>

<style scoped>
.hero {
  position: relative;
  min-height: 540px;
  overflow: hidden;
}

.hero-bg {
  position: absolute;
  inset: 0;
  background:
    linear-gradient(90deg, rgba(15, 32, 27, 0.86), rgba(15, 32, 27, 0.54), rgba(238, 243, 240, 0.92)),
    url("https://images.unsplash.com/photo-1521587760476-6c12a4b040da?auto=format&fit=crop&w=1800&q=80") center/cover;
}

.hero-inner {
  position: relative;
  z-index: 1;
  display: grid;
  grid-template-columns: minmax(0, 1fr) 430px;
  gap: 44px;
  align-items: center;
  min-height: 540px;
  padding: 52px 0 58px;
}

.hero-copy {
  color: #fff;
}

.eyebrow {
  display: inline-flex;
  padding: 7px 11px;
  border-radius: 6px;
  color: #e9d6bc;
  background: rgba(255, 255, 255, 0.12);
  font-weight: 700;
  font-size: 13px;
  text-transform: uppercase;
}

.hero h1 {
  max-width: 760px;
  margin: 20px 0 18px;
  font-size: clamp(44px, 6vw, 76px);
  line-height: 1.02;
  letter-spacing: 0;
}

.hero p {
  max-width: 610px;
  margin: 0;
  color: rgba(255, 255, 255, 0.82);
  font-size: 18px;
  line-height: 1.9;
}

.hero-actions {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-top: 30px;
}

.text-action {
  border: 0;
  padding: 0;
  color: #f1dfc8;
  background: transparent;
  font-size: 15px;
  font-weight: 700;
  cursor: pointer;
}

.hero-console {
  padding: 24px;
  border: 1px solid rgba(255, 255, 255, 0.28);
  border-radius: 10px;
  background: rgba(252, 253, 250, 0.86);
  box-shadow: 0 30px 80px rgba(9, 22, 18, 0.28);
  backdrop-filter: blur(18px);
}

.console-head,
.section-head,
.room-title {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 14px;
}

.console-head > div {
  display: grid;
  gap: 4px;
}

.console-head span {
  color: var(--muted);
}

.console-head strong {
  color: var(--primary);
}

.room-select {
  width: 150px;
}

.quick-stats {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 10px;
  margin: 20px 0;
}

.console-note {
  margin: 0 0 12px;
  color: var(--muted);
  font-size: 13px;
}

.quick-stats div {
  padding: 14px;
  border: 1px solid #e0e8e2;
  border-radius: 8px;
  background: #f8faf7;
}

.quick-stats strong {
  display: block;
  font-size: 25px;
  color: var(--primary);
}

.quick-stats span {
  color: var(--muted);
  font-size: 12px;
}

.mini-seat-grid {
  display: grid;
  grid-template-columns: repeat(8, 1fr);
  gap: 8px;
}

.mini-seat {
  height: 34px;
  display: grid;
  place-items: center;
  border-radius: 6px;
  color: #fff;
  font-size: 12px;
  font-weight: 700;
}

.seat-caption {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 16px;
  color: var(--muted);
  font-size: 12px;
}

.console-action {
  width: 100%;
  margin-top: 18px;
}

.seat-caption span {
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.seat-caption i {
  width: 10px;
  height: 10px;
  border-radius: 3px;
}

.available { background: #248a63; }
.occupied { background: #8d9b94; }
.reserved { background: #2f6f9f; }
.locked { background: #c44949; }

.home-content {
  display: grid;
  grid-template-columns: 0.82fr 1.18fr;
  gap: 24px;
  padding: 34px 0 54px;
}

.notice-panel {
  padding: 22px;
}

.text-link {
  color: var(--primary);
  font-weight: 700;
}

.notice-list {
  display: grid;
  gap: 12px;
  margin-top: 18px;
}

.notice-item {
  padding: 14px 0;
  border-top: 1px solid var(--line);
}

.notice-item time {
  color: var(--muted);
  font-size: 12px;
}

.notice-item h3 {
  margin: 6px 0;
  font-size: 16px;
}

.notice-item p {
  margin: 0;
  color: var(--muted);
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.room-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
  margin-top: 18px;
}

.room-card {
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.22s ease, box-shadow 0.22s ease;
}

.room-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 24px 58px rgba(20, 43, 35, 0.16);
}

.room-card img {
  width: 100%;
  height: 164px;
  object-fit: cover;
  display: block;
}

.room-card__body {
  padding: 16px;
}

.room-card h3 {
  margin: 0;
  font-size: 18px;
}

.room-card p {
  margin: 8px 0 14px;
  color: var(--muted);
}

.room-meta {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  color: #4f6259;
  font-size: 13px;
}

@media (max-width: 960px) {
  .hero-inner,
  .home-content {
    grid-template-columns: 1fr;
  }

  .room-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 560px) {
  .hero {
    min-height: auto;
  }

  .hero-bg {
    background:
      linear-gradient(180deg, rgba(15, 32, 27, 0.86), rgba(15, 32, 27, 0.72)),
      url("https://images.unsplash.com/photo-1521587760476-6c12a4b040da?auto=format&fit=crop&w=900&q=80") center/cover;
  }

  .hero-inner {
    min-height: auto;
    gap: 26px;
    padding: 38px 0 34px;
  }

  .hero h1 {
    font-size: 40px;
  }

  .hero p {
    font-size: 16px;
    line-height: 1.75;
  }

  .hero-actions {
    align-items: stretch;
    flex-direction: column;
    gap: 12px;
  }

  .text-action {
    min-height: 42px;
    border: 1px solid rgba(255, 255, 255, 0.24);
    border-radius: 7px;
    color: #fff;
  }

  .hero-console {
    padding: 18px;
  }

  .console-head {
    align-items: flex-start;
    flex-direction: column;
  }

  .room-select {
    width: 100%;
  }

  .quick-stats {
    grid-template-columns: repeat(3, minmax(0, 1fr));
    gap: 8px;
  }

  .quick-stats div {
    padding: 12px 8px;
  }

  .quick-stats strong {
    font-size: 22px;
  }

  .mini-seat-grid {
    grid-template-columns: repeat(4, 1fr);
  }

  .home-content {
    gap: 18px;
    padding: 22px 0 38px;
  }

  .section-head,
  .room-title {
    align-items: flex-start;
    flex-direction: column;
  }

  .notice-panel {
    padding: 18px;
  }

  .room-card img {
    height: 188px;
  }
}
</style>
