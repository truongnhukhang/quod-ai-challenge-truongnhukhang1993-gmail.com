package ai.quod.challenge.tranfomer.github.calculator;

import ai.quod.challenge.domain.github.GithubEvent;
import ai.quod.challenge.domain.github.Repository;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static ai.quod.challenge.converter.GithubEventConverter.*;

/**
 * Created by truongnhukhang on 10/30/19.
 */
public class RatioCommitPerDevelopersCalculator extends BaseCalculator {

  private Double maxRatioCommitPerDev = null;
  private Map<Long,RatioCommitPerDev> ratioCommitRepos = null;

  @Override
  public void initMetric() {
    maxRatioCommitPerDev = 0.0;
    ratioCommitRepos = new HashMap<>();
  }

  @Override
  public void metricCalculate(GithubEvent event) throws Exception {
    if(PUSH_EVENT.equals(event.getType())) {
      Double currentRatioCommitRepo = updateCurrentRatioRepo(event);
      updateMaxRatioCommit(currentRatioCommitRepo);
    }
  }

  private void updateMaxRatioCommit(Double currentRatioCommitRepo) {
    if(currentRatioCommitRepo.compareTo(maxRatioCommitPerDev)>0){
      maxRatioCommitPerDev = currentRatioCommitRepo;
    }
  }

  private Double updateCurrentRatioRepo(GithubEvent event) {
    Long repository = event.getRepository().getId();
    Integer numCommit = event.getCommitSize();
    String actorLogin = event.getActorLogin();
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
    RatioCommitPerDev ratioCommitInfo = ratioCommitRepos.get(repository.getId());
    double currentScore = repository.getHealthScore();
    if(ratioCommitInfo!=null) {
      Double ratioCommitPerDev = ratioCommitInfo.ratioCommitPerDev;
      repository.setRatioCommitPerDev(ratioCommitPerDev);
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
