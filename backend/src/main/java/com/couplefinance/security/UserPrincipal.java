package com.couplefinance.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户主体信息（用于Spring Security）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPrincipal {
    
    private Long userId;
    private String username;
}
