package com.sample;

public class DES {

    private final boolean[][] key = new boolean[16][];

    public DES(long key) {
        keySchedule(d2b(key));
    }

    private boolean[] d2b(long key) {
        boolean[] target = new boolean[64];
        if (key < 0) {
            target[0] = true;
            key += Long.MIN_VALUE;
        } else {
            target[0] = false;
        }
        for (int i = 63; i > 0; i--) {
            target[i] = key % 2 == 1;
            key >>= 1;
        }
        return target;
    }

    public boolean[] encrypt(boolean[] in) {
        boolean[] temp = Table.IP(in);
        boolean[] r = new boolean[32], l = new boolean[32], oldL;
        for (int i = 0; i < 32; i++) {
            l[i] = temp[i];
            r[i] = temp[32 + i];
        }
        for (int i = 0; i < 16; i++) {
            oldL = l;
            l = r;
            r = xor(oldL, f(r, i));
        }
        return Table.IIP(merge(r, l));
    }

    public boolean[] decrypt(boolean[] in) {
        return encrypt(in);
    }

    private boolean[] f(boolean[] in, int i) { // f function
        boolean[] B = xor(Table.E(in), key[i]), dB = new boolean[6], C = {};
        for (int j = 0; j < 8; j++) {
            for (int k = 0; k < 6; k++) {
                dB[k] = B[6 * j + k];
            }
            C = merge(C, Table.S(dB, j));
        }
        return Table.P(C);
    }

    private boolean[] xor(boolean[] t1, boolean[] t2) { // "+" in finite field 2
        if (t1.length != t2.length) {
            return null;
        }
        boolean[] target = new boolean[t1.length];
        for (int i = 0; i < t1.length; i++) {
            target[i] = !t1[i] == t2[i];
        }
        return target;
    }

    private boolean[] LS(boolean[] in, int i) { // left-shift
        boolean[] target = new boolean[in.length];
        int pointer, gamma = (i == 1 || i == 2 || i == 9 || i == 16) ? 1 : 2;
        for (int j = 0; j < in.length; j++) {
            pointer = (j + gamma) % in.length;
            target[j] = in[pointer];
        }
        return target;
    }

    private void keySchedule(boolean[] in) { // generate 16 keys with given key
        boolean[] y = Table.PC1(in), C = new boolean[28], D = new boolean[28];
        for (int i = 0; i < 28; i++) {
            C[i] = y[i];
            D[i] = y[28 + i];
        }
        for (int i = 0; i < 16; i++) {
            LS(C, i + 1);
            LS(D, i + 1);
            key[i] = Table.PC2(merge(C, D));
        }
    }

    private boolean[] merge(boolean[] t1, boolean[] t2) { // merge 2 array
        boolean[] target = new boolean[t1.length + t2.length];
        int ct = 0;
        while (ct < t1.length && ct < t2.length) {
            target[ct] = t1[ct];
            target[t1.length + ct] = t2[ct++];
        }
        while (ct < t1.length) {
            target[ct] = t1[ct++];
        }
        while (ct < t2.length) {
            target[ct + t1.length] = t2[ct++];
        }
        return target;
    }
}
