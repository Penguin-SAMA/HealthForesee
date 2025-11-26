package com.silver.service;

import com.silver.entity.FamilyInvitation;
import com.silver.entity.FamilyMember;
import com.silver.entity.FamilyRelation;
import com.silver.entity.InvitationToken;
import com.silver.entity.User;
import com.silver.repository.FamilyInvitationRepository;
import com.silver.repository.FamilyMemberRepository;
import com.silver.repository.FamilyRelationRepository;
import com.silver.repository.InvitationTokenRepository;
import com.silver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private FamilyInvitationRepository familyInvitationRepository;
    
    @Autowired
    private FamilyMemberRepository familyMemberRepository;
    
    @Autowired
    private FamilyRelationRepository familyRelationRepository;
    
    @Autowired
    private InvitationTokenRepository invitationTokenRepository;
    
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    public Optional<User> findByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber);
    }
    
    public User save(User user) {
        return userRepository.save(user);
    }
    
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    
    public FamilyInvitation inviteFamilyMember(Long inviterId, String inviteePhone, String relation) {
        FamilyInvitation invitation = new FamilyInvitation();
        invitation.setInviterId(inviterId);
        invitation.setInviteePhone(inviteePhone);
        invitation.setRelation(relation);
        return familyInvitationRepository.save(invitation);
    }
    
    public List<FamilyInvitation> getPendingInvitations(String inviteePhone) {
        return familyInvitationRepository.findByInviteePhone(inviteePhone);
    }
    
    public List<FamilyMember> getFamilyMembersByUserId(Long userId) {
        return familyMemberRepository.findByUserId(userId);
    }
    
    public List<FamilyRelation> getFamilyRelationsByUserId(Long userId) {
        return familyRelationRepository.findByUserId(userId);
    }
    
    public FamilyRelation saveFamilyRelation(FamilyRelation relation) {
        return familyRelationRepository.save(relation);
    }
    
    public FamilyRelation findFamilyRelation(Long userId, Long memberId) {
        // 简化实现，实际应用中可能需要更复杂的查询
        List<FamilyRelation> relations = familyRelationRepository.findByUserId(userId);
        return relations.stream()
                .filter(r -> r.getMemberId().equals(memberId))
                .findFirst()
                .orElse(null);
    }
    
    public Optional<FamilyRelation> findFamilyRelationById(Long relationId) {
        return familyRelationRepository.findById(relationId);
    }
    
    public void deleteFamilyRelation(Long relationId) {
        familyRelationRepository.deleteById(relationId);
    }
    
    public InvitationToken saveInvitationToken(InvitationToken token) {
        return invitationTokenRepository.save(token);
    }
    
    public Optional<InvitationToken> findValidInvitationToken(String token) {
        return invitationTokenRepository.findByTokenAndUsedFalse(token);
    }
}