package ai.quod.challenge.tranfomer.github.calculator;
import static ai.quod.challenge.tranfomer.github.domain.GithubEvent.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class NumberContributorCalculator extends BaseCalculator
    implements Consumer<Map<String,Object>>, Function<Map<String,Object>,Map<String,Object>> {

  private static final String MAX_NUMBER_CONTRIBUTOR = "maxContributor";
  private static final String NUMBER_CONTRIBUTOR_FOR_EACH_REPO = "contributorEachRepo";

  @Override
  public void initMetric() {
    calculateResult.put(MAX_NUMBER_CONTRIBUTOR,0);
    calculateResult.put(NUMBER_CONTRIBUTOR_FOR_EACH_REPO,new HashMap<Long,Integer>());
  }

  @Override
  public void accept(Map<String, Object> event) {
    if(PULL_REQUEST_EVENT.equals(event.get(TYPE))) {
      Integer currentNumberContributorOfRepo = updateNumberContributorOfRepo(event);
      updateMaxNumberContributor(currentNumberContributorOfRepo);
    }
  }

  private void updateMaxNumberContributor(Integer currentNumberContributorOfRepo) {
    Integer currentMaxNumberContributor = (Integer) calculateResult.get(MAX_NUMBER_CONTRIBUTOR);
    if(currentMaxNumberContributor<currentNumberContributorOfRepo) {
      calculateResult.put(MAX_NUMBER_CONTRIBUTOR,currentNumberContributorOfRepo);
    }
  }

  private Integer updateNumberContributorOfRepo(Map<String, Object> event) {
    Long repository = getRepositoryId(event);
    Map<Long,Integer> numContributorsOfRepos = (Map<Long, Integer>) calculateResult.get(NUMBER_CONTRIBUTOR_FOR_EACH_REPO);
    Integer currentContributor = numContributorsOfRepos.get(repository);
    if(currentContributor==null) {
      currentContributor = 1;
    } else {
      currentContributor = currentContributor + 1;
    }
    numContributorsOfRepos.put(repository,currentContributor);
    return currentContributor;
  }

  @Override
  public Map<String, Object> apply(Map<String, Object> repository) {
    Map<Long,Integer> repoContributors = (Map<Long, Integer>) calculateResult.get(NUMBER_CONTRIBUTOR_FOR_EACH_REPO);
    Integer numberContributorOfProject = repoContributors.get(repository.get("id"));
    double currentScore = (double) repository.get("health_score");
    if(numberContributorOfProject!=null) {
      Integer maxNumberCommit = (Integer) calculateResult.get(MAX_NUMBER_CONTRIBUTOR);
      repository.put("num_contributor", numberContributorOfProject);
      double commit_score = numberContributorOfProject*1.0/maxNumberCommit;
      repository.put("health_score", currentScore+commit_score);
    } else {
      repository.put("num_contributor", 0);
    }
    return repository;
  }
}
