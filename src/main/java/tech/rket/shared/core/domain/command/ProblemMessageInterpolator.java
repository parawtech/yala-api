package tech.rket.shared.core.domain.command;

import jakarta.validation.MessageInterpolator;

import java.util.Locale;

public class ProblemMessageInterpolator implements MessageInterpolator {
    @Override
    public String interpolate(String messageTemplate, Context context) {
        return messageTemplate;
    }

    @Override
    public String interpolate(String messageTemplate, Context context, Locale locale) {
        return messageTemplate;
    }
}