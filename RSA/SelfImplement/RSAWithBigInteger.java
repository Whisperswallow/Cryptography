package com.sample;

import java.io.*;
import java.math.BigInteger;
import java.security.SecureRandom;

public class RSAWithBigInteger implements Serializable {

    private final BigInteger privateKey;
    public final BigInteger publicKey, pTq;
    private final int bits;

    public RSAWithBigInteger(int bits) { // bits : maximum bits for p and q
        this.bits = bits;
        BigInteger[] pq = primeGenerator(bits);
        System.out.println("p = " + pq[0]);
        System.out.println("q = " + pq[1]);
        BigInteger phi_n = (pq[0].subtract(BigInteger.ONE))
                .multiply(pq[1].subtract(BigInteger.ONE));
        pTq = pq[0].multiply(pq[1]);
        System.out.println("pTq = " + pTq);

        BigInteger tempKey;
        do {
            tempKey = BigInteger.probablePrime(bits - 1, new SecureRandom());
        } while (!tempKey.gcd(phi_n).equals(BigInteger.ONE));
        publicKey = tempKey;
        System.out.println("publicKey = " + publicKey);
        privateKey = tempKey.modInverse(phi_n);
        System.out.println("privateKey = " + privateKey);
    }

    public static BigInteger encrypt(BigInteger P, BigInteger pKey, BigInteger n) {
        return P.modPow(pKey, n);
    }

    public BigInteger decrypt(BigInteger C) {
        return C.modPow(privateKey, pTq);
    }

    public void toFile() {
        // save the RSA object to file

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("src/RSA-" + 2 * bits + "-bits_with_BigInteger_version.rsa"))) {
            oos.writeObject(this);
            oos.flush();
            System.out.println("Writing data success...");
        } catch (IOException e) {
            System.out.println("Fail to write data!\n Error message : " + e.getMessage());
        }
    }

    public void keyFile() {
        // save the public key and pTq to file for deliverying

        try (FileWriter fw = new FileWriter("src/Key_File-" + (bits << 1) + "-bits.key")) {
            fw.write(publicKey.toString(16) + "\n");
            fw.write(pTq.toString(16));
            fw.flush();
            System.out.println("Writing key file success.");
        } catch (IOException e) {
            System.out.println("Fail to write key to file!\n Error message : " + e.getMessage());
        }
    }

    private BigInteger[] primeGenerator(int bits) {
        System.out.println("Prime generating");
        SecureRandom generator = new SecureRandom();
        BigInteger[] pq = new BigInteger[2];

        pq[0] = BigInteger.probablePrime(bits, generator);
        do {
            pq[1] = BigInteger.probablePrime(bits - 1, generator);
        } while (pq[0].equals(pq[1]));

        return pq;
    }
}
