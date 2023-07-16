package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.exeption.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;

    public void followUser(long followerId, long followeeId) {
        if (isNotValid(followerId, followeeId)) {
            throw new DataValidationException(followerId, followeeId);
        }
        subscriptionRepository.followUser(followerId, followeeId);
    }

    public void unfollowUser(long followerId, long followeeId) {
        if (!isNotValid(followerId, followeeId)) {
            throw new DataValidationException(String.format("User with id %d doesn't follow user with id %d", followerId, followeeId));
        }
        subscriptionRepository.unfollowUser(followerId, followeeId);
    }

    public boolean isNotValid(long followerId, long followeeId) {
        return subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId);
    }
}
