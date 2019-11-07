package ai.quod.challenge.tranfomer.github.calculator;

import ai.quod.challenge.domain.github.GithubEvent;
import ai.quod.challenge.domain.github.Repository;

import java.util.HashMap;
import java.util.Map;

import static ai.quod.challenge.converter.GithubEventConverter.*;

public class CommitCalculator extends BaseCalculator {

  private static final String MAX_NUMBER_COMMIT = "maxEventCommit";
  private static final String TOTAL_REPO_COMMIT = "totalRepoCommit";

  @Override
  public void initMetric() {
    calculateResult.put(MAX_NUMBER_COMMIT,(long)0);
    calculateResult.put(TOTAL_REPO_COMMIT,new HashMap<Long,Long>());
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
    Map<Long,Long> repoCommits = (Map<Long, Long>) calculateResult.get(TOTAL_REPO_COMMIT);
    Long numberCommitOfProject = repoCommits.get(repository.getId());
    double currentScore = repository.getHealthScore();
    if(numberCommitOfProject!=null) {
      Long maxNumberCommit = (Long) calculateResult.get(MAX_NUMBER_COMMIT);
      repository.setNumberCommit(numberCommitOfProject);
      double commit_score = numberCommitOfProject*1.0/maxNumberCommit;
      repository.setHealthScore(currentScore+commit_score);
    } else {
      repository.setNumberCommit(0L);
    }
    return repository;
  }

  private void updateMaxNumberCommit(Long currentRepoCommit) {
    Long currentMaxCommit = (Long) calculateResult.get(MAX_NUMBER_COMMIT);
    if(currentMaxCommit < currentRepoCommit) {
      calculateResult.put(MAX_NUMBER_COMMIT,currentRepoCommit);
    }
  }

  private Long updateCurrentCommitOfRepository(GithubEvent event) {
    Map<Long,Long> numberCommitsOfEachRepository = (Map<Long, Long>) calculateResult.get(TOTAL_REPO_COMMIT);
    Long repository = event.getRepository().getId();
    Long currentRepoCommit = 0L;
    if(numberCommitsOfEachRepository.containsKey(repository)) {
      currentRepoCommit = numberCommitsOfEachRepository.get(repository);
    }
    currentRepoCommit = currentRepoCommit+event.getCommitSize();
    numberCommitsOfEachRepository.put(repository,currentRepoCommit);
    return currentRepoCommit;
  }
}
