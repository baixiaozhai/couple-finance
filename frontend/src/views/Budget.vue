<template>
  <div class="budget-page">
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

    <!-- 预算概览 -->
    <div class="budget-overview">
      <div class="overview-item">
        <div class="overview-label">总预算</div>
        <div class="overview-value">¥{{ formatAmount(totalBudget) }}</div>
      </div>
      <div class="overview-item">
        <div class="overview-label">已支出</div>
        <div class="overview-value expense">¥{{ formatAmount(totalExpense) }}</div>
      </div>
      <div class="overview-item">
        <div class="overview-label">剩余</div>
        <div class="overview-value" :class="{ warning: remaining < 0 }">
          ¥{{ formatAmount(remaining) }}
        </div>
      </div>
    </div>

    <!-- 预算列表 -->
    <div class="budget-list-card">
      <div class="list-header">
        <h3 class="card-title">分类预算</h3>
        <el-button type="primary" size="small" @click="showAddDialog = true">
          <el-icon><Plus /></el-icon>
          添加
        </el-button>
      </div>

      <div v-if="budgetList.length === 0" class="empty-tip">
        暂无预算设置，点击右上角添加
      </div>
      <div v-else class="budget-list">
        <div
          v-for="item in budgetList"
          :key="item.category"
          class="budget-item"
        >
          <div class="item-header">
            <div class="item-category">{{ item.category }}</div>
            <div class="item-amount">
              <span class="actual" :class="{ over: item.actual > item.budget }">
                ¥{{ formatAmount(item.actual) }}
              </span>
              <span class="divider">/</span>
              <span class="budget">¥{{ formatAmount(item.budget) }}</span>
            </div>
          </div>
          <div class="progress-bar">
            <div
              class="progress-fill"
              :class="{ over: item.usageRate > 100 }"
              :style="{ width: Math.min(item.usageRate, 100) + '%' }"
            />
          </div>
          <div class="item-footer">
            <span class="usage-text" :class="{ over: item.usageRate > 100 }">
              {{ item.usageRate.toFixed(1) }}%
            </span>
            <el-button
              type="danger"
              link
              size="small"
              @click="handleDelete(item)"
            >
              删除
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 添加预算对话框 -->
    <el-dialog v-model="showAddDialog" title="设置预算" width="90%">
      <el-form :model="addForm" label-position="top">
        <el-form-item label="分类">
          <el-select v-model="addForm.category" placeholder="选择分类" style="width: 100%">
            <el-option
              v-for="cat in availableCategories"
              :key="cat"
              :label="cat"
              :value="cat"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="预算金额">
          <el-input-number
            v-model="addForm.amount"
            :precision="2"
            :min="1"
            :controls="false"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleAdd">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft, ArrowRight, Plus } from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import { getBudgets, setBudget, deleteBudget } from '../api/budget'
import { getMonthlyStatistics } from '../api/statistics'

// 当前月份
const currentMonthObj = ref(dayjs())
const currentMonth = computed(() => currentMonthObj.value.format('YYYY-MM'))

// 数据
const budgetList = ref([])
const totalBudget = ref(0)
const totalExpense = ref(0)
const showAddDialog = ref(false)
const submitting = ref(false)

const addForm = ref({
  category: '',
  amount: 1000
})

// 所有支出分类
const allCategories = ['餐饮', '交通', '购物', '娱乐', '住房', '医疗', '教育', '通讯', '人情', '其他']

// 可用的分类（排除已设置的）
const availableCategories = computed(() => {
  const usedCategories = budgetList.value.map(item => item.category)
  return allCategories.filter(cat => !usedCategories.includes(cat))
})

// 计算剩余预算
const remaining = computed(() => totalBudget.value - totalExpense.value)

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
  try {
    // 加载预算列表
    const budgetRes = await getBudgets(currentMonth.value)
    if (budgetRes.code === 200) {
      const budgets = budgetRes.data
      
      // 加载统计数据获取实际支出
      const statsRes = await getMonthlyStatistics(currentMonth.value)
      if (statsRes.code === 200) {
        const categoryStats = statsRes.data.categoryStats
        
        // 合并预算和实际支出数据
        budgetList.value = budgets.map(budget => {
          const stat = categoryStats.find(s => s.category === budget.category)
          const actual = stat ? Number(stat.actual) : 0
          const budgetAmount = Number(budget.amount)
          return {
            category: budget.category,
            budget: budgetAmount,
            actual: actual,
            usageRate: budgetAmount > 0 ? (actual / budgetAmount * 100) : 0
          }
        })
        
        // 计算总计
        totalBudget.value = budgetList.value.reduce((sum, item) => sum + item.budget, 0)
        totalExpense.value = budgetList.value.reduce((sum, item) => sum + item.actual, 0)
      }
    }
  } catch (error) {
    console.error('加载数据失败:', error)
  }
}

// 添加预算
const handleAdd = async () => {
  if (!addForm.value.category) {
    ElMessage.warning('请选择分类')
    return
  }
  if (!addForm.value.amount || addForm.value.amount <= 0) {
    ElMessage.warning('请输入有效的预算金额')
    return
  }

  submitting.value = true
  try {
    const res = await setBudget({
      month: currentMonth.value,
      category: addForm.value.category,
      amount: addForm.value.amount
    })
    if (res.code === 200) {
      ElMessage.success('预算设置成功')
      showAddDialog.value = false
      addForm.value = { category: '', amount: 1000 }
      loadData()
    } else {
      ElMessage.error(res.message)
    }
  } finally {
    submitting.value = false
  }
}

// 删除预算
const handleDelete = async (item) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除「${item.category}」的预算设置吗？`,
      '提示',
      { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }
    )
    
    const res = await deleteBudget(currentMonth.value, item.category)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      loadData()
    } else {
      ElMessage.error(res.message)
    }
  } catch (error) {
    // 用户取消
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
.budget-page {
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

.budget-overview {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
  margin-bottom: 16px;
}

.overview-item {
  background: white;
  border-radius: 12px;
  padding: 16px 12px;
  text-align: center;
}

.overview-label {
  font-size: 12px;
  color: #999;
  margin-bottom: 8px;
}

.overview-value {
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.overview-value.expense {
  color: #f56c6c;
}

.overview-value.warning {
  color: #e6a23c;
}

.budget-list-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.empty-tip {
  text-align: center;
  color: #999;
  padding: 40px 0;
}

.budget-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.budget-item {
  padding: 16px;
  background: #f8f9fa;
  border-radius: 12px;
}

.item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.item-category {
  font-size: 15px;
  font-weight: 500;
  color: #333;
}

.item-amount {
  font-size: 14px;
}

.item-amount .actual {
  color: #67c23a;
  font-weight: 500;
}

.item-amount .actual.over {
  color: #f56c6c;
}

.item-amount .divider {
  color: #ccc;
  margin: 0 4px;
}

.item-amount .budget {
  color: #999;
}

.progress-bar {
  height: 8px;
  background: #e0e0e0;
  border-radius: 4px;
  overflow: hidden;
  margin-bottom: 8px;
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

.item-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.usage-text {
  font-size: 13px;
  color: #4CAF50;
}

.usage-text.over {
  color: #f56c6c;
}
</style>
