package com.example.consolidate.pointsaccount;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Service
class PointsAccountService {

    private final PointsAccountRepository pointsAccountRepository;
    private final PointsRepository pointsRepository;

    PointsAccountService(PointsAccountRepository pointsAccountRepository,
                         PointsRepository pointsRepository) {
        this.pointsAccountRepository = pointsAccountRepository;
        this.pointsRepository = pointsRepository;
    }

    record PointsHistoryWithEtag(List<Points> pointsHistory, String etag) {
    }

    @Transactional
    PointsAccount createAccountFor(Long participantPid) {
        return pointsAccountRepository.save(new PointsAccount(participantPid));
    }

    @Transactional(readOnly = true)
    PointsAccount findByParticipant(Long participantPid) {
        return pointsAccountRepository.findByParticipantPid(participantPid)
                .orElseThrow(() -> new PointsAccountNotFoundException(participantPid));
    }

    @Transactional
    Points addPoints(Long participantPid, int amount, String reason) {
        PointsAccount pointsAccount = findByParticipant(participantPid);
        Points saved = pointsRepository.save(pointsAccount.createPointsEntry(amount, reason));
        recalculateBalance(participantPid);
        return saved;
    }

    @Transactional(readOnly = true)
    List<Points> getPointsHistory(Long participantPid) {
        PointsAccount pointsAccount = findByParticipant(participantPid);
        return pointsRepository.findByAccountIdOrderByIdAsc(pointsAccount.getId());
    }

    @Transactional(readOnly = true)
    PointsHistoryWithEtag getPointsHistoryWithEtag(Long participantPid) {
        List<Points> pointsHistory = getPointsHistory(participantPid);
        return new PointsHistoryWithEtag(pointsHistory, computePointsHistoryEtag(pointsHistory));
    }

    private static String computePointsHistoryEtag(List<Points> pointsHistory) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            for (Points points : pointsHistory) {
                digest.update(String.valueOf(points.getId()).getBytes(StandardCharsets.UTF_8));
                digest.update((byte) ':');
                digest.update(String.valueOf(points.getAmount()).getBytes(StandardCharsets.UTF_8));
                digest.update((byte) ':');
                digest.update(points.getReason() == null ? new byte[0] : points.getReason().getBytes(StandardCharsets.UTF_8));
                digest.update((byte) ':');
                digest.update(String.valueOf(points.getCreatedAt()).getBytes(StandardCharsets.UTF_8));
                digest.update((byte) ';');
            }
            return "\"" + bytesToHex(digest.digest()) + "\"";
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Unable to compute ETag", e);
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }

    private void recalculateBalance(Long participantPid) {
        PointsAccount pointsAccount = findByParticipant(participantPid);
        List<Points> pointsHistory = pointsRepository.findByAccountIdOrderByIdAsc(pointsAccount.getId());
        pointsAccount.recalculateBalanceFrom(pointsHistory);
        pointsAccountRepository.save(pointsAccount);
    }

    @Transactional
    PointsAccount updateBalance(Long participantPid, int newBalance) {
        PointsAccount pointsAccount = findByParticipant(participantPid);
        pointsAccount.setBalance(newBalance);
        return pointsAccountRepository.save(pointsAccount);
    }
}