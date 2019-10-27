package ai.quod.challenge.tranfomer.calculator;

import java.util.concurrent.ConcurrentHashMap;

public abstract class BaseCalculator {
  protected ConcurrentHashMap<String,Object> consumeResult;

  public abstract void initMetric();

  public void setConsumeResult(ConcurrentHashMap<String, Object> consumeResult) {
    this.consumeResult = consumeResult;
  }
}
