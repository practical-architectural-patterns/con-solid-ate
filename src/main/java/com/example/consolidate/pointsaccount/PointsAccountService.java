package com.example.consolidate.pointsaccount;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        return pointsRepository.findByAccountId(pointsAccount.getId());
    }

    private void recalculateBalance(Long participantPid) {
        PointsAccount pointsAccount = findByParticipant(participantPid);
        List<Points> pointsHistory = pointsRepository.findByAccountId(pointsAccount.getId());
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