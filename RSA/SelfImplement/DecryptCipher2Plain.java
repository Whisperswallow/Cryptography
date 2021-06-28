package com.sample;

import java.io.*;
import java.math.BigInteger;
import java.util.*;

public class DecryptCipher2Plain {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        RSAWithBigInteger rsawi = null;
        StringBuilder outString;
        //String file = "src/RSA-" + sc.nextInt() + "-bits_with_BigInteger_version.rsa";
        String file = "src/RSA-2048-bits_with_BigInteger_version.rsa";
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            rsawi = (RSAWithBigInteger) ois.readObject();
            System.out.println("Load object success.");
        } catch (Exception e) {
            System.out.println("Failed to read data!\n Error message : " + e.getMessage());
        }
        if (rsawi != null) {
            try (BufferedReader br = new BufferedReader(new FileReader("src/cipher_dummy.txt"));
                    FileWriter fw = new FileWriter("src/decrypt_dummy.txt")) {
                String in;
                byte[] bArray;
                while ((in = br.readLine()) != null) {
                    outString = new StringBuilder();
                    bArray = rsawi.decrypt(new BigInteger(in,16)).toByteArray();
                    for(byte b:bArray){
                        //System.out.print((char)b);
                        outString.append((char)b);
                    }
                    fw.write(outString.toString());
                    fw.flush();
                }
            } catch (Exception e) {
                System.out.println("Failed to read text!\n Error message : " + e.getMessage());
            }
        }
    }

}