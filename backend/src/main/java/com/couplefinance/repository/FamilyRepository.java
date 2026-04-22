package com.couplefinance.repository;

import com.couplefinance.entity.Family;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 家庭数据访问层
 */
@Repository
public interface FamilyRepository extends JpaRepository<Family, Long> {
    
    /**
     * 根据邀请码查找家庭
     */
    Optional<Family> findByInviteCode(String inviteCode);
    
    /**
     * 检查邀请码是否存在
     */
    boolean existsByInviteCode(String inviteCode);
}
