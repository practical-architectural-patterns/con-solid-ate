package com.example.consolidate.participant;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/participants")
public class ParticipantApi {

    private final ParticipantManager manager;

    public ParticipantApi(ParticipantManager manager) {
        this.manager = manager;
    }

    @PostMapping
    public Participant create(@RequestBody Participant p) {
        return manager.register(p);
    }

    @GetMapping
    public List<Participant> list() {
        return manager.fetch();
    }
}