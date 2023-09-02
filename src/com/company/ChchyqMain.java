package com.company;

import it.unisa.dia.gas.jpbc.Element;

import java.math.BigInteger;

public class ChchyqMain {
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
        int i = 0;

//        b.Test(PK,b,1,0,len1,len2,len3);
//
//        while(i<=255){
//            System.out.println("-------------------------------New pixel---------------------------------");
//            System.out.println("i:"+i);
//            b.Test_1(PK,b,i);
//            i+=1;
//        }
        b.Test_1(PK, b, 0);
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
