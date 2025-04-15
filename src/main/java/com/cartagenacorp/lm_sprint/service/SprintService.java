package com.cartagenacorp.lm_sprint.service;

import com.cartagenacorp.lm_sprint.entity.Sprint;
import com.cartagenacorp.lm_sprint.repository.SprintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class SprintService {

    private final SprintRepository sprintRepository;
    private final ProjectValidationService projectValidationService;

    @Autowired
    public SprintService(SprintRepository sprintRepository, ProjectValidationService projectValidationService) {
        this.sprintRepository = sprintRepository;
        this.projectValidationService = projectValidationService;
    }

    @Transactional(readOnly = true)
    public Sprint getSprintById(UUID sprintId){
        return sprintRepository.findById(sprintId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sprint not found"));
    }

    @Transactional(readOnly = true)
    public List<Sprint> getAllSprintsByProjectId(UUID projectId){
        return sprintRepository.findByProjectId(projectId);
    }

    @Transactional
    public void deleteSprint(UUID sprintId){
        Sprint sprint = sprintRepository.findById(sprintId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sprint not found"));
        sprintRepository.delete(sprint);
    }

    @Transactional(readOnly = true)
    public boolean sprintExist(UUID sprintId){
        return sprintRepository.existsById(sprintId);
    }

    @Transactional
    public Sprint createSprint(Sprint sprint){
        if (!projectValidationService.validateProjectExists(sprint.getProjectId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The project ID provided is not valid");
        }
        if (sprint.getStartDate() != null && sprint.getEndDate() != null &&
                sprint.getEndDate().isBefore(sprint.getStartDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The end date must be after or equal to the start date");
        }
        return sprintRepository.save(sprint);
    }

    @Transactional
    public Sprint updateSprint(UUID sprintId, Sprint sprint){
        Sprint sprintSearch = sprintRepository.findById(sprintId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sprint not found"));

        if (sprint.getStartDate() != null && sprint.getEndDate() != null &&
                sprint.getEndDate().isBefore(sprint.getStartDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The end date must be after or equal to the start date");
        }

        sprintSearch.setProjectId(sprintSearch.getProjectId());
        sprintSearch.setTitle(sprint.getTitle());
        sprintSearch.setGoal(sprint.getGoal());
        sprintSearch.setStatus(sprint.getStatus());
        sprintSearch.setStartDate(sprint.getStartDate());
        sprintSearch.setEndDate(sprint.getEndDate());

        return sprintRepository.save(sprintSearch);
    }
}
