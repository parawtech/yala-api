package tech.rket.shared.infrastructure.js;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NashronJsExecutor {
    private final ScriptEngine engine;
    private static final List<Pattern> PLACEHOLDER_PATTERNS = new ArrayList<>();

    static {
        PLACEHOLDER_PATTERNS.add(Pattern.compile("\\$\\{([^}]+)}"));
        PLACEHOLDER_PATTERNS.add(Pattern.compile("\\{\\{(?:js|javascript)([^}]+)}}"));
    }

    public NashronJsExecutor() {
        ScriptEngineManager manager = new ScriptEngineManager();
        this.engine = manager.getEngineByName("nashorn");
        this.engine.setContext(new SimpleScriptContext());
    }

    public String eval(String template, Map<String, Object> context) {
        clearContext();
        for (Map.Entry<String, Object> entry : context.entrySet()) {
            engine.put(entry.getKey(), entry.getValue());
        }
        Optional<Matcher> matcherOptional = findMatcher(template);
        if (matcherOptional.isEmpty()) {
            return template;
        }
        Matcher matcher = matcherOptional.get();
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            String jsExpression = matcher.group(1).trim();
            Object result;
            try {
                result = engine.eval(jsExpression);
            } catch (ScriptException e) {
                result = "";
            }
            if (result == null || result.equals("null")) {
                result = "";
            }
            matcher.appendReplacement(sb, Matcher.quoteReplacement(String.valueOf(result)));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public void clearContext() {
        engine.setContext(new SimpleScriptContext());
    }

    private Optional<Matcher> findMatcher(String template) {
        for (Pattern pattern : PLACEHOLDER_PATTERNS) {
            Matcher matcher = pattern.matcher(template);
            if (matcher.find()) {
                return Optional.of(matcher.reset());
            }
        }
        return Optional.empty();
    }

}
