package ai.quod.challenge.tranfomer.github.calculator;

import ai.quod.challenge.tranfomer.github.domain.Repository;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static ai.quod.challenge.tranfomer.github.domain.GithubEvent.*;

/**
 * Created by truongnhukhang on 10/30/19.
 */
public class RatioCommitPerDevelopersCalculator extends BaseCalculator {

  private static final String MAX_RATIO_COMMIT_PER_DEVELOPER = "maxRatioCommit";
  private static final String RATIO_COMMIT_PER_DEVELOPER_REPO = "ratioCommitPerDeveloper";

  @Override
  public void initMetric() {
    calculateResult.put(MAX_RATIO_COMMIT_PER_DEVELOPER,0.0);
    calculateResult.put(RATIO_COMMIT_PER_DEVELOPER_REPO,new HashMap<Long,RatioCommitPerDev>());
  }

  @Override
  public void metricCalculate(Map<String, Object> event) throws Exception {
    if(PUSH_EVENT.equals(event.get(TYPE))) {
      Double currentRatioCommitRepo = updateCurrentRatioRepo(event);
      updateMaxRatioCommit(currentRatioCommitRepo);
    }
  }

  private void updateMaxRatioCommit(Double currentRatioCommitRepo) {
    if(currentRatioCommitRepo.compareTo((Double) calculateResult.get(MAX_RATIO_COMMIT_PER_DEVELOPER))>0){
      calculateResult.put(MAX_RATIO_COMMIT_PER_DEVELOPER,currentRatioCommitRepo);
    }
  }

  private Double updateCurrentRatioRepo(Map<String, Object> event) {
    Long repository = getRepositoryId(event);
    Integer numCommit = getCommitSize(event);
    String actorLogin = getActorLogin(event);
    Map<Long,RatioCommitPerDev> ratioCommitRepos = (Map<Long, RatioCommitPerDev>) calculateResult.get(RATIO_COMMIT_PER_DEVELOPER_REPO);
    RatioCommitPerDev ratioCommitInfo = ratioCommitRepos.get(repository);
    if(ratioCommitInfo==null) {
      ratioCommitInfo = new RatioCommitPerDev();
      Set<String> users = new HashSet<>();
      users.add(actorLogin);
      updateRatioCommitPerDev(ratioCommitInfo, users, numCommit);
    } else {
      Set<String> users = ratioCommitInfo.users;
      users.add(actorLogin);
      updateRatioCommitPerDev(ratioCommitInfo, users, ratioCommitInfo.numCommit + numCommit);
    }
    ratioCommitRepos.put(repository,ratioCommitInfo);
    return ratioCommitInfo.ratioCommitPerDev;
  }

  private void updateRatioCommitPerDev(RatioCommitPerDev ratioCommitInfo, Set<String> users, int numCommit) {
    ratioCommitInfo.numCommit = numCommit;
    ratioCommitInfo.users = users;
    ratioCommitInfo.ratioCommitPerDev = ratioCommitInfo.numCommit * 1.0 / users.size();
  }

  @Override
  public Repository healthScoreCalculate(Repository repository) throws Exception {
    Map<Long,RatioCommitPerDev> ratioCommitRepos = (Map<Long, RatioCommitPerDev>) calculateResult.get(RATIO_COMMIT_PER_DEVELOPER_REPO);
    RatioCommitPerDev ratioCommitInfo = ratioCommitRepos.get(repository.getId());
    double currentScore = repository.getHealthScore();
    if(ratioCommitInfo!=null) {
      Double ratioCommitPerDev = ratioCommitInfo.ratioCommitPerDev;
      repository.setRatioCommitPerDev(ratioCommitPerDev);
      Double maxRatioCommitPerDev = (Double) calculateResult.get(MAX_RATIO_COMMIT_PER_DEVELOPER);
      if(maxRatioCommitPerDev!=0) {
        repository.setHealthScore(currentScore+ratioCommitPerDev/maxRatioCommitPerDev);
      }
    } else {
      repository.setRatioCommitPerDev(0.0);
    }
    return repository;
  }

  private class RatioCommitPerDev {
    Integer numCommit;
    Set<String> users;
    Double ratioCommitPerDev;
  }
}
