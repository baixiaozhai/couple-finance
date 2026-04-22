package com.couplefinance.service.impl;

import com.couplefinance.dto.ApiResponse;
import com.couplefinance.dto.TransactionRequest;
import com.couplefinance.entity.FamilyMember;
import com.couplefinance.entity.TransactionRecord;
import com.couplefinance.repository.FamilyMemberRepository;
import com.couplefinance.repository.TransactionRepository;
import com.couplefinance.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 记账服务实现类
 */
@Service
public class TransactionServiceImpl implements TransactionService {
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private FamilyMemberRepository familyMemberRepository;
    
    @Override
    @Transactional
    public ApiResponse<TransactionRecord> addTransaction(Long userId, TransactionRequest request) {
        // 获取用户的家庭ID
        Optional<FamilyMember> memberOpt = familyMemberRepository.findByUserId(userId);
        if (memberOpt.isEmpty()) {
            return ApiResponse.error("您尚未加入任何家庭，无法记账");
        }
        
        Long familyId = memberOpt.get().getFamilyId();
        
        // 检查付款人是否属于该家庭
        if (!familyMemberRepository.existsByUserIdAndFamilyId(request.getPayerId(), familyId)) {
            return ApiResponse.error("付款人必须是家庭成员");
        }
        
        // 创建记账记录
        TransactionRecord record = TransactionRecord.builder()
                .familyId(familyId)
                .userId(userId)
                .amount(request.getAmount())
                .category(request.getCategory())
                .type(request.getType())
                .payerId(request.getPayerId())
                .recordDate(request.getRecordDate())
                .note(request.getNote())
                .build();
        
        transactionRepository.save(record);
        
        return ApiResponse.success("记账成功", record);
    }
    
    @Override
    public ApiResponse<List<TransactionRecord>> getFamilyTransactions(Long userId) {
        // 获取用户的家庭ID
        Optional<FamilyMember> memberOpt = familyMemberRepository.findByUserId(userId);
        if (memberOpt.isEmpty()) {
            return ApiResponse.error("您尚未加入任何家庭");
        }
        
        Long familyId = memberOpt.get().getFamilyId();
        List<TransactionRecord> records = transactionRepository.findByFamilyIdOrderByRecordDateDesc(familyId);
        
        return ApiResponse.success(records);
    }
    
    @Override
    @Transactional
    public ApiResponse<Void> deleteTransaction(Long userId, Long transactionId) {
        // 获取用户的家庭ID
        Optional<FamilyMember> memberOpt = familyMemberRepository.findByUserId(userId);
        if (memberOpt.isEmpty()) {
            return ApiResponse.error("您尚未加入任何家庭");
        }
        
        Long familyId = memberOpt.get().getFamilyId();
        
        // 查找记录
        Optional<TransactionRecord> recordOpt = transactionRepository.findById(transactionId);
        if (recordOpt.isEmpty()) {
            return ApiResponse.error("记录不存在");
        }
        
        TransactionRecord record = recordOpt.get();
        
        // 验证记录是否属于该家庭
        if (!record.getFamilyId().equals(familyId)) {
            return ApiResponse.error("无权删除此记录");
        }
        
        transactionRepository.delete(record);
        
        return ApiResponse.success("删除成功", null);
    }
}
