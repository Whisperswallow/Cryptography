package com.sample;

import java.io.*;
import java.security.SecureRandom;

public class RSA implements Serializable {

    private final ExtendInteger privateKey;
    public final ExtendInteger publicKey, pTq;
    private final int bits; 

    public RSA(int bits) { // bits : maximum bits for p and q
        this.bits = bits;
        //System.out.println("Start constructing RSA object.");
        ExtendInteger[] pq = primeGenerator(bits);
        System.out.println("p = " + pq[0]);
        System.out.println("q = " + pq[1]);
        ExtendInteger phi_n = ExtendInteger.multiple(
                ExtendInteger.subtract(pq[0], ExtendInteger.one()),
                ExtendInteger.subtract(pq[1], ExtendInteger.one()));
        pTq = ExtendInteger.multiple(pq[0], pq[1]);
        System.out.println("pTq = " + pTq);

        // Compute a pair of keys
        ExtendInteger tempKey;
        ExtendInteger[] MMIR;
        do {
            tempKey = ExtendInteger.randomEI(bits - 1, new SecureRandom());
            MMIR = MMI(phi_n, tempKey);
        } while (!ExtendInteger.equal(MMIR[2], ExtendInteger.one()));
        publicKey = tempKey;
        System.out.println("publicKey = " + publicKey);
        privateKey = MMIR[1];
        System.out.println("privateKey = " + privateKey);
    }

    public ExtendInteger encrypt(ExtendInteger P) {
        // encrypt function : C = P^e mod n
        
        return ExtendInteger.mod(P, privateKey, pTq);
    }

    public static ExtendInteger decrypt(ExtendInteger C, ExtendInteger pKey, ExtendInteger n) {
        // decrypt function : P = C^d mod n
        
        return ExtendInteger.mod(C, pKey, n);
    }
    
    public void toFile(){
        // save the RSA object to file
        
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("src/RSA-" + 2*bits + "-bits_version.rsa"))) {
            oos.writeObject(this);
            oos.flush();
            System.out.println("Writing data success...");
        } catch (IOException e) {
            System.out.println("Fail to write data!\n Error message : " + e.getMessage());
        }
    }

    private ExtendInteger[] primeGenerator(int bits) {
        // generating a pair of primes p and q.
        
        SecureRandom generator = new SecureRandom();
        ExtendInteger p, q;
        
        // define header of p and q respectively to make p and q not too close.
        boolean[] pH = {true, false, true}, qH = {true, false, false};
        
        // generate p
        p = ExtendInteger.randomEI(bits, pH, generator);
        while (!ExtendInteger.primalityTest(p)) {
            p = ExtendInteger.randomEI(bits, pH, generator);
        }
        
        // generate q
        q = ExtendInteger.randomEI(bits, qH, generator);
        while (!ExtendInteger.primalityTest(q)) {
            q = ExtendInteger.randomEI(bits, qH, generator);
        }
        
        ExtendInteger[] pq = {p, q};
        return pq;
    }

    public ExtendInteger[] MMI(ExtendInteger ei1, ExtendInteger ei2) {
        // Compute modular multiplicative inverse by Extended Euclidean algorithm
        // according to the code of EEA implementing with C on wikipedia.

        ExtendInteger r, s, t, old_r, old_s, old_t, q, temp;
        ExtendInteger[] target = new ExtendInteger[3];
        
        if (ExtendInteger.equal(ei2, ExtendInteger.zero())) {
            target[0] = ExtendInteger.one();
            target[1] = ExtendInteger.zero();
            target[2] = ExtendInteger.zero();
            return target;
        }
        old_r = new ExtendInteger(ei1.value);
        r = new ExtendInteger(ei2.value);
        old_s = ExtendInteger.one();
        s = ExtendInteger.zero();
        old_t = ExtendInteger.zero();
        t = ExtendInteger.one();

        while (!ExtendInteger.equal(r, ExtendInteger.zero())) {
            q = ExtendInteger.integerDivision(old_r, r);

            temp = new ExtendInteger(old_r.value);
            temp.sign = old_r.sign;
            old_r = new ExtendInteger(r.value);
            old_r.sign = r.sign;
            r = ExtendInteger.subtract(temp, ExtendInteger.multiple(q, r));

            temp = new ExtendInteger(old_s.value);
            temp.sign = old_s.sign;
            old_s = new ExtendInteger(s.value);
            old_s.sign = s.sign;
            s = ExtendInteger.subtract(temp, ExtendInteger.multiple(q, s));

            temp = new ExtendInteger(old_t.value);
            temp.sign = old_t.sign;
            old_t = new ExtendInteger(t.value);
            old_t.sign = t.sign;
            t = ExtendInteger.subtract(temp, ExtendInteger.multiple(q, t));
        }
        
        target[0] = old_s;
        target[1] = (old_t.sign) ? old_t : ExtendInteger.add(old_t, ei1);
        target[2] = old_r;
        return target;
    }
}
