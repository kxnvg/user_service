package school.faang.user_service.service;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.commonMessages.ErrorMessages;
import school.faang.user_service.filters.UserFilterDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.contact.Contact;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.filters.UserFilter;
import school.faang.user_service.repository.SubscriptionRepository;

import java.util.List;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {
    @Mock
    private SubscriptionRepository subscriptionRepository;
    @Mock
    private UserFilter userFilter;
    @InjectMocks
    private SubscriptionService subscriptionService;

    //positive
    @Test
    void followUserCallRepositoryMethod(){
        int followerId = 11;
        int followeeId = 15;
        subscriptionService.followUser(followerId, followeeId);
        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .followUser(followerId, followeeId);
    }
    @Test
    void unfollowUserCallRepositoryMethod(){
        int followerId = 11;
        int followeeId = 15;
        subscriptionService.unfollowUser(followerId, followeeId);
        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .unfollowUser(followerId, followeeId);
    }

    @Test
    void getFollowersCallRepositoryMethod(){
        long followeeId = 15;
        UserFilterDto filterDto = createUserFilterDto();
        Mockito.when(subscriptionRepository.findByFolloweeId(followeeId))
                .thenReturn(Stream.of(createUser()));
        subscriptionService.getFollowers(followeeId, filterDto);
        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .findByFolloweeId(followeeId);
    }

    @Test
    void getFollowersCountCallRepositoryMethod(){
        int userID = 15;
        int amountOfFollowers = 50;
        Mockito.when(subscriptionRepository.findFollowersAmountByFolloweeId(userID))
                        .thenReturn(amountOfFollowers);
        subscriptionService.getFollowersCount(userID);
        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .findFollowersAmountByFolloweeId(userID);
    }

    @Test
    void getFollowingCallRepositoryMethod(){
        long followeeId = 15;
        UserFilterDto filterDto = createUserFilterDto();
        Mockito.when(subscriptionRepository.findByFolloweeId(followeeId))
                .thenReturn(Stream.of(createUser()));
        subscriptionService.getFollowers(followeeId, filterDto);
        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .findByFolloweeId(followeeId);
    }

    @Test
    void getFollowingCountCallRepositoryMethod(){
        long followeeId = 15;
        int amountOfFollowers = 50;
        Mockito.when(subscriptionRepository.findFolloweesAmountByFollowerId(followeeId))
                .thenReturn(amountOfFollowers);
        subscriptionService.getFollowingCount(followeeId);
        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .findFolloweesAmountByFollowerId(followeeId);
    }

    //Exceptions
    @Test
    void followUserThrowIllegalException(){
        int followerId = -11;
        int followeeId = -15;
        IllegalArgumentException e = Assert.assertThrows(IllegalArgumentException.class,
                ()-> subscriptionService.followUser(followerId, followeeId));
        Assertions.assertEquals(ErrorMessages.NEGATIVE_ID, e.getMessage());
    }
    @Test
    void followUserThrowDataValidException() {
        int idUser = 11;
        Assert.assertThrows(DataValidationException.class,
                ()-> subscriptionService.followUser(idUser, idUser));
    }

    @ParameterizedTest
    @CsvSource({"-10", "0"})
    void getFollowersThrowIllegalException(long idUser){
        Assert.assertThrows(IllegalArgumentException.class,
                () -> subscriptionService.getFollowers(idUser, new UserFilterDto()));
    }

    private UserFilterDto createUserFilterDto(){
        UserFilterDto userFilterDto = new UserFilterDto();
        userFilterDto.setNamePattern("\\D+");
        userFilterDto.setAboutPattern("\\D+");
        userFilterDto.setEmailPattern("\\D+@\\D+");
        userFilterDto.setContactPattern("\\D+");
        userFilterDto.setCountryPattern("\\D+");
        userFilterDto.setCityPattern("\\D+");
        userFilterDto.setPhonePattern("\\d+");
        userFilterDto.setSkillPattern("\\D+");
        userFilterDto.setExperienceMin(1);
        userFilterDto.setExperienceMax(7);
        return userFilterDto;
    }
    private User createUser(){
        User user = new User();
        user.setUsername("Username");
        user.setEmail("user@mail.com");
        user.setPhone("89991324567");
        user.setAboutMe("I'm interesting person");
        user.setCountry(createCountry());
        user.setCity("UserCity");
        user.setExperience(4);
        user.setContacts(createContacts());
        user.setSkills(createSkills());
        return user;
    }

    private Country createCountry(){
        Country country = new Country();
        country.setTitle("UserCountry");
        return country;
    }
    private List<Contact> createContacts(){
        Contact contact = new Contact();
        Contact contact2 = new Contact();
        contact.setContact("UserContact");
        contact2.setContact("UserContact");
        return List.of(contact, contact2);
    }
    private List<Skill> createSkills(){
        Skill skill = new Skill();
        Skill skill2 = new Skill();
        skill.setTitle("userSkill");
        skill2.setTitle("userSkill");
        return List.of(skill, skill2);
    }
}