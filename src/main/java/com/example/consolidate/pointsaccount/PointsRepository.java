package com.example.consolidate.pointsaccount;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

interface PointsRepository extends JpaRepository<Points, Long> {
    List<Points> findByAccountId(Long accountId);

    long countByAccountId(Long accountId);

    @Query("SELECT COALESCE(MAX(p.id), 0) FROM Points p WHERE p.accountId = :accountId")
    long maxIdByAccountId(@Param("accountId") Long accountId);
}