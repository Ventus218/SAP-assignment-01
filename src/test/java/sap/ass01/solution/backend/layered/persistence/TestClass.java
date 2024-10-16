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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + a;
        result = prime * result + ((b == null) ? 0 : b.hashCode());
        result = prime * result + ((rec == null) ? 0 : rec.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TestClass other = (TestClass) obj;
        if (a != other.a)
            return false;
        if (b == null) {
            if (other.b != null)
                return false;
        } else if (!b.equals(other.b))
            return false;
        if (rec == null) {
            if (other.rec != null)
                return false;
        } else if (!rec.equals(other.rec))
            return false;
        return true;
    }

}
