package com.couplefinance.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 记账记录实体类
 */
@Data
@Entity
@Table(name = "transaction_record")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "family_id", nullable = false)
    private Long familyId;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;  // 记录创建者
    
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;
    
    @Column(nullable = false, length = 50)
    private String category;  // 分类：餐饮、房租、娱乐等
    
    @Column(nullable = false, length = 20)
    private String type;  // income-收入, expense-支出
    
    @Column(name = "payer_id", nullable = false)
    private Long payerId;  // 实际付款人
    
    @Column(name = "record_date", nullable = false)
    private LocalDate recordDate;
    
    @Column(length = 500)
    private String note;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
