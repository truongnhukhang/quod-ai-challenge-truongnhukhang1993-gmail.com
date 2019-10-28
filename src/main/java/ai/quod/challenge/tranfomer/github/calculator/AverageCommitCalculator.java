package ai.quod.challenge.tranfomer.github.calculator;

import static ai.quod.challenge.tranfomer.github.domain.GithubEvent.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class AverageCommitCalculator extends BaseCalculator
    implements Consumer<Map<String,Object>>, Function<Map<String,Object>,Map<String,Object>> {

  private static final String MAX_AVERAGE_COMMIT_PER_DAY = "maxAverage";
  private static final String AVERAGE_COMMIT_PER_DAY_BY_REPO_ID = "average";
  @Override
  public void initMetric() {
    calculateResult.put(MAX_AVERAGE_COMMIT_PER_DAY,0.0);
    calculateResult.put(AVERAGE_COMMIT_PER_DAY_BY_REPO_ID,new HashMap<Long,Map<String,Object>>());
  }

  @Override
  public void accept(Map<String, Object> event) {
    if(PUSH_EVENT.equals(event.get(TYPE))) {
      Double currentAverageCommitOfRepo = updateAverageCommitOfRepo(event);
      updateMaxAverageCommit(currentAverageCommitOfRepo);
    }
  }

  private void updateMaxAverageCommit(Double currentAverageCommitOfRepo) {
    Double maxAverageCommit = (Double) calculateResult.get(MAX_AVERAGE_COMMIT_PER_DAY);
    if(maxAverageCommit.compareTo(currentAverageCommitOfRepo) < 0) {
      calculateResult.put(MAX_AVERAGE_COMMIT_PER_DAY,currentAverageCommitOfRepo);
    }
  }

  private Double updateAverageCommitOfRepo(Map<String, Object> event) {
    Map<Long,Map<String,Object>> averageCommitPerDayInfoByRepo = (Map<Long, Map<String, Object>>) calculateResult.get(AVERAGE_COMMIT_PER_DAY_BY_REPO_ID);
    Long repository = getRepositoryId(event);
    Map<String,Object> averageCommitPerDayInfo = averageCommitPerDayInfoByRepo.get(repository);
    if(averageCommitPerDayInfo==null) {
      averageCommitPerDayInfo = new HashMap<>();
      averageCommitPerDayInfo.put("numPush",1);
      averageCommitPerDayInfo.put("numDays",1);
      averageCommitPerDayInfo.put("lastPushDate",getCommitDate(event).toLocalDate());
    } else {
      Integer numPush = (Integer) averageCommitPerDayInfo.get("numPush");
      LocalDate pushDate = getCommitDate(event).toLocalDate();
      LocalDate lastPushDate = (LocalDate) averageCommitPerDayInfo.get("lastPushDate");
      if(pushDate.isAfter(lastPushDate)) {
        Integer numDays = (Integer) averageCommitPerDayInfo.get("numDays");
        averageCommitPerDayInfo.put("numPush",numPush+1);
        averageCommitPerDayInfo.put("numDays",numDays+1);
        averageCommitPerDayInfo.put("lastPushDate",getCommitDate(event).toLocalDate());
      } else {
        averageCommitPerDayInfo.put("numPush",numPush+1);
      }
    }
    Integer numPush = (Integer) averageCommitPerDayInfo.get("numPush");
    Integer numDays = (Integer) averageCommitPerDayInfo.get("numDays");
    Double currentAverageCommit = numPush*1.0/numDays;
    averageCommitPerDayInfo.put("averageCommit",currentAverageCommit);
    averageCommitPerDayInfoByRepo.put(repository,averageCommitPerDayInfo);
    return currentAverageCommit;
  }

  @Override
  public Map<String, Object> apply(Map<String, Object> repository) {
    Map<Long,Map<String,Object>> averageCommitPerDayInfoByRepo = (Map<Long, Map<String, Object>>) calculateResult.get(AVERAGE_COMMIT_PER_DAY_BY_REPO_ID);
    Map<String,Object> averageCommitPerDayInfo = averageCommitPerDayInfoByRepo.get(repository.get("id"));
    double currentScore = (double) repository.get("health_score");
    if(averageCommitPerDayInfo!=null) {
      Double averageCommit = (Double) averageCommitPerDayInfo.get("averageCommit");
      repository.put("average_commit(push)_per_day",averageCommit);
      Double maxAverageCommit = (Double) calculateResult.get(MAX_AVERAGE_COMMIT_PER_DAY);
      repository.put("health_score",currentScore+averageCommit*1.0/maxAverageCommit);
    } else {
      repository.put("average_commit_per_day",0.0);
    }
    return repository;
  }
}
