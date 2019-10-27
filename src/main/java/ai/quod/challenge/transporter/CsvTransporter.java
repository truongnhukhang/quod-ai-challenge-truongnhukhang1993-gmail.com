package ai.quod.challenge.transporter;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CsvTransporter implements Transporter {

  private static final Logger LOGGER = Logger.getLogger(CsvTransporter.class.getClass().getName());

  @Override
  public void sendTo(Map<String, Object> data) {
    try {
      Stream<Map<String, Object>> projectStream = (Stream<Map<String, Object>>) data.get("data");
      String[] headers = (String[]) data.get("headers");
      String filename = (String) data.get("filename");
      FileWriter out = new FileWriter(filename+".csv");
      List<Map<String, Object>> projects = projectStream.collect(Collectors.toList());
      try (CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT
          .withHeader(headers))) {
        for (Map<String, Object> project : projects) {
          List<Object> records = Stream.of(headers).map(project::get).collect(Collectors.toList());
          printer.printRecord(records);
        }
      }
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, "IOException " + e.getMessage());
    }
  }
}
