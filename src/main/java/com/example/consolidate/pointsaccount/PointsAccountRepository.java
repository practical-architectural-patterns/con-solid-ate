package com.example.consolidate.pointsaccount;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

interface PointsAccountRepository extends JpaRepository<PointsAccount, Long> {
    Optional<PointsAccount> findByParticipantPid(Long participantPid);
}