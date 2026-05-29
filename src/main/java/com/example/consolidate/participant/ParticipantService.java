package com.example.consolidate.participant;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
class ParticipantService {

    private final ParticipantRepository participantRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    ParticipantService(ParticipantRepository participantRepository,
                       ApplicationEventPublisher applicationEventPublisher) {
        this.participantRepository = participantRepository;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Transactional
    Participant register(Participant participant) {
        Participant savedParticipant = participantRepository.save(participant);
        applicationEventPublisher.publishEvent(new ParticipantCreatedEvent(savedParticipant.getPid()));
        return savedParticipant;
    }

    @Transactional(readOnly = true)
    List<Participant> findAll() {
        return participantRepository.findAll();
    }
}