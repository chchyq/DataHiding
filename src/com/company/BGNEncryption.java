package com.company;

import java.awt.desktop.SystemEventListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Objects;
import java.util.Random;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.PairingParameters;
import it.unisa.dia.gas.plaf.jpbc.field.curve.CurveElement;
import it.unisa.dia.gas.plaf.jpbc.field.curve.CurveField;
import it.unisa.dia.gas.plaf.jpbc.pairing.a1.TypeA1CurveGenerator;
import it.unisa.dia.gas.plaf.jpbc.pairing.a1.TypeA1Pairing;
import it.unisa.dia.gas.plaf.jpbc.util.math.BigIntegerUtils;

import javax.imageio.ImageIO;


public class BGNEncryption {

    public static final String start = "start";
    public static final String end = "end";
    private PairingParameters param;
    private BigInteger r;
    private BigInteger q; // This is the private key.
    private BigInteger order;
    private SecureRandom rng;
    private BigInteger Rand;

    public PublicKey gen(int bits) {
        rng = new SecureRandom();
        TypeA1CurveGenerator a1 = new TypeA1CurveGenerator(rng, 2, bits);//// the number of primes，bits:the bit length of each prime
        param = a1.generate();
        TypeA1Pairing pairing = new TypeA1Pairing(param);
        order = param.getBigInteger("n");
        r = param.getBigInteger("n0");//random number 1
        q = param.getBigInteger("n1");//random number 2
        System.out.println("r:"+r+" q:"+q+" order:"+order);
        Field f = pairing.getG1();
        Element P = f.newRandomElement();
        BigInteger l=param.getBigInteger("l");
//        System.out.println("P:"+P+" l:"+l);
        P = P.mul(l);//P=P^l
//        System.out.println("P:"+P+" l:"+l); //p + 1 = n * l
        //p is prime, not the same as P here. unknown usage of p at least now. n is the order of the group.
        Element Q = f.newElement();
        Q = Q.set(P);
        Q = Q.mul(r);//Q=P ^r
        System.out.println("P:"+P+" Q:"+Q);
        PublicKey key = new PublicKey(pairing, P, Q, order);
        return key;
    }

    public Element encrypt(PublicKey PK, int msg, int secretMSG1) {

//        BigInteger t = BigIntegerUtils.getRandom(PK.getN());//随机数
//        int secretLeftShift= secretMSG1*1000;
//        BigInteger t= BigInteger.valueOf(secretLeftShift);
        BigInteger t= BigInteger.valueOf(secretMSG1);
        int m = msg;
//		System.out.println("Hash is " + m);
        Field f = PK.getField();
        Element A = f.newElement();
        Element B = f.newElement();
        Element C = f.newElement();
        A = A.set(PK.getP());
        A = A.mul(BigInteger.valueOf(m));
        B = B.set(PK.getQ());
        B = B.mul(t);//mul:pow
//        System.out.println(t);
        Rand = t;
        C = C.set(A);
        C = C.add(B);//multiply
        Element D = f.newElement();
        D.set(randomChoice(PK, C, 1));
        System.out.println("New"+D);
        C.set(D);
        return C;
    }

    public Element randomChoice(PublicKey PK, Element EnMsg, int secretMSG2){
        Field f = PK.getField();
        Element B = f.newElement();
        B = B.set(PK.getQ());//h

        Element C = f.newElement();
        C = C.set(EnMsg);
        BigInteger aaa = mod(PK,C);
        BigInteger sMSG2 = BigInteger.valueOf(secretMSG2);
        if(aaa==sMSG2){
            System.out.println("Use the original C");
            return C;
        }
        Element New = f.newElement();
        New = New.set(C);
        System.out.println("before choosing current C ="+C);
        int rNew;

        for(int i=1;i<8;i++){

            rNew = i;
//            String str = Integer.toBinaryString(rNew);
//            int R = Integer.parseInt(str);
//            BigInteger t= BigInteger.valueOf(R);
            BigInteger t = BigInteger.valueOf(rNew);
            B = B.mul(t);//mul:pow
            New = New.add(B);
            aaa = mod(PK,New);
            if(sMSG2.equals(aaa)){
                System.out.println("rMore"+t);
                Rand = Rand.add(t);
                System.out.println("Random Number now="+Rand);
                C.set(New);
                break;
            }
            New = New.set(C);
        }
        System.out.println("Module of the final result right now="+aaa);
        System.out.println("secretMSG2="+sMSG2);
        return C;
    }

