package ai.quod.challenge.tranfomer.github.calculator;

import ai.quod.challenge.converter.DateTimeConverter;
import ai.quod.challenge.tranfomer.github.domain.Repository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import static ai.quod.challenge.tranfomer.github.domain.GithubEvent.*;
/**
 * Created by truongnhukhang on 10/29/19.
 */
public class AverageTimeIssueOpenCalculator extends BaseCalculator {

  private static final String MIN_TIME_ISSUE_REMAIN_OPEN = "minTimeOpen";
  private static final String AVERAGE_TIME_ISSUE_OPEN_EACH_REPO = "averageTimeIssueOpen";

  @Override
  public void initMetric() {
    calculateResult.put(MIN_TIME_ISSUE_REMAIN_OPEN,Integer.MAX_VALUE*1.0);
    calculateResult.put(AVERAGE_TIME_ISSUE_OPEN_EACH_REPO,new HashMap<Long,AverageTimeIssueOpen>());
  }

  @Override
  public void metricCalculate(Map<String, Object> event) {
    if(isIssueEventAndIssueClosed(event)) {
      Map<String,Object> issueInfo = getIssue(event);
      if(issueInfo!=null) {
        LocalDateTime issueCreateAt = DateTimeConverter.convertStringIS8601ToLocalDateTime((String) issueInfo.get("created_at"));
        LocalDateTime issueCloseAt = DateTimeConverter.convertStringIS8601ToLocalDateTime((String) issueInfo.get("closed_at"));
        Double issueTimeRemainOpen = (issueCloseAt.toEpochSecond(ZoneOffset.UTC)-issueCreateAt.toEpochSecond(ZoneOffset.UTC))*1.0/60/60;
        updateAverageTimeIssue(issueTimeRemainOpen,event);
        updateMinTimeIssueRemainOpen(issueTimeRemainOpen);
      }
    }
  }

  @Override
  public Repository healthScoreCalculate(Repository repository) {
    Map<Long,AverageTimeIssueOpen> averageTimeIssueOpenRepos = (Map<Long, AverageTimeIssueOpen>) calculateResult.get(AVERAGE_TIME_ISSUE_OPEN_EACH_REPO);
    AverageTimeIssueOpen averageTimeIssueInfo = averageTimeIssueOpenRepos.get(repository.getId());
    double currentScore = repository.getHealthScore();
    if(averageTimeIssueInfo!=null) {
      Double averageTime = averageTimeIssueInfo.averageTime;
      repository.setAverageHoursIssueRemainOpen(averageTime);
      Double minTimeIssue = (Double) calculateResult.get(MIN_TIME_ISSUE_REMAIN_OPEN);
      if(averageTime>0) {
        repository.setHealthScore(currentScore+minTimeIssue*1.0/averageTime);
      }
    } else {
      repository.setAverageHoursIssueRemainOpen(0.0);
    }
    return repository;
  }

  private void updateMinTimeIssueRemainOpen(Double issueTimeRemainOpen) {
    Double minIssue = (Double) calculateResult.get(MIN_TIME_ISSUE_REMAIN_OPEN);
    if(minIssue.compareTo(issueTimeRemainOpen)>0) {
      calculateResult.put(MIN_TIME_ISSUE_REMAIN_OPEN,issueTimeRemainOpen);
    }
  }

  private void updateAverageTimeIssue(Double issueTimeRemainOpen, Map<String, Object> event) {
    Map<Long,AverageTimeIssueOpen> averageTimeIssueOpenRepos = (Map<Long, AverageTimeIssueOpen>) calculateResult.get(AVERAGE_TIME_ISSUE_OPEN_EACH_REPO);
    Long repository = getRepositoryId(event);
    AverageTimeIssueOpen averageTimeIssue = averageTimeIssueOpenRepos.get(repository);
    if(averageTimeIssue==null) {
      averageTimeIssue = new AverageTimeIssueOpen();
      averageTimeIssue.issues=1;
      averageTimeIssue.averageTime=issueTimeRemainOpen*1.0;
      averageTimeIssue.totalTime = issueTimeRemainOpen;
    } else {
      Integer issues = averageTimeIssue.issues+1;
      Double totalTime = averageTimeIssue.totalTime + issueTimeRemainOpen;
      Double averageTime = totalTime/issues;
      averageTimeIssue.issues = issues;
      averageTimeIssue.averageTime = averageTime;
      averageTimeIssue.totalTime = totalTime;
    }
    averageTimeIssueOpenRepos.put(repository,averageTimeIssue);
  }

  private class AverageTimeIssueOpen {
    Integer issues;
    Double totalTime;
    Double averageTime;

  }

}
