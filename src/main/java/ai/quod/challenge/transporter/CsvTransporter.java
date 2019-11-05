package ai.quod.challenge.transporter;

import ai.quod.challenge.tranfomer.github.domain.Repository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
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
      Stream<Repository> projectStream = (Stream<Repository>) data.get("data");
      String[] headers = (String[]) data.get("headers");
      String filename = (String) data.get("filename");
      File file = new File(filename);
      if(file.exists()) {
        file.delete();
      }
      FileWriter out = new FileWriter(filename);
      try (CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT
          .withHeader(headers))) {
        projectStream.sequential().forEach(repository -> {
           List<Object> records = Arrays.asList(repository.getOrg(),
               repository.getName(), repository.getHealthScore(),repository.getNumberCommit(),repository.getAveragePushPerDay(),
               repository.getAverageHoursIssueRemainOpen(),repository.getRatioCommitPerDev());
          try {
            printer.printRecord(records);
          } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "IOException " + e.getMessage() + " records : " + repository.toString());
          }
        });

      }
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, "IOException " + e.getMessage());
    }
  }
}
