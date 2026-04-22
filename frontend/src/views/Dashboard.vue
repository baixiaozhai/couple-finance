<template>
  <div class="dashboard">
    <!-- 家庭提示 -->
    <div v-if="!userStore.hasFamily" class="family-tip">
      <el-alert
        title="您还没有加入家庭"
        description="创建或加入一个家庭，开始共同记账吧！"
        type="info"
        show-icon
        :closable="false"
      />
      <div class="family-actions">
        <el-button type="primary" @click="showCreateDialog = true">创建家庭</el-button>
        <el-button @click="showJoinDialog = true">加入家庭</el-button>
      </div>
    </div>

    <!-- 概览卡片 -->
    <div v-else class="overview-cards">
      <div class="card expense-card">
        <div class="card-label">本月支出</div>
        <div class="card-value expense">¥{{ formatAmount(monthExpense) }}</div>
      </div>
      <div class="card budget-card">
        <div class="card-label">预算剩余</div>
        <div class="card-value" :class="{ warning: budgetRemaining < 0 }">
          ¥{{ formatAmount(budgetRemaining) }}
        </div>
      </div>
      <div class="card income-card">
        <div class="card-label">本月收入</div>
        <div class="card-value income">¥{{ formatAmount(monthIncome) }}</div>
      </div>
    </div>

    <!-- 快捷记账按钮 -->
    <div v-if="userStore.hasFamily" class="quick-add">
      <el-button type="primary" size="large" class="add-btn" @click="goToTransaction">
        <el-icon><Plus /></el-icon>
        记一笔
      </el-button>
    </div>

    <!-- 最近消费 -->
    <div v-if="userStore.hasFamily" class="recent-section">
      <h3 class="section-title">最近消费</h3>
      <div v-if="recentTransactions.length === 0" class="empty-tip">
        暂无消费记录，快去记一笔吧！
      </div>
      <div v-else class="transaction-list">
        <div
          v-for="item in recentTransactions"
          :key="item.id"
          class="transaction-item"
        >
          <div class="item-left">
            <div class="category-tag" :class="item.type">
              {{ item.category }}
            </div>
            <div class="item-info">
              <div class="item-note">{{ item.note || item.category }}</div>
              <div class="item-date">{{ formatDate(item.recordDate) }}</div>
            </div>
          </div>
          <div class="item-right">
            <div class="item-amount" :class="item.type">
              {{ item.type === 'income' ? '+' : '-' }}¥{{ formatAmount(item.amount) }}
            </div>
            <div class="item-payer">{{ getPayerName(item.payerId) }}</div>
          </div>
        </div>
      </div>
      <div v-if="recentTransactions.length > 0" class="view-more">
        <el-button link type="primary" @click="goToTransaction">查看全部</el-button>
      </div>
    </div>

    <!-- 创建家庭对话框 -->
    <el-dialog v-model="showCreateDialog" title="创建家庭" width="90%" :close-on-click-modal="false">
      <el-form :model="createForm" label-position="top">
        <el-form-item label="家庭名称">
          <el-input v-model="createForm.familyName" placeholder="例如：我们的家" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" :loading="loading" @click="handleCreateFamily">创建</el-button>
      </template>
    </el-dialog>

    <!-- 加入家庭对话框 -->
    <el-dialog v-model="showJoinDialog" title="加入家庭" width="90%" :close-on-click-modal="false">
      <el-form :model="joinForm" label-position="top">
        <el-form-item label="邀请码">
          <el-input v-model="joinForm.inviteCode" placeholder="请输入6位邀请码" maxlength="6" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showJoinDialog = false">取消</el-button>
        <el-button type="primary" :loading="loading" @click="handleJoinFamily">加入</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { useUserStore } from '../stores/user'
import { createFamily, joinFamily, getFamilyInfo } from '../api/family'
import { getTransactions } from '../api/transaction'
import { getMonthlyStatistics } from '../api/statistics'
import dayjs from 'dayjs'

const router = useRouter()
const userStore = useUserStore()

// 数据
const monthExpense = ref(0)
const monthIncome = ref(0)
const budgetRemaining = ref(0)
const recentTransactions = ref([])
const familyMembers = ref([])
const loading = ref(false)
const showCreateDialog = ref(false)
const showJoinDialog = ref(false)

const createForm = ref({ familyName: '我们的家' })
const joinForm = ref({ inviteCode: '' })

