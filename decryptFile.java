/**
 * 
 * @author Sidney Shane Dizon
 * UCID: 10149277
 * CPSC 418 Assignment 1 Problem 6
 * decryptFile.java
 *
 */


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class decryptFile {
	private static KeyGenerator keyGen = null;
	private static KeyGenerator hmacKey = null;
	private static SecretKey secKey = null;
	private static byte[] raw = null;
	private static SecretKeySpec secKeySpec = null;
	private static Cipher secCipher = null;
	private static int digestLength = 20;

	public static void main(String[] args) {
		FileInputStream cipherFile = null;
		FileOutputStream msgFile = null;
		byte[] hmacHashCheck = null;
		
		try {
			//open files
			cipherFile = new FileInputStream(args[0]);
			msgFile = new FileOutputStream(args[1]);
			//read file into a byte array
			byte[] cipher = new byte[cipherFile.available()];
			cipherFile.read(cipher);
			
			//Parse the seed for the PRNG
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
			random.setSeed(args[2].getBytes());

			
			//Generate the keys
			hmacKey = KeyGenerator.getInstance("HMACSHA1");
			hmacKey.init(128, random);
			SecretKey secretKey = hmacKey.generateKey();

			Mac mac = Mac.getInstance("HMACSHA1");
			mac.init(secretKey);
			//Generate 128 AES Key
			keyGen = KeyGenerator.getInstance("AES");
			keyGen.init(128, random);
			secKey = keyGen.generateKey();
			raw = secKey.getEncoded();
			secKeySpec = new SecretKeySpec(raw, "AES");
			secCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

			//Generate IV 
			byte[] iv = new byte[128/8];
			random.nextBytes(iv);
			IvParameterSpec ivSpec = new IvParameterSpec(iv);

			
			//Decrypt the file
			secCipher.init(Cipher.DECRYPT_MODE, secKeySpec, ivSpec);
			byte[] decrypted = secCipher.doFinal(cipher);

			
			//Separate the msg and Digest
			byte[] msg = Arrays.copyOfRange(decrypted, 0, decrypted.length-digestLength);
			byte[] digest = Arrays.copyOfRange(decrypted, decrypted.length-digestLength, decrypted.length);

			
			//Generate the hash for the decrypted file
			
			hmacHashCheck = mac.doFinal(msg);
			//Check is the digest are equal
			if(Arrays.equals(hmacHashCheck, digest)) {
				System.out.println("The message was not tampered.");
			} else {
				System.out.println("WARNING: MESSAGE WAS TAMPERED.");
			}
			
			//Write the plaintext to the Output File
			msgFile.write(msg);
			msgFile.close();
			System.out.println("result written to: " + args[1]);
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	   /*
     * Converts a byte array to hex string
     * this code from http://java.sun.com/j2se/1.4.2/docs/guide/security/jce/JCERefGuide.html#HmacEx
     */
    public static String toHexString(byte[] block) {
        StringBuffer buf = new StringBuffer();

        int len = block.length;

        for (int i = 0; i < len; i++) {
             byte2hex(block[i], buf);
             if (i < len-1) {
                 buf.append(":");
             }
        } 
        return buf.toString();
    }
    /*
     * Converts a byte to hex digit and writes to the supplied buffer
     * this code from http://java.sun.com/j2se/1.4.2/docs/guide/security/jce/JCERefGuide.html#HmacEx
     */
    public static void byte2hex(byte b, StringBuffer buf) {
        char[] hexChars = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
                            '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        int high = ((b & 0xf0) >> 4);
        int low = (b & 0x0f);
        buf.append(hexChars[high]);
        buf.append(hexChars[low]);
    }
}
