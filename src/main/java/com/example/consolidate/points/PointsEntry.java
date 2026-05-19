package com.example.consolidate.points;

import com.example.consolidate.participant.Participant;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class PointsEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant_pid")
    @JsonIgnore
    private Participant participant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_participant_pid")
    @JsonIgnore
    private Participant sourceParticipant;

    private int amount;
    private LocalDateTime timestamp;

    public PointsEntry() {}

    public PointsEntry(Participant owner, Participant sourceParticipant, int amount) {
        this.participant = owner;
        this.sourceParticipant = sourceParticipant;
        this.amount = amount;
        this.timestamp = LocalDateTime.now();
    }

}