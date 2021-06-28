package com.sample;

import java.io.Serializable;
import java.security.SecureRandom;
import java.util.Random;

public class ExtendInteger implements Serializable {

    public long[] value;
    // A 64-bits long integers is used to save 32-bits data only, and each 
    // value[i] saves the (63*i+1)-th bit to the (63*i+64)-th bit of the 
    // ExtendInteger.

    public static final long BOUND = (long) 1 << 32;
    public boolean sign = true;
    // There is 4096 bits but only 2048 bits are used,
    // other bits are reserved for multipling easily.

    public ExtendInteger(String in) {
        // Constructor for string input (for initial input)
        // I still don't know how to convert the string of a very large number
        // into a 01-string or spliting into several integer for reading, so
        // this constructor is not supported yet.

        System.err.println("Not support yet!");
        value = new long[64];
    }

    public ExtendInteger(long[] in) {
        // constructor for long array input (for copying data)

        value = new long[64];
        System.arraycopy(in, 0, value, 0, in.length);
    }

    public ExtendInteger(boolean[] in) {
        // constructor for boolean array input

        value = new long[64];
        boolean[] temp = new boolean[64 * 32];

        if (in.length > 64 * 32) { // cut the bits string that is too long
            System.err.println("bits too long!");
            System.arraycopy(in, in.length - 64 * 32, temp, 0, in.length);
        } else { // filled zeros to unused entries
            System.arraycopy(in, 0, temp, 64 * 32 - in.length, in.length);
        }

        for (int i = 63; i >= 0; i--) {
            boolean[] tempIn = new boolean[64];
            System.arraycopy(temp, 32 * i, tempIn, 32, 32);
            value[63 - i] = MainClass.b2d(tempIn);
        }
    }

    @Override
    public String toString() {
        // Convert ExtendInteger to binary array (easily showing on screen).
        // Using the form of a*(2^32)^i for i = 1,...,63 make me easy to check
        // the result with caculator on website.

        String target = "";
        for (int i = 0; i < 64; i++) {
            if (value[i] != 0) {
                target += "+" + value[i] + "(2^32)^" + i;
            }
        }
        return ("".equals(target)) ? "0" : (target + " , " + ((sign) ? "pos" : "neg"));
    }

    public boolean[] binary(boolean compress) {
        // convert ExtendInteger to binary array
        // compress : whether you want to compress the result

        boolean[] origin = new boolean[64 * 32];
        for (int i = 63; i >= 0; i--) {
            boolean[] temp = MainClass.d2b(value[i]);
            System.arraycopy(temp, 32, origin, (63 - i) * 32, 32);
        }

        if (!compress) { // return 64*32 length boolean[]
            return origin;
        }

        // delete all 0s before the first 1 appear
        int bits = 0;
        for (int i = 0; i < origin.length; i++) {
            if (origin[i]) {
                bits = origin.length - i;
                break;
            }
        }
        boolean[] target = new boolean[bits];
        System.arraycopy(origin, 64 * 32 - bits, target, 0, bits);
        return target;
    }

    public static ExtendInteger add(ExtendInteger ei1, ExtendInteger ei2) {

        // dealing with sign
        if (ei1.sign ^ ei2.sign) {
            ExtendInteger n1 = new ExtendInteger(ei1.value),
                    n2 = new ExtendInteger(ei2.value);
            return (ei1.sign) ? subtract(n1, n2) : subtract(n2, n1);
        }

        ExtendInteger target = new ExtendInteger(ei1.value);
        if (!ei1.sign) {
            target.sign = false;
        }

        // Compute number part
        boolean carryBit = false;
        for (int i = 0; i < 64; i++) {
            if (carryBit) {
                target.value[i]++;
                carryBit = false;
            }
            target.value[i] += ei2.value[i];
            if (target.value[i] >= BOUND) {
                if (i == 63) {
                    System.err.println("Overflow warning!");
                }
                target.value[i] -= BOUND;
                carryBit = true;
            }
        }
        return target;
    }

    public static ExtendInteger subtract(ExtendInteger ei1, ExtendInteger ei2) {

        ExtendInteger target = zero(), n1, n2;

        // dealing with sign
        if (ei1.sign ^ ei2.sign) {
            n1 = new ExtendInteger(ei1.value);
            n2 = new ExtendInteger(ei2.value);
            target = add(n1, n2);
            target.sign = ei1.sign;
            return target;
        }
        if (!ei1.sign) {
            n1 = new ExtendInteger(ei2.value);
            n2 = new ExtendInteger(ei1.value);
        } else {
            n1 = new ExtendInteger(ei1.value);
            n2 = new ExtendInteger(ei2.value);
        }
        if (!larger(n1, n2)) {
            ExtendInteger temp = new ExtendInteger(n1.value);
            n1 = new ExtendInteger(n2.value);
            n2 = new ExtendInteger(temp.value);
            target.sign = false;
        }

        // Compute number part
        boolean carryBit = false;
        for (int i = 0; i < 64; i++) {
            if (carryBit) {
                target.value[i]--;
                carryBit = false;
            }
            target.value[i] += n1.value[i] - n2.value[i];
            if (target.value[i] < 0) {
                target.value[i] += BOUND;
                carryBit = true;
            }
        }
        return target;
    }

