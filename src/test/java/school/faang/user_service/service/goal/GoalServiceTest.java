package school.faang.user_service.service.goal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.goal.GoalRepository;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.internal.verification.VerificationModeFactory.times;

class GoalServiceTest {

    @Mock
    GoalRepository goalRepository;

    @ExtendWith(MockitoExtension.class)
    @InjectMocks
    GoalService service;

    @Test
    public void findGoalsByUserIdLessThanOneTest() {
        assertThrows(DataValidationException.class, () -> {
            service.findGoalsByUserId(0L);
        });
    }

    @Test
    public void findGoalsByUserIdDataWasCollectedTest() {
        service.findGoalsByUserId(1L);
        Mockito.verify(goalRepository).findGoalsByUserId(Mockito.anyLong());
    }

    @Test
    public void getGoalsByUserLessThanOneTest() {
        assertThrows(DataValidationException.class, () -> {
            service.getGoalsByUser(0L, null);
        });
    }

    @Test
    public void deleteGoalValidId() {
        long goalId = 1L;
        service.deleteGoal(goalId);
        Mockito.verify(goalRepository, times(1)).deleteById(goalId);
    }
}