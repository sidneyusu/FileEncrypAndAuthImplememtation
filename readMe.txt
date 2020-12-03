/**
 * 
 * @author Sidney Shane Dizon
 * UCID: 10149277
 * CPSC 418 Assignment 1 Problem 6
 * readme.txt
 *
 */

Description:
-List of files submitted:
    -secureFile.java
        -Takes in a file and encrypts the contents of the file
        -Writes the encryption to a text file specified in the command line arguments
        -MAC Algorithm:
            -HMACSHA1
        -PRNG:
            Type: SecureRandom
            Source: SHA1PRNG - JAVA Built in 
        -Method of Encoding Input to AES-128
            -input = file|digest 
                -where the digest is always 20 bytes long
        -Compile and Run:
            -java secureFile [plaintext-filename] [ciphertext-output-filename] [seed]
        Server: 
            -linux.cpsc.ucalgary.ca
        
        The Problem is Solved in Full 

    -decryptFile.java
        -Takes in a text file and decrypts the contents of the file 
        -Checks whether there were modifications in the ciphertext
        -Prints a warning in the console and writes the recovered plaintext message from the ciphertext 
            to a text file specified in the command line arguments 
        -MAC Algorithm:
            -HMACSHA1
        -PRNG:
            Type: SecureRandom
            Source: SHA1PRNG - JAVA Built in 
        -Method of Decoding the Decrypted File
            -digestLength = 20;
            -byte[] msg = Arrays.copyOfRange(decrypted, 0, decrypted.length-digestLength);
			-byte[] digest = Arrays.copyOfRange(decrypted, decrypted.length-digestLength, decrypted.length);
        -Compile and Run:
            -java decryptFile [ciphertext-filename] [plaintext-output-filename] [seed]
        -Server:
            -linux.cpsc.ucalgary.ca
            
        The Problem is Solved in Full 
        
    -readMe.txt 
        -Read me descriptions of using the java Programs

