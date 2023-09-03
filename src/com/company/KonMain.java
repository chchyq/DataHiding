package com.company;

import java.math.BigInteger;
import java.security.SecureRandom;

public class KonMain {
    public static void main(String[] args) {
        SecureRandom random = new SecureRandom(new byte[]{0x01});

        System.out.print("Testing nextInt()..");
        int[] expected = {417885477, -1770363278, -364100061, -923580003, -1116182179, -512665530, 1024819304, 523492671};
        for (int value : expected) {
            int generated = random.nextInt();
            if (generated != value) {
                throw new RuntimeException("Nope! " + generated + " != " + value);
            }
        }
        System.out.println(" works :)");

        System.out.print("Testing probablePrime()..");
        int[] primes = {251, 151, 139, 233, 241, 229, 251, 151};
        for (int prime : primes) {
            BigInteger generated = BigInteger.probablePrime(8, random);
            if (!generated.equals(BigInteger.valueOf(prime))) {
                throw new RuntimeException("Nope! " + generated + " != " + prime);
            }
        }
        System.out.println(" works :)");
    }
}
