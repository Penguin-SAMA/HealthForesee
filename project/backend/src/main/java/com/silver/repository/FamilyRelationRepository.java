package com.silver.repository;

import com.silver.entity.FamilyRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FamilyRelationRepository extends JpaRepository<FamilyRelation, Long> {
    List<FamilyRelation> findByUserId(Long userId);
    List<FamilyRelation> findByMemberId(Long memberId);
    List<FamilyRelation> findByUserIdAndStatus(Long userId, FamilyRelation.RelationStatus status);
}