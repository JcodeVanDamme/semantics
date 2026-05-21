package com.example.demo;
import com.example.demo.tripleStore.bMatrix.BMatrix;
import com.example.demo.tripleStore.bMatrix.BMatrixBuilder;
import com.example.demo.tripleStore.triple.TestTripleProvider;
import org.junit.jupiter.api.Test;

public class BMatrixTests {

    @Test
    public void bMatrixTest() {
        BMatrixBuilder builder = new BMatrixBuilder();
        BMatrix tripleStore = builder.build(2, 10, new TestTripleProvider());
    }
}