    public BigInteger mod(PublicKey PK,Element C){
        Field f = PK.getField();
//        Element modulo = f.newElement();
//        System.out.println("mod");
        ModuloElement<Element, CurveField<Field>> moduloElement = new ModuloElement<>(new CurveElement<>((CurveField<Field>) f));
        moduloElement.set(C);
        BigInteger modulo = moduloElement.modulo();

        return modulo;
    }

    public Element add(PublicKey PK, Element A, Element B) {
        BigInteger t = BigIntegerUtils.getRandom(PK.getN());
        Field f = PK.getField();
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
        Field f = PK.getField();
        Element T = f.newElement();
        Element K = f.newElement();
        T = T.set(PK.getP());
        T = T.mul(sk);
        K = K.set(C);
        K = K.mul(sk);
        BigInteger k = logarithm(PK, T, K, BigInteger.ONE);
        return k.toString();
    }

    private BigInteger logarithm(PublicKey publicKey, Element base, Element value, BigInteger start) {
        //to find the first one that equals R and in the group
        Field gen = publicKey.getField();
        Element current = gen.newElement();
        current.set(base);
        BigInteger result = start;
        while (!current.isEqual(value)) {
            // This is a brute force implementation of finding the discrete
            // logarithm.
            // Performance may be improved using algorithms such as Pollard's
            // Kangaroo.
            current = current.add(base);
//            System.out.println("current"+current);
            result = result.add(BigInteger.valueOf(1));
        }
        System.out.println("value:"+value+" current:"+current+" res:"+result);
        return result;
    }
    private BigInteger logarithm2(PublicKey publicKey, Element base, Element value, BigInteger start) {
        //if the random number exceed the group and cannot find the random number, use the acknoledged Rand to find it back and to test how many rounds
        BigInteger result=logarithm(publicKey,base,value,start);
        Field f=publicKey.getField();
        Element current=f.newElement();
        current.set(value);
        System.out.println("Rand"+Rand+" result"+result);
//        assert false;
        while(!result.equals(Rand)){
            current = current.add(base);
//            System.out.println("current"+current);
            result = result.add(BigInteger.valueOf(1));
            if(current.isEqual(value)){
                System.out.println("value:"+value+" current:"+current+" res:"+result);
            }
        }
        System.out.println("value:"+value+" current:"+current+" res:"+result);
        return result;
    }

