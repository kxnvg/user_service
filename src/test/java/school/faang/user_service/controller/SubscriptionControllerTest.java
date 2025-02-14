package school.faang.user_service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SubscriptionService;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(value = {MockitoExtension.class})
public class SubscriptionControllerTest {

    @Mock
    private SubscriptionService subscriptionService;

    @InjectMocks
    private SubscriptionController subscriptionController;
    private User user1;
    private User user2;
    private UserFilterDto filter;

    @BeforeEach
    public void setUp() {
        long followerId = new Random().nextLong();
        long followeeId = new Random().nextLong();
        user1 = User.builder().id(followerId + 1).build();
        user2 = User.builder().id(followeeId + 1).build();
        filter = UserFilterDto.builder().build();
    }

    @Test
    public void selfSubscribe() {
        assertThrows(DataValidationException.class, () -> subscriptionController.followUser(user1.getId(), user1.getId()));
    }

    @Test
    public void userFollowedSuccess() {
        subscriptionController.followUser(user1.getId(), user2.getId());
        verify(subscriptionService, times(1)).followUser(user1.getId(), user2.getId());
    }

    @Test
    public void selfUnsubscribe(){
        assertThrows(DataValidationException.class, () -> subscriptionController.unfollowUser(user1.getId(), user1.getId()));
    }

    @Test
    public void userUnfollowSuccess() {
        subscriptionController.unfollowUser(user1.getId(), user2.getId());
        verify(subscriptionService, times(1)).unfollowUser(user1.getId(), user2.getId());
    }

    @Test
    public void getFollowersSuccess(){
        subscriptionController.getFollowers(user1.getId(), filter);
        verify(subscriptionService, times(1)).getFollowers(user1.getId(), filter);
    }

    @Test
    public void followersCount(){
        subscriptionController.getFollowersCount(3L);
        verify(subscriptionService).getFollowersCount(3L);
    }

    @Test
    public void getFollowingSuccess(){
        subscriptionController.getFollowing(user1.getId(), filter);
        verify(subscriptionService).getFollowing(user1.getId(), filter);
    }

    @Test
    public void followingCount(){
        subscriptionController.getFollowingCount(3L);
        verify(subscriptionService).getFollowingCount(3L);
    }
}
