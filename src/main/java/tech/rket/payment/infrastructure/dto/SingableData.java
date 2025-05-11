package tech.rket.payment.infrastructure.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public interface SingableData {
    String getSignData();

    void setSignData(String string);

    String getSingableData();

    default void sign(String key) {
        try {
            setSignData(encryptPkcs7(getSingableData(), key));
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | IllegalBlockSizeException | BadPaddingException |
                 InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }


    private static String encryptPkcs7(String str, String key) throws NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        byte[] keyBytes = Base64.getDecoder().decode(key);
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "DESede");

        Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);

        byte[] plainTextBytes = str.getBytes(StandardCharsets.UTF_8);
        byte[] cipherTextBytes = cipher.doFinal(plainTextBytes);
        return Base64.getEncoder().encodeToString(cipherTextBytes);
    }

    @Builder
    @Data
    @AllArgsConstructor
    class Impl implements SingableData {
        private final String data;
        private String signData;

        public String getSingableData() {
            return data;
        }
    }
}
