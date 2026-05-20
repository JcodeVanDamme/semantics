package com.example.demo;
import com.example.demo.bMatrix.TestTripleProvider;
import com.example.demo.bMatrix.BMatrix;
import org.junit.jupiter.api.Test;

public class BMatrixTests {

    @Test
    public void bMatrixTest() {

        BMatrix bMatrix = new BMatrix(2, new TestTripleProvider());
    }
}
