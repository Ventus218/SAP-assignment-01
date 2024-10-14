package sap.ass01.solution.backend.layered.persistence;

import java.util.Optional;

public class TestClass {
    private int a;
    private Optional<String> b;
    private TestRecord rec;

    public TestClass() {
    }

    public TestClass(int a, Optional<String> b, TestRecord rec) {
        this.a = a;
        this.b = b;
        this.rec = rec;
    }

    public int getA() {
        return a;
    }

    public Optional<String> getB() {
        return b;
    }

    public TestRecord getRec() {
        return rec;
    }

    @Override
    public String toString() {
        return "TestClass [a=" + a + ", b=" + b + ", rec=" + rec + "]";
    }

}
