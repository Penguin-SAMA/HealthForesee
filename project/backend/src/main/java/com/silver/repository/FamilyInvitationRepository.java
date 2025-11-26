package com.silver.repository;

import com.silver.entity.FamilyInvitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FamilyInvitationRepository extends JpaRepository<FamilyInvitation, Long> {
    List<FamilyInvitation> findByInviteePhone(String inviteePhone);
    List<FamilyInvitation> findByInviterIdAndStatus(Long inviterId, FamilyInvitation.InvitationStatus status);
}