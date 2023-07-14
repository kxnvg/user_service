package school.faang.user_service.filters;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserFilterTest {
    UserFilter userFilter = new UserFilter();

    @Test
    void testFilterUsersWrongName() {
        User user = User.builder().username("John").build();
        Stream<User> userStream = Stream.of(user);
        UserFilterDto userFilterDto = UserFilterDto.builder().namePattern("Name").build();
        List<User> userList = userFilter.filterUsers(userStream, userFilterDto);
        assertEquals(0, userList.size());
    }

    @Test
    void testFilterUsersWrongAbout() {
        User user = User.builder().aboutMe("").build();
        Stream<User> userStream = Stream.of(user);
        UserFilterDto userFilterDto = UserFilterDto.builder().aboutPattern("I am a student").build();
        List<User> userList = userFilter.filterUsers(userStream, userFilterDto);
        assertEquals(0, userList.size());
    }

    @Test
    void testFilterUsersWrongEmail() {
        User user = User.builder().email(" ").build();
        Stream<User> userStream = Stream.of(user);
        UserFilterDto userFilterDto = UserFilterDto.builder().emailPattern("email@example").build();
        List<User> userList = userFilter.filterUsers(userStream, userFilterDto);
        assertEquals(0, userList.size());
    }

    @Test
    void testFilterUsersWrongContact() {
        User user = User.builder().contacts(Collections.emptyList()).build();
        Stream<User> userStream = Stream.of(user);
        UserFilterDto userFilterDto = UserFilterDto.builder().contactPattern("123456789").build();
        List<User> userList = userFilter.filterUsers(userStream, userFilterDto);
        assertEquals(0, userList.size());
    }

    @Test
    void testFilterUsersWrongCountry() {
        User user = User.builder().country(new Country(1L, " ", Collections.emptyList())).build();
        Stream<User> userStream = Stream.of(user);
        UserFilterDto userFilterDto = UserFilterDto.builder().countryPattern("Peace").build();
        List<User> userList = userFilter.filterUsers(userStream, userFilterDto);
        assertEquals(0, userList.size());
    }

    @Test
    void testFilterUsersWrongCity() {
        User user = User.builder().city("City").build();
        Stream<User> userStream = Stream.of(user);
        UserFilterDto userFilterDto = UserFilterDto.builder().cityPattern("Town").build();
        List<User> userList = userFilter.filterUsers(userStream, userFilterDto);
        assertEquals(0, userList.size());
    }

    @Test
    void testFilterUsersWrongPhone() {
        User user = User.builder().phone("1234567").build();
        Stream<User> userStream = Stream.of(user);
        UserFilterDto userFilterDto = UserFilterDto.builder().phonePattern("123").build();
        List<User> userList = userFilter.filterUsers(userStream, userFilterDto);
        assertEquals(0, userList.size());
    }

    @Test
    void testFilterUsersWrongSkill() {
        User user = User.builder().skills(Collections.emptyList()).build();
        Stream<User> userStream = Stream.of(user);
        UserFilterDto userFilterDto = UserFilterDto.builder().skillPattern("Faster").build();
        List<User> userList = userFilter.filterUsers(userStream, userFilterDto);
        assertEquals(0, userList.size());
    }

    @Test
    void testFilterUsersWrongExperienceMin() {
        User user = User.builder().experience(10).build();
        Stream<User> userStream = Stream.of(user);
        UserFilterDto userFilterDto = UserFilterDto.builder().experienceMin(11).build();
        List<User> userList = userFilter.filterUsers(userStream, userFilterDto);
        assertEquals(0, userList.size());
    }

    @Test
    void testFilterUsersWrongExperienceMax() {
        User user = User.builder().experience(10).build();
        Stream<User> userStream = Stream.of(user);
        UserFilterDto userFilterDto = UserFilterDto.builder().experienceMax(5).build();
        List<User> userList = userFilter.filterUsers(userStream, userFilterDto);
        assertEquals(0, userList.size());
    }

    @Test
    void testFilterUsersRightAllParameters() {
        User user = User.builder()
                .username("John")
                .aboutMe("I am a student")
                .email("email@example")
                .contacts(Collections.emptyList())
                .country(new Country(1L, "Peace", Collections.emptyList()))
                .city("Town")
                .phone("1234567")
                .skills(Collections.emptyList())
                .experience(10)
                .build();
        Stream<User> userStream = Stream.of(user);
        UserFilterDto userFilterDto = UserFilterDto.builder()
                .namePattern("John")
                .aboutPattern("I am a student")
                .emailPattern("email@example")
                .contactPattern("")
                .countryPattern("Peace")
                .cityPattern("Town")
                .phonePattern("1234567")
                .skillPattern("")
                .experienceMin(5)
                .experienceMax(15)
                .build();
        List<User> userList = userFilter.filterUsers(userStream, userFilterDto);
        assertEquals(1, userList.size());
    }
}