package com.example.consolidate.participant;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParticipantService {

    private final ParticipantRepository repository;

    public ParticipantService(ParticipantRepository repository) {
        this.repository = repository;
    }

    public Participant addParticipant(Participant participant) {
        return repository.save(participant);
    }

    public List<Participant> getAll() {
        return repository.findAll();
    }
}