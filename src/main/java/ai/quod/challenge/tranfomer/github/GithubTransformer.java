package ai.quod.challenge.tranfomer.github;

import ai.quod.challenge.domain.github.GithubEvent;
import ai.quod.challenge.tranfomer.Transformer;
import ai.quod.challenge.tranfomer.github.calculator.BaseCalculator;
import ai.quod.challenge.converter.GithubEventConverter;
import ai.quod.challenge.domain.github.Repository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GithubTransformer implements Transformer<Stream<Repository>,Stream<GithubEvent>> {

  private List<BaseCalculator> calculateFunctions = null;

  public GithubTransformer(List<BaseCalculator> calculateFunctions) {
    this.calculateFunctions = calculateFunctions;
    calculateFunctions.forEach(BaseCalculator::initMetric);
  }

  @Override
  public Stream<Repository> transform(Stream<GithubEvent> githubEvents) {
    githubEvents = githubEvents.parallel().filter(githubEvent -> githubEvent.getRepository()!=null);
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

  private Stream<GithubEvent> applyMetricCalculatorFor(Stream<GithubEvent> githubEvents) {
    for (int i = 0; i < calculateFunctions.size(); i++) {
      Consumer<GithubEvent> metricCalculator = calculateFunctions.get(i);
      githubEvents = githubEvents.peek(metricCalculator);
    }
    return githubEvents;
  }




}
