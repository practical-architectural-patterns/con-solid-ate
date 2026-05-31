package com.example.consolidate.pointsaccount;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PointsAccountServiceTest {

    private static final Long PARTICIPANT_PID = 5L;

    private static final int QUIZ_AMOUNT = 10;
    private static final String QUIZ_REASON = "quiz";

    private static final int TASK_AMOUNT = 5;
    private static final String TASK_REASON = "task";

    private static final int PENALTY_AMOUNT = -3;
    private static final String PENALTY_REASON = "penalty";

    @Mock private PointsAccountRepository pointsAccountRepository;
    @Mock private PointsRepository pointsRepository;
    @InjectMocks private PointsAccountService pointsAccountService;

    private PointsAccount pointsAccount;

    @BeforeEach
    void setUp() {
        pointsAccount = new PointsAccount(PARTICIPANT_PID);
    }

    @Test
    void should_SaveAccountWithZeroBalance_When_CreatingAccountForParticipant() {
        when(pointsAccountRepository.save(any(PointsAccount.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        pointsAccountService.createAccountFor(PARTICIPANT_PID);

        ArgumentCaptor<PointsAccount> pointsAccountCaptor = ArgumentCaptor.forClass(PointsAccount.class);
        verify(pointsAccountRepository).save(pointsAccountCaptor.capture());
        assertThat(pointsAccountCaptor.getValue().getParticipantPid()).isEqualTo(PARTICIPANT_PID);
        assertThat(pointsAccountCaptor.getValue().getBalance()).isZero();
    }

    @Test
    void should_ReturnAccount_When_FindingByParticipantAndAccountExists() {
        when(pointsAccountRepository.findByParticipantPid(PARTICIPANT_PID))
                .thenReturn(Optional.of(pointsAccount));

        assertThat(pointsAccountService.findByParticipant(PARTICIPANT_PID)).isSameAs(pointsAccount);
    }

    @Test
    void should_ThrowNotFoundException_When_FindingByParticipantAndAccountMissing() {
        when(pointsAccountRepository.findByParticipantPid(PARTICIPANT_PID))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> pointsAccountService.findByParticipant(PARTICIPANT_PID))
                .isInstanceOf(PointsAccountNotFoundException.class);
    }

    @Test
    void should_SavePointsEntry_When_AddingPoints() {
        when(pointsAccountRepository.findByParticipantPid(PARTICIPANT_PID))
                .thenReturn(Optional.of(pointsAccount));
        when(pointsRepository.save(any(Points.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Points savedPointsEntry = pointsAccountService.addPoints(PARTICIPANT_PID, QUIZ_AMOUNT, QUIZ_REASON);

        assertThat(savedPointsEntry.getAmount()).isEqualTo(QUIZ_AMOUNT);
        assertThat(savedPointsEntry.getReason()).isEqualTo(QUIZ_REASON);
        verify(pointsRepository).save(savedPointsEntry);
    }

    @Test
    void should_UpdateBalance_When_AddingPoints() {
        when(pointsAccountRepository.findByParticipantPid(PARTICIPANT_PID))
                .thenReturn(Optional.of(pointsAccount));
        when(pointsRepository.save(any(Points.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        // after saving, recalculateBalance will query history; stub history to include the new entry
        when(pointsRepository.findByAccountIdOrderByIdAsc(pointsAccount.getId()))
                .thenReturn(List.of(new Points(pointsAccount.getId(), QUIZ_AMOUNT, QUIZ_REASON)));
        when(pointsAccountRepository.save(pointsAccount)).thenReturn(pointsAccount);

        pointsAccountService.addPoints(PARTICIPANT_PID, QUIZ_AMOUNT, QUIZ_REASON);

        assertThat(pointsAccount.getBalance()).isEqualTo(QUIZ_AMOUNT);
        verify(pointsAccountRepository).save(pointsAccount);
    }

    @Test
    void should_ThrowNotFoundException_When_AddingPointsAndAccountMissing() {
        when(pointsAccountRepository.findByParticipantPid(PARTICIPANT_PID))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> pointsAccountService.addPoints(PARTICIPANT_PID, QUIZ_AMOUNT, QUIZ_REASON))
                .isInstanceOf(PointsAccountNotFoundException.class);
        verifyNoInteractions(pointsRepository);
    }

    @Test
    void should_ReturnHistory_When_GettingPointsHistoryForExistingAccount() {
        Points pointsEntry = new Points(pointsAccount.getId(), QUIZ_AMOUNT, QUIZ_REASON);
        when(pointsAccountRepository.findByParticipantPid(PARTICIPANT_PID))
                .thenReturn(Optional.of(pointsAccount));
        when(pointsRepository.findByAccountIdOrderByIdAsc(pointsAccount.getId()))
                .thenReturn(List.of(pointsEntry));

        assertThat(pointsAccountService.getPointsHistory(PARTICIPANT_PID)).containsExactly(pointsEntry);
    }

    @Test
    void should_ThrowNotFoundException_When_GettingPointsHistoryAndAccountMissing() {
        when(pointsAccountRepository.findByParticipantPid(PARTICIPANT_PID))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> pointsAccountService.getPointsHistory(PARTICIPANT_PID))
                .isInstanceOf(PointsAccountNotFoundException.class);
        verifyNoInteractions(pointsRepository);
    }

    @Test
    void should_SetBalanceToSumOfHistory_When_RecalculatingBalance() {
        List<Points> pointsHistory = List.of(
                new Points(pointsAccount.getId(), QUIZ_AMOUNT, QUIZ_REASON),
                new Points(pointsAccount.getId(), TASK_AMOUNT, TASK_REASON),
                new Points(pointsAccount.getId(), PENALTY_AMOUNT, PENALTY_REASON)
        );
        when(pointsAccountRepository.findByParticipantPid(PARTICIPANT_PID))
                .thenReturn(Optional.of(pointsAccount));
        when(pointsRepository.findByAccountIdOrderByIdAsc(pointsAccount.getId())).thenReturn(pointsHistory);
        when(pointsRepository.save(any(Points.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(pointsAccountRepository.save(pointsAccount)).thenReturn(pointsAccount);

        // trigger recalculation via addPoints (which calls recalculateBalance internally)
        pointsAccountService.addPoints(PARTICIPANT_PID, 0, "noop");

        assertThat(pointsAccount.getBalance()).isEqualTo(QUIZ_AMOUNT + TASK_AMOUNT + PENALTY_AMOUNT);
    }

    @Test
    void should_SetBalanceToZero_When_RecalculatingBalanceWithEmptyHistory() {
        when(pointsAccountRepository.findByParticipantPid(PARTICIPANT_PID))
                .thenReturn(Optional.of(pointsAccount));
        when(pointsRepository.findByAccountIdOrderByIdAsc(pointsAccount.getId())).thenReturn(List.of());
        when(pointsRepository.save(any(Points.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(pointsAccountRepository.save(pointsAccount)).thenReturn(pointsAccount);

        pointsAccountService.addPoints(PARTICIPANT_PID, 0, "noop");

        assertThat(pointsAccount.getBalance()).isZero();
    }

    @Test
    void should_PersistAccount_When_RecalculatingBalance() {
        when(pointsAccountRepository.findByParticipantPid(PARTICIPANT_PID))
                .thenReturn(Optional.of(pointsAccount));
        when(pointsRepository.findByAccountIdOrderByIdAsc(pointsAccount.getId())).thenReturn(List.of());
        when(pointsAccountRepository.save(pointsAccount)).thenReturn(pointsAccount);

        when(pointsRepository.save(any(Points.class))).thenAnswer(invocation -> invocation.getArgument(0));
        pointsAccountService.addPoints(PARTICIPANT_PID, 0, "noop");

        verify(pointsAccountRepository).save(pointsAccount);
    }

    @Test
    void should_ThrowNotFoundException_When_RecalculatingBalanceOfMissingAccount() {
        when(pointsAccountRepository.findByParticipantPid(PARTICIPANT_PID))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> pointsAccountService.addPoints(PARTICIPANT_PID, 0, "noop"))
                .isInstanceOf(PointsAccountNotFoundException.class);
        verifyNoInteractions(pointsRepository);
    }
}