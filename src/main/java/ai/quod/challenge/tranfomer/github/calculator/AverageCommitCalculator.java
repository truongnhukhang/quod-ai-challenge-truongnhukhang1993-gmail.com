package ai.quod.challenge.tranfomer.github.calculator;

import ai.quod.challenge.domain.github.GithubEvent;
import ai.quod.challenge.domain.github.Repository;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static ai.quod.challenge.converter.GithubEventConverter.*;


public class AverageCommitCalculator extends BaseCalculator {

  private static final String MAX_AVERAGE_COMMIT_PER_DAY = "maxAverage";
  private static final String AVERAGE_COMMIT_PER_DAY_BY_REPO_ID = "averageCommitRepos";

  @Override
  public void initMetric() {
    calculateResult.put(MAX_AVERAGE_COMMIT_PER_DAY,0.0);
    calculateResult.put(AVERAGE_COMMIT_PER_DAY_BY_REPO_ID,new HashMap<Long,AveragePushPerDay>());
  }

  /**
   * this function use to calculate and update Average commit of repository per day and update the Max Average Commit.
   * AvarageCommitPerDay = number push/ number days
   * @param event
   */
  @Override
  public void metricCalculate(GithubEvent event) {
    if(PUSH_EVENT.equals(event.getType())) {
      Double currentAverageCommitOfRepo = updateAverageCommitOfRepo(event);
      updateMaxAverageCommit(currentAverageCommitOfRepo);
    }
  }

  @Override
  public Repository healthScoreCalculate(Repository repository) {
    Map<Long,AveragePushPerDay> averageCommitPerDayInfoByRepo = (Map<Long, AveragePushPerDay>) calculateResult.get(AVERAGE_COMMIT_PER_DAY_BY_REPO_ID);
    AveragePushPerDay averagePushPerDayInfo = averageCommitPerDayInfoByRepo.get(repository.getId());
    double currentScore = repository.getHealthScore();
    if(averagePushPerDayInfo!=null) {
      Double averagePushPerDay = averagePushPerDayInfo.currentAveragePush;
      repository.setAveragePushPerDay(averagePushPerDay);
      Double maxAverageCommit = (Double) calculateResult.get(MAX_AVERAGE_COMMIT_PER_DAY);
      repository.setHealthScore(currentScore+averagePushPerDay*1.0/maxAverageCommit);
    } else {
      repository.setAveragePushPerDay(0.0);
    }
    return repository;
  }

  private void updateMaxAverageCommit(Double currentAverageCommitOfRepo) {
    Double maxAverageCommit = (Double) calculateResult.get(MAX_AVERAGE_COMMIT_PER_DAY);
    if(maxAverageCommit.compareTo(currentAverageCommitOfRepo) < 0) {
      calculateResult.put(MAX_AVERAGE_COMMIT_PER_DAY,currentAverageCommitOfRepo);
    }
  }

  private Double updateAverageCommitOfRepo(GithubEvent event) {
    Map<Long,AveragePushPerDay> averageCommitPerDayInfoByRepo = (Map<Long, AveragePushPerDay>) calculateResult.get(AVERAGE_COMMIT_PER_DAY_BY_REPO_ID);
    Long repository = event.getRepository().getId();
    LocalDate pushDate = event.getCommitDate().toLocalDate();
    Integer numPush = 1;
    Integer numDays = 1;
    Double currentAverageCommit = 1.0;
    AveragePushPerDay averageCommitPerDayInfo = averageCommitPerDayInfoByRepo.computeIfAbsent(repository,repositoryKey -> new AveragePushPerDay());
    // update count number push
    numPush = averageCommitPerDayInfo.numPush + 1;
    LocalDate lastPushDate = averageCommitPerDayInfo.pushDate;
    if(lastPushDate!=null) {
      // update count number days if push date is after last push date .
      if(pushDate.isAfter(lastPushDate)) {
        numDays = averageCommitPerDayInfo.numDays + 1;
      } else {
        pushDate = lastPushDate;
      }
    }
    // calculate average commit perday = number push/ number day
    currentAverageCommit = numPush*1.0/numDays;
    updateAverageCommitPerDayInfo(pushDate, numPush, numDays, currentAverageCommit, averageCommitPerDayInfo);
    return currentAverageCommit;
  }

  private void updateAverageCommitPerDayInfo(LocalDate pushDate, Integer numPush, Integer numDays, Double currentAveragePush, AveragePushPerDay averageCommitPerDayInfo) {
    averageCommitPerDayInfo.numPush = numPush;
    averageCommitPerDayInfo.numDays = numDays;
    averageCommitPerDayInfo.pushDate = pushDate;
    averageCommitPerDayInfo.currentAveragePush = currentAveragePush;
  }

  private class AveragePushPerDay {
    LocalDate pushDate;
    Integer numPush = 0;
    Integer numDays = 0;
    Double currentAveragePush;
  }

}
