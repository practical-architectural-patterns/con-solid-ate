package com.example.consolidate.pointsaccount;

import com.example.consolidate.participant.ParticipantCreatedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
class PointsAccountCreator {

    private final PointsAccountService pointsAccountService;

    PointsAccountCreator(PointsAccountService pointsAccountService) {
        this.pointsAccountService = pointsAccountService;
    }

    @EventListener
    void createAccountForNewParticipant(ParticipantCreatedEvent participantCreatedEvent) {
        pointsAccountService.createAccountFor(participantCreatedEvent.participantPid());
    }
}