# GitHub Repository health measure Project

Project help you evaluate which repository have a good health. The health score was calculate based on some metric such as commit , contributor , number pull request .

## Building

Project is built using http://maven.apache.org/[Apache Maven] .

run `mvn clean package` to build . The project was build to 2 file jar , one of them is a fat jar ( include all dependencies ) .

run `mvn clean package -Dskiptest` . To build without unit test .

## Using 

go to the build folder result ( usually `target` folder )

run `java -jar challenge-1.0-SNAPSHOT-jar-with-dependencies.jar [start_time] [end_time]`

start_time and end_time must be in ISO8601 Format

Example : `java -jar challenge-1.0-SNAPSHOT-jar-with-dependencies.jar 2014-12-30T23:00:00Z 2015-01-07T00:00:00Z`

The application will start and export the health_score.csv in the same folder . 

## Dependencies

This project use 3 small library to handle IO processing, Json parsing, and Export CSV

1. commons-io 
2. jackson-databind
3. commons-csv

## Exetending 

Overview flow 

![img](https://i.imgur.com/rS5w8RJ.png)

Transformer flow 

![img](https://i.imgur.com/aJZk7nn.png)

Each GitHub Event will go through Tranfomer's pipe and apply everything metric and heath score calculator function in the pipe . Because health score calculate need the metric result data . So We have a calculateResult storage (ConcurrentHashMap) to store all metric result 

we can create or remove a metric without violate open-closed principle

To add a new metric calculator .

1. Create a new  MetricCalculator class , extend BaseCalculator .
2. Implement three 3 functions

   * initMetric() -> init metric result default value ( ex : totalCommit , averageCommit for each repository )
   
   ```java
   calculateResult.put(MAX_NUMBER_COMMIT,(long)0);
   calculateResult.put(TOTAL_REPO_COMMIT,new HashMap<Long,Long>());
   ```
   * metricCalculate(event) -> we put metric calculator business  (ex : count commit , contributor , find maxNumberCommit ...)  here. 
   ```java
   if(PUSH_EVENT.equals(event.get(TYPE))) {
      Long currentRepoCommit = updateCurrentCommitOfRepository(event);
      updateMaxNumberCommit(currentRepoCommit);
    }
    ```
   * healthScoreCalculate(repository) -> we put health score calculator business here. 
   ```java
   if(PUSH_EVENT.equals(event.get(TYPE))) {
      Long currentRepoCommit = updateCurrentCommitOfRepository(event);
      updateMaxNumberCommit(currentRepoCommit);
    }
    ```
 3. Add Metric to metric list when we init Transformer instance .
   
   ```java
      BaseCalculator commitMetric = new CommitCalculator();
      BaseCalculator averageCommitPerDayMetric = new AverageCommitCalculator();
      BaseCalculator numberContributorMetric = new NumberContributorCalculator();
      BaseCalculator averageTimeIssues = new AverageTimeIssueOpenCalculator();
      BaseCalculator ratioCommitPerDev = new RatioCommitPerDevelopersCalculator();
      Transformer<Stream<Map<String,Object>>> git = new GithubTransformer(Arrays.asList(commitMetric,averageCommitPerDayMetric,numberContributorMetric, averageTimeIssues, ratioCommitPerDev));
   ```
## Technical decisions
### what frameworks/libraries did you use? 

   As mentioned in dependencies section . i use 3 small libraries apache-common-io, jackson-databind , apache-common-csv to handle IO , JSON , CSV .

### What are the benefits of those libraries?

   I dont want to reinvent the wheel . 
   
### What would you improve in your code?

1. Application download data from GHArchive by mutilthreading mechanism which each thread take responsibility one hour data . Sometime the GHArchive stop our request (may be i reach their rate limit ) lead to download thread stop and we lost some hour data -> thinking about implement exponential backoff function .

2. Sometime we got the wrong Json format exception . I just log it and show to screent . Should be save it to DB or file so we can try to investigate it later

3. Comment metric calculator .
   
