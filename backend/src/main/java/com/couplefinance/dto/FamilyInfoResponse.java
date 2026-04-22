package com.couplefinance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 家庭信息响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FamilyInfoResponse {
    
    private Long familyId;
    private String familyName;
    private String inviteCode;
    private Long createdBy;
    private List<MemberInfo> members;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberInfo {
        private Long userId;
        private String username;
        private String nickname;
        private Boolean isCreator;
    }
}
