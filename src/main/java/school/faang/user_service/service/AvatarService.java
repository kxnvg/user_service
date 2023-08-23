package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.event.EventMapper;

@Service
@RequiredArgsConstructor
public class AvatarService {
    private final EventMapper eventMapper;

    @Value("${dicebear.url}")
    private String diceBearApiUrl;

    public String getRandomAvatar() throws IOException {

        String uniqueFilename = UUID.randomUUID().toString() + ".svg";

        String avatarUrl = diceBearApiUrl + "/" + uniqueFilename;

        URL url = new URL(avatarUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (InputStream inputStream = connection.getInputStream()) {

                UserDto currentUserDto = userService.getCurrentUser();
                if (currentUserDto != null) {
                    User user = userMapper.toEntity(currentUserDto);

                    saveAvatarToDatabase(user.getId(), avatarUrl);
                }
            }
        }

        return avatarUrl;
    }

    public static void saveAvatarToDatabase(Long userId, String avatarUrl) {
    }

    saveAvatarByUserId

}