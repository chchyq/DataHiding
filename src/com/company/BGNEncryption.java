package com.company;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.PairingParameters;
import it.unisa.dia.gas.plaf.jpbc.field.base.AbstractPointElement;
import it.unisa.dia.gas.plaf.jpbc.pairing.a1.TypeA1CurveGenerator;
import it.unisa.dia.gas.plaf.jpbc.pairing.a1.TypeA1Pairing;
import it.unisa.dia.gas.plaf.jpbc.util.math.BigIntegerUtils;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.Objects;


public class BGNEncryption {
    private BigInteger m_R;
    private BigInteger m_Q; // This is the private key.
    private BigInteger m_Prime;
    private BigInteger m_Rand;

    public PublicKey gen(int bits) throws NoSuchAlgorithmException, NoSuchProviderException {
        System.out.println("Generating");

        SecureRandom rng = SecureRandom.getInstance("SHA1PRNG", "SUN");
        rng.setSeed(new byte[]{0x50});

        TypeA1CurveGenerator a1 = new TypeA1CurveGenerator(rng, 2, bits);//// the number of primes，bits:the bit length of each prime
        PairingParameters param = a1.generate();
        TypeA1Pairing pairing = new TypeA1Pairing(rng, param);

        BigInteger order = param.getBigInteger("n");
        m_R = param.getBigInteger("n0");//random number 1
        m_Q = param.getBigInteger("n1");//random number 2
        m_Prime = param.getBigInteger("p");

        assert m_R.multiply(m_Q).equals(order);
        System.out.println("\tr: " + m_R);
        System.out.println("\tq: " + m_Q);
        System.out.println("\torder: " + order);
        System.out.println("\tprime: " + m_Prime);

        Field<?> f = pairing.getG1();
        BigInteger l = param.getBigInteger("l");

        System.out.println("Choosing random element");

        Element random_generator = f.newRandomElement();
        System.out.println("\trandom: " + random_generator);

        random_generator.mul(l); // P=P^l /* not necessary to do E = E.method()! */
        System.out.println("\tgenerator P: " + random_generator);

        //p is prime, not the same as P here. unknown usage of p at least now. n is the order of the group.
        Element generator_pow_r = f.newElement();
        generator_pow_r.set(random_generator);
        generator_pow_r.mul(m_R); // Q = P^r

        System.out.println("\tgenerator Q: " + generator_pow_r);
        return new PublicKey(pairing, random_generator, generator_pow_r, order);
    }

    public Element encrypt(PublicKey PK, int msg, BigInteger secretMSG1) {
//        msg > 0
//        secretMSG1
        System.out.println("Encryption");
        System.out.println("\tmsg: " + msg);
        System.out.println("\tsecret: " + secretMSG1);
//        BigInteger t = BigInteger.valueOf(secretMSG1);
        Field<?> f = PK.getField();

        Element A = f.newElement();
        Element B = f.newElement();
        Element C = f.newElement();
        A = A.set(PK.getP());
        A = A.mul(BigInteger.valueOf(msg));
        System.out.println("\tA (PK.P * msg): " + A);

        B = B.set(PK.getQ());
        B = B.mul(secretMSG1);//mul:pow
        System.out.println("\tB (PK.Q ^ secret): " + B);
        m_Rand = secretMSG1;

        C = C.set(A);
        C = C.add(B);//multiply
        System.out.println("\tC (A + B) : " + C);

        Element D = f.newElement();
        D.set(randomChoice(PK, C, 1));
        System.out.println("\tD (random C): " + D);
        C.set(D);

        return C;
    }

