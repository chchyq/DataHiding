//package src.com.company;


import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.plaf.jpbc.pairing.a1.TypeA1Pairing;
import java.math.BigInteger;


public class PublicKey {
    private final TypeA1Pairing map;
    private final Element P;
    private final Element Q;
    private final BigInteger n;
    private final Field<?> f;

    public PublicKey(TypeA1Pairing pairing, Element gen, Element point,
                     BigInteger order) {
        map = pairing;
        P = gen.set(gen);
        Q = point.set(point);
        n = order;
        f = pairing.getG1();
    }

    public Element doPairing(Element A, Element B) {
        return map.pairing(A, B);
    }

    public Element getP() {
        return this.P;
    }

    public Element getQ() {
        return this.Q;
    }

    public BigInteger getN() {
        return this.n;
    }

    public Field<?> getField() {
        return this.f;
    }
}
