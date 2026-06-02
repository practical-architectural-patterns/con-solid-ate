package com.example.consolidate.pointsaccount;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

record AddPointsRequest(
        int amount,
        @NotBlank @Size(max = 200) String reason) {
}
