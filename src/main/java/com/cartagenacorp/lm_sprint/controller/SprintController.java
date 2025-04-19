package com.cartagenacorp.lm_sprint.controller;

import com.cartagenacorp.lm_sprint.entity.Sprint;
import com.cartagenacorp.lm_sprint.service.IssueService;
import com.cartagenacorp.lm_sprint.service.SprintService;
import com.cartagenacorp.lm_sprint.util.RequiresPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/sprints")
public class SprintController {

    private final SprintService sprintService;
    private final IssueService issueService;

    @Autowired
    public SprintController(SprintService sprintService, IssueService issueService) {
        this.sprintService = sprintService;
        this.issueService = issueService;
    }

    @GetMapping("/{sprintId}")
    @RequiresPermission({"SPRINT_CRUD", "SPRINT_READ"})
    public ResponseEntity<?> getSprint(@PathVariable String sprintId) {
        UUID uuid = UUID.fromString(sprintId);
        return ResponseEntity.ok(sprintService.getSprintById(uuid));
    }

    @GetMapping("/project/{projectId}")
    @RequiresPermission({"SPRINT_CRUD", "SPRINT_READ"})
    public ResponseEntity<?> getAllSprintsByProject(@PathVariable String projectId) {
        UUID uuid = UUID.fromString(projectId);
        return ResponseEntity.ok(sprintService.getAllSprintsByProjectId(uuid));
    }

    @GetMapping("/{sprintId}/exists")
    public ResponseEntity<Boolean> sprintExists(@PathVariable String sprintId) {
        UUID uuid = UUID.fromString(sprintId);
        return ResponseEntity.ok(sprintService.sprintExist(uuid));
    }

    @PostMapping
    @RequiresPermission({"SPRINT_CRUD"})
    public ResponseEntity<?> createSprint(@RequestBody Sprint sprint) {
        return new ResponseEntity<>(sprintService.createSprint(sprint), HttpStatus.CREATED);
    }

    @PutMapping("/{sprintId}")
    @RequiresPermission({"SPRINT_CRUD"})
    public ResponseEntity<?> updateSprint(@PathVariable String sprintId, @RequestBody Sprint sprint) {
        UUID uuid = UUID.fromString(sprintId);
        return ResponseEntity.ok(sprintService.updateSprint(uuid, sprint));
    }

    @DeleteMapping("/{sprintId}")
    @RequiresPermission({"SPRINT_CRUD"})
    public ResponseEntity<?> deleteSprint(@PathVariable String sprintId) {
        UUID uuid = UUID.fromString(sprintId);
        sprintService.deleteSprint(uuid);
        return ResponseEntity.noContent().build();

    }

    @PostMapping("/{sprintId}/assign-issues")
    @RequiresPermission({"SPRINT_CRUD"})
    public ResponseEntity<Void> assignIssues(
            @PathVariable UUID sprintId,
            @RequestBody List<UUID> issueIds) {
        issueService.assignIssuesToSprint(sprintId, issueIds);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/remove-issues")
    @RequiresPermission({"SPRINT_CRUD"})
    public ResponseEntity<Void> removeIssues(
            @RequestBody List<UUID> issueIds) {
        issueService.removeIssuesFromSprint(issueIds);
        return ResponseEntity.ok().build();
    }

}
