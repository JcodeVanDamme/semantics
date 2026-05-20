package com.example.demo.bMatrix;

import java.util.*;

public class K2Tree {

    private final int k;

    // Temporary List-Structures used for Assembly
    private final List<Boolean> tTemp;
    private final List<Boolean> lTemp;

    // Necessary to achieve Level-ordered Bits
    // -> Recursive Assembly-Flow would naturally append in a depth-first Order
    private final Map<Integer, List<Boolean>> tLevels;
    private int maxTLevel;

    // Holds final BitString after Assembly
    private BitString t;
    private BitString l;

    public K2Tree(int k) {
        this.k = k;
        tTemp = new ArrayList<>();
        lTemp = new ArrayList<>();
        tLevels = new HashMap<>();
    }
    public BitString getT() { return t; }
    public BitString getL() { return l; }

    // Setters only used during Assembly
    // -> Modify temporary List-Structures
    public void setT(int matrixSize, boolean val) {
        if (matrixSize > maxTLevel) { maxTLevel = matrixSize; }
        tLevels.computeIfAbsent(matrixSize, x -> new ArrayList<>()).add(val);
    }
    public void setL(boolean val) {
        // L-Bits as opposed to T-Bits need to be directly appended when encountered to keep the correct Order
        lTemp.add(val);
    }
    public void assembleBitStrings() {
        // First assemble final TTemp Bits in the correct Order
        for (int lvl = maxTLevel; lvl > 0; lvl = lvl / k) {
            List<Boolean> bits = tLevels.get(lvl);
            if (bits != null) {
                tTemp.addAll(bits);
            }
        }
        t = new BitString(tTemp);
        l = new BitString(lTemp);
    }
    @Override
    public String toString() {
        StringBuilder strb = new StringBuilder();
        strb.append("T: ");
        generateString(strb, tTemp);
        strb.append("\nL: ");
        generateString(strb, lTemp);
        return strb.toString();
    }
    private void generateString(StringBuilder strb, List<Boolean> b) {
        for (int i = 0; i < b.size(); i ++) {
            String bit = b.get(i) ? "1" : "0";
            strb.append(bit);
            if ((i + 1) % (k*k) == 0) {
                strb.append(' ');
            }
        }
    }
}
