package ai.quod.challenge.tranfomer.github.calculator;

import ai.quod.challenge.domain.github.GithubEvent;
import ai.quod.challenge.domain.github.Repository;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class BaseCalculator implements Consumer<GithubEvent>, Function<Repository,Repository> {
  private static final Logger LOGGER = Logger.getLogger(BaseCalculator.class.getClass().getName());

  public abstract void initMetric();
  public abstract void metricCalculate(GithubEvent event) throws Exception;
  public abstract Repository healthScoreCalculate(Repository repository) throws Exception;

  @Override
  public void accept(GithubEvent event) {
    try {
      metricCalculate(event);
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE,e.getMessage() + " in event : " + event.getId() + " - " + event.getType() + " - " + event.getCreateAt());
    }
  }

  @Override
  public Repository apply(Repository repository) {
    try {
      return healthScoreCalculate(repository);
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE,e.getMessage() + " in repository : " + repository.toString());
    }
    return repository;
  }

}
