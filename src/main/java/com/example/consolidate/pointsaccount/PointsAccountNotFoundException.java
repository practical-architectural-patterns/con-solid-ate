package com.example.consolidate.pointsaccount;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
class PointsAccountNotFoundException extends RuntimeException {
    PointsAccountNotFoundException(Long participantPid) {
        super("Points account for participant " + participantPid + " not found");
    }
}