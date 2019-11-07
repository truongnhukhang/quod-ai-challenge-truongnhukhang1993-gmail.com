package ai.quod.challenge.extractor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Stream;

public interface Extractor<T> {
  Stream<T> extractDataFrom(LocalDateTime start,LocalDateTime end);
}
