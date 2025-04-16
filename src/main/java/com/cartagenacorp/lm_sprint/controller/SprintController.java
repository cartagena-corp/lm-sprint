package com.cartagenacorp.lm_sprint.controller;

import com.cartagenacorp.lm_sprint.entity.Sprint;
import com.cartagenacorp.lm_sprint.service.IssueService;
import com.cartagenacorp.lm_sprint.service.SprintService;
import com.cartagenacorp.lm_sprint.util.RequiresPermission;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
        try {
            UUID uuid = UUID.fromString(sprintId);
            return ResponseEntity.ok(sprintService.getSprintById(uuid));
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
        } catch (IllegalArgumentException ex){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/project/{projectId}")
    @RequiresPermission({"SPRINT_CRUD", "SPRINT_READ"})
    public ResponseEntity<?> getAllSprintsByProject(@PathVariable String projectId) {
        try {
            UUID uuid = UUID.fromString(projectId);
            return ResponseEntity.ok(sprintService.getAllSprintsByProjectId(uuid));
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
        } catch (IllegalArgumentException ex){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{sprintId}/exists")
    public ResponseEntity<Boolean> sprintExists(@PathVariable String sprintId) {
        try {
            UUID uuid = UUID.fromString(sprintId);
            return ResponseEntity.ok(sprintService.sprintExist(uuid));
        }  catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IllegalArgumentException ex){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    @RequiresPermission({"SPRINT_CRUD"})
    public ResponseEntity<?> createSprint(@RequestBody Sprint sprint) {
        try {
            return new ResponseEntity<>(sprintService.createSprint(sprint), HttpStatus.CREATED);
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
        } catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{sprintId}")
    @RequiresPermission({"SPRINT_CRUD"})
    public ResponseEntity<?> updateSprint(@PathVariable String sprintId, @RequestBody Sprint sprint) {
        try {
            UUID uuid = UUID.fromString(sprintId);
            return ResponseEntity.ok(sprintService.updateSprint(uuid, sprint));
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
        }  catch (IllegalArgumentException ex){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }  catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{sprintId}")
    @RequiresPermission({"SPRINT_CRUD"})
    public ResponseEntity<?> deleteSprint(@PathVariable String sprintId) {
        try {
            UUID uuid = UUID.fromString(sprintId);
            sprintService.deleteSprint(uuid);
            return ResponseEntity.noContent().build();
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
        }  catch (IllegalArgumentException ex){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
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
