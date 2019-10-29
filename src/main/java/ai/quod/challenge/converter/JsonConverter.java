package ai.quod.challenge.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class JsonConverter {

  private static final Logger LOGGER = Logger.getLogger(JsonConverter.class.getClass().getName());
  static ObjectMapper mapper = new ObjectMapper();

  public static Stream<Map<String, Object>> convertJsonObjectsToStreamFromFile(String fileLocation) throws IOException {
    Stream<Map<String, Object>> mapStream = Files.lines(Paths.get(fileLocation)).map(line -> {
      Map<String, Object> mapResult = null;
      try {
        mapResult = mapper.readValue(line, new TypeReference<Map<String, Object>>() {
        });
      } catch (JsonProcessingException e) {
        LOGGER.log(Level.WARNING, "JsonProcessingException error :" + line);
      }
      return mapResult;
    }).filter(Objects::nonNull);
    return mapStream;
  }
}
