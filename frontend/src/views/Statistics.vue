<template>
  <div class="statistics-page">
    <!-- 月份选择 -->
    <div class="month-selector">
      <el-button link @click="changeMonth(-1)">
        <el-icon><ArrowLeft /></el-icon>
      </el-button>
      <span class="month-text">{{ currentMonth }}月</span>
      <el-button link @click="changeMonth(1)">
        <el-icon><ArrowRight /></el-icon>
      </el-button>
    </div>

    <!-- 总结卡片 -->
    <div v-if="statistics" class="summary-card">
      <div class="summary-title">月度总结</div>
      <div class="summary-content">{{ statistics.summary }}</div>
    </div>

    <!-- 收支概览 -->
    <div v-if="statistics" class="overview-cards">
      <div class="overview-item income">
        <div class="item-icon">
          <el-icon><Top /></el-icon>
        </div>
        <div class="item-info">
          <div class="item-label">总收入</div>
          <div class="item-value">¥{{ formatAmount(statistics.totalIncome) }}</div>
        </div>
      </div>
      <div class="overview-item expense">
        <div class="item-icon">
          <el-icon><Bottom /></el-icon>
        </div>
        <div class="item-info">
          <div class="item-label">总支出</div>
          <div class="item-value">¥{{ formatAmount(statistics.totalExpense) }}</div>
        </div>
      </div>
      <div class="overview-item balance">
        <div class="item-icon">
          <el-icon><Wallet /></el-icon>
        </div>
        <div class="item-info">
          <div class="item-label">结余</div>
          <div class="item-value">¥{{ formatAmount(statistics.balance) }}</div>
        </div>
      </div>
    </div>

    <!-- 分类统计 -->
    <div v-if="statistics && statistics.categoryStats.length > 0" class="category-card">
      <h3 class="card-title">分类统计</h3>
      <div class="category-list">
        <div
          v-for="item in statistics.categoryStats"
          :key="item.category"
          class="category-item"
          :class="{ over: item.overBudget }"
        >
          <div class="item-header">
            <span class="category-name">{{ item.category }}</span>
            <span class="category-amount">¥{{ formatAmount(item.actual) }}</span>
          </div>
          <div class="budget-info">
            <span class="budget-text">
              预算: ¥{{ formatAmount(item.budget) }}
              <template v-if="item.budget > 0">
                | 使用率: {{ item.usageRate.toFixed(1) }}%
              </template>
            </span>
            <span v-if="item.overBudget" class="over-badge">超支</span>
          </div>
          <div class="progress-bar">
            <div
              class="progress-fill"
              :class="{ over: item.overBudget }"
              :style="{ width: Math.min(item.usageRate, 100) + '%' }"
            />
          </div>
        </div>
      </div>
    </div>

    <!-- 无数据提示 -->
    <div v-else-if="!loading" class="empty-tip">
      暂无统计数据
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { ArrowLeft, ArrowRight, Top, Bottom, Wallet } from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import { getMonthlyStatistics } from '../api/statistics'

// 当前月份
const currentMonthObj = ref(dayjs())
const currentMonth = computed(() => currentMonthObj.value.format('YYYY-MM'))

// 数据
const statistics = ref(null)
const loading = ref(false)

// 格式化金额
const formatAmount = (amount) => {
  return Number(amount).toFixed(2)
}

// 切换月份
const changeMonth = (delta) => {
  currentMonthObj.value = currentMonthObj.value.add(delta, 'month')
}

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const res = await getMonthlyStatistics(currentMonth.value)
    if (res.code === 200) {
      statistics.value = res.data
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
  } finally {
    loading.value = false
  }
}

// 监听月份变化
watch(currentMonth, () => {
  loadData()
})

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.statistics-page {
  padding-bottom: 20px;
}

.month-selector {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 20px;
  margin-bottom: 16px;
  padding: 12px;
  background: white;
  border-radius: 12px;
}

.month-text {
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.summary-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 16px;
  color: white;
}

.summary-title {
  font-size: 14px;
  opacity: 0.9;
  margin-bottom: 8px;
}

.summary-content {
  font-size: 15px;
  line-height: 1.6;
}

.overview-cards {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
  margin-bottom: 16px;
}

.overview-item {
  background: white;
  border-radius: 12px;
  padding: 16px 12px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.item-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
}

.overview-item.income .item-icon {
  background: #f0f9eb;
  color: #67c23a;
}

.overview-item.expense .item-icon {
  background: #fef0f0;
  color: #f56c6c;
}

.overview-item.balance .item-icon {
  background: #ecf5ff;
  color: #409eff;
}

.item-info {
  text-align: center;
}

.item-label {
  font-size: 12px;
  color: #999;
  margin-bottom: 4px;
}

.item-value {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.category-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 20px;
  color: #333;
}

.category-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.category-item {
  padding: 16px;
  background: #f8f9fa;
  border-radius: 12px;
  border-left: 4px solid #4CAF50;
}

.category-item.over {
  border-left-color: #f56c6c;
}

.item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.category-name {
  font-size: 15px;
  font-weight: 500;
  color: #333;
}

.category-amount {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.budget-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.budget-text {
  font-size: 12px;
  color: #999;
}

.over-badge {
  background: #f56c6c;
  color: white;
  font-size: 11px;
  padding: 2px 8px;
  border-radius: 10px;
}

.progress-bar {
  height: 8px;
  background: #e0e0e0;
  border-radius: 4px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: #4CAF50;
  border-radius: 4px;
  transition: width 0.3s;
}

.progress-fill.over {
  background: #f56c6c;
}

.empty-tip {
  text-align: center;
  color: #999;
  padding: 60px 0;
  background: white;
  border-radius: 12px;
}
</style>
