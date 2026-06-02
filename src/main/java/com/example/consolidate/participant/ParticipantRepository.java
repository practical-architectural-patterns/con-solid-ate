package com.example.consolidate.participant;


import org.springframework.data.jpa.repository.JpaRepository;

interface ParticipantRepository extends JpaRepository<Participant, Long> {
}