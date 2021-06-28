package com.sample;

import java.util.*;

public class MainClass {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int bits = sc.nextInt();
        RSA rsa = new RSA(bits);

        char[] in = sc.next().toCharArray();
        boolean[] inB = new boolean[in.length];
        for (int i = 0; i < in.length; i++) {
            inB[i] = in[i] == '1';
        }
        ExtendInteger p = new ExtendInteger(inB);
        System.out.println("pt = " + p);

        ExtendInteger ep = rsa.encrypt(p);
        System.out.println("ep = " + ep);

        ExtendInteger dp = RSA.decrypt(ep, rsa.publicKey, rsa.pTq);
        System.out.println("dp = " + dp);
        
        rsa.toFile();
    }

    public static long b2d(boolean[] in) {
        // convert binary array to long integer

        long target = 0;
        for (int i = 0; i < 64; i++) {
            target <<= 1;
            if (in[i]) {
                target++;
            }
        }
        return target;
    }

    public static boolean[] d2b(long in) {
        // convert long integer to 64 bits binary array

        boolean[] target = new boolean[64];
        if (in < 0) {
            target[0] = true;
            in += Long.MIN_VALUE;
        } else {
            target[0] = false;
        }
        for (int i = 63; i > 0; i--) {
            target[i] = in % 2 == 1;
            in >>= 1;
        }
        return target;
    }

}
