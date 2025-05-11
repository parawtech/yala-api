package tech.rket.metric.infrastructure.aspect;

import io.micrometer.common.annotation.ValueExpressionResolver;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class SpelValueExpressionResolver implements ValueExpressionResolver {
    private static final ExpressionParser parser = new SpelExpressionParser();

    @Override
    public String resolve(String expression, Object context) {
        EvaluationContext evalContext = new StandardEvaluationContext(context);
        return parser.parseExpression(expression).getValue(evalContext, String.class);
    }
}