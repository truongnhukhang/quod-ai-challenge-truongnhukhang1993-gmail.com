package ai.quod.challenge.tranfomer.calculator;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class CommitCalculator extends BaseCalculator
    implements Consumer<Map<String,Object>>, Function<Map<String,Object>,Map<String,Object>> {

  public static final String TOTAL_EVENT_COMMIT = "totalEventCommit";
  public static final String TOTAL_REPO_COMMIT = "totalRepoCommit";

  @Override
  public void initMetric() {
    calculateResult.put(TOTAL_EVENT_COMMIT,(long)0);
    calculateResult.put(TOTAL_REPO_COMMIT,new HashMap<Long,Long>());
  }

  @Override
  public void accept(Map<String, Object> event) {
    if(event.get("type").equals("PushEvent")) {
      Long currentCount = (Long) calculateResult.get(TOTAL_EVENT_COMMIT);
      calculateResult.put(TOTAL_EVENT_COMMIT,currentCount+1);
      Map<String,Object> repo = (Map<String, Object>) event.get("repo");
      if(repo==null) {
        repo = (Map<String, Object>) event.get("repository");
      }
      Long id = Long.valueOf((Integer) repo.get("id"));
      Map<Long,Long> repoCommits = (Map<Long, Long>) calculateResult.get(TOTAL_REPO_COMMIT);
      Long currentRepoCommit = (long)0;
      if(repoCommits.containsKey(id)) {
        currentRepoCommit = repoCommits.get(id);
      }
      repoCommits.put(id,currentRepoCommit+1);
    }
  }

  @Override
  public Map<String, Object> apply(Map<String, Object> stringObjectMap) {
    Map<Long,Long> repoCommits = (Map<Long, Long>) calculateResult.get(TOTAL_REPO_COMMIT);
    Long numberCommitOfProject = repoCommits.get(stringObjectMap.get("id"));
    double currentScore = (double) stringObjectMap.get("health_score");
    if(numberCommitOfProject!=null) {
      Long totalCommit = (Long) calculateResult.get(TOTAL_EVENT_COMMIT);
      stringObjectMap.put("num_commits", numberCommitOfProject);
      double commit_score = numberCommitOfProject*1.0/totalCommit;
      stringObjectMap.put("health_score", currentScore+commit_score);
    } else {
      stringObjectMap.put("num_commits", 0);
    }
    return stringObjectMap;
  }


}
