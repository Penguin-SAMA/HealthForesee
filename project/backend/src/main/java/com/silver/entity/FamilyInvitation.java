package com.silver.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "family_invitations")
public class FamilyInvitation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // 邀请人ID
    private Long inviterId;
    
    // 被邀请人手机号
    private String inviteePhone;
    
    // 关系
    private String relation;
    
    // 邀请状态: PENDING(待接受), ACCEPTED(已接受), REJECTED(已拒绝)
    @Enumerated(EnumType.STRING)
    private InvitationStatus status = InvitationStatus.PENDING;
    
    // 邀请时间
    private LocalDateTime inviteTime = LocalDateTime.now();
    
    // 处理时间
    private LocalDateTime processTime;
    
    public enum InvitationStatus {
        PENDING, ACCEPTED, REJECTED
    }
}