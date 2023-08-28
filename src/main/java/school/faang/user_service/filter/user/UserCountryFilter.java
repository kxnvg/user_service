package school.faang.user_service.filter.user;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.List;

@Component
public class UserCountryFilter implements UserFilter {
    @Override
    public boolean isApplicable(UserFilterDto filters) {
        return filters.getCountryPattern() != null && !filters.getCountryPattern().isBlank();
    }

    @Override
    public void apply(List<User> users, UserFilterDto filters) {
        users.removeIf(user -> !user.getCountry().getTitle().contains(filters.getCountryPattern()));
    }
}
