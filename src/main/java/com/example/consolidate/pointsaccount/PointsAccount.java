package com.example.consolidate.pointsaccount;

import jakarta.persistence.*;

import java.util.List;

@Entity
class PointsAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long participantPid;

    private int balance;

    protected PointsAccount() {
    }

    PointsAccount(Long participantPid) {
        this.participantPid = participantPid;
        this.balance = 0;
    }

    Points createPointsEntry(int amount, String reason) {
        return new Points(id, amount, reason);
    }

    void recalculateBalanceFrom(List<Points> pointsHistory) {
        this.balance = pointsHistory.stream().mapToInt(Points::getAmount).sum();
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public Long getId() {
        return id;
    }

    public Long getParticipantPid() {
        return participantPid;
    }

    public int getBalance() {
        return balance;
    }
}