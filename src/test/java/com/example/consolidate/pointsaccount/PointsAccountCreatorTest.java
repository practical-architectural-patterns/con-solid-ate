package com.example.consolidate.pointsaccount;

import com.example.consolidate.participant.ParticipantCreatedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PointsAccountCreatorTest {

    private static final Long PARTICIPANT_PID = 42L;

    @Mock private PointsAccountService pointsAccountService;
    @InjectMocks private PointsAccountCreator pointsAccountCreator;

    @Test
    void should_CreateAccountForParticipant_When_ReceivingParticipantCreatedEvent() {
        ParticipantCreatedEvent participantCreatedEvent = new ParticipantCreatedEvent(PARTICIPANT_PID);

        pointsAccountCreator.createAccountForNewParticipant(participantCreatedEvent);

        verify(pointsAccountService).createAccountFor(PARTICIPANT_PID);
    }
}