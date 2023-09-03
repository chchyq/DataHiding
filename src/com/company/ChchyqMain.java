package com.company;

import it.unisa.dia.gas.jpbc.Element;

import java.math.BigInteger;

public class ChchyqMain {
    public static void main(String[] args) {
        BGNEncryption b = new BGNEncryption();
        PublicKey PK = b.gen(20);//20
        int i = 1;

//        b.Test(PK,b,1,0,len1,len2,len3);
//
//        while(i<=255){
//            System.out.println("-------------------------------New pixel---------------------------------");
//            System.out.println("i:"+i);
//            b.Test_1(PK,b,i);
//            i+=1;
//        }
        b.Test_1(PK, 1);
//        Validation experiment
//        while(i<=255){
//            b.Test(PK,b,i,0,len1,len2,len3);
//            b.Test(PK,b,i,1,len1,len2,len3);
//            i+=1;
//        }
    }
}
