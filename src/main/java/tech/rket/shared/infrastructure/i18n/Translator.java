package tech.rket.shared.infrastructure.i18n;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class Translator implements ApplicationContextAware {
    private static final Map<String, MessageSource> MESSAGE_SOURCES = new ConcurrentHashMap<>();
    private static MessageSource defaultMessageSource = null;
    private static Locale normalize(Locale locale) {
        return locale == null ? LocaleContextHolder.getLocale() : locale;
    }

    private static MessageSource getMessageSource(@Nullable String group) {
        return group == null ? defaultMessageSource : messageSource(group);
    }

    private static String fetch(@Nullable String group, @Nonnull String key, @Nullable Locale locale) {
        if (key == null) {
            return null;
        }
        try {
            return getMessageSource(group).getMessage(key, new Object[0], normalize(locale));
        } catch (Exception e) {
            return null;
        }
    }

    public static String text(@Nonnull String group, @Nonnull String key, @Nonnull Locale locale) {
        return fetch(group, key, locale);
    }

    public static String text(@Nonnull String group, @Nonnull String key) {
        return fetch(group, key, null);
    }

    public static String text(@Nonnull String key) {
        return fetch(null, key, null);
    }

    public static String text(@Nonnull String key, @Nonnull Locale locale) {
        return fetch(null, key, locale);
    }

    public static MessageSource messageSource(String group) {
        return MESSAGE_SOURCES.computeIfAbsent(group, k -> {
            ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
            messageSource.setBasename(group);
            messageSource.setDefaultEncoding("UTF-8");
            return messageSource;
        });
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Translator.defaultMessageSource = applicationContext.getBean(MessageSource.class);
    }
}
