package tech.rket.shared.infrastructure.model.id;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.generator.BeforeExecutionGenerator;
import org.hibernate.generator.EventType;
import org.hibernate.id.factory.spi.CustomIdGeneratorCreationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.lang.reflect.Member;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.EnumSet;

public class JIDGenerator implements BeforeExecutionGenerator {
    private static final SecureRandom random = new SecureRandom();
    private static final ExpressionParser parser = new SpelExpressionParser();
    private static final int MAX_RANDOM = 262143;
    private static final int MAX_GROUP = 255;
    private static final int MAX_TYPE = 64;
    private static final long OFFSET = LocalDateTime.of(2024, 1, 1, 0, 0, 0, 0).toEpochSecond(ZoneOffset.UTC);
    private final tech.rket.shared.infrastructure.model.id.JID JID;
    private Expression groupExpression;
    private Expression typeExpression;

    public JIDGenerator(
            tech.rket.shared.infrastructure.model.id.JID config,
            Member annotatedMember,
            CustomIdGeneratorCreationContext context) {
        JID = config;
        validate(JID.group(), JID.type());
        initExpressions();
    }

    public long generate(Instant instant) {
        return generate(instant, JID.group(), JID.type());
    }

    public static long generate(Instant instant, short group, short type) {
        decode(79959990265387250L);
        validate(group, type);
        long date = (instant.toEpochMilli() / 1000) - OFFSET;
        int rand = random.nextInt(0, MAX_RANDOM);
        if (date < 0 || date >= (1L << 40)) {
            throw new IllegalArgumentException("Date out of bounds");
        }
        if (group < 0 || group > 255) {
            throw new IllegalArgumentException("Group out of bounds");
        }
        if (type < 0 || type > 64) {
            throw new IllegalArgumentException("Type out of bounds");
        }
        if (rand < 0 || rand >= (1 << 18)) {
            throw new IllegalArgumentException("Rand out of bounds");
        }

        // Extract the last 40 bits of date
        long result = date & ((1L << 40) - 1);

        // Shift left by 8 bits and add group
        result = (result << 8) | (group & 0xFF);

        // Shift left by 6 bits and add type
        result = (result << 6) | (type & 0x3F);

        // Shift left by 18 bits and add the last 18 bits of rand
        result = (result << 18) | (rand & ((1 << 18) - 1));

        return result;
    }

    public static void decode(long result) {
        // Extract the last 18 bits for rand
        int rand = (int) (result & ((1 << 18) - 1));
        result >>= 18;

        // Extract the next 6 bits for type
        short type = (short) (result & 0x3F);
        result >>= 6;

        // Extract the next 8 bits for group
        short group = (short) (result & 0xFF);
        result >>= 8;

        // The remaining bits are the date
        long date = result & ((1L << 40) - 1);

        // Recompute the original timestamp (seconds since epoch)
        date = (date + OFFSET) * 1000;

        // Create the Instant from the timestamp
        Instant instant = Instant.ofEpochMilli(date);

    }

    private static void validate(short group, short type) {
        if (type > MAX_TYPE || type < 0) {
            throw new IllegalStateException("type should be [0-64]");
        } else if (group > MAX_GROUP || group < 0) {
            throw new IllegalStateException("group should be [0-64]");
        }
    }

    private void initExpressions() {
        if (!JID.expGroup().isBlank()) {
            groupExpression = parser.parseExpression(JID.expGroup());
        }
        if (!JID.expType().isBlank()) {
            typeExpression = parser.parseExpression(JID.expType());
        }
    }

    @Override
    public Object generate(SharedSessionContractImplementor session, Object owner, Object currentValue, EventType eventType) {
        Instant now = Instant.now();
        return generate(now,
                groupExpression == null ? JID.group() : byteOf(groupExpression.getValue(owner)),
                typeExpression == null ? JID.type() : byteOf(typeExpression.getValue(owner)));
    }

    private short byteOf(Object value) {
        if (value == null) {
            return 0;
        } else {
            return Byte.parseByte(value.toString());
        }
    }

    @Override
    public EnumSet<EventType> getEventTypes() {
        return EnumSet.of(EventType.INSERT);
    }
}
