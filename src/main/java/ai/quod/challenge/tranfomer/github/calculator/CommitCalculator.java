package ai.quod.challenge.tranfomer.github.calculator;

import java.util.HashMap;
import java.util.Map;

import static ai.quod.challenge.tranfomer.github.domain.GithubEvent.*;

public class CommitCalculator extends BaseCalculator {

  private static final String MAX_NUMBER_COMMIT = "maxEventCommit";
  private static final String TOTAL_REPO_COMMIT = "totalRepoCommit";

  @Override
  public void initMetric() {
    calculateResult.put(MAX_NUMBER_COMMIT,(long)0);
    calculateResult.put(TOTAL_REPO_COMMIT,new HashMap<Long,Long>());
  }

  @Override
  public void metricCalculate(Map<String, Object> event) throws Exception{
    if(PUSH_EVENT.equals(event.get(TYPE))) {
      Long currentRepoCommit = updateCurrentCommitOfRepository(event);
      updateMaxNumberCommit(currentRepoCommit);
    }
  }

  @Override
  public Map<String, Object> healthScoreCalculate(Map<String, Object> repository) {
    Map<Long,Long> repoCommits = (Map<Long, Long>) calculateResult.get(TOTAL_REPO_COMMIT);
    Long numberCommitOfProject = repoCommits.get(repository.get("id"));
    double currentScore = (double) repository.get("health_score");
    if(numberCommitOfProject!=null) {
      Long maxNumberCommit = (Long) calculateResult.get(MAX_NUMBER_COMMIT);
      repository.put("num_commits", numberCommitOfProject);
      double commit_score = numberCommitOfProject*1.0/maxNumberCommit;
      repository.put("health_score", currentScore+commit_score);
    } else {
      repository.put("num_commits", 0);
    }
    return repository;
  }

  private void updateMaxNumberCommit(Long currentRepoCommit) {
    Long currentMaxCommit = (Long) calculateResult.get(MAX_NUMBER_COMMIT);
    if(currentMaxCommit < currentRepoCommit) {
      calculateResult.put(MAX_NUMBER_COMMIT,currentRepoCommit);
    }
  }

  private Long updateCurrentCommitOfRepository(Map<String, Object> event) {
    Map<Long,Long> numberCommitsOfEachRepository = (Map<Long, Long>) calculateResult.get(TOTAL_REPO_COMMIT);
    Long repository = getRepositoryId(event);
    Long currentRepoCommit = 0L;
    if(numberCommitsOfEachRepository.containsKey(repository)) {
      currentRepoCommit = numberCommitsOfEachRepository.get(repository);
    }
    currentRepoCommit = currentRepoCommit+getCommitSize(event);
    numberCommitsOfEachRepository.put(repository,currentRepoCommit);
    return currentRepoCommit;
  }
}
