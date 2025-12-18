package findcafe.cafe.util;

import lombok.RequiredArgsConstructor;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EncryptionUtil {

    private final StringEncryptor jasyptStringEncryptor;

    public String encrypt(String plainText) {
        return jasyptStringEncryptor.encrypt(plainText);
    }

    public String decrypt(String encryptedText) {
        return jasyptStringEncryptor.decrypt(encryptedText);
    }
}
