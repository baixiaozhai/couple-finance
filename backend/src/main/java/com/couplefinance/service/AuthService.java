package com.couplefinance.service;

import com.couplefinance.dto.*;

/**
 * 认证服务接口
 */
public interface AuthService {
    
    /**
     * 用户注册
     */
    ApiResponse<AuthResponse> register(RegisterRequest request);
    
    /**
     * 用户登录
     */
    ApiResponse<AuthResponse> login(LoginRequest request);
    
    /**
     * 获取当前用户信息
     */
    ApiResponse<AuthResponse> getCurrentUser(Long userId);
}
