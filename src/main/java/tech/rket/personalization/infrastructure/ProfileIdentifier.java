package tech.rket.personalization.infrastructure;

import org.jetbrains.annotations.NotNull;

public record ProfileIdentifier(
        int index,
        String trait,
        String value) implements Comparable<ProfileIdentifier> {

    @Override
    public int compareTo(@NotNull ProfileIdentifier o) {
        return Integer.compare(this.index, o.index);
    }
}
