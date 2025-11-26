package com.silver.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "invitation_tokens")
public class InvitationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // 邀请令牌
    @Column(unique = true)
    private String token;
    
    // 邀请人ID
    private Long inviterId;
    
    // 被邀请人手机号
    private String inviteePhone;
    
    // 关系
    private String relation;
    
    // 创建时间
    private LocalDateTime createTime = LocalDateTime.now();
    
    // 过期时间
    private LocalDateTime expireTime;
    
    // 是否已使用
    private Boolean used = false;
    
    // 使用时间
    private LocalDateTime usedTime;
    
    public InvitationToken(Long inviterId, String inviteePhone, String relation) {
        this.inviterId = inviterId;
        this.inviteePhone = inviteePhone;
        this.relation = relation;
        this.token = UUID.randomUUID().toString();
        this.createTime = LocalDateTime.now();
        this.expireTime = LocalDateTime.now().plusDays(7); // 7天后过期
    }
}