package com.sample;

import java.util.Scanner;

public class RSAFileGenerater {

    public static void main(String[] args) {
        //Scanner sc = new Scanner(System.in);
        long t1 = System.currentTimeMillis();
        RSAWithBigInteger rsawi = new RSAWithBigInteger(4000);
        long t2 = System.currentTimeMillis();
        System.out.println(t2-t1);
        //rsawi.toFile();
        //rsawi.keyFile();
    }
    
}
