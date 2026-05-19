package com.example.consolidate.pointsaccount;

record PointsAccountResponse(int balance) {

    static PointsAccountResponse from(PointsAccount pointsAccount) {
        return new PointsAccountResponse(pointsAccount.getBalance());
    }
}