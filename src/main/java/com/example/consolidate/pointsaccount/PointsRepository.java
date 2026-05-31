package com.example.consolidate.pointsaccount;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

interface PointsRepository extends JpaRepository<Points, Long> {
    List<Points> findByAccountIdOrderByIdAsc(Long accountId);
}