package ai.quod.challenge.tranfomer.github.calculator;

import ai.quod.challenge.converter.DateTimeConverter;
import ai.quod.challenge.domain.github.GithubEvent;
import ai.quod.challenge.domain.github.Repository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

import static ai.quod.challenge.converter.GithubEventConverter.getIssue;
import static ai.quod.challenge.converter.GithubEventConverter.getRepositoryId;

/**
 * Created by truongnhukhang on 10/29/19.
 */
public class AverageTimeIssueOpenCalculator extends BaseCalculator {

  private Map<Long, AverageTimeIssueOpen> averageTimeIssueOpenRepos = null;
  private Double minTimeIssue = null;

  @Override
  public void initMetric() {
    minTimeIssue = 0.0;
    averageTimeIssueOpenRepos = new HashMap<>();
  }

  @Override
  public void metricCalculate(GithubEvent event) {
    if (event.isIssueEventAndIssueClosed()) {
      LocalDateTime issueCreateAt = event.getIssueCreateAt();
      LocalDateTime issueCloseAt = event.getIssueCloseAt();
      if(issueCloseAt!=null && issueCreateAt!=null) {
        Double issueTimeRemainOpen = (issueCloseAt.toEpochSecond(ZoneOffset.UTC) - issueCreateAt.toEpochSecond(ZoneOffset.UTC)) * 1.0 / 60 / 60;
        updateAverageTimeIssue(issueTimeRemainOpen, event);
        updateMinTimeIssueRemainOpen(issueTimeRemainOpen);
      }
    }
  }

  @Override
  public Repository healthScoreCalculate(Repository repository) {
    AverageTimeIssueOpen averageTimeIssueInfo = averageTimeIssueOpenRepos.get(repository.getId());
    double currentScore = repository.getHealthScore();
    if (averageTimeIssueInfo != null) {
      Double averageTime = averageTimeIssueInfo.averageTime;
      repository.setAverageHoursIssueRemainOpen(averageTime);
      if (averageTime > 0) {
        repository.setHealthScore(currentScore + minTimeIssue * 1.0 / averageTime);
      }
    } else {
      repository.setAverageHoursIssueRemainOpen(0.0);
    }
    return repository;
  }

  private void updateMinTimeIssueRemainOpen(Double issueTimeRemainOpen) {
    if (minTimeIssue.compareTo(issueTimeRemainOpen) > 0) {
      minTimeIssue = issueTimeRemainOpen;
    }
  }

  private void updateAverageTimeIssue(Double issueTimeRemainOpen, GithubEvent event) {
    Long repository = event.getRepository().getId();
    AverageTimeIssueOpen averageTimeIssue = averageTimeIssueOpenRepos.get(repository);
    if (averageTimeIssue == null) {
      averageTimeIssue = new AverageTimeIssueOpen();
      averageTimeIssue.issues = 1;
      averageTimeIssue.averageTime = issueTimeRemainOpen * 1.0;
      averageTimeIssue.totalTime = issueTimeRemainOpen;
    } else {
      Integer issues = averageTimeIssue.issues + 1;
      Double totalTime = averageTimeIssue.totalTime + issueTimeRemainOpen;
      Double averageTime = totalTime / issues;
      averageTimeIssue.issues = issues;
      averageTimeIssue.averageTime = averageTime;
      averageTimeIssue.totalTime = totalTime;
    }
    averageTimeIssueOpenRepos.put(repository, averageTimeIssue);
  }

  private class AverageTimeIssueOpen {
    Integer issues;
    Double totalTime;
    Double averageTime;

  }

}
