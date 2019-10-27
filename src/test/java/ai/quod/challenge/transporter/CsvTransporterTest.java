package ai.quod.challenge.transporter;

import ai.quod.challenge.tranfomer.GithubTransformer;
import ai.quod.challenge.tranfomer.Transformer;
import ai.quod.challenge.tranfomer.calculator.BaseCalculator;
import ai.quod.challenge.tranfomer.calculator.CommitCalculator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CsvTransporterTest {

  String dataTest = "{\"id\":\"2489651045\",\"type\":\"CreateEvent\",\"actor\":{\"id\":665991,\"login\":\"petroav\",\"gravatar_id\":\"\",\"url\":\"https://api.github.com/users/petroav\",\"avatar_url\":\"https://avatars.githubusercontent.com/u/665991?\"},\"repo\":{\"id\":28688495,\"name\":\"petroav/6.828\",\"url\":\"https://api.github.com/repos/petroav/6.828\"},\"payload\":{\"ref\":\"master\",\"ref_type\":\"branch\",\"master_branch\":\"master\",\"description\":\"Solution to homework and assignments from MIT's 6.828 (Operating Systems Engineering). Done in my spare time.\",\"pusher_type\":\"user\"},\"public\":true,\"created_at\":\"2015-01-01T15:00:00Z\"}\n" +
      "{\"id\":\"2489651051\",\"type\":\"PushEvent\",\"actor\":{\"id\":3854017,\"login\":\"rspt\",\"gravatar_id\":\"\",\"url\":\"https://api.github.com/users/rspt\",\"avatar_url\":\"https://avatars.githubusercontent.com/u/3854017?\"},\"repo\":{\"id\":28671719,\"name\":\"rspt/rspt-theme\",\"url\":\"https://api.github.com/repos/rspt/rspt-theme\"},\"payload\":{\"push_id\":536863970,\"size\":1,\"distinct_size\":1,\"ref\":\"refs/heads/master\",\"head\":\"6b089eb4a43f728f0a594388092f480f2ecacfcd\",\"before\":\"437c03652caa0bc4a7554b18d5c0a394c2f3d326\",\"commits\":[{\"sha\":\"6b089eb4a43f728f0a594388092f480f2ecacfcd\",\"author\":{\"email\":\"5c682c2d1ec4073e277f9ba9f4bdf07e5794dabe@rspt.ch\",\"name\":\"rspt\"},\"message\":\"Fix main header height on mobile\",\"distinct\":true,\"url\":\"https://api.github.com/repos/rspt/rspt-theme/commits/6b089eb4a43f728f0a594388092f480f2ecacfcd\"}]},\"public\":true,\"created_at\":\"2015-01-01T15:00:01Z\"}\n" +
      "{\"id\":\"2489651051\",\"type\":\"PushEvent\",\"actor\":{\"id\":3854017,\"login\":\"rspt\",\"gravatar_id\":\"\",\"url\":\"https://api.github.com/users/rspt\",\"avatar_url\":\"https://avatars.githubusercontent.com/u/3854017?\"},\"repo\":{\"id\":28671719,\"name\":\"rspt/rspt-theme\",\"url\":\"https://api.github.com/repos/rspt/rspt-theme\"},\"payload\":{\"push_id\":536863970,\"size\":1,\"distinct_size\":1,\"ref\":\"refs/heads/master\",\"head\":\"6b089eb4a43f728f0a594388092f480f2ecacfcd\",\"before\":\"437c03652caa0bc4a7554b18d5c0a394c2f3d326\",\"commits\":[{\"sha\":\"6b089eb4a43f728f0a594388092f480f2ecacfcd\",\"author\":{\"email\":\"5c682c2d1ec4073e277f9ba9f4bdf07e5794dabe@rspt.ch\",\"name\":\"rspt\"},\"message\":\"Fix main header height on mobile\",\"distinct\":true,\"url\":\"https://api.github.com/repos/rspt/rspt-theme/commits/6b089eb4a43f728f0a594388092f480f2ecacfcd\"}]},\"public\":true,\"created_at\":\"2015-01-01T15:00:01Z\"}\n" +
      "{\"id\":\"2489651053\",\"type\":\"PushEvent\",\"actor\":{\"id\":6339799,\"login\":\"izuzero\",\"gravatar_id\":\"\",\"url\":\"https://api.github.com/users/izuzero\",\"avatar_url\":\"https://avatars.githubusercontent.com/u/6339799?\"},\"repo\":{\"id\":28270952,\"name\":\"izuzero/xe-module-ajaxboard\",\"url\":\"https://api.github.com/repos/izuzero/xe-module-ajaxboard\"},\"payload\":{\"push_id\":536863972,\"size\":1,\"distinct_size\":1,\"ref\":\"refs/heads/develop\",\"head\":\"ec819b9df4fe612bb35bf562f96810bf991f9975\",\"before\":\"590433109f221a96cf19ea7a7d9a43ca333e3b3e\",\"commits\":[{\"sha\":\"ec819b9df4fe612bb35bf562f96810bf991f9975\",\"author\":{\"email\":\"df05f55543db3c62cf64f7438018ec37f3605d3c@gmail.com\",\"name\":\"Eunsoo Lee\"},\"message\":\"#20 게시글 및 댓글 삭제 시 새로고침이 되는 문제 해결\\n\\n원래 의도는 새로고침이 되지 않고 확인창만으로 해결되어야 함.\\n기본 게시판 대응 플러그인에서 발생한 이슈.\",\"distinct\":true,\"url\":\"https://api.github.com/repos/izuzero/xe-module-ajaxboard/commits/ec819b9df4fe612bb35bf562f96810bf991f9975\"}]},\"public\":true,\"created_at\":\"2015-01-01T15:00:01Z\"}\n" +
      "{\"id\":\"2489651057\",\"type\":\"WatchEvent\",\"actor\":{\"id\":6894991,\"login\":\"SametSisartenep\",\"gravatar_id\":\"\",\"url\":\"https://api.github.com/users/SametSisartenep\",\"avatar_url\":\"https://avatars.githubusercontent.com/u/6894991?\"},\"repo\":{\"id\":2871998,\"name\":\"visionmedia/debug\",\"url\":\"https://api.github.com/repos/visionmedia/debug\"},\"payload\":{\"action\":\"started\"},\"public\":true,\"created_at\":\"2015-01-01T15:00:03Z\",\"org\":{\"id\":9285252,\"login\":\"visionmedia\",\"gravatar_id\":\"\",\"url\":\"https://api.github.com/orgs/visionmedia\",\"avatar_url\":\"https://avatars.githubusercontent.com/u/9285252?\"}}\n" +
      "{\"id\":\"2489651062\",\"type\":\"PushEvent\",\"actor\":{\"id\":485033,\"login\":\"winterbe\",\"gravatar_id\":\"\",\"url\":\"https://api.github.com/users/winterbe\",\"avatar_url\":\"https://avatars.githubusercontent.com/u/485033?\"},\"repo\":{\"id\":28593843,\"name\":\"winterbe/streamjs\",\"url\":\"https://api.github.com/repos/winterbe/streamjs\"},\"payload\":{\"push_id\":536863975,\"size\":1,\"distinct_size\":1,\"ref\":\"refs/heads/master\",\"head\":\"15b303203be31bd295bc831075da8f74b99b3981\",\"before\":\"0fef99f604154ccfe1d2fcd0aadeffb5c58e43ff\",\"commits\":[{\"sha\":\"15b303203be31bd295bc831075da8f74b99b3981\",\"author\":{\"email\":\"52a47bffd52d9cea1ee1362f2bd0c5f87fac9262@googlemail.com\",\"name\":\"Benjamin Winterberg\"},\"message\":\"Add comparator support for min, max operations\",\"distinct\":true,\"url\":\"https://api.github.com/repos/winterbe/streamjs/commits/15b303203be31bd295bc831075da8f74b99b3981\"}]},\"public\":true,\"created_at\":\"2015-01-01T15:00:03Z\"}\n" +
      "{\"id\":\"2489651063\",\"type\":\"PushEvent\",\"actor\":{\"id\":4319954,\"login\":\"hermanwahyudi\",\"gravatar_id\":\"\",\"url\":\"https://api.github.com/users/hermanwahyudi\",\"avatar_url\":\"https://avatars.githubusercontent.com/u/4319954?\"},\"repo\":{\"id\":27826205,\"name\":\"hermanwahyudi/selenium\",\"url\":\"https://api.github.com/repos/hermanwahyudi/selenium\"},\"payload\":{\"push_id\":536863976,\"size\":1,\"distinct_size\":0,\"ref\":\"refs/heads/master\",\"head\":\"1b58dd4c4e14ea9cf5212b981774bd448a266c3c\",\"before\":\"20b10e3a605bd177efff62f1130943774ac07bf3\",\"commits\":[{\"sha\":\"1b58dd4c4e14ea9cf5212b981774bd448a266c3c\",\"author\":{\"email\":\"2bb20d8a71fb7adbc1d6239cc9ff4130f26819dc@gmail.com\",\"name\":\"Herman\"},\"message\":\"Update README.md\",\"distinct\":false,\"url\":\"https://api.github.com/repos/hermanwahyudi/selenium/commits/1b58dd4c4e14ea9cf5212b981774bd448a266c3c\"}]},\"public\":true,\"created_at\":\"2015-01-01T15:00:03Z\"}\n" +
      "{\"id\":\"2489651064\",\"type\":\"PushEvent\",\"actor\":{\"id\":2881602,\"login\":\"jdilt\",\"gravatar_id\":\"\",\"url\":\"https://api.github.com/users/jdilt\",\"avatar_url\":\"https://avatars.githubusercontent.com/u/2881602?\"},\"repo\":{\"id\":28682546,\"name\":\"jdilt/jdilt.github.io\",\"url\":\"https://api.github.com/repos/jdilt/jdilt.github.io\"},\"payload\":{\"push_id\":536863977,\"size\":1,\"distinct_size\":1,\"ref\":\"refs/heads/master\",\"head\":\"d13cbd1e5c68b189fc91cfa14fdae1f52ef6f9e1\",\"before\":\"8515c4a9efb40332659e4389821a73800ce6a4bf\",\"commits\":[{\"sha\":\"d13cbd1e5c68b189fc91cfa14fdae1f52ef6f9e1\",\"author\":{\"email\":\"3e9bbe622d800410f1d4d0a4bb92004e147f1b1e@163.com\",\"name\":\"jdilt\"},\"message\":\"refine index page and about page\",\"distinct\":true,\"url\":\"https://api.github.com/repos/jdilt/jdilt.github.io/commits/d13cbd1e5c68b189fc91cfa14fdae1f52ef6f9e1\"}]},\"public\":true,\"created_at\":\"2015-01-01T15:00:03Z\"}\n" +
      "{\"id\":\"2489651066\",\"type\":\"PushEvent\",\"actor\":{\"id\":3495129,\"login\":\"sundaymtn\",\"gravatar_id\":\"\",\"url\":\"https://api.github.com/users/sundaymtn\",\"avatar_url\":\"https://avatars.githubusercontent.com/u/3495129?\"},\"repo\":{\"id\":24147122,\"name\":\"sundaymtn/waterline\",\"url\":\"https://api.github.com/repos/sundaymtn/waterline\"},\"payload\":{\"push_id\":536863979,\"size\":1,\"distinct_size\":1,\"ref\":\"refs/heads/master\",\"head\":\"2a2ec35bfefb9341b1df2f213aad1dac804bc2ea\",\"before\":\"a7dba8faf22d2f342b7398ff76bfd10a30106191\",\"commits\":[{\"sha\":\"2a2ec35bfefb9341b1df2f213aad1dac804bc2ea\",\"author\":{\"email\":\"7fbc091194a9488bfb16868527a7c3a8ba469dba@gmail.com\",\"name\":\"Seth Carter\"},\"message\":\"Thu Jan  1 10:00:02 EST 2015\",\"distinct\":true,\"url\":\"https://api.github.com/repos/sundaymtn/waterline/commits/2a2ec35bfefb9341b1df2f213aad1dac804bc2ea\"}]},\"public\":true,\"created_at\":\"2015-01-01T15:00:04Z\"}\n" +
      "{\"id\":\"2489651067\",\"type\":\"PushEvent\",\"actor\":{\"id\":10363514,\"login\":\"zhouzhi2015\",\"gravatar_id\":\"\",\"url\":\"https://api.github.com/users/zhouzhi2015\",\"avatar_url\":\"https://avatars.githubusercontent.com/u/10363514?\"},\"repo\":{\"id\":28686619,\"name\":\"zhouzhi2015/temp\",\"url\":\"https://api.github.com/repos/zhouzhi2015/temp\"},\"payload\":{\"push_id\":536863980,\"size\":1,\"distinct_size\":1,\"ref\":\"refs/heads/master\",\"head\":\"22019c081480435bb7d6e629766f2204c6c219bd\",\"before\":\"d5926ef8c6a8a43724f8dc94007c3c5a918391c3\",\"commits\":[{\"sha\":\"22019c081480435bb7d6e629766f2204c6c219bd\",\"author\":{\"email\":\"421c4f4cb8c7fe07ea1166286558dc42a56cf3a7\",\"name\":\"1184795629@qq.com\"},\"message\":\"测测\",\"distinct\":true,\"url\":\"https://api.github.com/repos/zhouzhi2015/temp/commits/22019c081480435bb7d6e629766f2204c6c219bd\"}]},\"public\":true,\"created_at\":\"2015-01-01T15:00:04Z\"}\n";


  @Test
  public void testCsvTransporter() {
    ObjectMapper objectMapper = new ObjectMapper();
    List<Map<String,Object>> dataJson = Stream.of(dataTest.split("\n")).map(s -> {
      try {
        return objectMapper.readValue(s,new TypeReference<Map<String, Object>>() {
        });
      } catch (JsonProcessingException e) {
        e.printStackTrace();
      }
      return null;
    }).collect(Collectors.toList());
    Map<String,Object> data = new HashMap<>();
    data.put("data",dataJson.stream());
    BaseCalculator commitMetric = new CommitCalculator();
    System.out.println("Start Transform : " + new Date());
    Transformer<Stream<Map<String,Object>>> git = new GithubTransformer(Arrays.asList(commitMetric));
    data.put("data",git.transform(data));
    data.put("filename","heath_score.csv");
    data.put("headers",new String[]{"org","repo_name","health_score","num_commits"});
    Transporter csvTransporter = new CsvTransporter();
    csvTransporter.sendTo(data);
    File file = new File((String) data.get("filename"));
    Assertions.assertTrue(file.exists());
  }
}
