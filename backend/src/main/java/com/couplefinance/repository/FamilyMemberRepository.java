package com.couplefinance.repository;

import com.couplefinance.entity.FamilyMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 家庭成员数据访问层
 */
@Repository
public interface FamilyMemberRepository extends JpaRepository<FamilyMember, Long> {
    
    /**
     * 根据用户ID查找家庭成员记录
     */
    Optional<FamilyMember> findByUserId(Long userId);
    
    /**
     * 根据家庭ID查找所有成员
     */
    List<FamilyMember> findByFamilyId(Long familyId);
    
    /**
     * 检查用户是否已在家庭中
     */
    boolean existsByUserId(Long userId);
    
    /**
     * 检查用户是否属于指定家庭
     */
    boolean existsByUserIdAndFamilyId(Long userId, Long familyId);
    
    /**
     * 获取家庭所有成员的用户ID列表
     */
    @Query("SELECT fm.userId FROM FamilyMember fm WHERE fm.familyId = :familyId")
    List<Long> findUserIdsByFamilyId(@Param("familyId") Long familyId);
}
