package com.company;

public class KonMain {
    public static void main(String[] args) {
        BGNEncryption b = new BGNEncryption();
        PublicKey PK = b.gen(8); // 20

        System.out.println("Running Test!");
        b.Test_1(PK, 0);

    }
}
