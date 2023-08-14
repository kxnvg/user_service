package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

@Service
@RequiredArgsConstructor
public class MentorshipService {
    private final MentorshipRepository mentorshipRepository;

    public void deleteMentees(long userId) {
        User mentor = findById(userId);
        mentor.getMentees().clear();
    }

    public User findById(long userId) {
        return mentorshipRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("Mentor by id: " + userId + " not found"));
    }
}
