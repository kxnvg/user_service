package school.faang.user_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.goal.Goal;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "skill")
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "title", length = 64, nullable = false, unique = true)
    private String title;

    @ManyToMany(mappedBy = "skills")
    private List<User> users;

    @ManyToMany(mappedBy = "guaranteedSkills")
    private List<User> guarantees;

    @ManyToMany(mappedBy = "relatedSkills")
    private List<Event> events;

    @ManyToMany(mappedBy = "skillsToAchieve")
    private List<Goal> goals;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public void addGuarantees(List<User> users) {
        guarantees.addAll(users);
    }

    public void addGuarantee(User user) {
        guarantees.add(user);
    }
}