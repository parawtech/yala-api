package tech.rket.shared.infrastructure.redis;

import org.springframework.data.redis.serializer.RedisSerializer;

public class ByteArrayRedisSerializer implements RedisSerializer<byte[]> {

    @Override
    public byte[] serialize(byte[] bytes) {
        return bytes;
    }

    @Override
    public byte[] deserialize(byte[] bytes) {
        if (bytes == null || bytes.length < 1) {
            return new byte[0];
        }
        byte[] decodedBytes = new byte[bytes.length - 1];
        System.arraycopy(bytes, 1, decodedBytes, 0, bytes.length - 1);
        return decodedBytes;
    }
}
