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
    public ResponseEntity<List<PointsResponse>> getPointsHistory(
            @PathVariable("id") Long participantPid,
            WebRequest request) {

        List<PointsResponse> history = pointsAccountService.getPointsHistory(participantPid).stream()
                .map(PointsResponse::from)
                .toList();

        // Deterministyczny ETag bazujący na zawartości listy (jej hashCode)
        String etag = "\"" + Integer.toHexString(history.hashCode()) + "\"";

        // Jeśli nagłówek If-None-Match zgadza się z ETagiem, zwraca 304 Not Modified
        if (request.checkNotModified(etag)) {
            return null;
        }

        return ResponseEntity.ok()
                .eTag(etag)
                .body(history);
    }
}