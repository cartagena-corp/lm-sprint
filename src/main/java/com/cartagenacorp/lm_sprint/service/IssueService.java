package com.cartagenacorp.lm_sprint.service;

import com.cartagenacorp.lm_sprint.dto.AssignRequest;
import com.cartagenacorp.lm_sprint.dto.RemoveRequest;
import com.cartagenacorp.lm_sprint.util.JwtContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class IssueService {

    @Value("${issues.service.url}")
    private String issueServiceUrl;

    private final RestTemplate restTemplate;

    @Autowired
    public IssueService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void assignIssuesToSprint(UUID sprintId, List<UUID> issueIds) {
        String token = JwtContextHolder.getToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<AssignRequest> entity = new HttpEntity<>(new AssignRequest(sprintId, issueIds), headers);
        try {
            restTemplate.postForEntity(issueServiceUrl + "/assign", entity, Void.class);
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            throw new ResponseStatusException(ex.getStatusCode(), "Error assigning issues: " + ex.getResponseBodyAsString());
        } catch (ResourceAccessException ex) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Issue service is not available");
        }

    }

    public void removeIssuesFromSprint(List<UUID> issueIds) {
        String token = JwtContextHolder.getToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<RemoveRequest> entity = new HttpEntity<>(new RemoveRequest(issueIds), headers);

        try {
            restTemplate.postForEntity(issueServiceUrl + "/remove", entity, Void.class);
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            throw new ResponseStatusException(ex.getStatusCode(), "Error removing issues: " + ex.getResponseBodyAsString());
        } catch (ResourceAccessException ex) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Issue service is not available");
        }
    }
}
