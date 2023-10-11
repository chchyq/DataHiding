package com.company;

import it.unisa.dia.gas.jpbc.Element;

import java.io.IOException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;


public class ChchyqMain {
    public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchProviderException, IOException {
        BGNEncryption b = new BGNEncryption();
        PublicKey PK = b.gen(20);//20
        String filePath = "/Users/chenyingqing/Library/Mobile Documents/com~apple~CloudDocs/Documents/study/ThreeDays/同态加密/bgn_java";
        String picPath = filePath+"/LenaRGB.bmp";

        Gray ima = new Gray();
        int[] GrayImage = ima.GrayImage(filePath, picPath);
        int width = GrayImage[0];
        int height = GrayImage[1];
        int size = width * height;
        Element []CipherImg = new Element [size];

        for(int i =2; i<size+2; i++){
//            b.Test_1(PK, GrayImage[i]);
            BigInteger tMax = (PK.getN().subtract(BigInteger.valueOf(9))).divide(b.publicKey()); // tMax = (n-1)/q.intValue();
            Element CipherText = b.encrypt(PK, GrayImage[i], tMax);
            CipherImg[i-2] = CipherText;
            System.out.println("Got CipherText: " + CipherText);
        }
        int []RecoverImg = new int [size];
        for(int i=2; i<size+2; i++){
            String DecryptStr = b.decrypt(PK, b.privateKey(), CipherImg[i]);
            if(GrayImage[i] == Integer.parseInt(DecryptStr)){
                System.out.println("Decryption success");
                int decInt = Integer.parseInt(DecryptStr);
                RecoverImg[i+2] = decInt;
                String R = b.restoreR(PK, GrayImage[i], CipherImg[i+2]);
            }
            else{
                System.out.println("fail in decryption");
            }
        }
        ima.imgSave(RecoverImg,filePath);

//        while(i<=255){
//            System.out.println("-------------------------------New pixel---------------------------------");
//            System.out.println("i:"+i);
//            b.Test_1(PK, i);
//            i+=1;
//        }
//        b.Test_1(PK, 1);
    }
}
