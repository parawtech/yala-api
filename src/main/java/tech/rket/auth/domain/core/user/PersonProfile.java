package tech.rket.auth.domain.core.user;

public record PersonProfile(String prefix,
                            String firstName,
                            String middleName,
                            String lastName,
                            String suffix
) implements UserProfile {
    @Override
    public String getType() {
        return "PERSON";
    }

    @Override
    public String toName() {
        return String.join(" ",
                (prefix != null ? prefix : ""),
                firstName,
                (middleName != null ? middleName : ""),
                lastName,
                (suffix != null ? suffix : "")
        ).replaceAll("\\s+", " ").trim();
    }
}