package kz.diplomka.startupmatch.ui.home.module;

public class CommandMemberData {

    private final String fullName;
    private final String role;
    private final String experience;
    private final String portfolio;
    private final String avatarUri;

    public CommandMemberData(String fullName, String role, String experience, String portfolio, String avatarUri) {
        this.fullName = fullName;
        this.role = role;
        this.experience = experience;
        this.portfolio = portfolio;
        this.avatarUri = avatarUri;
    }

    public String getFullName() {
        return fullName;
    }

    public String getRole() {
        return role;
    }

    public String getExperience() {
        return experience;
    }

    public String getPortfolio() {
        return portfolio;
    }

    public String getAvatarUri() {
        return avatarUri;
    }
}
