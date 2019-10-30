package ai.quod.challenge.tranfomer.github.calculator;

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
    calculateResult.put(RATIO_COMMIT_PER_DEVELOPER_REPO,new HashMap<Long,Map<String,Object>>());
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
    Map<Long,Map<String,Object>> ratioCommitRepos = (Map<Long, Map<String, Object>>) calculateResult.get(RATIO_COMMIT_PER_DEVELOPER_REPO);
    Map<String,Object> ratioCommitInfo = ratioCommitRepos.get(repository);
    if(ratioCommitInfo==null) {
      ratioCommitInfo = new HashMap<>();
      Set<String> logins = new HashSet<>();
      ratioCommitRepos.put(repository,ratioCommitInfo);
      ratioCommitInfo.put("numCommit",numCommit);
      logins.add(actorLogin);
      ratioCommitInfo.put("logins",logins);
      Double averageCommitPerDev = numCommit*1.0/logins.size();
      ratioCommitInfo.put("ratioCommitPerDev",averageCommitPerDev);
    } else {
      numCommit = (Integer) ratioCommitInfo.get("numCommit") + numCommit;
      ratioCommitInfo.put("numCommit",numCommit);
      Set<String> logins = (Set<String>) ratioCommitInfo.get("logins");
      logins.add(actorLogin);
      ratioCommitInfo.put("logins",logins);
      Double averageCommitPerDev = numCommit*1.0/logins.size();
      ratioCommitInfo.put("ratioCommitPerDev",averageCommitPerDev);
    }
    return (Double) ratioCommitInfo.get("ratioCommitPerDev");
  }

  @Override
  public Map<String, Object> healthScoreCalculate(Map<String, Object> repository) throws Exception {
    Map<Long,Map<String,Object>> ratioCommitRepos = (Map<Long, Map<String, Object>>) calculateResult.get(RATIO_COMMIT_PER_DEVELOPER_REPO);
    Map<String,Object> ratioCommitInfo = ratioCommitRepos.get(repository.get("id"));
    double currentScore = (double) repository.get("health_score");
    if(ratioCommitInfo!=null) {
      Double ratioCommitPerDev = (Double) ratioCommitInfo.get("ratioCommitPerDev");
      repository.put("Ratio_Commit_Per_Dev",ratioCommitPerDev);
      Double maxRatioCommitPerDev = (Double) calculateResult.get(MAX_RATIO_COMMIT_PER_DEVELOPER);
      if(maxRatioCommitPerDev!=0) {
        repository.put("health_score",currentScore+ratioCommitPerDev/maxRatioCommitPerDev);
      }
    } else {
      repository.put("Ratio_Commit_Per_Dev",0.0);
    }
    return repository;
  }
}
