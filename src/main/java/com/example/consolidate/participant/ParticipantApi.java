package com.example.consolidate.participant;

import com.example.consolidate.points.PointsEntry;
import com.example.consolidate.points.PointsEntryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/participants")
class ParticipantApi {

    private final ParticipantRepository participantRepository;
    private final PointsEntryRepository pointsEntryRepository;

    public ParticipantApi(ParticipantRepository r1, PointsEntryRepository r2) {
        this.participantRepository = r1;
        this.pointsEntryRepository = r2;
    }



    @PostMapping
    public Participant create(@RequestBody Participant p) {
        return participantRepository.save(p);
    }

    @GetMapping
    public List<Participant> list() {
        return participantRepository.findAll();
    }




    @GetMapping("/{id}/points-account")
    public Map<String, Integer> getBalance(@PathVariable Long id) {
        verifyParticipantExists(id);
        int balance = pointsEntryRepository.calculateTotalBalance(id);
        return Map.of("value", balance);
    }




    @GetMapping("/{id}/points-account/points")
    public List<PointsEntry> getPointsHistory(@PathVariable Long id) {
        verifyParticipantExists(id);
        return pointsEntryRepository.findByParticipantPidOrderByTimestampDesc(id);
    }



    @PostMapping("/{id}/points-account/points")
    public PointsEntry addPointsEntry(@PathVariable Long id, @RequestBody PointsEntry entry) {
        Participant participant = verifyParticipantExists(id);
        entry.setParticipant(participant);

        if (entry.getSourceParticipant() != null && entry.getSourceParticipant().getPid() != null) {
            Participant managedSource = verifyParticipantExists(entry.getSourceParticipant().getPid());
            entry.setSourceParticipant(managedSource);
        }

        entry.setTimestamp(LocalDateTime.now());

        return pointsEntryRepository.save(entry);
    }




    @PutMapping("/{id}/points-account")
    public Map<String, Integer> updateBalance(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        Participant participant = verifyParticipantExists(id);

        int targetBalance = Integer.parseInt(payload.get("value"));

        Participant sourceParticipant = null;
        if (payload.containsKey("sourceParticipantId") && payload.get("sourceParticipantId") != null) {
            Long sourceId = Long.parseLong(payload.get("sourceParticipantId"));
            sourceParticipant = verifyParticipantExists(sourceId);
        }

        int currentBalance = pointsEntryRepository.calculateTotalBalance(id);
        int difference = targetBalance - currentBalance;

        if (difference != 0) {
            PointsEntry correctionEntry = new PointsEntry(participant, sourceParticipant, difference);
            pointsEntryRepository.save(correctionEntry);
        }

        return Map.of("value", targetBalance);
    }

    private Participant verifyParticipantExists(Long id) {
        return participantRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Participant not found"));
    }
}