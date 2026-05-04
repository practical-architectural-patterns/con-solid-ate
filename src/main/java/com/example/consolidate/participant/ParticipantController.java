package com.example.consolidate.participant;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/participants")
public class ParticipantController {

    private final ParticipantService service;

    public ParticipantController(ParticipantService service) {
        this.service = service;
    }

    @PostMapping
    public Participant addParticipant(@RequestBody Participant participant) {
        return service.addParticipant(participant);
    }

    @GetMapping
    public List<Participant> getAll() {
        return service.getAll();
    }
}