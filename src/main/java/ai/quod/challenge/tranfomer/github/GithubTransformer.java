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

public class GithubTransformer implements Transformer<Stream<Repository>> {

  private List<BaseCalculator> calculateFunctions = null;

  public GithubTransformer(List<BaseCalculator> calculateFunctions) {
    this.calculateFunctions = calculateFunctions;
    calculateFunctions.forEach(baseCalculator -> {
      baseCalculator.setCalculateResult(new ConcurrentHashMap<>());
      baseCalculator.initMetric();
    });
  }

  @Override
  public Stream<Repository> transform(Map<String, Object> data) {
    Stream<Map<String, Object>> githubEvents = (Stream<Map<String, Object>>) data.get("data");
    githubEvents = githubEvents.parallel().filter(GithubEvent::containRepositoryInfo);
    githubEvents = applyMetricCalculatorFor(githubEvents);
    Stream<Repository> repositoryStream = githubEvents.map(GithubEvent::getRepository).distinct().collect(Collectors.toList()).parallelStream();
    repositoryStream = applyHealthScoreCalculatorFor(repositoryStream);
    repositoryStream = repositoryStream.sorted((o1, o2) -> o2.getHealthScore().compareTo(o1.getHealthScore()));
    return repositoryStream;
  }

  private Stream<Repository> applyHealthScoreCalculatorFor(Stream<Repository> repositoryStream) {
    for (int i = 0; i < calculateFunctions.size(); i++) {
      Function<Repository,Repository> healthScoreCalculator = calculateFunctions.get(i);
      repositoryStream = repositoryStream.map(healthScoreCalculator);
    }
    return repositoryStream;
  }

  private Stream<Map<String, Object>> applyMetricCalculatorFor(Stream<Map<String, Object>> githubEvents) {
    for (int i = 0; i < calculateFunctions.size(); i++) {
      Consumer<Map<String, Object>> metricCalculator = calculateFunctions.get(i);
      githubEvents = githubEvents.peek(metricCalculator);
    }
    return githubEvents;
  }




}
