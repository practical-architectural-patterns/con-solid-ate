package com.example.consolidate.participant;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/participants")
class ParticipantApi {

    private final ParticipantService participantService;

    ParticipantApi(ParticipantService participantService) {
        this.participantService = participantService;
    }

    @PostMapping
    public Participant create(@RequestBody Participant participant) {
        return participantService.register(participant);
    }

    @GetMapping
    public List<Participant> list() {
        return participantService.findAll();
    }
}
