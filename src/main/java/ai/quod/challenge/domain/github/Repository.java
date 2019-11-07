package ai.quod.challenge.domain.github;

/**
 * Created by truongnhukhang on 11/1/19.
 */
public class Repository {
  public static final String HEALTH_SCORE = "health_score";
  public static final String AVERAGE_COMMIT_PUSH_PER_DAY = "average_commit(push)_per_day";
  Long id;
  String org;
  String name;
  Double healthScore;
  Double averagePushPerDay;
  Double averageHoursIssueRemainOpen;
  Long numberCommit;
  Integer numberContributor;
  Double ratioCommitPerDev;


  public Repository(Long id, String org, String name, Double healthScore) {
    this.id = id;
    this.org = org;
    this.name = name;
    this.healthScore = healthScore;
  }

  public Repository(Long id) {
    this.id = id;
  }

  public Double getHealthScore() {
    return healthScore;
  }

  public void setHealthScore(Double healthScore) {
    this.healthScore = healthScore;
  }

  public Double getAveragePushPerDay() {
    return averagePushPerDay;
  }

  public void setAveragePushPerDay(Double averagePushPerDay) {
    this.averagePushPerDay = averagePushPerDay;
  }

  public Double getAverageHoursIssueRemainOpen() {
    return averageHoursIssueRemainOpen;
  }

  public void setAverageHoursIssueRemainOpen(Double averageHoursIssueRemainOpen) {
    this.averageHoursIssueRemainOpen = averageHoursIssueRemainOpen;
  }

  public Long getNumberCommit() {
    return numberCommit;
  }

  public void setNumberCommit(Long numberCommit) {
    this.numberCommit = numberCommit;
  }

  public Integer getNumberContributor() {
    return numberContributor;
  }

  public void setNumberContributor(Integer numberContributor) {
    this.numberContributor = numberContributor;
  }

  public Double getRatioCommitPerDev() {
    return ratioCommitPerDev;
  }

  public void setRatioCommitPerDev(Double ratioCommitPerDev) {
    this.ratioCommitPerDev = ratioCommitPerDev;
  }

  public Long getId() {
    return id;
  }

  public String getOrg() {
    return org;
  }

  public String getName() {
    return name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Repository)) return false;

    Repository that = (Repository) o;

    if (!id.equals(that.id)) return false;
    if (!org.equals(that.org)) return false;
    return name.equals(that.name);
  }

  @Override
  public int hashCode() {
    int result = id.hashCode();
    result = 31 * result + org.hashCode();
    result = 31 * result + name.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return "Repository{" +
        "id=" + id +
        ", org='" + org + '\'' +
        ", name='" + name + '\'' +
        ", healthScore=" + healthScore +
        ", averagePushPerDay=" + averagePushPerDay +
        ", averageHoursIssueRemainOpen=" + averageHoursIssueRemainOpen +
        ", numberCommit=" + numberCommit +
        ", numberContributor=" + numberContributor +
        ", ratioCommitPerDev=" + ratioCommitPerDev +
        '}';
  }
}
