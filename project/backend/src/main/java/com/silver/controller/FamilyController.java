package com.silver.controller;

import com.silver.entity.FamilyRelation;
import com.silver.entity.InvitationToken;
import com.silver.entity.User;
import com.silver.service.UserService;
import com.silver.utils.Result;
import com.silver.utils.JwtUtil;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/family")
public class FamilyController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/members")
    public Result<Map<String, Object>> getFamilyMembers(@RequestHeader("Authorization") String token) {
        try {
            // 解析JWT Token获取当前用户信息
            Map<String, Object> userInfo = JwtUtil.parseToken(token.replace("Bearer ", ""));
            Long currentUserId = (Long) userInfo.get("userId");
            
            // 获取当前用户的家庭关系
            List<FamilyRelation> relations = userService.getFamilyRelationsByUserId(currentUserId);
            
            // 获取家庭成员信息
            List<Map<String, Object>> members = new ArrayList<>();
            for (FamilyRelation relation : relations) {
                Optional<User> userOptional = userService.findById(relation.getMemberId());
                if (userOptional.isPresent()) {
                    User member = userOptional.get();
                    Map<String, Object> memberData = Map.of(
                        "relationId", relation.getId(),
                        "userId", member.getId(),
                        "name", member.getName(),
                        "role", member.getRole() != null ? member.getRole().name() : "PARENT",
                        "relation", relation.getRelation(),
                        "status", relation.getStatus() != null ? relation.getStatus().name() : "ACTIVE"
                    );
                    members.add(memberData);
                }
            }
            
            return Result.success(Map.of("members", members));
        } catch (Exception e) {
            return Result.error("获取家庭成员信息失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/invite")
    public Result<Map<String, Object>> inviteFamily(@RequestBody InviteReq req, @RequestHeader("Authorization") String token) {
        try {
            // 解析JWT Token获取当前用户信息
            Map<String, Object> userInfo = JwtUtil.parseToken(token.replace("Bearer ", ""));
            Long currentUserId = (Long) userInfo.get("userId");
            
            // 查找被邀请用户
            Optional<User> inviteeOptional = userService.findByPhoneNumber(req.getInviteePhone());
            if (!inviteeOptional.isPresent()) {
                return Result.error("未找到该手机号用户，请确认手机号是否正确");
            }
            
            User invitee = inviteeOptional.get();
            
            // 检查是否已经存在家庭关系
            // 这里简化处理，实际应用中应该更复杂的检查逻辑
            FamilyRelation existingRelation = userService.findFamilyRelation(currentUserId, invitee.getId());
            if (existingRelation != null) {
                return Result.error("该用户已经是您的家庭成员");
            }
            
            // 创建邀请令牌
            InvitationToken invitationToken = new InvitationToken(currentUserId, req.getInviteePhone(), req.getRelation());
            invitationToken = userService.saveInvitationToken(invitationToken);
            
            // 在实际应用中，这里应该发送短信给被邀请用户，包含邀请链接
            // 邀请链接格式：http://your-app.com/accept-invitation?token=invitationToken.getToken()
            
            // 返回邀请链接
            String invitationLink = "/accept-invitation?token=" + invitationToken.getToken();
            
            Map<String, Object> data = Map.of(
                "invitationLink", invitationLink,
                "inviteeName", invitee.getName()
            );
            
            return Result.success(data, "邀请已生成，邀请链接: " + invitationLink);
        } catch (Exception e) {
            return Result.error("邀请发送失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/invitation-info")
    public Result<Map<String, Object>> getInvitationInfo(@RequestParam String token) {
        try {
            // 查找邀请令牌
            Optional<InvitationToken> tokenOptional = userService.findValidInvitationToken(token);
            if (!tokenOptional.isPresent()) {
                return Result.error("邀请链接已过期或无效");
            }
            
            InvitationToken invitationToken = tokenOptional.get();
            
            // 获取邀请人信息
            Optional<User> inviterOptional = userService.findById(invitationToken.getInviterId());
            if (!inviterOptional.isPresent()) {
                return Result.error("邀请人信息不存在");
            }
            
            User inviter = inviterOptional.get();
            
            Map<String, Object> data = Map.of(
                "inviterName", inviter.getName(),
                "relation", invitationToken.getRelation(),
                "inviteePhone", invitationToken.getInviteePhone()
            );
            
            return Result.success(data);
        } catch (Exception e) {
            return Result.error("获取邀请信息失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/accept-invitation")
    public Result<String> acceptInvitation(@RequestBody AcceptInvitationReq req) {
        try {
            // 查找邀请令牌
            Optional<InvitationToken> tokenOptional = userService.findValidInvitationToken(req.getToken());
            if (!tokenOptional.isPresent()) {
                return Result.error("邀请链接已过期或无效");
            }
            
            InvitationToken invitationToken = tokenOptional.get();
            
            // 获取当前用户（被邀请人）
            // 注意：在实际应用中，这里应该从JWT token中获取当前用户信息
            // 为了简化，我们通过手机号查找用户
            Optional<User> inviteeOptional = userService.findByPhoneNumber(invitationToken.getInviteePhone());
            if (!inviteeOptional.isPresent()) {
                return Result.error("用户信息不存在");
            }
            
            User invitee = inviteeOptional.get();
            
            // 创建家庭关系
            FamilyRelation relation = new FamilyRelation();
            relation.setUserId(invitationToken.getInviterId());
            relation.setMemberId(invitee.getId());
            relation.setRelation(invitationToken.getRelation());
            userService.saveFamilyRelation(relation);
            
            // 标记邀请令牌为已使用
            invitationToken.setUsed(true);
            invitationToken.setUsedTime(java.time.LocalDateTime.now());
            userService.saveInvitationToken(invitationToken);
            
            return Result.success("您已成功加入家庭组");
        } catch (Exception e) {
            return Result.error("接受邀请失败: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/members/{relationId}")
    public Result<String> removeFamilyMember(@PathVariable Long relationId, @RequestHeader("Authorization") String token) {
        try {
            // 解析JWT Token获取当前用户信息
            Map<String, Object> userInfo = JwtUtil.parseToken(token.replace("Bearer ", ""));
            Long currentUserId = (Long) userInfo.get("userId");
            
            // 查找家庭关系
            Optional<FamilyRelation> relationOptional = userService.findFamilyRelationById(relationId);
            if (!relationOptional.isPresent()) {
                return Result.error("家庭关系不存在");
            }
            
            FamilyRelation relation = relationOptional.get();
            
            // 检查这个关系是否属于当前用户
            if (!relation.getUserId().equals(currentUserId)) {
                return Result.error("无权限删除此家庭成员");
            }
            
            // 删除家庭关系
            userService.deleteFamilyRelation(relationId);
            
            return Result.success("家庭成员删除成功");
        } catch (Exception e) {
            return Result.error("删除家庭成员失败: " + e.getMessage());
        }
    }
    
    @Data
    static class InviteReq {
        private String inviteePhone;
        private String relation;
    }
    
    @Data
    static class AcceptInvitationReq {
        private String token;
    }
}