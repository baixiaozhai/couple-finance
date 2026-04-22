package com.couplefinance.service;

import com.couplefinance.dto.ApiResponse;
import com.couplefinance.dto.BudgetRequest;
import com.couplefinance.entity.Budget;

import java.util.List;

/**
 * 预算服务接口
 */
public interface BudgetService {
    
    /**
     * 设置预算
     */
    ApiResponse<Budget> setBudget(Long userId, BudgetRequest request);
    
    /**
     * 获取家庭某月预算列表
     */
    ApiResponse<List<Budget>> getFamilyBudgets(Long userId, String month);
    
    /**
     * 删除预算
     */
    ApiResponse<Void> deleteBudget(Long userId, String month, String category);
}
