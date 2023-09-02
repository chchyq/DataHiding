package com.company;

public class KonMain {
    public static void main(String[] args) {
        BGNEncryption b = new BGNEncryption();
        PublicKey PK = b.gen(8);//20
        b.Test_1(PK, b, 0);
    }
}
