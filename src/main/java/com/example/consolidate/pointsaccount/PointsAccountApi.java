package com.example.consolidate.pointsaccount;

import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<PointsResponse>> getPointsHistory(
            @PathVariable("id") Long participantPid,
            @RequestHeader(value = HttpHeaders.IF_NONE_MATCH, required = false) String ifNoneMatch) {
        PointsAccountService.PointsHistoryWithEtag historyWithEtag = pointsAccountService.getPointsHistoryWithEtag(participantPid);
        String currentEtag = historyWithEtag.etag();

        if (ifNoneMatch != null && ifNoneMatch.equals(currentEtag)) {
            return ResponseEntity.status(304).eTag(currentEtag).build();
        }

        return ResponseEntity.ok()
                .eTag(currentEtag)
                .body(historyWithEtag.pointsHistory().stream()
                        .map(PointsResponse::from)
                        .toList());
    }
}

