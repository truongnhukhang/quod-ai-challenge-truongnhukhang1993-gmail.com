package ai.quod.challenge.extract;

import ai.quod.challenge.converter.DateTimeConverter;
import ai.quod.challenge.domain.github.GithubEvent;
import ai.quod.challenge.extractor.Extractor;
import ai.quod.challenge.extractor.GithubExtractor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

public class GithubExtractorTest {

  @Test
  public void testDownloadAndExtract()  {
    Extractor<GithubEvent> githubExtractor = new GithubExtractor();
    LocalDateTime startTime = DateTimeConverter.convertStringIS8601ToLocalDateTime("2011-02-12T08:00:00Z");
    LocalDateTime endTime = DateTimeConverter.convertStringIS8601ToLocalDateTime("2011-02-12T08:00:00Z");
    Stream<GithubEvent> downloadedData = githubExtractor.extractDataFrom(startTime,endTime);
    Assertions.assertNotNull(downloadedData);
    System.out.println(downloadedData.count());

  }

  @Test
  public void testBuildUrlForEachHour() {
    LocalDateTime startTime = DateTimeConverter.convertStringIS8601ToLocalDateTime("2019-08-20T00:00:00Z");
    LocalDateTime endTime = DateTimeConverter.convertStringIS8601ToLocalDateTime("2019-08-20T08:00:00Z");
    List<String> urls = DateTimeConverter.buildUrlForEachHour("https://data.gharchive.org/",startTime,endTime);
    Assertions.assertEquals(9,urls.size());
  }
}
