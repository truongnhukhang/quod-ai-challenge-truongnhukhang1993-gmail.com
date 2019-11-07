package ai.quod.challenge.domain.github;

import ai.quod.challenge.Main;
import ai.quod.challenge.converter.DateTimeConverter;

import static ai.quod.challenge.converter.GithubEventConverter.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GithubEvent {

  private static final Logger LOGGER = Logger.getLogger(GithubEvent.class.getClass().getName());

  String id;
  String type;
  String createAt;
  Repository repository;
  String actorLogin;
  Integer userPullRequestId;
  LocalDateTime commitDate;
  Integer commitSize;
  LocalDateTime issueCreateAt;
  LocalDateTime issueCloseAt;
  boolean isIssueEventAndIssueClosed;

  public GithubEvent(Map<String,Object> eventJson){
    try {
      this.type = (String) eventJson.get("type");
      this.id = (String) eventJson.get("id");
      this.repository = containRepositoryInfo(eventJson) ? getRepositoryFrom(eventJson) : null;
      this.actorLogin = getActorLoginFrom(eventJson);
      this.userPullRequestId = PULL_REQUEST_EVENT.equals(this.type) ? getPullRequestUserId(eventJson) : null;
      this.commitDate = PUSH_EVENT.equals(this.type) ? getCommitDateFrom(eventJson) : null;
      this.commitSize = PUSH_EVENT.equals(this.type) ? getCommitSizeFrom(eventJson) : null;
      if(ISSUE_EVENT.equals(this.type)) {
        Map<String,Object> issueInfo = getIssue(eventJson);
        this.issueCreateAt = issueInfo!=null && issueInfo.get("created_at")!=null ? DateTimeConverter.convertStringIS8601ToLocalDateTime((String) issueInfo.get("created_at")) : null;
        this.issueCloseAt = issueInfo!=null && issueInfo.get("closed_at")!=null ? DateTimeConverter.convertStringIS8601ToLocalDateTime((String) issueInfo.get("closed_at")) : null;
      }
      this.isIssueEventAndIssueClosed = isIssueEventAndIssueClosedFrom(eventJson);
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE,"Convert error : " + e.getMessage() + " id="+eventJson.get("id"));
    }
  }

  public Integer getCommitSize() {
    return commitSize;
  }

  public String getCreateAt() {
    return createAt;
  }

  public String getId() {
    return id;
  }

  public boolean isIssueEventAndIssueClosed() {
    return isIssueEventAndIssueClosed;
  }

  public String getType() {
    return type;
  }

  public Repository getRepository() {
    return repository;
  }

  public String getActorLogin() {
    return actorLogin;
  }

  public Integer getUserPullRequestId() {
    return userPullRequestId;
  }

  public LocalDateTime getCommitDate() {
    return commitDate;
  }

  public LocalDateTime getIssueCreateAt() {
    return issueCreateAt;
  }

  public LocalDateTime getIssueCloseAt() {
    return issueCloseAt;
  }

}