    // trying to understand this
    public Element randomChoice(PublicKey PK, Element EnMsg, int secretMSG2) {
        System.out.println("Choose Ciphertext according to secretMSG2");
        Field<?> f = PK.getField();
        Element B = f.newElement();
        B = B.set(PK.getQ()); // h

        Element C = f.newElement();
        C = C.set(EnMsg);
        BigInteger modRes = mod(C, 2);
        BigInteger sMSG2 = BigInteger.valueOf(secretMSG2);
        System.out.println("\tResult of module: "+modRes);
        System.out.println("\tsecretMSG2: "+sMSG2);
        if (sMSG2.equals(modRes)) {
            System.out.println("\tUse the original CipherText=" + C);
            return C;
        }
        System.out.println("Different! RandomChoice started");
        Element New = f.newElement();
        New = New.set(C);
        System.out.println("\tBefore changing, current C =" + C);
        int rNew;

        for (int i = 1; i < 8; i++) {

            rNew = i;
//            String str = Integer.toBinaryString(rNew);
//            int R = Integer.parseInt(str);
//            BigInteger t= BigInteger.valueOf(R);
            BigInteger t = BigInteger.valueOf(rNew);
            B = B.mul(t);//mul:pow
            New = New.add(B);
            modRes = mod(New, 2);
            if (sMSG2.equals(modRes)) {
                System.out.println("\tadd " + t+" to Random Number");
                m_Rand = m_Rand.add(t);
                System.out.println("\tRandom Number now=" + m_Rand);
                C.set(New);
                break;
            }
            New = New.set(C);
        }

        System.out.println("\tModule of the final result now=" + modRes);
        System.out.println("\tsecretMSG2=" + sMSG2);
        return C;
    }

    public BigInteger mod(Element C, int value) {
        assert C instanceof AbstractPointElement<?, ?>;
        BigInteger y = ((AbstractPointElement<?, ?>) C).getY().toBigInteger();
        return y.mod(BigInteger.valueOf(value));
    }

    public Element add(PublicKey PK, Element A, Element B) {
        BigInteger t = BigIntegerUtils.getRandom(PK.getN());
        Field<?> f = PK.getField();
        Element output = f.newElement();
        Element aux = f.newElement();
        aux = aux.set(PK.getQ());
        aux = aux.mul(t);
        output = output.set(A);
        output = output.add(B);
        output = output.add(aux);
        return output;
    }

    public Element mul(PublicKey PK, Element C, Element D) {
        BigInteger t = BigIntegerUtils.getRandom(PK.getN());
//		double t1 = System.currentTimeMillis();
        Element T = PK.doPairing(C, D);
//		double t2 = System.currentTimeMillis();
//		System.out.println("一次对运算操作的时间"+(t2-t1)+"ms");
        Element K = PK.doPairing(PK.getQ(), PK.getQ());
        K = K.pow(t);
        return T.mul(K);
    }

    public String decryptMul(PublicKey PK, BigInteger sk, Element C) {
        Element PSK = PK.doPairing(PK.getP(), PK.getP());
        PSK.pow(sk);

        Element CSK = C.duplicate();
        CSK.pow(sk);
        Element aux = PSK.duplicate();

        BigInteger m = new BigInteger("1");
        while (!aux.isEqual(CSK)) {
            aux = aux.mul(PSK);
            m = m.add(BigInteger.valueOf(1));
        }
        return m.toString();
    }

    public String decrypt(PublicKey PK, BigInteger sk, Element C) {
        System.out.println("Decryption");
        Field<?> f = PK.getField();
        Element T = f.newElement();
        Element K = f.newElement();
        T = T.set(PK.getP());
        T = T.mul(sk);
        K = K.set(C);
        K = K.mul(sk);
        BigInteger k = logarithm(PK, T, K);
        return k.toString();
    }

    private BigInteger logarithm(PublicKey publicKey, Element base, Element value) {
        //to find the first one that equals R and in the group
        Field<?> gen = publicKey.getField();
        Element current = gen.newElement();
        current.set(base);
        BigInteger result = BigInteger.ONE;
        while (!current.isEqual(value)) {
            // This is a brute force implementation of finding the discrete
            // logarithm.
            // Performance may be improved using algorithms such as Pollard's
            // Kangaroo.
            current = current.add(base);
//            System.out.println("current"+current);
            result = result.add(BigInteger.valueOf(1));
        }
        System.out.println("\tvalue:" + value + " current:" + current + " res:" + result);
        return result;
    }

    private BigInteger logarithm2(PublicKey publicKey, Element base, Element value) {
        //if the random number exceed the group and cannot find the random number, use the acknoledged Rand to find it back and to test how many rounds
        BigInteger result = logarithm(publicKey, base, value);
        System.out.println("\tactual secret: "+m_Rand);
        System.out.println("\tRestored secret Result: "+result);
        if(result.equals(m_Rand)){
            System.out.println("success!");
            return result;
        }
        System.out.println("failed! Test how many to get it back! ");
        Field<?> f = publicKey.getField();
        Element current = f.newElement();
        current.set(value);

        while (!result.equals(m_Rand)) {
            current = current.add(base);
//            System.out.println("current"+current);
            result = result.add(BigInteger.valueOf(1));
            if (current.isEqual(value)) {
                System.out.println("\tvalue:" + value + " current:" + current + " res:" + result);
            }
        }
        return result;
    }

