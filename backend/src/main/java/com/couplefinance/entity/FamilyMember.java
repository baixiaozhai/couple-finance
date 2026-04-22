package com.couplefinance.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * 家庭成员关联实体类
 */
@Data
@Entity
@Table(name = "family_member", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "family_id"}))
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FamilyMember {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "family_id", nullable = false)
    private Long familyId;
    
    @Column(name = "joined_at", updatable = false)
    private LocalDateTime joinedAt;
    
    @PrePersist
    protected void onCreate() {
        joinedAt = LocalDateTime.now();
    }
}
