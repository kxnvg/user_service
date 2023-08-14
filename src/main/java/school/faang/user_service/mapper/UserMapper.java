package school.faang.user_service.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    User toEntity(UserDto userDto);

    UserDto toDto(User user);

    default void obfuscateUser(User user) {
        user.setActive(false);
        user.setUsername("*".repeat(user.getUsername().length()));
        user.setEmail(user.getEmail().replaceAll("(\\w{1,2})(\\w+)(@.*)", "$1****$3"));
        user.setPhone(null);
        user.setAboutMe(null);
        user.setCity(null);
        user.setExperience(null);
        user.setUserProfilePic(null);
        user.getSetGoals().clear();
    }
}