// 获取当前月份
const currentMonth = computed(() => dayjs().format('YYYY-MM'))

// 格式化金额
const formatAmount = (amount) => {
  return Number(amount).toFixed(2)
}

// 格式化日期
const formatDate = (date) => {
  return dayjs(date).format('MM-DD')
}

// 获取付款人名称
const getPayerName = (payerId) => {
  const member = familyMembers.value.find(m => m.userId === payerId)
  return member?.nickname || '未知'
}

// 加载数据
const loadData = async () => {
  if (!userStore.hasFamily) return
  
  try {
    // 加载统计数据
    const statsRes = await getMonthlyStatistics(currentMonth.value)
    if (statsRes.code === 200) {
      monthExpense.value = statsRes.data.totalExpense
      monthIncome.value = statsRes.data.totalIncome
      
      // 计算预算剩余
      const totalBudget = statsRes.data.categoryStats.reduce((sum, item) => sum + Number(item.budget), 0)
      budgetRemaining.value = totalBudget - Number(statsRes.data.totalExpense)
    }
    
    // 加载最近消费
    const transRes = await getTransactions()
    if (transRes.code === 200) {
      recentTransactions.value = transRes.data.slice(0, 5)
    }
    
    // 加载家庭成员
    const familyRes = await getFamilyInfo()
    if (familyRes.code === 200) {
      familyMembers.value = familyRes.data.members
    }
  } catch (error) {
    console.error('加载数据失败:', error)
  }
}

// 创建家庭
const handleCreateFamily = async () => {
  if (!createForm.value.familyName.trim()) {
    ElMessage.warning('请输入家庭名称')
    return
  }
  
  loading.value = true
  try {
    const res = await createFamily({ familyName: createForm.value.familyName })
    if (res.code === 200) {
      ElMessage.success('家庭创建成功')
      userStore.setFamilyId(res.data.familyId)
      showCreateDialog.value = false
      loadData()
    } else {
      ElMessage.error(res.message)
    }
  } finally {
    loading.value = false
  }
}

// 加入家庭
const handleJoinFamily = async () => {
  if (!joinForm.value.inviteCode.trim()) {
    ElMessage.warning('请输入邀请码')
    return
  }
  
  loading.value = true
  try {
    const res = await joinFamily({ inviteCode: joinForm.value.inviteCode.trim().toUpperCase() })
    if (res.code === 200) {
      ElMessage.success('加入家庭成功')
      userStore.setFamilyId(res.data.familyId)
      showJoinDialog.value = false
      loadData()
    } else {
      ElMessage.error(res.message)
    }
  } finally {
    loading.value = false
  }
}

// 跳转到记账页
const goToTransaction = () => {
  router.push('/transaction')
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.dashboard {
  padding-bottom: 20px;
}

.family-tip {
  margin-bottom: 20px;
}

.family-actions {
  margin-top: 16px;
  display: flex;
  gap: 12px;
  justify-content: center;
}

.overview-cards {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
  margin-bottom: 20px;
}

.card {
  background: white;
  border-radius: 12px;
  padding: 16px 12px;
  text-align: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.card-label {
  font-size: 12px;
  color: #999;
  margin-bottom: 8px;
}

.card-value {
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.card-value.expense {
  color: #f56c6c;
}

.card-value.income {
  color: #67c23a;
}

.card-value.warning {
  color: #e6a23c;
}

.quick-add {
  margin-bottom: 20px;
}

.add-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
}

.recent-section {
  background: white;
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 16px;
  color: #333;
}

.empty-tip {
  text-align: center;
  color: #999;
  padding: 40px 0;
}

.transaction-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.transaction-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.transaction-item:last-child {
  border-bottom: none;
}

.item-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.category-tag {
  padding: 4px 10px;
  border-radius: 4px;
  font-size: 12px;
  background: #f0f0f0;
  color: #666;
}

.category-tag.expense {
  background: #fef0f0;
  color: #f56c6c;
}

.category-tag.income {
  background: #f0f9eb;
  color: #67c23a;
}

.item-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.item-note {
  font-size: 14px;
  color: #333;
}

.item-date {
  font-size: 12px;
  color: #999;
}

.item-right {
  text-align: right;
}

.item-amount {
  font-size: 16px;
  font-weight: 600;
}

.item-amount.expense {
  color: #f56c6c;
}

.item-amount.income {
  color: #67c23a;
}

.item-payer {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}

.view-more {
  text-align: center;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
}
</style>
