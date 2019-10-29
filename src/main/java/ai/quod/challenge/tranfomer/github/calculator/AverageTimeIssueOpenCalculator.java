package ai.quod.challenge.tranfomer.github.calculator;

import ai.quod.challenge.converter.DateTimeConverter;

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
public class AverageTimeIssueOpenCalculator extends BaseCalculator
    implements Consumer<Map<String,Object>>, Function<Map<String,Object>,Map<String,Object>> {

  private static final String MIN_TIME_ISSUE_REMAIN_OPEN = "minTimeOpen";
  private static final String AVERAGE_TIME_ISSUE_OPEN_EACH_REPO = "averageTimeIssueOpen";

  @Override
  public void initMetric() {
    calculateResult.put(MIN_TIME_ISSUE_REMAIN_OPEN,Integer.MAX_VALUE);
    calculateResult.put(AVERAGE_TIME_ISSUE_OPEN_EACH_REPO,new HashMap<Long,HashMap<String,Object>>());
  }

  @Override
  public void accept(Map<String, Object> event) {
    if(isIssueEventAndIssueClosed(event)) {
      Map<String,Object> issueInfo = getIssue(event);
      LocalDateTime issueCreateAt = DateTimeConverter.convertStringIS8601ToLocalDateTime((String) issueInfo.get("created_at"));
      LocalDateTime issueCloseAt = DateTimeConverter.convertStringIS8601ToLocalDateTime((String) issueInfo.get("closed_at"));
      Double issueTimeRemainOpen = (issueCloseAt.toEpochSecond(ZoneOffset.UTC)-issueCreateAt.toEpochSecond(ZoneOffset.UTC))*1.0/60/60;
      updateAverageTimeIssue(issueTimeRemainOpen,event);
    }
  }

  private void updateAverageTimeIssue(Double issueTimeRemainOpen, Map<String, Object> event) {
    Map<Long,HashMap<String,Object>> averageTimeIssueOpenRepos = (Map<Long, HashMap<String, Object>>) calculateResult.get(AVERAGE_TIME_ISSUE_OPEN_EACH_REPO);
    Long repository = getRepositoryId(event);
    Map<String,Object> averageTimeIssue = averageTimeIssueOpenRepos.get(repository);
    if(averageTimeIssue==null) {
      averageTimeIssue = new HashMap<>();
      averageTimeIssue.put("issues",1);
      averageTimeIssue.put("averageTime",issueTimeRemainOpen);
      averageTimeIssue.put("totalTime",issueTimeRemainOpen);
    } else {
      Integer issues = (Integer) averageTimeIssue.get("issues");
      issues = issues+1;
      Double totalTime = (Double) averageTimeIssue.get("totalTime");
      totalTime = totalTime + issueTimeRemainOpen;
      Double averageTime = totalTime/issues;
      averageTimeIssue.put("issues",issues);
      averageTimeIssue.put("averageTime",averageTime);
      averageTimeIssue.put("totalTime",totalTime);
    }
  }

  @Override
  public Map<String, Object> apply(Map<String, Object> stringObjectMap) {
    return null;
  }
}
