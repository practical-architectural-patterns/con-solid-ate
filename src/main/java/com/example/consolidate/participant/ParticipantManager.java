package com.example.consolidate.participant;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ParticipantManager {

    private final ParticipantRepository repo;

    public ParticipantManager(ParticipantRepository repo) {
        this.repo = repo;
    }

    public Participant register(Participant p) {
        return repo.save(p);
    }

    public List<Participant> fetch() {
        return repo.findAll();
    }
}