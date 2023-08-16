package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.service.goal.GoalService;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Lazy))
public class UserService {
    private final UserRepository userRepository;
    private final GoalService goalService;
    @Lazy
    private final EventService eventService;
    private final UserMapper userMapper;

    public boolean areOwnedSkills(long userId, List<Long> skillIds) {
        if (skillIds.isEmpty()) {
            return true;
        }
        return userRepository.countOwnedSkills(userId, skillIds) == skillIds.size();
    }

    @Transactional
    public void deactivateUser(long userId) {
        User user = getUserById(userId);

        if (!user.isActive()) {
            throw new DataValidationException("User with id: " + userId + " is already deactivated");
        }

        deleteActiveGoalsWithOneUser(user);
        discardActiveGoals(user);
        cancelAndDeleteOwnedPlannedEvents(user);
        discardPlannedGoals(user);

        userMapper.obfuscateUser(user);
        discardMentees(user);
    }

    private void deleteActiveGoalsWithOneUser(User user) {
        for (Goal goal : user.getGoals()) {
            if ((goal.getStatus() == GoalStatus.ACTIVE) && (goal.getUsers().size() == 1)) {
                goalService.deleteGoal(goal.getId());
            }
        }
    }

    private void discardActiveGoals(User user) {
        user.getGoals().removeIf(goal -> goal.getStatus() == GoalStatus.ACTIVE);
    }

    private void cancelAndDeleteOwnedPlannedEvents(User user) {
        for (Event event : user.getOwnedEvents()) {
            if (event.getStatus() == EventStatus.PLANNED
                    || event.getStatus() == EventStatus.IN_PROGRESS) {
                eventService.cancelEvent(event.getId());
            }
        }
    }

    private void discardPlannedGoals(User user) {
        user.getParticipatedEvents()
                .removeIf(
                        event -> event.getStatus() == EventStatus.PLANNED
                );
    }

    private void discardMentees(User user) {
        user.setMentees(new ArrayList<>());
    }

    public User getUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User by id: " + userId + " not found"));
    }
}
