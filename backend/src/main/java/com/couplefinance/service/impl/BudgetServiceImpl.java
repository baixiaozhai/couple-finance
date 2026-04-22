package com.couplefinance.service.impl;

import com.couplefinance.dto.ApiResponse;
import com.couplefinance.dto.BudgetRequest;
import com.couplefinance.entity.Budget;
import com.couplefinance.entity.FamilyMember;
import com.couplefinance.repository.BudgetRepository;
import com.couplefinance.repository.FamilyMemberRepository;
import com.couplefinance.service.BudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 预算服务实现类
 */
@Service
public class BudgetServiceImpl implements BudgetService {
    
    @Autowired
    private BudgetRepository budgetRepository;
    
    @Autowired
    private FamilyMemberRepository familyMemberRepository;
    
    @Override
    @Transactional
    public ApiResponse<Budget> setBudget(Long userId, BudgetRequest request) {
        // 获取用户的家庭ID
        Optional<FamilyMember> memberOpt = familyMemberRepository.findByUserId(userId);
        if (memberOpt.isEmpty()) {
            return ApiResponse.error("您尚未加入任何家庭");
        }
        
        Long familyId = memberOpt.get().getFamilyId();
        
        // 检查是否已存在该分类的预算
        Optional<Budget> existingBudget = budgetRepository.findByFamilyIdAndMonthAndCategory(
                familyId, request.getMonth(), request.getCategory());
        
        Budget budget;
        if (existingBudget.isPresent()) {
            // 更新现有预算
            budget = existingBudget.get();
            budget.setAmount(request.getAmount());
        } else {
            // 创建新预算
            budget = Budget.builder()
                    .familyId(familyId)
                    .month(request.getMonth())
                    .category(request.getCategory())
                    .amount(request.getAmount())
                    .build();
        }
        
        budgetRepository.save(budget);
        
        return ApiResponse.success("预算设置成功", budget);
    }
    
    @Override
    public ApiResponse<List<Budget>> getFamilyBudgets(Long userId, String month) {
        // 获取用户的家庭ID
        Optional<FamilyMember> memberOpt = familyMemberRepository.findByUserId(userId);
        if (memberOpt.isEmpty()) {
            return ApiResponse.error("您尚未加入任何家庭");
        }
        
        Long familyId = memberOpt.get().getFamilyId();
        List<Budget> budgets = budgetRepository.findByFamilyIdAndMonth(familyId, month);
        
        return ApiResponse.success(budgets);
    }
    
    @Override
    @Transactional
    public ApiResponse<Void> deleteBudget(Long userId, String month, String category) {
        // 获取用户的家庭ID
        Optional<FamilyMember> memberOpt = familyMemberRepository.findByUserId(userId);
        if (memberOpt.isEmpty()) {
            return ApiResponse.error("您尚未加入任何家庭");
        }
        
        Long familyId = memberOpt.get().getFamilyId();
        
        budgetRepository.deleteByFamilyIdAndMonthAndCategory(familyId, month, category);
        
        return ApiResponse.success("预算删除成功", null);
    }
}
