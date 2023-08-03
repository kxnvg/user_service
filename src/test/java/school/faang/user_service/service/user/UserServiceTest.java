package school.faang.user_service.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.MentorshipService;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.service.goal.GoalService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private GoalService goalService;
    @Mock
    private EventService eventService;
    @Mock
    private MentorshipService mentorshipService;
    @InjectMocks
    @Spy
    private UserService userService;

    private User user;

    @BeforeEach
    public void init() {
        user = User.builder()
                .id(1L)
                .username("test")
                .email("a@a.com")
                .phone("8-800-555-35-35")
                .password("Qdsf32jsdfad")
                .active(true)
                .aboutMe("I love Java")
                .country(new Country())
                .city("Great Ustuk")
                .experience(5)
                .build();
    }

    @Test
    void areOwnedSkills() {
        assertTrue(userService.areOwnedSkills(1L, List.of()));
    }

    @Test
    void areOwnedSkillsFalse() {
        Mockito.when(userRepository.countOwnedSkills(1L, List.of(2L))).thenReturn(3);
        assertFalse(userService.areOwnedSkills(1L, List.of(2L)));
    }

    @Test
    void deactivateProfileValidTest() {
        User user2 = User.builder().id(2L).build();
        User user3 = User.builder().id(3L).build();

        Goal deactivateUserInGoal1 = Goal.builder()
                .id(1L)
                .status(GoalStatus.ACTIVE)
                .users(new ArrayList<>(Arrays.asList(user, user2, user3)))
                .build();
        Goal deactivateUserInGoal2 = Goal.builder()
                .id(2L)
                .status(GoalStatus.ACTIVE)
                .users(List.of(user))
                .build();
        Goal deactivateUserGoal = Goal.builder()
                .id(3L)
                .mentor(user)
                .build();

        Event deactivateUserEvent1 = Event.builder()
                .id(1L)
                .status(EventStatus.PLANNED)
                .owner(user)
                .build();
        Event deactivateUserEvent2 = Event.builder()
                .id(2L)
                .status(EventStatus.IN_PROGRESS)
                .owner(user)
                .build();
        Event deactivateUserInEvent = Event.builder()
                .id(3L)
                .status(EventStatus.PLANNED)
                .attendees(new ArrayList<>(Arrays.asList(user, user2)))
                .build();

        user.setGoals(new ArrayList<>(Arrays.asList(deactivateUserInGoal1, deactivateUserInGoal2)));
        user.setSetGoals(new ArrayList<>(Collections.singletonList(deactivateUserGoal)));
        user.setOwnedEvents(new ArrayList<>(Arrays.asList(deactivateUserEvent1, deactivateUserEvent2)));
        user.setParticipatedEvents(new ArrayList<>(Collections.singletonList(deactivateUserInEvent)));

        Mockito.doReturn(user).when(userService).getUserById(user.getId());

        userService.deactivateProfile(user.getId());

        Mockito.verify(userService, Mockito.atLeast(1))
                .getUserById(Mockito.anyLong());

        Mockito.verify(goalService, Mockito.atLeast(1))
                .deleteGoal(Mockito.anyLong());
        Mockito.verify(goalService, Mockito.times(1))
                .deleteGoal(deactivateUserInGoal2.getId());

        Mockito.verify(eventService, Mockito.times(1))
                .cancelEvent(Mockito.anyLong());
        Mockito.verify(eventService, Mockito.times(1))
                .cancelEvent(deactivateUserEvent1.getId());
        Mockito.verify(eventService, Mockito.times(1))
                .deleteEvent(Mockito.anyLong());
        Mockito.verify(eventService, Mockito.times(1))
                .deleteEvent(deactivateUserEvent1.getId());

        Mockito.verify(mentorshipService, Mockito.times(1))
                .deleteMentees(user.getId());

        assertEquals("****", user.getUsername());
        assertEquals(user.getSetGoals().size(), 0);
    }
}