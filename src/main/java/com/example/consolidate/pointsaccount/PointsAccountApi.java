package com.example.consolidate.pointsaccount;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/participants/{id}/points-account")
class PointsAccountApi {

    private final PointsAccountService pointsAccountService;

    PointsAccountApi(PointsAccountService pointsAccountService) {
        this.pointsAccountService = pointsAccountService;
    }

    @GetMapping
    public PointsAccountResponse getAccount(@PathVariable("id") Long participantPid) {
        return PointsAccountResponse.from(pointsAccountService.findByParticipant(participantPid));
    }

    @PutMapping
    public PointsAccountResponse recalculateBalance(@PathVariable("id") Long participantPid) {
        return PointsAccountResponse.from(pointsAccountService.recalculateBalance(participantPid));
    }

    @PostMapping("/points")
    public PointsResponse addPoints(@PathVariable("id") Long participantPid,
                                    @Valid @RequestBody AddPointsRequest addPointsRequest) {
        Points savedPoints = pointsAccountService.addPoints(
                participantPid,
                addPointsRequest.amount(),
                addPointsRequest.reason());
        return PointsResponse.from(savedPoints);
    }

    @GetMapping("/points")
    public List<PointsResponse> getPointsHistory(@PathVariable("id") Long participantPid) {
        return pointsAccountService.getPointsHistory(participantPid).stream()
                .map(PointsResponse::from)
                .toList();
    }
}