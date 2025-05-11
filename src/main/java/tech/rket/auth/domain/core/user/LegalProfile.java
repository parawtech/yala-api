package tech.rket.auth.domain.core.user;

public record LegalProfile(
        String registeredName,
        String legalType,
        String tradingName,
        String registrationNumber,
        String countryOfRegistration
) implements UserProfile {
    @Override
    public String getType() {
        return "LEGAL";
    }

    @Override
    public String toName() {
        return String.format("%s %s", registeredName, legalType).trim();
    }
}