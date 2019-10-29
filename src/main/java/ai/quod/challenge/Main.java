package ai.quod.challenge;

import ai.quod.challenge.converter.DateTimeConverter;
import ai.quod.challenge.extractor.Extractor;
import ai.quod.challenge.extractor.GithubExtractor;
import ai.quod.challenge.tranfomer.github.GithubTransformer;
import ai.quod.challenge.tranfomer.Transformer;
import ai.quod.challenge.tranfomer.github.calculator.AverageCommitCalculator;
import ai.quod.challenge.tranfomer.github.calculator.BaseCalculator;
import ai.quod.challenge.tranfomer.github.calculator.CommitCalculator;
import ai.quod.challenge.tranfomer.github.calculator.NumberContributorCalculator;
import ai.quod.challenge.transporter.CsvTransporter;
import ai.quod.challenge.transporter.Transporter;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class Main {

  private static final Logger LOGGER = Logger.getLogger(Main.class.getClass().getName());

  public static void main(String[] args) {
    boolean validate = true;
    if(args.length < 2) {
      LOGGER.log(Level.WARNING,"Required start time and end time");
      validate = false;
    } else {
      String startTime = args[0];
      try {
       DateTimeConverter.convertStringIS8601ToLocalDateTime(startTime);
      } catch (Exception e) {
        validate = false;
        LOGGER.log(Level.WARNING,"Required start time and end time in ISO8601 format");
      }
      String endTime = args[1];
      try {
        DateTimeConverter.convertStringIS8601ToLocalDateTime(endTime);
      } catch (Exception e) {
        validate = false;
        LOGGER.log(Level.WARNING,"Required start time and end time in ISO8601 format");
      }
      if(DateTimeConverter.convertStringIS8601ToLocalDateTime(startTime).isAfter(DateTimeConverter.convertStringIS8601ToLocalDateTime(endTime))) {
        validate = false;
        LOGGER.log(Level.WARNING,"Required start time before or equals end time");
      }
    }

    if(validate) {
      LocalDateTime startTime = DateTimeConverter.convertStringIS8601ToLocalDateTime(args[0]);
      LocalDateTime endTime = DateTimeConverter.convertStringIS8601ToLocalDateTime(args[1]);
      Map<String,Object> resourceUrl = new HashMap<>();
      resourceUrl.put("startTime",startTime);
      resourceUrl.put("endTime",endTime);
      Date startExtractTime = new Date();
      Map<String,Object> data = new HashMap<>();
      Extractor<Map<String,Object>> extractor = new GithubExtractor();
      data.put("data",extractor.extractDataFrom(resourceUrl));
      LOGGER.log(Level.INFO, "Finish Extract Data " + startTime + " - " + endTime + " , it took : " + (System.currentTimeMillis() - startExtractTime.getTime()) / 1000 + " seconds");
      Date startTransform = new Date();
      BaseCalculator commitMetric = new CommitCalculator();
      BaseCalculator averageCommitPerDayMetric = new AverageCommitCalculator();
      BaseCalculator numberContributorMetric = new NumberContributorCalculator();
      Transformer<Stream<Map<String,Object>>> git = new GithubTransformer(Arrays.asList(commitMetric,averageCommitPerDayMetric,numberContributorMetric));
      data.put("data",git.transform(data));
      data.put("filename","heath_score.csv");
      data.put("headers",new String[]{"org","repo_name","health_score","num_commits","average_commit(push)_per_day","num_contributor"});
      Transporter csvTransporter = new CsvTransporter();
      csvTransporter.sendTo(data);
      LOGGER.log(Level.INFO, "Finish Transform and send Data " + startTime + " - " + endTime + " , it took : " + (System.currentTimeMillis() - startTransform.getTime()) / 1000 + " seconds");
    }
  }
}
