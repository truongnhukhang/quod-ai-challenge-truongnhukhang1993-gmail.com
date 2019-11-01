package ai.quod.challenge.tranfomer.github;

import ai.quod.challenge.tranfomer.Transformer;
import ai.quod.challenge.tranfomer.github.calculator.BaseCalculator;
import ai.quod.challenge.tranfomer.github.domain.GithubEvent;
import ai.quod.challenge.tranfomer.github.domain.Repository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GithubTransformer implements Transformer<Stream<Map<String,Object>>> {

  private List<BaseCalculator> calculateFunctions = null;

  public GithubTransformer(List<BaseCalculator> calculateFunctions) {
    this.calculateFunctions = calculateFunctions;
    calculateFunctions.forEach(baseCalculator -> {
      baseCalculator.setCalculateResult(new ConcurrentHashMap<>());
      baseCalculator.initMetric();
    });
  }

  @Override
  public Stream<Map<String, Object>> transform(Map<String, Object> data) {
    Stream<Map<String, Object>> githubEvents = (Stream<Map<String, Object>>) data.get("data");
    githubEvents = githubEvents.parallel().filter(GithubEvent::containRepositoryInfo);
    githubEvents = applyMetricCalculatorFor(githubEvents);
    githubEvents = githubEvents.map(GithubEvent::getRepository).distinct().collect(Collectors.toList()).stream();
    githubEvents = applyHealthScoreCalculatorFor(githubEvents);
    githubEvents = githubEvents.sorted((o1, o2) -> {
      Double heathScore1 = (Double) o1.get(Repository.HEALTH_SCORE);
      Double heathScore2 = (Double) o2.get(Repository.HEALTH_SCORE);
      return heathScore2.compareTo(heathScore1);
    });
    return githubEvents;
  }

  private Stream<Map<String, Object>> applyHealthScoreCalculatorFor(Stream<Map<String, Object>> githubEvents) {
    for (int i = 0; i < calculateFunctions.size(); i++) {
      Function<Map<String, Object>,Map<String, Object>> healthScoreCalculator = calculateFunctions.get(i);
      githubEvents = githubEvents.map(healthScoreCalculator);
    }
    return githubEvents;
  }

  private Stream<Map<String, Object>> applyMetricCalculatorFor(Stream<Map<String, Object>> githubEvents) {
    for (int i = 0; i < calculateFunctions.size(); i++) {
      Consumer<Map<String, Object>> metricCalculator = calculateFunctions.get(i);
      githubEvents = githubEvents.peek(metricCalculator);
    }
    return githubEvents;
  }




}
