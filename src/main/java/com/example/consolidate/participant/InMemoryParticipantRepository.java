package com.example.consolidate.participant;

import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryParticipantRepository implements ParticipantRepository {

    private final Map<Long, Participant> storage = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Participant save(Participant participant) {
        Long id = idGenerator.getAndIncrement();
        participant.setId(id);
        storage.put(id, participant);
        return participant;
    }

    @Override
    public List<Participant> findAll() {
        return new ArrayList<>(storage.values());
    }
}