package com.example.consolidate.pointsaccount;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

record PointsAccountUpdate(int balance) {

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public PointsAccountUpdate(@JsonProperty("balance") int balance) {
        this.balance = balance;
    }
}

