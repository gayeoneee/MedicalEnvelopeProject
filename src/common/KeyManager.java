package common;

// 리펙토링때 이거 고치면 좋을듯 (광범위 -> 특정 모듈)
import java.io.*;
import java.security.*;

public class KeyManager {
    public static PublicKey loadPublicKey(String path) throws Exception {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path))) {
            return (PublicKey) ois.readObject();
        }
    }

    public static PrivateKey loadPrivateKey(String path) throws Exception {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path))) {
            return (PrivateKey) ois.readObject();
        }
    }
}
