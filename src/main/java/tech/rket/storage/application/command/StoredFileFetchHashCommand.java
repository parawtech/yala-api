package tech.rket.storage.application.command;

import jakarta.validation.constraints.NotNull;

public record StoredFileFetchHashCommand(@NotNull String characteristics, @NotNull String key) {

    @Override
    public String toString() {
        return String.format("%s/%s", characteristics, key);
    }
}
