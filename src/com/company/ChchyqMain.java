package com.company;

import it.unisa.dia.gas.jpbc.Element;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public class ChchyqMain {
    public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchProviderException {
        BGNEncryption b = new BGNEncryption();
        PublicKey PK = b.gen(20);//20
        int i = 1;

//        b.Test(PK,b,1,0,len1,len2,len3);
//
        while(i<=255){
            System.out.println("-------------------------------New pixel---------------------------------");
            System.out.println("i:"+i);
            b.Test_1(PK, i);
            i+=1;
        }
//        b.Test_1(PK, 1);
    }
}
