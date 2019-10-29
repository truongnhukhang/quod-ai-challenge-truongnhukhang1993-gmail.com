package ai.quod.challenge.tranfomer.github.calculator;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class BaseCalculator implements Consumer<Map<String,Object>>, Function<Map<String,Object>,Map<String,Object>> {
  private static final Logger LOGGER = Logger.getLogger(BaseCalculator.class.getClass().getName());
  protected ConcurrentHashMap<String,Object> calculateResult;

  public abstract void initMetric();
  public abstract void metricCalculate(Map<String,Object> event) throws Exception;
  public abstract Map<String, Object> healthScoreCalculate(Map<String,Object> repository) throws Exception;

  @Override
  public void accept(Map<String, Object> event) {
    try {
      metricCalculate(event);
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE,e.getMessage() + " in event : " + event.toString());
    }
  }

  @Override
  public Map<String, Object> apply(Map<String, Object> repository) {
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
