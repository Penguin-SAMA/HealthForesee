package com.silver.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "family_relations")
public class FamilyRelation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // 用户ID（子女）
    private Long userId;
    
    // 家庭成员ID（父母）
    private Long memberId;
    
    // 关系类型
    private String relation;
    
    // 状态
    @Enumerated(EnumType.STRING)
    private RelationStatus status = RelationStatus.ACTIVE;
    
    public enum RelationStatus {
        ACTIVE, INACTIVE
    }
}