    public static ExtendInteger multiple(ExtendInteger ei1, ExtendInteger ei2) {
        // Need to be improved

        ExtendInteger target = new ExtendInteger(new boolean[1]);

        // dealing with sign
        target.sign = !ei1.sign ^ ei2.sign;

        // Compute number part
        long carry = 0, tmp;
        for (int i = 0; i < 32; i++) {
            for (int j = 0; j < 32; j++) {
                target.value[i + j] += carry;
                tmp = ei1.value[i] * ei2.value[j];
                carry = tmp >>> 32;
                tmp ^= carry << 32;
                target.value[i + j] += tmp;
                while ((tmp = target.value[i + j] >>> 32) > 0) {
                    target.value[i + j] ^= tmp << 32;
                    carry += tmp;
                }
            }
            if (carry != 0) {
                target.value[i + 32] += carry;
            }
        }
        return target;
    }

    public static ExtendInteger integerDivision(ExtendInteger ei1, ExtendInteger ei2) {

        ExtendInteger target = new ExtendInteger(new boolean[1]);

        // dealing with sign
        target.sign = ei1.sign ^ ei2.sign;

        // Compute number part
        if (!larger(ei1, ei2)) {
            return zero();
        }
        ExtendInteger quotient = zero(),
                remainder = new ExtendInteger(ei1.value),
                multiple, devisor;

        while (larger(remainder, ei2)) {
            devisor = new ExtendInteger(ei2.value);
            multiple = one();
            while (larger(remainder, add(devisor, devisor))) {
                devisor = add(devisor, devisor);
                multiple = add(multiple, multiple);
            }
            while (larger(remainder, devisor)) {
                remainder = subtract(remainder, devisor);
                quotient = add(quotient, multiple);
            }
        }

        return quotient;
    }

    public static ExtendInteger mod(ExtendInteger a, ExtendInteger x, ExtendInteger n) {
        // only for convenient when calling this function

        boolean[] xb = x.binary(true);
        return mod(a, xb, n);
    }

    public static ExtendInteger mod(ExtendInteger a, boolean[] x, ExtendInteger n) {
        // compute a^x mod n

        ExtendInteger y = one(), aMn = mod(a, n);
        // pre-calculate mod(a, n) since it need to be called many times

        for (int i = 0; i < x.length; i++) {
            y = mod(multiple(y, y), n);
            if (x[i]) {
                y = mod(multiple(y, aMn), n);
            }
        }
        return y;
    }

    public static ExtendInteger mod(ExtendInteger a, ExtendInteger n) {
        // use integer division compute a mod n

        if (!larger(a, n)) { // when n > a, a is the answer
            return a;
        }

        return subtract(a, multiple(integerDivision(a, n), n));
    }

    public static boolean larger(ExtendInteger ei1, ExtendInteger ei2) {
        for (int i = 63; i >= 0; i--) {
            if (ei1.value[i] != ei2.value[i]) {
                return ei1.value[i] > ei2.value[i];
            }
        }
        return true;
    }

    public static boolean equal(ExtendInteger ei1, ExtendInteger ei2) {
        if (ei1 == ei2) {
            return true;
        }
        
        for (int i = 63; i >= 0; i--) {
            if (ei1.value[i] != ei2.value[i]) {
                return false;
            }
        }
        return true;
    }

    public static ExtendInteger zero() {
        // generate 0

        boolean[] temp = {false};
        return new ExtendInteger(temp);
    }

    public static ExtendInteger one() {
        // generate 1

        boolean[] temp = {true};
        return new ExtendInteger(temp);
    }

    public static boolean primalityTest(ExtendInteger n) {
        // based on Fermat's little theorem

        SecureRandom generator = new SecureRandom();
        ExtendInteger a;
        int bNlen = n.binary(true).length, t;

        t = bNlen; // I think this much times is enough
        for (; t >= 0; t--) {
            a = randomEI(bNlen, generator);
            if (!equal(mod(a, subtract(n, one()), n), one())) {
                return false;
            }
        }
        return true; // n is prime whp if t is large enough
    }

    public static ExtendInteger randomEI(int bits, Random generator) {
        // generate random ExtendInteger for given bits.

        boolean[] rn = new boolean[bits];
        rn[0] = true;
        for (int i = 1; i < bits - 1; i++) {
            rn[i] = generator.nextDouble() < 0.5;
        }
        rn[bits - 1] = true;
        return new ExtendInteger(rn);
    }

    public static ExtendInteger randomEI(int bits, boolean[] head, Random generator) {
        // generate random ExtendInteger for given bits and fixed head.

        if (head.length >= bits) { // if head is enough for needed bits, just return.
            return new ExtendInteger(head);
        }

        boolean[] rn = new boolean[bits];
        System.arraycopy(head, 0, rn, 0, head.length);
        for (int i = head.length; i < bits - 1; i++) {
            rn[i] = generator.nextDouble() < 0.5;
        }
        rn[bits - 1] = true;
        return new ExtendInteger(rn);
    }
}
