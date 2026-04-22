package com.couplefinance.repository;

import com.couplefinance.entity.TransactionRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 记账记录数据访问层
 */
@Repository
public interface TransactionRepository extends JpaRepository<TransactionRecord, Long> {
    
    /**
     * 查询家庭的所有记录（按日期倒序）
     */
    List<TransactionRecord> findByFamilyIdOrderByRecordDateDesc(Long familyId);
    
    /**
     * 查询家庭某日期范围内的记录
     */
    List<TransactionRecord> findByFamilyIdAndRecordDateBetweenOrderByRecordDateDesc(
            Long familyId, LocalDate startDate, LocalDate endDate);
    
    /**
     * 查询家庭某月的支出统计（按分类汇总）
     */
    @Query("SELECT t.category, SUM(t.amount) FROM TransactionRecord t " +
           "WHERE t.familyId = :familyId AND t.type = 'expense' " +
           "AND t.recordDate >= :startDate AND t.recordDate <= :endDate " +
           "GROUP BY t.category")
    List<Object[]> sumExpenseByCategoryAndMonth(
            @Param("familyId") Long familyId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
    
    /**
     * 查询家庭某月的总收入
     */
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM TransactionRecord t " +
           "WHERE t.familyId = :familyId AND t.type = 'income' " +
           "AND t.recordDate >= :startDate AND t.recordDate <= :endDate")
    BigDecimal sumIncomeByMonth(
            @Param("familyId") Long familyId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
    
    /**
     * 查询家庭某月的总支出
     */
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM TransactionRecord t " +
           "WHERE t.familyId = :familyId AND t.type = 'expense' " +
           "AND t.recordDate >= :startDate AND t.recordDate <= :endDate")
    BigDecimal sumExpenseByMonth(
            @Param("familyId") Long familyId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
