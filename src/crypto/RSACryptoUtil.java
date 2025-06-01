package crypto;

import java.security.*;
import javax.crypto.Cipher;

public class RSACryptoUtil {
    public static byte[] encrypt(byte[] data, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        
        return cipher.doFinal(data);
    }

    public static byte[] decrypt(byte[] data, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        
        return cipher.doFinal(data);
    }

    public static byte[] sign(byte[] data, PrivateKey privateKey) throws Exception {
        Signature signature = Signature.getInstance("SHA256withRSA");
        
        signature.initSign(privateKey);
        signature.update(data);
        
        return signature.sign();
    }

    public static boolean verify(byte[] data, byte[] signatureBytes, PublicKey publicKey) throws Exception {
        Signature signature = Signature.getInstance("SHA256withRSA");
        
        signature.initVerify(publicKey);
        signature.update(data);
        
        return signature.verify(signatureBytes);
    }
}