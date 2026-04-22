package com.couplefinance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 月度统计响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsResponse {
    
    private String month;  // 月份：2026-04
    private BigDecimal totalIncome;  // 总收入
    private BigDecimal totalExpense;  // 总支出
    private BigDecimal balance;  // 结余
    private List<CategoryStat> categoryStats;  // 分类统计
    private String summary;  // 自动总结
    
    /**
     * 分类统计项
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryStat {
        private String category;  // 分类名称
        private BigDecimal budget;  // 预算
        private BigDecimal actual;  // 实际支出
        private BigDecimal remaining;  // 剩余
        private Double usageRate;  // 使用率（百分比）
        private Boolean overBudget;  // 是否超支
    }
}
