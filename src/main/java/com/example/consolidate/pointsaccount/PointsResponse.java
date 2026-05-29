package com.example.consolidate.pointsaccount;

import java.time.Instant;

record PointsResponse(
        Long id,
        int amount,
        String reason,
        Instant createdAt) {

    static PointsResponse from(Points points) {
        return new PointsResponse(
                points.getId(),
                points.getAmount(),
                points.getReason(),
                points.getCreatedAt());
    }
}