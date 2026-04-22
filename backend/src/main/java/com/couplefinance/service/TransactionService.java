package com.couplefinance.service;

import com.couplefinance.dto.ApiResponse;
import com.couplefinance.dto.TransactionRequest;
import com.couplefinance.entity.TransactionRecord;

import java.util.List;

/**
 * 记账服务接口
 */
public interface TransactionService {
    
    /**
     * 添加记账记录
     */
    ApiResponse<TransactionRecord> addTransaction(Long userId, TransactionRequest request);
    
    /**
     * 获取家庭记账列表
     */
    ApiResponse<List<TransactionRecord>> getFamilyTransactions(Long userId);
    
    /**
     * 删除记账记录
     */
    ApiResponse<Void> deleteTransaction(Long userId, Long transactionId);
}
