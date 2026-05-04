package com.example.consolidate.participant;

import java.util.List;

public interface ParticipantRepository {
    Participant save(Participant participant);
    List<Participant> findAll();
}