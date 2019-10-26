package ai.quod.challenge.extractor;

import ai.quod.challenge.converter.DateTimeConverter;
import ai.quod.challenge.converter.JsonConverter;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

public class GithubExtractor implements Extractor<Map<String, Object>> {

  private static final Logger LOGGER = Logger.getLogger(GithubExtractor.class.getClass().getName());
  private static final String GIT_HUB_URL = "https://data.gharchive.org/";
  private static final int NUM_DOWNLOAD_THREAD = 16;

  /**
   * This function will extract data from GHArchive to a Stream contain Git event information .
   * In case , the period between of start time and end time to large ,
   * we have to make that period become a list of smaller period . And extract them concurrently.
   * @param resourceUrl
   * @return
   */
  public Stream<Map<String, Object>> extractDataFrom(Map<String, Object> resourceUrl) {
    Date startExtractTime = new Date();
    LocalDateTime startTime = (LocalDateTime) resourceUrl.get("startTime");
    LocalDateTime endTime = (LocalDateTime) resourceUrl.get("endTime");
    LOGGER.log(Level.INFO, "Start Extract Data " + startTime + " - " + endTime);
    Stream<Map<String, Object>> result = Stream.empty();
    List<String> urls = DateTimeConverter.buildUrlForEachHour(GIT_HUB_URL, startTime, endTime);
    List<Callable<Stream<Map<String, Object>>>> worker = assignWorkerForEachUrl(urls);
    ExecutorService executorService = Executors.newFixedThreadPool(NUM_DOWNLOAD_THREAD);
    try {
      List<Future<Stream<Map<String, Object>>>> futures = executorService.invokeAll(worker);
      for (int i = 0; i < futures.size(); i++) {
        result = Stream.concat(result,futures.get(i).get());
      }
      LOGGER.log(Level.INFO, "Finish Extract Data " + startTime + " - " + endTime + " , it took : " + (System.currentTimeMillis() - startExtractTime.getTime()) / 1000 + " seconds");
    } catch (InterruptedException e) {
      LOGGER.log(Level.SEVERE,"InterruptedException "+e.getMessage());
    } catch (ExecutionException e) {
      LOGGER.log(Level.SEVERE,"ExecutionException "+e.getMessage());
    }
    return result;
  }

  /**
   * Assign worker for each url . Each worker is a Callable , it's jobs include download data from url and transform it to Stream json
   * @param urls
   * @return
   */
  private List<Callable<Stream<Map<String, Object>>>> assignWorkerForEachUrl(List<String> urls) {
    return urls.stream().map(url -> (Callable<Stream<Map<String, Object>>>) () -> {
      String fileLocation = downloadFile(url);
      Stream<Map<String, Object>> mapStream = JsonConverter.convertJsonObjectsToStreamFromFile(fileLocation);
      new File(fileLocation).deleteOnExit();
      return mapStream;
    }).collect(Collectors.toList());
  }


  /**
   * Download file use GZIPInputStream to unzip and return file location
   * @param url
   * @return
   */
  public String downloadFile(String url) {

    LOGGER.log(Level.INFO, "Start download Data from " + url);
    // download file
    URLConnection openConnection = null;
    try {
      openConnection = new URL(url).openConnection();
      openConnection.setConnectTimeout(0);
      openConnection.setReadTimeout(0);
      openConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");
      InputStream is = openConnection.getInputStream();
      is = new GZIPInputStream(is);
      String extractFileName = "Temp" + System.currentTimeMillis() + ".json";
      FileOutputStream extractFileStream = new FileOutputStream(extractFileName);
      IOUtils.copy(is, extractFileStream, 1024 * 1024);
      is.close();
      extractFileStream.flush();
      extractFileStream.close();
      return extractFileName;
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, "IOException " + e.getMessage());
    }
    return "";
  }
}
