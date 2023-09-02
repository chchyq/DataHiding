package com.company;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.plaf.jpbc.field.curve.CurveElement;
import it.unisa.dia.gas.plaf.jpbc.field.curve.CurveField;

import java.math.BigInteger;

public class ModuloElement<E extends Element, F extends CurveField> extends CurveElement<E, F> {
    public ModuloElement(CurveElement<E, F> curveElement) {
        super(curveElement);
    }
//    public CurveElement<E, F> modulo() {
    public BigInteger modulo() {
// Use the y of the element to calculate module result
        System.out.println("Module Process Start");
        System.out.println("BigInteger: this.y="+this.y.duplicate().toBigInteger());
//        System.out.println("Element this="+this);
        BigInteger y =this.y.duplicate().toBigInteger();
        BigInteger two= BigInteger.valueOf(2);
        BigInteger one= BigInteger.valueOf(1);
        BigInteger res= y.mod(two);
        System.out.println("result of module"+res);
        return res;
    }
}
