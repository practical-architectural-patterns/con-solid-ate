package com.example.consolidate.participant;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/participants")
class ParticipantApi {

    private final ParticipantRepository repository;

    public ParticipantApi(ParticipantRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    public Participant create(@RequestBody Participant p) {
        return repository.save(p);
    }

    @GetMapping
    public List<Participant> list() {
        return repository.findAll();
    }
}