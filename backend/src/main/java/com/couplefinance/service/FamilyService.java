package com.couplefinance.service;

import com.couplefinance.dto.ApiResponse;
import com.couplefinance.dto.FamilyInfoResponse;

/**
 * 家庭服务接口
 */
public interface FamilyService {
    
    /**
     * 创建家庭
     */
    ApiResponse<FamilyInfoResponse> createFamily(Long userId, String familyName);
    
    /**
     * 通过邀请码加入家庭
     */
    ApiResponse<FamilyInfoResponse> joinFamily(Long userId, String inviteCode);
    
    /**
     * 获取家庭信息
     */
    ApiResponse<FamilyInfoResponse> getFamilyInfo(Long userId);
    
    /**
     * 退出家庭
     */
    ApiResponse<Void> leaveFamily(Long userId);
}
