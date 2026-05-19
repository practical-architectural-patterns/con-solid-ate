package com.example.consolidate.points;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;


public interface PointsEntryRepository extends JpaRepository<PointsEntry, Long> {

    List<PointsEntry> findByParticipantPidOrderByTimestampDesc(Long pid);

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM PointsEntry e WHERE e.participant.pid = :pid")
    int calculateTotalBalance(@Param("pid") Long pid);
}