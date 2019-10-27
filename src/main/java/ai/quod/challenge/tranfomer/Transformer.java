package ai.quod.challenge.tranfomer;

import java.util.Map;

public interface Transformer<T> {
  public T transform(Map<String,Object> data);
}