    public String restoreR(PublicKey PK, int msg, Element C) {
        System.out.println("Restore secret message");
        //function that restore random number R
        Field<?> f = PK.getField();
        Element L = f.newElement();
        Element H = f.newElement();
        Element HpowT = f.newElement();
        Element LpowM = f.newElement();
        L = L.set(PK.getP());
        H = H.set(PK.getQ());

        LpowM.set(L);
        LpowM = LpowM.mul(BigInteger.valueOf(msg));
        System.out.println("\tL^m: " + LpowM);

        HpowT.set(H);
        HpowT = HpowT.mul(m_Rand);
        System.out.println("\th^t: " + HpowT);

        Element fraction = C.div(LpowM);//C= L^m * H^t
        System.out.println("\tfraction = C - L^m = H^t：" + fraction);
        BigInteger answer = logarithm2(PK, H, fraction);

//        BigInteger correct = logarithm(PK, H, HpowT);

        System.out.println("\tsecretMsg found: " + answer);
        System.out.println("\tactual secretMsg: " + m_Rand);
//        System.out.println("actual actual: " + correct);
//        Element msg2=f.newElement();
//        msg2=msg2.set(BigInteger.valueOf(1));
//        Element modRes=f.newElement();
//        modRes=modRes.set(C.toBigInteger().mod(BigInteger.valueOf(2)));
//        System.out.println("modRes:"+modRes);

        assert Objects.equals(answer, m_Rand);

        return answer.toString();
    }

    public void Test_1(PublicKey PK, int m) {
        BigInteger n = PK.getN();
        BigInteger tMax = (PK.getN().subtract(BigInteger.valueOf(9))).divide(m_R); // tMax = (n-1)/q.intValue();
//        System.out.println("Original Value: n: "+ PK.getN() +" q: "+m_R);
//        System.out.println("Int value: n:"+n+"  q: "+m_R.intValue()+"  tMax: "+tMax);
        Element CipherText = encrypt(PK, m, tMax);
        System.out.println("Got CipherText: " + CipherText);
        String DecryptStr = decrypt(PK, m_Q, CipherText);
        if(m == Integer.parseInt(DecryptStr)){
            System.out.println("Decryption success");
            int decInt = Integer.parseInt(DecryptStr);
            String R = restoreR(PK, m, CipherText);
        }
        else{
            System.out.println("fail in decryption");
        }
    }

    public void Test(PublicKey PK, BGNEncryption b, int m, int n) {

        Element msg1 = b.encrypt(PK, m, BigInteger.valueOf(110));
        int len = msg1.getLengthInBytes();
        Element msg2 = b.encrypt(PK, n, BigInteger.valueOf(110));
        int len6 = msg2.getLengthInBytes();
        Element add = b.add(PK, msg1, msg2);
        String jiemiAdd = b.decrypt(PK, b.m_Q, add);
        int decAdd = Integer.parseInt(jiemiAdd);
        if (decAdd != m + n) {
            System.out.println("m:" + m);
            System.out.println("n:" + n);
        }
//    System.out.println("Addition: " + jiemiAdd);
        int len4 = add.getLengthInBytes();
//		double t5 = System.currentTimeMillis();
        if (n != 0) {
            Element mul = b.mul(PK, msg1, msg2);
            String jiemiMul = b.decryptMul(PK, b.m_Q, mul);
            int decMul = Integer.parseInt(jiemiMul);
//		double t6 = System.currentTimeMillis();
//		System.out.println("一次同态乘法的时间"+(t6-t5)+"ms");
            if (decMul != m * n) {
                System.out.println("m:" + m);
                System.out.println("n:" + n);
            }
            String R = b.restoreR(PK, m + n, add);
            System.out.println("r:" + R);
//        System.out.println("Mul: " + jiemiMul);
//        int len5 = mul.getLengthInBytes();
//        System.out.println("length of mulHom:" + len5);
        }

//    System.out.println("m:" + m);
//    System.out.println("n:" + n);
//    System.out.println("length of P:" + len1);
//    System.out.println("length of Q:" + len2);
//    System.out.println("Order:" + len3);
//    System.out.println("length of msg1:" + len);
//    System.out.println("length of addHom:" + len4);
    }
}
