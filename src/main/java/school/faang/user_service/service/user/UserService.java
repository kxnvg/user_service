package school.faang.user_service.service.user;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.UserAlreadyDeactivated;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.MentorshipService;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final GoalService goalService;
    private final EventService eventService;
    private final MentorshipService mentorshipService;

    public UserService(UserRepository userRepository, GoalService goalService, @Lazy EventService eventService, MentorshipService mentorshipService) {
        this.userRepository = userRepository;
        this.goalService = goalService;
        this.eventService = eventService;
        this.mentorshipService = mentorshipService;
    }

    public boolean areOwnedSkills(long userId, List<Long> skillIds) {
        if (skillIds.isEmpty()) {
            return true;
        }
        return userRepository.countOwnedSkills(userId, skillIds) == skillIds.size();
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
            if (event.getStatus() == EventStatus.PLANNED) {
                eventService.cancelEvent(event.getId());
                eventService.deleteEvent(event.getId());
            }
        }
    }

    private void discardPlannedGoals(User user) {
        user.getParticipatedEvents().removeIf(event -> event.getStatus() == EventStatus.PLANNED);
    }

    private void discardSetGoals(User user) {
        user.getSetGoals().clear();
    }

    private void discardMentees(User user) {
        mentorshipService.deleteMentees(user.getId());
    }

    @Transactional
    public void deactivateUser(long userId) {
        User user = getUserById(userId);

        if (!user.isActive()) {
            throw new UserAlreadyDeactivated("User with id: " + userId + " is already deactivated");
        }

        deleteActiveGoalsWithOneUser(user);
        discardActiveGoals(user);
        cancelAndDeleteOwnedPlannedEvents(user);
        discardPlannedGoals(user);

        obfuscateUserData(user);
        discardSetGoals(user);
        discardMentees(user);
    }

    private void obfuscateUserData(User user) {
        user.setActive(false);
        user.setUsername("*".repeat(user.getUsername().length()));
        user.setEmail(user.getEmail().replaceAll("(\\w{1,2})(\\w+)(@.*)", "$1****$3"));
        user.setPhone(null);
        user.setAboutMe(null);
        user.setCity(null);
        user.setExperience(null);
        user.setUserProfilePic(null);
    }

    public User getUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User by id: " + userId + " not found"));
    }
}
