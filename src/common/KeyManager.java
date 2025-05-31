package common;

import java.io.*;
import java.security.*;
//리펙토링때 이거 고치면 좋을듯 (광범위 -> 특정 모듈)
/*
 import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.security.PublicKey;
import java.security.PrivateKey;
import java.security.GeneralSecurityException;
 */


public class KeyManager {
	
    public static PublicKey loadPublicKey(String fname) throws IOException, ClassNotFoundException {
    	
    	ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fname));
    	PublicKey publicKey = (PublicKey) ois.readObject();
    	ois.close();
    	
    	return publicKey;
    	
    	// 리팩토링으로 사용
    	/*
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fname))) {
            return (PublicKey) ois.readObject();
        }
        */
    }

    public static PrivateKey loadPrivateKey(String fname) throws IOException, ClassNotFoundException {        
    	ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fname));
    	PrivateKey privateKey = (PrivateKey) ois.readObject();
    	ois.close();
    	
    	return privateKey;
    	
    	// 리팩토링으로 사용
    	/*
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fname))) {
            return (PrivateKey) ois.readObject();
        }
        */
    }
}