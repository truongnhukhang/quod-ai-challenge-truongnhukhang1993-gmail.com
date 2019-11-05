package ai.quod.challenge.tranfomer.github.calculator;

import ai.quod.challenge.tranfomer.github.domain.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class BaseCalculator implements Consumer<Map<String,Object>>, Function<Repository,Repository> {
  private static final Logger LOGGER = Logger.getLogger(BaseCalculator.class.getClass().getName());
  protected ConcurrentHashMap<String,Object> calculateResult;

  public abstract void initMetric();
  public abstract void metricCalculate(Map<String,Object> event) throws Exception;
  public abstract Repository healthScoreCalculate(Repository repository) throws Exception;

  @Override
  public void accept(Map<String, Object> event) {
    try {
      metricCalculate(event);
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE,e.getMessage() + " in event : " + event.get("id") + " - " + event.get("type") + " - " + event.get("created_at"));
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

  public void setCalculateResult(ConcurrentHashMap<String, Object> calculateResult) {
    this.calculateResult = calculateResult;
  }
}
