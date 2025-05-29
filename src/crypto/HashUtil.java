package crypto;

import java.security.MessageDigest;
import java.util.Arrays;

public class HashUtil {
    public static byte[] generateSHA256(byte[] data) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        return md.digest(data);
    }

    public static boolean verifySHA256(byte[] data, byte[] hash) throws Exception {
        byte[] calcHash = generateSHA256(data);
        return Arrays.equals(calcHash, hash);
    }
}
