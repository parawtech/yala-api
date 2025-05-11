package tech.rket.shared.infrastructure.model.domain;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.generator.BeforeExecutionGenerator;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.SecureRandom;

public class JdIdGenerator implements IdentifierGenerator, BeforeExecutionGenerator {
    private static final SecureRandom secureRandom = new SecureRandom();


    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        return generate();
    }

    public static BigInteger generate()  {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES * 2);
        buffer.putLong(System.currentTimeMillis());
        buffer.putInt(secureRandom.nextInt());
        return new BigInteger(buffer.array());
    }
}