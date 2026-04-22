package com.couplefinance.controller;

import com.couplefinance.dto.ApiResponse;
import com.couplefinance.dto.FamilyInfoResponse;
import com.couplefinance.security.UserPrincipal;
import com.couplefinance.service.FamilyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 家庭控制器
 */
@RestController
@RequestMapping("/api/family")
@CrossOrigin(origins = "*")
public class FamilyController {
    
    @Autowired
    private FamilyService familyService;
    
    /**
     * 创建家庭
     */
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<FamilyInfoResponse>> createFamily(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody Map<String, String> request) {
        String familyName = request.getOrDefault("familyName", "我们的家");
        return ResponseEntity.ok(familyService.createFamily(userPrincipal.getUserId(), familyName));
    }
    
    /**
     * 通过邀请码加入家庭
     */
    @PostMapping("/join")
    public ResponseEntity<ApiResponse<FamilyInfoResponse>> joinFamily(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody Map<String, String> request) {
        String inviteCode = request.get("inviteCode");
        if (inviteCode == null || inviteCode.trim().isEmpty()) {
            return ResponseEntity.ok(ApiResponse.error("邀请码不能为空"));
        }
        return ResponseEntity.ok(familyService.joinFamily(userPrincipal.getUserId(), inviteCode.trim()));
    }
    
    /**
     * 获取家庭信息
     */
    @GetMapping("/info")
    public ResponseEntity<ApiResponse<FamilyInfoResponse>> getFamilyInfo(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(familyService.getFamilyInfo(userPrincipal.getUserId()));
    }
    
    /**
     * 退出家庭
     */
    @PostMapping("/leave")
    public ResponseEntity<ApiResponse<Void>> leaveFamily(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(familyService.leaveFamily(userPrincipal.getUserId()));
    }
}
