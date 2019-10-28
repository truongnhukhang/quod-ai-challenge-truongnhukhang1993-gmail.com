package ai.quod.challenge.tranfomer.github.calculator;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class NumberContributorCalculator extends BaseCalculator
    implements Consumer<Map<String,Object>>, Function<Map<String,Object>,Map<String,Object>> {

  private static final String MAX_NUMBER_CONTRIBUTOR = "maxContributor";
  private static final String NUMBER_CONTRIBUTOR_FOR_EACH_REPO = "contributorEachRepo";

  @Override
  public void initMetric() {
    calculateResult.put(MAX_NUMBER_CONTRIBUTOR,0);
    calculateResult.put(NUMBER_CONTRIBUTOR_FOR_EACH_REPO,new HashMap<Long,Integer>());
  }

  @Override
  public void accept(Map<String, Object> event) {

  }

  @Override
  public Map<String, Object> apply(Map<String, Object> repository) {
    return null;
  }
}
