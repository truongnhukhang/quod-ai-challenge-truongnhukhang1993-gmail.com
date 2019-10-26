package ai.quod.challenge.test.extract;

import ai.quod.challenge.converter.DateTimeConverter;
import ai.quod.challenge.extractor.Extractor;
import ai.quod.challenge.extractor.GithubExtractor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class GithubExtractorTest {

  @Test
  public void testDownloadAndExtract() throws IOException {
    Extractor<Map<String,Object>> githubExtractor = new GithubExtractor();
    Map<String,Object> resourceUrl = new HashMap<>();
    LocalDateTime startTime = DateTimeConverter.convertStringIS8601ToLocalDateTime("2019-08-20T08:00:00Z");
    LocalDateTime endTime = DateTimeConverter.convertStringIS8601ToLocalDateTime("2019-08-20T09:00:00Z");
    resourceUrl.put("startTime",startTime);
    resourceUrl.put("endTime",endTime);
    Stream<Map<String,Object>> downloadedData = githubExtractor.extractDataFrom(resourceUrl);
    Assertions.assertNotNull(downloadedData);
    System.out.println(downloadedData.count());

  }

  @Test
  public void testBuildUrlForEachHour() {
    LocalDateTime startTime = DateTimeConverter.convertStringIS8601ToLocalDateTime("2019-08-20T00:00:00Z");
    LocalDateTime endTime = DateTimeConverter.convertStringIS8601ToLocalDateTime("2019-08-25T00:00:00Z");
    List<String> urls = DateTimeConverter.buildUrlForEachHour("https://data.gharchive.org/",startTime,endTime);
    urls.forEach(System.out::println);
  }
}
