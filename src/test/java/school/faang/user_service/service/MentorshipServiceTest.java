package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class MentorshipServiceTest {
    @InjectMocks
    private MentorshipService mentorshipService;
    @Mock
    private MentorshipRepository mentorshipRepository;
    private User user;

    @BeforeEach
    public void init() {
        user = User.builder()
                .id(1L)
                .mentees(new ArrayList<>(Arrays.asList(
                        User.builder().id(2L).build(),
                        User.builder().id(3L).build())))
                .build();
    }

    @Test
    public void deleteMenteesValidTest() {
        long mentorId = user.getId();
        Mockito.when(mentorshipRepository.findById(mentorId)).thenReturn(Optional.ofNullable(user));
        mentorshipService.deleteMentees(mentorId);
        assertEquals(user.getMentees().size(), 0);
    }
}
