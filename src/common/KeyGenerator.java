package common;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Scanner;

public class KeyGenerator {
	public static void main(String[] args) throws Exception {
		@SuppressWarnings("resource")
		Scanner scan = new Scanner(System.in);
		
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(1024); 
        KeyPair keypair = keyPairGen.generateKeyPair();

        PublicKey publicKey = keypair.getPublic();
        PrivateKey privateKey = keypair.getPrivate();
        
        System.out.print("공개키를 저장할 파일 이름: ");
        String publicFile = scan.next();
        FileOutputStream bos = new FileOutputStream(publicFile);
        ObjectOutputStream os = new ObjectOutputStream(bos);
        os.writeObject(publicKey);
        os.close();
        
        System.out.print("개인키를 저장할 파일 이름: ");
        String privateFile = scan.next();
        FileOutputStream bos2 = new FileOutputStream(privateFile);
        ObjectOutputStream os2 = new ObjectOutputStream(bos2);
        os2.writeObject(privateKey);
        os2.close();
        
    }
}
