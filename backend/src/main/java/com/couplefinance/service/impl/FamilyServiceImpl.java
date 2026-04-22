package com.couplefinance.service.impl;

import com.couplefinance.dto.ApiResponse;
import com.couplefinance.dto.FamilyInfoResponse;
import com.couplefinance.entity.Family;
import com.couplefinance.entity.FamilyMember;
import com.couplefinance.entity.User;
import com.couplefinance.repository.FamilyMemberRepository;
import com.couplefinance.repository.FamilyRepository;
import com.couplefinance.repository.UserRepository;
import com.couplefinance.service.FamilyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 家庭服务实现类
 */
@Service
public class FamilyServiceImpl implements FamilyService {
    
    @Autowired
    private FamilyRepository familyRepository;
    
    @Autowired
    private FamilyMemberRepository familyMemberRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int INVITE_CODE_LENGTH = 6;
    
    @Override
    @Transactional
    public ApiResponse<FamilyInfoResponse> createFamily(Long userId, String familyName) {
        // 检查用户是否已在家庭中
        if (familyMemberRepository.existsByUserId(userId)) {
            return ApiResponse.error("您已加入一个家庭，请先退出当前家庭");
        }
        
        // 生成邀请码
        String inviteCode = generateInviteCode();
        while (familyRepository.existsByInviteCode(inviteCode)) {
            inviteCode = generateInviteCode();
        }
        
        // 创建家庭
        Family family = Family.builder()
                .name(familyName)
                .inviteCode(inviteCode)
                .createdBy(userId)
                .build();
        familyRepository.save(family);
        
        // 添加创建者为家庭成员
        FamilyMember member = FamilyMember.builder()
                .userId(userId)
                .familyId(family.getId())
                .build();
        familyMemberRepository.save(member);
        
        return ApiResponse.success("家庭创建成功", buildFamilyInfoResponse(family, List.of(member)));
    }
    
    @Override
    @Transactional
    public ApiResponse<FamilyInfoResponse> joinFamily(Long userId, String inviteCode) {
        // 检查用户是否已在家庭中
        if (familyMemberRepository.existsByUserId(userId)) {
            return ApiResponse.error("您已加入一个家庭，请先退出当前家庭");
        }
        
        // 查找家庭
        Optional<Family> familyOpt = familyRepository.findByInviteCode(inviteCode.toUpperCase());
        if (familyOpt.isEmpty()) {
            return ApiResponse.error("邀请码无效");
        }
        
        Family family = familyOpt.get();
        
        // 不能加入自己创建的家庭（理论上不会发生，因为创建时已加入）
        if (family.getCreatedBy().equals(userId)) {
            return ApiResponse.error("您已是该家庭的创建者");
        }
        
        // 添加为家庭成员
        FamilyMember member = FamilyMember.builder()
                .userId(userId)
                .familyId(family.getId())
                .build();
        familyMemberRepository.save(member);
        
        // 获取所有成员
        List<FamilyMember> members = familyMemberRepository.findByFamilyId(family.getId());
        
        return ApiResponse.success("加入家庭成功", buildFamilyInfoResponse(family, members));
    }
    
    @Override
    public ApiResponse<FamilyInfoResponse> getFamilyInfo(Long userId) {
        // 查找用户的家庭
        Optional<FamilyMember> memberOpt = familyMemberRepository.findByUserId(userId);
        if (memberOpt.isEmpty()) {
            return ApiResponse.error("您尚未加入任何家庭");
        }
        
        FamilyMember member = memberOpt.get();
        Optional<Family> familyOpt = familyRepository.findById(member.getFamilyId());
        if (familyOpt.isEmpty()) {
            return ApiResponse.error("家庭信息不存在");
        }
        
        Family family = familyOpt.get();
        List<FamilyMember> members = familyMemberRepository.findByFamilyId(family.getId());
        
        return ApiResponse.success(buildFamilyInfoResponse(family, members));
    }
    
    @Override
    @Transactional
    public ApiResponse<Void> leaveFamily(Long userId) {
        Optional<FamilyMember> memberOpt = familyMemberRepository.findByUserId(userId);
        if (memberOpt.isEmpty()) {
            return ApiResponse.error("您尚未加入任何家庭");
        }
        
        FamilyMember member = memberOpt.get();
        Optional<Family> familyOpt = familyRepository.findById(member.getFamilyId());
        
        if (familyOpt.isPresent()) {
            Family family = familyOpt.get();
            // 如果是创建者，删除整个家庭
            if (family.getCreatedBy().equals(userId)) {
                // 删除所有成员
                List<FamilyMember> members = familyMemberRepository.findByFamilyId(family.getId());
                familyMemberRepository.deleteAll(members);
                // 删除家庭
                familyRepository.delete(family);
            } else {
                // 普通成员只删除自己的关联
                familyMemberRepository.delete(member);
            }
        }
        
        return ApiResponse.success("退出家庭成功", null);
    }
    
    /**
     * 生成6位邀请码
     */
    private String generateInviteCode() {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(INVITE_CODE_LENGTH);
        for (int i = 0; i < INVITE_CODE_LENGTH; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }
    
    /**
     * 构建家庭信息响应
     */
    private FamilyInfoResponse buildFamilyInfoResponse(Family family, List<FamilyMember> members) {
        List<FamilyInfoResponse.MemberInfo> memberInfos = new ArrayList<>();
        
        for (FamilyMember member : members) {
            Optional<User> userOpt = userRepository.findById(member.getUserId());
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                memberInfos.add(FamilyInfoResponse.MemberInfo.builder()
                        .userId(user.getId())
                        .username(user.getUsername())
                        .nickname(user.getNickname())
                        .isCreator(family.getCreatedBy().equals(user.getId()))
                        .build());
            }
        }
        
        return FamilyInfoResponse.builder()
                .familyId(family.getId())
                .familyName(family.getName())
                .inviteCode(family.getInviteCode())
                .createdBy(family.getCreatedBy())
                .members(memberInfos)
                .build();
    }
}
