package com.couplefinance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 认证响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    
    private String token;
    private String type;  // Bearer
    private Long userId;
    private String username;
    private String nickname;
    private Long familyId;  // 如果已加入家庭
    private String message;
}
