package school.faang.user_service.dto.goal;

import lombok.*;
import school.faang.user_service.entity.goal.GoalStatus;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class GoalDto {
    private Long id;
    private String title;
    private String description;
    private Long parentId;
    private GoalStatus status;
    private List<Long> userIds;
    private List<Long> skillIds;
}
