package com.couplefinance.service.impl;

import com.couplefinance.dto.*;
import com.couplefinance.entity.FamilyMember;
import com.couplefinance.entity.User;
import com.couplefinance.repository.FamilyMemberRepository;
import com.couplefinance.repository.UserRepository;
import com.couplefinance.security.JwtUtil;
import com.couplefinance.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * 认证服务实现类
 */
@Service
public class AuthServiceImpl implements AuthService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private FamilyMemberRepository familyMemberRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Override
    @Transactional
    public ApiResponse<AuthResponse> register(RegisterRequest request) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(request.getUsername())) {
            return ApiResponse.error("用户名已存在");
        }
        
        // 创建用户
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname() != null ? request.getNickname() : request.getUsername())
                .build();
        
        userRepository.save(user);
        
        // 生成JWT Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        
        return ApiResponse.success("注册成功", AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .userId(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .build());
    }
    
    @Override
    public ApiResponse<AuthResponse> login(LoginRequest request) {
        // 查找用户
        Optional<User> userOpt = userRepository.findByUsername(request.getUsername());
        if (userOpt.isEmpty()) {
            return ApiResponse.error("用户名或密码错误");
        }
        
        User user = userOpt.get();
        
        // 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ApiResponse.error("用户名或密码错误");
        }
        
        // 生成JWT Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        
        // 查询用户是否已加入家庭
        Long familyId = null;
        Optional<FamilyMember> memberOpt = familyMemberRepository.findByUserId(user.getId());
        if (memberOpt.isPresent()) {
            familyId = memberOpt.get().getFamilyId();
        }
        
        return ApiResponse.success("登录成功", AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .userId(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .familyId(familyId)
                .build());
    }
    
    @Override
    public ApiResponse<AuthResponse> getCurrentUser(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ApiResponse.error("用户不存在");
        }
        
        User user = userOpt.get();
        
        // 查询用户是否已加入家庭
        Long familyId = null;
        Optional<FamilyMember> memberOpt = familyMemberRepository.findByUserId(user.getId());
        if (memberOpt.isPresent()) {
            familyId = memberOpt.get().getFamilyId();
        }
        
        return ApiResponse.success(AuthResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .familyId(familyId)
                .build());
    }
}
