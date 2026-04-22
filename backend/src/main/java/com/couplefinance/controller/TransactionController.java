package com.couplefinance.controller;

import com.couplefinance.dto.ApiResponse;
import com.couplefinance.dto.TransactionRequest;
import com.couplefinance.entity.TransactionRecord;
import com.couplefinance.security.UserPrincipal;
import com.couplefinance.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 记账控制器
 */
@RestController
@RequestMapping("/api/transaction")
@CrossOrigin(origins = "*")
public class TransactionController {
    
    @Autowired
    private TransactionService transactionService;
    
    /**
     * 添加记账记录
     */
    @PostMapping("/add")
    public ResponseEntity<ApiResponse<TransactionRecord>> addTransaction(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody TransactionRequest request) {
        return ResponseEntity.ok(transactionService.addTransaction(userPrincipal.getUserId(), request));
    }
    
    /**
     * 获取家庭记账列表
     */
    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<TransactionRecord>>> getTransactions(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(transactionService.getFamilyTransactions(userPrincipal.getUserId()));
    }
    
    /**
     * 删除记账记录
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTransaction(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long id) {
        return ResponseEntity.ok(transactionService.deleteTransaction(userPrincipal.getUserId(), id));
    }
}
