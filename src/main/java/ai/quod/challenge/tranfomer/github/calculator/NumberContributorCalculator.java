package ai.quod.challenge.tranfomer.github.calculator;

import ai.quod.challenge.tranfomer.github.domain.Repository;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static ai.quod.challenge.tranfomer.github.domain.GithubEvent.*;

public class NumberContributorCalculator extends BaseCalculator {

  private static final String MAX_NUMBER_CONTRIBUTOR = "maxContributor";
  private static final String NUMBER_CONTRIBUTOR_FOR_EACH_REPO = "contributorEachRepo";

  @Override
  public void initMetric() {
    calculateResult.put(MAX_NUMBER_CONTRIBUTOR,0);
    calculateResult.put(NUMBER_CONTRIBUTOR_FOR_EACH_REPO,new HashMap<Long, Set<Integer>>());
  }

  @Override
  public void metricCalculate(Map<String, Object> event) {
    if(PULL_REQUEST_EVENT.equals(event.get(TYPE))) {
      Integer currentNumberContributorOfRepo = updateNumberContributorOfRepo(event);
      updateMaxNumberContributor(currentNumberContributorOfRepo);
    }
  }

  @Override
  public Repository healthScoreCalculate(Repository repository) {
    Map<Long,Set<Integer>> repoContributors = (Map<Long, Set<Integer>>) calculateResult.get(NUMBER_CONTRIBUTOR_FOR_EACH_REPO);
    Set<Integer> numberContributorOfProject = repoContributors.get(repository.getId());
    double currentScore = repository.getHealthScore();
    if(numberContributorOfProject!=null) {
      Integer maxNumberContributor = (Integer) calculateResult.get(MAX_NUMBER_CONTRIBUTOR);
      repository.setNumberContributor(numberContributorOfProject.size());
      double commit_score = numberContributorOfProject.size()*1.0/maxNumberContributor;
      repository.setHealthScore(currentScore+commit_score);
    } else {
      repository.setNumberContributor(0);
    }
    return repository;
  }

  private void updateMaxNumberContributor(Integer currentNumberContributorOfRepo) {
    Integer currentMaxNumberContributor = (Integer) calculateResult.get(MAX_NUMBER_CONTRIBUTOR);
    if(currentMaxNumberContributor<currentNumberContributorOfRepo) {
      calculateResult.put(MAX_NUMBER_CONTRIBUTOR,currentNumberContributorOfRepo);
    }
  }

  private Integer updateNumberContributorOfRepo(Map<String, Object> event) {
    Long repository = getRepositoryId(event);
    Map<String,Object> user = getPullRequestUser(event);
    Map<Long,Set<Integer>> numContributorsOfRepos = (Map<Long, Set<Integer>>) calculateResult.get(NUMBER_CONTRIBUTOR_FOR_EACH_REPO);
    Set<Integer> currentContributors = numContributorsOfRepos.get(repository);
    if(currentContributors==null) {
      currentContributors = new HashSet<>();
    }
    currentContributors.add((Integer) user.get("id"));
    numContributorsOfRepos.put(repository,currentContributors);
    return currentContributors.size();
  }

}
