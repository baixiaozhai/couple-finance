package com.couplefinance.service.impl;

import com.couplefinance.dto.ApiResponse;
import com.couplefinance.dto.StatisticsResponse;
import com.couplefinance.entity.Budget;
import com.couplefinance.entity.FamilyMember;
import com.couplefinance.repository.BudgetRepository;
import com.couplefinance.repository.FamilyMemberRepository;
import com.couplefinance.repository.TransactionRepository;
import com.couplefinance.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 统计服务实现类
 */
@Service
public class StatisticsServiceImpl implements StatisticsService {
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private BudgetRepository budgetRepository;
    
    @Autowired
    private FamilyMemberRepository familyMemberRepository;
    
    @Override
    public ApiResponse<StatisticsResponse> getMonthlyStatistics(Long userId, String month) {
        // 获取用户的家庭ID
        Optional<FamilyMember> memberOpt = familyMemberRepository.findByUserId(userId);
        if (memberOpt.isEmpty()) {
            return ApiResponse.error("您尚未加入任何家庭");
        }
        
        Long familyId = memberOpt.get().getFamilyId();
        
        // 解析月份，获取起始和结束日期
        YearMonth yearMonth = YearMonth.parse(month, DateTimeFormatter.ofPattern("yyyy-MM"));
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        
        // 1. 获取总收入和总支出
        BigDecimal totalIncome = transactionRepository.sumIncomeByMonth(familyId, startDate, endDate);
        BigDecimal totalExpense = transactionRepository.sumExpenseByMonth(familyId, startDate, endDate);
        
        // 2. 获取分类支出统计
        List<Object[]> categoryExpenses = transactionRepository.sumExpenseByCategoryAndMonth(
                familyId, startDate, endDate);
        Map<String, BigDecimal> expenseMap = new HashMap<>();
        for (Object[] row : categoryExpenses) {
            String category = (String) row[0];
            BigDecimal amount = (BigDecimal) row[1];
            expenseMap.put(category, amount);
        }
        
        // 3. 获取预算列表
        List<Budget> budgets = budgetRepository.findByFamilyIdAndMonth(familyId, month);
        
        // 4. 构建分类统计
        List<StatisticsResponse.CategoryStat> categoryStats = new ArrayList<>();
        BigDecimal totalBudget = BigDecimal.ZERO;
        
        for (Budget budget : budgets) {
            BigDecimal actual = expenseMap.getOrDefault(budget.getCategory(), BigDecimal.ZERO);
            BigDecimal remaining = budget.getAmount().subtract(actual);
            
            // 计算使用率
            double usageRate = budget.getAmount().compareTo(BigDecimal.ZERO) > 0
                    ? actual.multiply(BigDecimal.valueOf(100))
                            .divide(budget.getAmount(), 2, RoundingMode.HALF_UP)
                            .doubleValue()
                    : 0.0;
            
            categoryStats.add(StatisticsResponse.CategoryStat.builder()
                    .category(budget.getCategory())
                    .budget(budget.getAmount())
                    .actual(actual)
                    .remaining(remaining)
                    .usageRate(usageRate)
                    .overBudget(remaining.compareTo(BigDecimal.ZERO) < 0)
                    .build());
            
            totalBudget = totalBudget.add(budget.getAmount());
        }
        
        // 添加有支出但没有预算的分类
        for (Map.Entry<String, BigDecimal> entry : expenseMap.entrySet()) {
            boolean hasBudget = budgets.stream()
                    .anyMatch(b -> b.getCategory().equals(entry.getKey()));
            if (!hasBudget) {
                categoryStats.add(StatisticsResponse.CategoryStat.builder()
                        .category(entry.getKey())
                        .budget(BigDecimal.ZERO)
                        .actual(entry.getValue())
                        .remaining(BigDecimal.ZERO.subtract(entry.getValue()))
                        .usageRate(100.0)
                        .overBudget(true)
                        .build());
            }
        }
        
        // 按使用率排序
        categoryStats.sort((a, b) -> Double.compare(b.getUsageRate(), a.getUsageRate()));
        
        // 5. 生成总结
        String summary = generateSummary(totalBudget, totalExpense, categoryStats);
        
        StatisticsResponse response = StatisticsResponse.builder()
                .month(month)
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .balance(totalIncome.subtract(totalExpense))
                .categoryStats(categoryStats)
                .summary(summary)
                .build();
        
        return ApiResponse.success(response);
    }
    
    /**
     * 生成月度总结
     */
    private String generateSummary(BigDecimal totalBudget, BigDecimal totalExpense, 
                                   List<StatisticsResponse.CategoryStat> categoryStats) {
        StringBuilder summary = new StringBuilder();
        
        // 总体情况
        if (totalBudget.compareTo(BigDecimal.ZERO) > 0) {
            double totalUsageRate = totalBudget.compareTo(BigDecimal.ZERO) > 0
                    ? totalExpense.multiply(BigDecimal.valueOf(100))
                            .divide(totalBudget, 2, RoundingMode.HALF_UP)
                            .doubleValue()
                    : 0.0;
            
            if (totalExpense.compareTo(totalBudget) > 0) {
                summary.append(String.format("本月总支出超支%.1f%%，", totalUsageRate - 100));
            } else {
                summary.append(String.format("本月总支出占预算%.1f%%，", totalUsageRate));
            }
        }
        
        // 超支分类
        List<StatisticsResponse.CategoryStat> overBudgetCategories = categoryStats.stream()
                .filter(StatisticsResponse.CategoryStat::getOverBudget)
                .limit(2)
                .collect(Collectors.toList());
        
        if (!overBudgetCategories.isEmpty()) {
            summary.append("超支项目：");
            for (int i = 0; i < overBudgetCategories.size(); i++) {
                StatisticsResponse.CategoryStat stat = overBudgetCategories.get(i);
                double overRate = (stat.getUsageRate() - 100);
                summary.append(String.format("%s超支%.0f%%", stat.getCategory(), overRate));
                if (i < overBudgetCategories.size() - 1) {
                    summary.append("、");
                }
            }
            summary.append("。");
        } else if (totalExpense.compareTo(BigDecimal.ZERO) > 0) {
            summary.append("所有项目均在预算范围内，继续保持！");
        } else {
            summary.append("本月暂无支出记录。");
        }
        
        return summary.toString();
    }
}
