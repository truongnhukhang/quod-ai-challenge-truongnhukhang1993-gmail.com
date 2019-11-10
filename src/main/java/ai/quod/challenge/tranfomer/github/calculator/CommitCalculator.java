package ai.quod.challenge.tranfomer.github.calculator;

import ai.quod.challenge.domain.github.GithubEvent;
import ai.quod.challenge.domain.github.Repository;

import java.util.HashMap;

import static ai.quod.challenge.converter.GithubEventConverter.*;

public class CommitCalculator extends BaseCalculator {

  private Long maxNumberCommit = null;
  private HashMap<Long,Long> totalRepoCommit = null;


  @Override
  public void initMetric() {
    maxNumberCommit = 0L;
    totalRepoCommit = new HashMap<>();
  }

  @Override
  public void metricCalculate(GithubEvent event) throws Exception{
    if(PUSH_EVENT.equals(event.getType())) {
      Long currentRepoCommit = updateCurrentCommitOfRepository(event);
      updateMaxNumberCommit(currentRepoCommit);
    }
  }

  @Override
  public Repository healthScoreCalculate(Repository repository) {
    Long numberCommitOfProject = totalRepoCommit.get(repository.getId());
    double currentScore = repository.getHealthScore();
    if(numberCommitOfProject!=null) {
      repository.setNumberCommit(numberCommitOfProject);
      double commit_score = numberCommitOfProject*1.0/maxNumberCommit;
      repository.setHealthScore(currentScore+commit_score);
    } else {
      repository.setNumberCommit(0L);
    }
    return repository;
  }

  private void updateMaxNumberCommit(Long currentRepoCommit) {
    if(maxNumberCommit < currentRepoCommit) {
      maxNumberCommit = currentRepoCommit;
    }
  }

  private Long updateCurrentCommitOfRepository(GithubEvent event) {
    Long repository = event.getRepository().getId();
    Long currentRepoCommit = 0L;
    if(totalRepoCommit.containsKey(repository)) {
      currentRepoCommit = totalRepoCommit.get(repository);
    }
    currentRepoCommit = currentRepoCommit+event.getCommitSize();
    totalRepoCommit.put(repository,currentRepoCommit);
    return currentRepoCommit;
  }
}
