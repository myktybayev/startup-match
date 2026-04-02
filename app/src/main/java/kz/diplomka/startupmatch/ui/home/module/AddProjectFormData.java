package kz.diplomka.startupmatch.ui.home.module;

public class AddProjectFormData {

    private final String projectName;
    private final String industry;
    private final String targetAudience;
    private final String market;
    private final String shortDescription;
    private final String fullDescription;

    public AddProjectFormData(
            String projectName,
            String industry,
            String targetAudience,
            String market,
            String shortDescription,
            String fullDescription
    ) {
        this.projectName = projectName;
        this.industry = industry;
        this.targetAudience = targetAudience;
        this.market = market;
        this.shortDescription = shortDescription;
        this.fullDescription = fullDescription;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getIndustry() {
        return industry;
    }

    public String getTargetAudience() {
        return targetAudience;
    }

    public String getMarket() {
        return market;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public String getFullDescription() {
        return fullDescription;
    }
}
