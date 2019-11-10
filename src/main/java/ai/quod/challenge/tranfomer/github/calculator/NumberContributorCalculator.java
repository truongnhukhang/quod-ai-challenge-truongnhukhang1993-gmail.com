package ai.quod.challenge.tranfomer.github.calculator;

import ai.quod.challenge.domain.github.GithubEvent;
import ai.quod.challenge.domain.github.Repository;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static ai.quod.challenge.converter.GithubEventConverter.PULL_REQUEST_EVENT;

public class NumberContributorCalculator extends BaseCalculator {

  private Integer maxNumberContributor = null;
  private HashMap<Long, Set<Integer>> repoContributors = null;
  @Override
  public void initMetric() {
    maxNumberContributor = 0;
    repoContributors = new HashMap<>();
  }

  @Override
  public void metricCalculate(GithubEvent event) {
    if(PULL_REQUEST_EVENT.equals(event.getType())) {
      Integer currentNumberContributorOfRepo = updateNumberContributorOfRepo(event);
      updateMaxNumberContributor(currentNumberContributorOfRepo);
    }
  }

  @Override
  public Repository healthScoreCalculate(Repository repository) {
    Set<Integer> numberContributorOfProject = repoContributors.get(repository.getId());
    double currentScore = repository.getHealthScore();
    if(numberContributorOfProject!=null) {
      repository.setNumberContributor(numberContributorOfProject.size());
      double commit_score = numberContributorOfProject.size()*1.0/maxNumberContributor;
      repository.setHealthScore(currentScore+commit_score);
    } else {
      repository.setNumberContributor(0);
    }
    return repository;
  }

  private void updateMaxNumberContributor(Integer currentNumberContributorOfRepo) {
    if(maxNumberContributor<currentNumberContributorOfRepo) {
      maxNumberContributor = currentNumberContributorOfRepo;
    }
  }

  private Integer updateNumberContributorOfRepo(GithubEvent event) {
    Long repository = event.getRepository().getId();
    Set<Integer> currentContributors = repoContributors.get(repository);
    if(currentContributors==null) {
      currentContributors = new HashSet<>();
    }
    currentContributors.add(event.getUserPullRequestId());
    repoContributors.put(repository,currentContributors);
    return currentContributors.size();
  }

}
