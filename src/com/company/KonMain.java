//package src.com.company;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;

public class KonMain {
    public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchProviderException {
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
        random.setSeed(new byte[]{0x01});

        System.out.print("Testing nextInt()..");
        int[] expected = {170601090, 1365618327, 1120287500, 1843067938};
        for (int value : expected) {
            int generated = random.nextInt();
            if (generated != value) {
                throw new RuntimeException("Nope! " + generated + " != " + value);
            }
        }
        System.out.println(" works :)");

        System.out.print("Testing probablePrime()..");
        int[] primes = {223, 157, 137, 199};
        for (int prime : primes) {
            BigInteger generated = BigInteger.probablePrime(8, random);
            if (!generated.equals(BigInteger.valueOf(prime))) {
                throw new RuntimeException("Nope! " + generated + " != " + prime);
            }
        }
        System.out.println(" works :)");
    }
}
