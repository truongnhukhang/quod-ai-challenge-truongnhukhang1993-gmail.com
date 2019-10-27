package ai.quod.challenge.extractor;

import java.util.Map;
import java.util.stream.Stream;

public interface Extractor<T> {
  Stream<T> extractDataFrom(Map<String,Object> resourceUrl);
}
