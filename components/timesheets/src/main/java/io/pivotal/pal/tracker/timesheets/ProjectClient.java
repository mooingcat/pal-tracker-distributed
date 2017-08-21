package io.pivotal.pal.tracker.timesheets;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestOperations;

import java.util.concurrent.ConcurrentHashMap;

public class ProjectClient {
    private final RestOperations restOperations;
    private final String registrationServerEndpoint;
    private final ConcurrentHashMap<Long, ProjectInfo> projects = new ConcurrentHashMap<>();
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public ProjectClient(RestOperations restOperations, String registrationServerEndpoint) {
        this.restOperations= restOperations;
        this.registrationServerEndpoint = registrationServerEndpoint;
    }

    @HystrixCommand(fallbackMethod = "getProjectFromCache")
    public ProjectInfo getProject(long projectId) {
        ProjectInfo project = restOperations.getForObject(registrationServerEndpoint + "/projects/" + projectId, ProjectInfo.class);
        projects.put(projectId, project);
        return project;
    }

    public ProjectInfo getProjectFromCache(long projectId) {
        logger.info("Getting project with id {} from cache", projectId);
        return projects.get(projectId);
    }
}
