package com.example.demo;
import com.example.demo.tripleStore.triple.TestTripleProvider;
import com.example.demo.tripleStore.bMatrix.BMatrix;
import org.junit.jupiter.api.Test;

public class BMatrixTests {

    @Test
    public void bMatrixTest() {

        BMatrix bMatrix = new BMatrix(2, 10, new TestTripleProvider());
    }
}
