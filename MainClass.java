package com.sample;

import java.util.Scanner;

public class MainClass {

    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter ID number:");
        char[] input = sc.next().toCharArray();
        if (!checkID(input)) {
            throw new Exception("Wrong input!");
        }
        System.out.println("Enter key:");
        long key = sc.nextLong();
        long eNum, dNum;
        int ct = 0;
        eNum = b2d(new DES(key + 13 * ct++).encrypt(d2b(c2long(input))));
        while (!isID(eNum % new Long("10000000000"))) {
            eNum = b2d(new DES(key + 13 * ct++).encrypt(d2b(eNum)));
        }
        System.out.println("eNum = " + eNum + ", ct = " + ct);
        System.out.println("ID number after encrypt:"
                + long2Id(eNum % new Long("10000000000")));
        dNum = eNum;
        while (ct-- > 0) {
            dNum = b2d(new DES(key + 13 * ct).encrypt(d2b(dNum)));
        }
        System.out.println("ID number after decrypt:" + long2Id(dNum));

    }

    private static long b2d(boolean[] in) {
        long target = 0;
        for (int i = 0; i < 64; i++) {
            target <<= 1;
            if (in[i]) {
                target++;
            }
        }
        return target;
    }

    private static boolean[] d2b(long in) {
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

    private static boolean checkID(char[] in) {
        if (in.length == 10) {
            try {
                if (in[0] >= 'A' && in[0] <= 'Z') {
                    if (in[1] == '1' || in[1] == '2') {
                        return 10 - (((in[0] - 'A' + 10) / 10 + ((in[0] - 'A') % 10 * 9)
                                + ((in[1] - '0') * 8) + ((in[2] - '0') * 7)
                                + ((in[3] - '0') * 6) + ((in[4] - '0') * 5)
                                + ((in[5] - '0') * 4) + ((in[6] - '0') * 3)
                                + ((in[7] - '0') * 2) + ((in[8] - '0') * 1)) % 10) == in[9] - '0';
                    }
                }
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    private static long c2long(char[] input) {
        long target = input[0] - 'A';
        for (int i = 1; i < input.length - 1; i++) {
            target *= 10;
            target += input[i] - '0';
        }
        return target;
    }

    private static boolean isID(long in) {
        if (in < 0 || in / 100000000 >= 26 || in / 100000000 < 0) {
            return false;
        }
        return in % 100000000 / 10000000 <= 1;
    }

    private static String long2Id(long in) {
        return String.format("%c%08d%d", (char)('A' + in / 100000000),
                in % 100000000,
                (10 - (in / 1000000000 + 1 + in / 100000000 % 10 * 9
                + in / 10000000 % 10 * 8 + in / 1000000 % 10 * 7
                + in / 100000 % 10 * 6 + in / 10000 % 10 * 5
                + in / 1000 % 10 * 4 + in / 100 % 10 * 3
                + in / 10 % 10 * 2 + in % 10) % 10));
    }
}
