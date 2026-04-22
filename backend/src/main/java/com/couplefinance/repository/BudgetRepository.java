package com.couplefinance.repository;

import com.couplefinance.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 预算数据访问层
 */
@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {
    
    /**
     * 查询家庭某月的所有预算
     */
    List<Budget> findByFamilyIdAndMonth(Long familyId, String month);
    
    /**
     * 查询家庭某月某分类的预算
     */
    Optional<Budget> findByFamilyIdAndMonthAndCategory(Long familyId, String month, String category);
    
    /**
     * 删除家庭某月某分类的预算
     */
    void deleteByFamilyIdAndMonthAndCategory(Long familyId, String month, String category);
}
