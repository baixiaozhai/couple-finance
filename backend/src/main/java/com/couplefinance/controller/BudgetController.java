package com.couplefinance.controller;

import com.couplefinance.dto.ApiResponse;
import com.couplefinance.dto.BudgetRequest;
import com.couplefinance.entity.Budget;
import com.couplefinance.security.UserPrincipal;
import com.couplefinance.service.BudgetService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 预算控制器
 */
@RestController
@RequestMapping("/api/budget")
@CrossOrigin(origins = "*")
public class BudgetController {
    
    @Autowired
    private BudgetService budgetService;
    
    /**
     * 设置预算
     */
    @PostMapping("/set")
    public ResponseEntity<ApiResponse<Budget>> setBudget(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody BudgetRequest request) {
        return ResponseEntity.ok(budgetService.setBudget(userPrincipal.getUserId(), request));
    }
    
    /**
     * 获取家庭某月预算列表
     */
    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<Budget>>> getBudgets(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam String month) {
        return ResponseEntity.ok(budgetService.getFamilyBudgets(userPrincipal.getUserId(), month));
    }
    
    /**
     * 删除预算
     */
    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse<Void>> deleteBudget(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam String month,
            @RequestParam String category) {
        return ResponseEntity.ok(budgetService.deleteBudget(userPrincipal.getUserId(), month, category));
    }
}
