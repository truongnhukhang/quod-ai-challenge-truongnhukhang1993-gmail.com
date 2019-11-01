package ai.quod.challenge.tranfomer.github.calculator;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import static ai.quod.challenge.tranfomer.github.domain.Repository.*;
import static ai.quod.challenge.tranfomer.github.domain.GithubEvent.*;


public class AverageCommitCalculator extends BaseCalculator {

  private static final String MAX_AVERAGE_COMMIT_PER_DAY = "maxAverage";
  private static final String AVERAGE_COMMIT_PER_DAY_BY_REPO_ID = "averageCommitRepos";
  public static final String AVERAGE_COMMIT = "averageCommit";
  public static final String LAST_PUSH_DATE = "lastPushDate";
  public static final String NUM_DAYS = "numDays";
  public static final String NUM_PUSH = "numPush";

  @Override
  public void initMetric() {
    calculateResult.put(MAX_AVERAGE_COMMIT_PER_DAY,0.0);
    calculateResult.put(AVERAGE_COMMIT_PER_DAY_BY_REPO_ID,new HashMap<Long,Map<String,Object>>());
  }

  /**
   * this function use to calculate and update Average commit of repository per day and update the Max Average Commit.
   * AvarageCommitPerDay = number push/ number days
   * @param event
   */
  @Override
  public void metricCalculate(Map<String, Object> event) {
    if(PUSH_EVENT.equals(event.get(TYPE))) {
      Double currentAverageCommitOfRepo = updateAverageCommitOfRepo(event);
      updateMaxAverageCommit(currentAverageCommitOfRepo);
    }
  }

  @Override
  public Map<String, Object> healthScoreCalculate(Map<String, Object> repository) {
    Map<Long,Map<String,Object>> averageCommitPerDayInfoByRepo = (Map<Long, Map<String, Object>>) calculateResult.get(AVERAGE_COMMIT_PER_DAY_BY_REPO_ID);
    Map<String,Object> averageCommitPerDayInfo = averageCommitPerDayInfoByRepo.get(repository.get(ID));
    double currentScore = (double) repository.get(HEALTH_SCORE);
    if(averageCommitPerDayInfo!=null) {
      Double averageCommit = (Double) averageCommitPerDayInfo.get(AVERAGE_COMMIT);
      repository.put(AVERAGE_COMMIT_PUSH_PER_DAY,averageCommit);
      Double maxAverageCommit = (Double) calculateResult.get(MAX_AVERAGE_COMMIT_PER_DAY);
      repository.put(HEALTH_SCORE,currentScore+averageCommit*1.0/maxAverageCommit);
    } else {
      repository.put(AVERAGE_COMMIT_PUSH_PER_DAY,0.0);
    }
    return repository;
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
    LocalDate pushDate = getCommitDate(event).toLocalDate();
    Integer numPush = 1;
    Integer numDays = 1;
    Double currentAverageCommit = 1.0;
    Map<String,Object> averageCommitPerDayInfo = averageCommitPerDayInfoByRepo.computeIfAbsent(repository,repositoryKey -> new HashMap<>());
    // update count number push
    numPush = getNumPush(averageCommitPerDayInfo) + 1;
    LocalDate lastPushDate = getLastPushDate(averageCommitPerDayInfo);
    if(lastPushDate!=null) {
      // update count number days if push date is after last push date .
      if(pushDate.isAfter(lastPushDate)) {
        numDays = getNumDays(averageCommitPerDayInfo) + 1;
      } else {
        pushDate = lastPushDate;
      }
    }
    // calculate average commit perday = number push/ number day
    currentAverageCommit = numPush*1.0/numDays;
    updateAverageCommitPerDayInfo(pushDate, numPush, numDays, currentAverageCommit, averageCommitPerDayInfo);
    return currentAverageCommit;
  }

  private Integer getNumDays(Map<String, Object> averageCommitPerDayInfo) {
    return averageCommitPerDayInfo.get(NUM_DAYS)==null ? 0 : (Integer) averageCommitPerDayInfo.get(NUM_DAYS);
  }

  private LocalDate getLastPushDate(Map<String, Object> averageCommitPerDayInfo) {
    return (LocalDate) averageCommitPerDayInfo.get(LAST_PUSH_DATE);
  }

  private Integer getNumPush(Map<String, Object> averageCommitPerDayInfo) {
    return averageCommitPerDayInfo.get(NUM_PUSH)==null ? 0 : (Integer) averageCommitPerDayInfo.get(NUM_PUSH);
  }

  private void updateAverageCommitPerDayInfo(LocalDate pushDate, Integer numPush, Integer numDays, Double currentAverageCommit, Map<String, Object> averageCommitPerDayInfo) {
    averageCommitPerDayInfo.put(NUM_PUSH,numPush);
    averageCommitPerDayInfo.put(NUM_DAYS,numDays);
    averageCommitPerDayInfo.put(LAST_PUSH_DATE,pushDate);
    averageCommitPerDayInfo.put(AVERAGE_COMMIT,currentAverageCommit);
  }

}
