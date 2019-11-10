package ai.quod.challenge;

import ai.quod.challenge.converter.DateTimeConverter;
import ai.quod.challenge.domain.github.GithubEvent;
import ai.quod.challenge.domain.github.Repository;
import ai.quod.challenge.extractor.Extractor;
import ai.quod.challenge.extractor.GithubExtractor;
import ai.quod.challenge.tranfomer.Transformer;
import ai.quod.challenge.tranfomer.github.GithubTransformer;
import ai.quod.challenge.tranfomer.github.calculator.*;
import ai.quod.challenge.transporter.RepositoryCsvTransporter;
import ai.quod.challenge.transporter.Transporter;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
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
      Date startExtractTime = new Date();
      Extractor<GithubEvent> extractor = new GithubExtractor();
      Stream<GithubEvent> githubEventStream = extractor.extractDataFrom(startTime,endTime);
      Date endExtractTime = new Date();
      Date startTransform = new Date();
      BaseCalculator commitMetric = new CommitCalculator();
      BaseCalculator averageCommitPerDayMetric = new AverageCommitCalculator();
      BaseCalculator numberContributorMetric = new NumberContributorCalculator();
      BaseCalculator averageTimeIssues = new AverageTimeIssueOpenCalculator();
      BaseCalculator ratioCommitPerDev = new RatioCommitPerDevelopersCalculator();
      Transformer<Stream<Repository>,Stream<GithubEvent>> git = new GithubTransformer(Arrays.asList(commitMetric,averageCommitPerDayMetric,numberContributorMetric, averageTimeIssues, ratioCommitPerDev));
      Stream<Repository> transformResult = git.transform(githubEventStream);
      String filename = "heath_score.csv";
      String[] headers = {"org", "repo_name", Repository.HEALTH_SCORE, "num_commits", Repository.AVERAGE_COMMIT_PUSH_PER_DAY, "num_contributor", "average_time_issue_remain_opened(hours)", "Ratio_Commit_Per_Dev"};
      Transporter<Stream<Repository>> csvTransporter = new RepositoryCsvTransporter(headers,filename);
      csvTransporter.sendTo(transformResult);
      LOGGER.log(Level.INFO, "Extract Data " + startTime + " - " + endTime + " time : " + (endExtractTime.getTime() - startExtractTime.getTime()) / 1000 + " seconds");
      LOGGER.log(Level.INFO, "Transform and send Data " + startTime + " - " + endTime + " time : " + (System.currentTimeMillis() - startTransform.getTime()) / 1000 + " seconds");
    }
  }
}
