package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.SkillOffer;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SkillService {

    private final SkillOfferRepository skillOfferRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final SkillRepository skillRepository;
    private final SkillMapper mapper;
    private final UserRepository userRepository;
    private static final int MIN_SKILL_OFFERS = 3;


    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        return skillOfferRepository.findAllByUserId(userId)
                .stream()
                .collect(Collectors.groupingBy(SkillOffer::getSkill, Collectors.counting()))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(entry -> mapper.toDto(entry.getKey()), Map.Entry::getValue))
                .entrySet()
                .stream()
                .map(entry -> new SkillCandidateDto(entry.getKey(), entry.getValue()))
                .toList();
    }

    public SkillDto acquireSkillFromOffers(long skillId, long userId) {
        Optional<Skill> userSkill = skillRepository.findUserSkill(skillId, userId);
        if (userSkill.isPresent()) {
            return null;
        }
        List<SkillOffer> allOffersOfSkill = skillOfferRepository.findAllOffersOfSkill(skillId, userId);
        if (allOffersOfSkill.size() >= MIN_SKILL_OFFERS) {
            skillRepository.assignSkillToUser(skillId, userId);
            Skill skill = skillRepository.findById(skillId).get();
            addGuarantees(skill, allOffersOfSkill, userId);
            return mapper.toDto(skill);
        }
        throw new DataValidationException("Not enough offers to add skill");
    }

    public List<SkillDto> getUserSkills(long userId) {
        verifyUserExist(userId);
        List<Skill> allByUserId = skillRepository.findAllByUserId(userId);
        return mapSkillsToDtos(allByUserId);
    }

    public SkillDto create(SkillDto skill) {
        SkillDto processedTitle = new SkillDto(skill.getId(), processTitle(skill.getTitle()));
        if (skillRepository.existsByTitle(processedTitle.getTitle())) {
            throw new DataValidationException("This skill already exist");
        }
        Skill savedSkill = skillRepository.save(mapper.toEntity(processedTitle));
        return mapper.toDto(savedSkill);
    }

    private void verifyUserExist(long userId) {
        if (!(userRepository.existsById(userId))) {
            throw new EntityNotFoundException(String.format("User with id=%d doesn't exist", userId));
        }
    }

    private String processTitle(String title) {
        title = title.replaceAll("[^A-Za-zА-Яа-я0-9+-/#]", " ");
        title = title.replaceAll("[\\s]+", " ");
        return title.trim().toLowerCase();
    }

    private List<SkillDto> mapSkillsToDtos(List<Skill> skills) {
        return skills.stream()
                .map(mapper::toDto)
                .toList();
    }

    private void addGuarantees(Skill skill, List<SkillOffer> offers, long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new EntityNotFoundException(String.format("User with id=%d doesn't exist", userId));
        }
        User user = optionalUser.get();

        List<UserSkillGuarantee> listGuarantees = offers.stream().map(skillOffer ->
                        UserSkillGuarantee.builder()
                                .user(user)
                                .skill(skill)
                                .guarantor(skillOffer.getRecommendation().getAuthor())
                                .build())
                .toList();

        listGuarantees.forEach((userSkillGuaranteeRepository::save));

        if (skill.getGuarantees() == null) {
            skill.setGuarantees(listGuarantees);
        } else {
            skill.getGuarantees().addAll(listGuarantees);
        }
    }
}
