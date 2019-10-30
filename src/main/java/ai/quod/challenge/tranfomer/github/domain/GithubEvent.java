package ai.quod.challenge.tranfomer.github.domain;

import ai.quod.challenge.converter.DateTimeConverter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is use to get data from Map<String,Object> githubEvent
 */
public class GithubEvent {

  public static final String TYPE = "type";

  public static final String PUSH_EVENT = "PushEvent";
  public static final String PULL_REQUEST_EVENT = "PullRequestEvent";
  public static final String ISSUE_EVENT = "IssuesEvent";
  public static final String ISSUE_CLOSED = "closed";

  public static Long getRepositoryId(Map<String, Object> event) {
    Map<String,Object> repo = (Map<String, Object>) event.get("repo");
    if(repo==null) {
      repo = (Map<String, Object>) event.get("repository");
    }
    return Long.valueOf((Integer) repo.get("id"));
  }

  public static boolean isIssueEventAndIssueClosed(Map<String,Object> event) {
    if(!ISSUE_EVENT.equals(event.get(TYPE))) {
      return false;
    }
    Map<String, Object> payload = getPayload(event);
    String action = (String) payload.get("action");
    return ISSUE_CLOSED.equals(action);
  }

  public static Map<String,Object> getIssue(Map<String,Object> issueEvent) {
    Map<String, Object> payload = getPayload(issueEvent);
    try {
      return  (Map<String, Object>) payload.get("issue");
    } catch (ClassCastException e) {
      return null;
    }
  }

  private static Map<String, Object> getPayload(Map<String, Object> githubEvent) {
    return (Map<String, Object>) githubEvent.get("payload");
  }

  public static String getActorLogin(Map<String,Object> githubEvent) {
    Map<String,Object> actor = (Map<String, Object>) githubEvent.get("actor_attributes");
    if(actor==null) {
      actor = (Map<String, Object>) githubEvent.get("actor");
    }
    return (String) actor.get("login");
  }

  public static Map<String,Object> getPullRequestUser(Map<String,Object> pullRequestEvent) {
    Map<String, Object> payload = getPayload(pullRequestEvent);
    Map<String,Object> pullRequest = (Map<String, Object>) payload.get("pull_request");
    return (Map<String, Object>) pullRequest.get("user");
  }

  public static Integer getCommitSize(Map<String, Object> event) {
    Map<String, Object> payload = getPayload(event);
    return (Integer) payload.get("size");
  }

  public static LocalDateTime getCommitDate(Map<String,Object> event) {
    String create_at = (String) event.get("created_at");
    return DateTimeConverter.convertStringIS8601ToLocalDateTime(create_at);
  }

  public static Map<String, Object> getRepository(Map<String, Object> githubEvent) {
    Map<String,Object> project = new HashMap<>();
    Map<String,Object> repo = (Map<String, Object>) githubEvent.get("repo");
    Long id = 0L;
    String repoOrg = "";
    String repoName = "";
    if(repo!=null) {
      id = Long.valueOf((Integer) repo.get("id"));
      repoOrg = ((String) repo.get("name")).split("/")[0];
      repoName = ((String) repo.get("name")).split("/")[1];
    } else {
      repo = (Map<String, Object>) githubEvent.get("repository");
      id = Long.valueOf((Integer) repo.get("id"));
      repoOrg = (String) repo.get("owner");
      repoName = (String) repo.get("name");
    }
    project.put("id",id);
    project.put("org",repoOrg);
    project.put("repo_name",repoName);
    project.put("health_score",0.0);
    return project;
  }

  public static boolean containRepositoryInfo(Map<String, Object> githubEvent) {
    Map<String,Object> repo = (Map<String, Object>) (githubEvent.get("repo")==null ? githubEvent.get("repository") : githubEvent.get("repo"));
    return repo!=null && repo.get("id")!=null;
  }
}