    public String restoreR(PublicKey PK, BigInteger q, int msg, Element C){
        //function that restore random number R
        Field f= PK.getField();
        Element L = f.newElement();
        Element H = f.newElement();
        Element HpowT = f.newElement();
        Element LpowM = f.newElement();
        L = L.set(PK.getP());
        H = H.set(PK.getQ());
//
        System.out.println("L: " + L);
//        System.out.println("C: " + C);
        System.out.println("H: " + H);
//        System.out.println("m: " + msg);
        System.out.println("t: " + Rand);

        LpowM.set(L);
        LpowM = LpowM.mul(BigInteger.valueOf(msg));
        System.out.println("L^m: " + LpowM);

        HpowT.set(H);
        HpowT = HpowT.mul(Rand);
        System.out.println("h^t: " + HpowT);

        Element fraction = C.sub(LpowM);//C= L^m * H^t
        System.out.println("H: " + H);
        System.out.println("fraction=C- L^m=H^t："+fraction);
        BigInteger answer = logarithm2(PK, H, fraction, BigInteger.ONE);

//        BigInteger correct = logarithm(PK, H, HpowT);

        System.out.println("div: " + answer);
        System.out.println("actual: " + Rand);
//        System.out.println("actual actual: " + correct);
//        Element msg2=f.newElement();
//        msg2=msg2.set(BigInteger.valueOf(1));
//        Element modRes=f.newElement();
//        modRes=modRes.set(C.toBigInteger().mod(BigInteger.valueOf(2)));
//        System.out.println("modRes:"+modRes);

        assert Objects.equals(answer, Rand);

//        K=K.set(L);
//        K=K.add(H);
//        J=J.set(H);
        System.out.println(C);
        System.out.println(L);
//        BigInteger first = logarithm(PK, H, C);
//        assert false;
//        BigInteger second = logarithm2(PK, H, L);
        return answer.toString();
//        return (first.subtract(second)).toString();
    }
    public void Test_1(PublicKey PK,BGNEncryption b,int m){
        int n = PK.getN().intValue();
        int tMax = (n-9)/q.intValue();//tMax = (n-1)/q.intValue();
//        BigInteger tMax = PK.getN().subtract(BigInteger.ONE).divide(r);
//        for(int i=2; i<tMax; i+=1){
////            Element CipherText=b.encrypt(PK,m,110);
//            Element CipherText=b.encrypt(PK,m,i);
//            String DecryptStr=b.decrypt(PK,b.q,CipherText);
//            int decInt = Integer.parseInt(DecryptStr);
//            String R=b.restoreR(PK,m,CipherText);
//            System.out.println("r:" + R);
//        }

        Element CipherText=b.encrypt(PK,m,tMax);
        String DecryptStr=b.decrypt(PK,b.q,CipherText);
        int decInt = Integer.parseInt(DecryptStr);
        String R=b.restoreR(PK,b.q,m,CipherText);
        System.out.println("r:" + R);
    }

public void Test(PublicKey PK, BGNEncryption b,int m,int n,int len1,int len2,int len3) {

    Element msg1 = b.encrypt(PK, m, 110);
    int len = msg1.getLengthInBytes();
    Element msg2 = b.encrypt(PK, n, 110);
    int len6 = msg2.getLengthInBytes();
    Element add = b.add(PK, msg1, msg2);
    String jiemiAdd = b.decrypt(PK, b.q, add);
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
        String jiemiMul = b.decryptMul(PK, b.q, mul);
        int decMul = Integer.parseInt(jiemiMul);
//		double t6 = System.currentTimeMillis();
//		System.out.println("一次同态乘法的时间"+(t6-t5)+"ms");
        if (decMul != m * n) {
            System.out.println("m:" + m);
            System.out.println("n:" + n);
        }
        String R = b.restoreR(PK, b.q, m + n, add);
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

    public static void main(String[] args) {

        BGNEncryption b = new BGNEncryption();
        PublicKey PK = b.gen(8);//20
        BigInteger f = PK.getN();
        Element P = PK.getP();
        Element Q = PK.getQ();
        BigInteger order = PK.getN();
        int len1 = P.getLengthInBytes();
        int len2 = Q.getLengthInBytes();
        int len3 = order.bitLength();
//        System.out.println("P的长度:" + len1);
//        System.out.println("Q的长度:" + len2);
//        System.out.println("阶为:" + len3);
        int i=0;

//        b.Test(PK,b,1,0,len1,len2,len3);

        while(i<=255){
            System.out.println("-------------------------------New pixel---------------------------------");
            System.out.println("i:"+i);
            b.Test_1(PK,b,i);
            i+=1;
        }
//        b.Test_1(PK,b,0);
//        Validation experiment
//        while(i<=255){
//            b.Test(PK,b,i,0,len1,len2,len3);
//            b.Test(PK,b,i,1,len1,len2,len3);
//            i+=1;
//        }
//        System.out.println("Checked");
//        b.Test(PK,b,50,25,len1,len2,len3);
//        Element msg1 = b.encrypt(PK, 50);
//        int len = msg1.getLengthInBytes();
//        Element msg2 = b.encrypt(PK, 25);
//        int len6 = msg2.getLengthInBytes();
//        Element add = b.add(PK, msg1, msg2);
//        String jiemi = b.decrypt(PK, b.q, add);
//        System.out.println("Addition: " + jiemi);
//        int len4 = add.getLengthInBytes();
////		double t5 = System.currentTimeMillis();
//        Element mul = b.mul(PK, msg1, msg2);
////		double t6 = System.currentTimeMillis();
////		System.out.println("一次同态乘法的时间"+(t6-t5)+"ms");
//        System.out.println("Mul: " + b.decryptMul(PK, b.q, mul));
//        int len5 = mul.getLengthInBytes();
//        System.out.println("P的长度:" + len1);
//        System.out.println("Q的长度:" + len2);
//        System.out.println("阶为:" + len3);
//        System.out.println("msg1的长度:" + len);
//        System.out.println("加同态" + len4);
//        System.out.println("乘同态" + len5);
    }
}
