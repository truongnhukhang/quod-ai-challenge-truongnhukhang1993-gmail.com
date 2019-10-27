package ai.quod.challenge.tranfomer;

import ai.quod.challenge.tranfomer.calculator.BaseCalculator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GithubTransformer implements Transformer<Stream<Map<String,Object>>> {

  private ConcurrentHashMap<String,Object> calculateResult = new ConcurrentHashMap<>();
  private List<BaseCalculator> calculateFunctions = null;

  public GithubTransformer(List<BaseCalculator> calculateFunctions) {
    this.calculateFunctions = calculateFunctions;
    calculateFunctions.forEach(baseCalculator -> {
      baseCalculator.setCalculateResult(calculateResult);
      baseCalculator.initMetric();
    });
  }

  @Override
  public Stream<Map<String, Object>> transform(Map<String, Object> data) {
    Stream<Map<String, Object>> githubEvents = (Stream<Map<String, Object>>) data.get("data");
    githubEvents = githubEvents.parallel();
    for (int i = 0; i < calculateFunctions.size(); i++) {
      Consumer<Map<String, Object>> metricCalculator = (Consumer<Map<String, Object>>) calculateFunctions.get(i);
      githubEvents = githubEvents.peek(metricCalculator);
    }
    githubEvents = githubEvents.filter(this::isRepositoryEvent).map(this::convertGithubEventToProject).distinct().collect(Collectors.toList()).stream();
    for (int i = 0; i < calculateFunctions.size(); i++) {
      Function<Map<String, Object>,Map<String, Object>> healthScoreCalculator = (Function<Map<String, Object>,Map<String, Object>>) calculateFunctions.get(i);
      githubEvents = githubEvents.map(healthScoreCalculator);
    }
    githubEvents = githubEvents.sorted((o1, o2) -> {
      Double heathScore1 = (Double) o1.get("health_score");
      Double heathScore2 = (Double) o2.get("health_score");
      return heathScore2.compareTo(heathScore1);
    });
    return githubEvents;
  }

  private boolean isRepositoryEvent(Map<String, Object> githubEvent) {
    return githubEvent.get("repo")!=null || githubEvent.get("repository")!=null;
  }

  private Map<String, Object> convertGithubEventToProject(Map<String, Object> githubEvent) {
    Map<String,Object> project = new HashMap<>();
    Map<String,Object> repo = (Map<String, Object>) githubEvent.get("repo");
    Long id = 0L;
    String repoOrg = "";
    String repoName = "";
    if(repo!=null) {
      id = Long.valueOf((Integer) repo.get("id"));
      repoOrg = ((String) repo.get("name")).split("/")[0];
      repoName = ((String) repo.get("name")).split("/")[1];
    } else {
      repo = (Map<String, Object>) githubEvent.get("repository");
      id = Long.valueOf((Integer) repo.get("id"));
      repoOrg = (String) repo.get("owner");
      repoName = (String) repo.get("name");
    }
    project.put("id",id);
    project.put("org",repoOrg);
    project.put("repo_name",repoName);
    project.put("health_score",0.0);
    return project;
  }
}
