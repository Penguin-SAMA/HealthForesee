package com.silver.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "family_members")
public class FamilyMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // 用户ID
    private Long userId;
    
    // 关联的邀请ID
    private Long invitationId;
    
    // 关系
    private String relation;
    
    // 状态: ACTIVE(激活), INACTIVE(未激活)
    @Enumerated(EnumType.STRING)
    private MemberStatus status = MemberStatus.ACTIVE;
    
    public enum MemberStatus {
        ACTIVE, INACTIVE
    }
}