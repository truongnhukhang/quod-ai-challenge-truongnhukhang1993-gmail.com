package ai.quod.challenge.transporter;

import ai.quod.challenge.domain.github.Repository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class RepositoryCsvTransporter implements Transporter<Stream<Repository>> {

  private static final Logger LOGGER = Logger.getLogger(RepositoryCsvTransporter.class.getClass().getName());

  String[] headers;
  String filename;

  public RepositoryCsvTransporter(String[] headers, String filename) {
    this.headers = headers;
    this.filename = filename;
  }

  @Override
  public void sendTo(Stream<Repository> projectStream) {
    try {
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
