package com.example.consolidate.pointsaccount;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

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
    public PointsAccountResponse updateAccount(@PathVariable("id") Long participantPid,
                                               @RequestBody PointsAccountUpdate update) {
        return PointsAccountResponse.from(pointsAccountService.updateBalance(participantPid, update.balance()));
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
    public ResponseEntity<List<PointsResponse>> getPointsHistory(@PathVariable("id") Long participantPid,
                                                                  WebRequest webRequest) {
        String eTag = pointsAccountService.computePointsHistoryETag(participantPid);
        if (webRequest.checkNotModified(eTag)) {
            return null;
        }
        return ResponseEntity.ok()
                .eTag(eTag)
                .body(pointsAccountService.getPointsHistory(participantPid).stream()
                        .map(PointsResponse::from)
                        .toList());
    }
}