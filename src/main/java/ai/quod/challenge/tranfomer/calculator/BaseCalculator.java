package ai.quod.challenge.tranfomer.calculator;

import java.util.concurrent.ConcurrentHashMap;

public abstract class BaseCalculator {
  protected ConcurrentHashMap<String,Object> calculateResult;

  public abstract void initMetric();

  public void setCalculateResult(ConcurrentHashMap<String, Object> calculateResult) {
    this.calculateResult = calculateResult;
  }
}
