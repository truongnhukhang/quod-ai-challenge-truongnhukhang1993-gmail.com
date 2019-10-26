package ai.quod.challenge.converter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.List;

public class DateTimeConverter {

  private static DateTimeFormatter ISO8601Formatter = new DateTimeFormatterBuilder()
      .append(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
      .optionalStart().appendOffset("+HH:MM", "+00:00").optionalEnd()
      .optionalStart().appendOffset("+HHMM", "+0000").optionalEnd()
      .optionalStart().appendOffset("+HH", "Z").optionalEnd()
      .toFormatter();

  public static List<String> buildUrlForEachHour(String rootUrl,LocalDateTime startTime, LocalDateTime endTime) {
    List<String> urls = new ArrayList<>();
    while (!startTime.isAfter(endTime)) {
      StringBuilder url = new StringBuilder(rootUrl);
      url.append(startTime.getYear()).append("-");
      if(startTime.getMonthValue()<10) {
        url.append("0").append(startTime.getMonthValue());
      } else {
        url.append(startTime.getMonthValue());
      }
      url.append("-");
      if(startTime.getDayOfMonth()<10) {
        url.append("0").append(startTime.getDayOfMonth());
      } else {
        url.append(startTime.getDayOfMonth());
      }
      url.append("-");
      url.append(startTime.getHour()).append(".json.gz");
      urls.add(url.toString());
      startTime = startTime.plusHours(1);
    }
    return urls;
  }

  public static LocalDateTime convertStringIS8601ToLocalDateTime(String dateTime) {
    return LocalDateTime.parse(dateTime, ISO8601Formatter);
  }
}
