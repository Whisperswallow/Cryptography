package com.sample;

import java.io.*;
import java.math.BigInteger;
import java.util.*;

public class EncryptText2Cipher {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        //int bLen = sc.nextInt();
        //String file = "src/Key_File-" + (bLen << 1) + "-bits.key";
        int bLen = 1024;
        String file = "src/Key_File-" + (bLen << 1) + "-bits.key";
        BigInteger publicKey = null, n = null;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            publicKey = new BigInteger(br.readLine(), 16);
            n = new BigInteger(br.readLine(), 16);
            System.out.println("Load key success.");
        } catch (Exception e) {
            System.out.println("Failed to read key file!\n Error message : " + e.getMessage());
        }
        bLen >>= 3;
        if (publicKey != null && n != null) {
            try (FileReader fr = new FileReader("src/dummy.txt");
                    FileWriter fw = new FileWriter("src/cipher_dummy.txt")) {
                int in, ct = 0;
                byte[] bArray = new byte[bLen];
                BigInteger pText, cText;
                while ((in = fr.read()) != -1) {
                    bArray[ct] = (byte) in;
                    ct++;
                    if (ct == bLen) {
                        pText = new BigInteger(bArray);
                        cText = RSAWithBigInteger.encrypt(pText, publicKey, n);
                        fw.write(cText.toString(16) + "\n");
                        fw.flush();
                        ct = 0;
                    }
                }
                if (ct != 0) {
                    while (ct < bLen) {
                        bArray[ct] = (byte) ' ';
                        ct++;
                    }
                    pText = new BigInteger(bArray);
                    cText = RSAWithBigInteger.encrypt(pText, publicKey, n);
                    fw.write(cText.toString(16) + "\n");
                    fw.flush();
                }
            } catch (Exception e) {
                System.out.println("Failed to read text!\n Error message : " + e.getMessage());
            }
        }
    }

}
