package com.example.consolidate.participant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ParticipantServiceTest {

    private static final Long PARTICIPANT_PID = 42L;
    private static final String FULL_NAME = "Jan Kowalski";
    private static final String CONTACT_MAIL = "jan@example.com";

    private static final String FIRST_FULL_NAME = "Anna";
    private static final String FIRST_CONTACT_MAIL = "anna@example.com";
    private static final String SECOND_FULL_NAME = "Piotr";
    private static final String SECOND_CONTACT_MAIL = "piotr@example.com";

    @Mock private ParticipantRepository participantRepository;
    @Mock private ApplicationEventPublisher applicationEventPublisher;
    @InjectMocks private ParticipantService participantService;

    private Participant inputParticipant;
    private Participant savedParticipant;

    @BeforeEach
    void setUp() {
        inputParticipant = buildParticipant(FULL_NAME, CONTACT_MAIL);
        savedParticipant = buildParticipantWithPid(PARTICIPANT_PID, FULL_NAME, CONTACT_MAIL);
    }

    @Test
    void should_ReturnPersistedParticipant_When_Registering() {
        when(participantRepository.save(inputParticipant)).thenReturn(savedParticipant);

        Participant returnedParticipant = participantService.register(inputParticipant);

        assertThat(returnedParticipant).isSameAs(savedParticipant);
    }

    @Test
    void should_PublishCreatedEventWithSavedPid_When_Registering() {
        when(participantRepository.save(inputParticipant)).thenReturn(savedParticipant);

        participantService.register(inputParticipant);

        verify(applicationEventPublisher).publishEvent(new ParticipantCreatedEvent(PARTICIPANT_PID));
    }

    @Test
    void should_ReturnAllParticipants_When_FindingAll() {
        Participant firstParticipant = buildParticipant(FIRST_FULL_NAME, FIRST_CONTACT_MAIL);
        Participant secondParticipant = buildParticipant(SECOND_FULL_NAME, SECOND_CONTACT_MAIL);
        when(participantRepository.findAll()).thenReturn(List.of(firstParticipant, secondParticipant));

        assertThat(participantService.findAll()).containsExactly(firstParticipant, secondParticipant);
    }

    @Test
    void should_ReturnEmptyList_When_FindingAllOnEmptyRepository() {
        when(participantRepository.findAll()).thenReturn(List.of());

        assertThat(participantService.findAll()).isEmpty();
    }

    private static Participant buildParticipant(String fullName, String contactMail) {
        Participant participant = new Participant();
        participant.setFullName(fullName);
        participant.setContactMail(contactMail);
        return participant;
    }

    private static Participant buildParticipantWithPid(Long pid, String fullName, String contactMail) {
        Participant participant = buildParticipant(fullName, contactMail);
        participant.setPid(pid);
        return participant;
    }
}