package com.github.jcodevandamme.semantics.rdf.structure.tree.dk2.dynamicbitvector;

import com.github.jcodevandamme.semantics.rdf.structure.bitstring.BitInterface;
import com.github.jcodevandamme.semantics.rdf.structure.bitstring.SuxBitString;

import java.util.ArrayList;
import java.util.List;

public final class DynamicBitVectorBuilder {
    private DynamicBitVectorBuilder() {}

    /**
     * Generates a dynamic Bit-Vector in Tree Form from a static Series of Bits.
     *
     * <p> This process converts the static bitmap into a dynamic structure with explicit nodes. </p>
     *
     * @param bitString static Bit String to generate the Dynamic-Bit-Vector from
     * @param chunkSize size of bit chunks used to partition the original
     *                  Bit-String; determines the size of leaf nodes
     *                  in the resulting Dynamic-Bit-Vector
     * @param minCapacity minimum number of entries allowed in internal nodes
     * @param maxCapacity maximum number of entries allowed in internal nodes
     * @return the generated DynamicBitVector
     */
    public static DynamicBitVector build(BitInterface bitString, int chunkSize, int minCapacity, int maxCapacity) {
        List<Node> leaves = generateLeaves(bitString, chunkSize);
        return buildTree(leaves, minCapacity, maxCapacity);
    }
    private static List<Node> generateLeaves(BitInterface bitString, int chunkSize) {
        // List of Leaf Nodes
        // -> leaf.bits contains respective Chunk of original BitString
        List<Node> leaves = new ArrayList<>();

        for (int i = 0; i < bitString.size(); i += chunkSize) {
            // Read until Capacity or End of original BitString
            int end = Math.min(bitString.size(), i + chunkSize);

            List<Boolean> chunk = new ArrayList<>();

            // Transcribe each Bit into the Chunk
            for (int j = i; j < end; j++) {
                chunk.add(bitString.access(j) == 1);
            }

            BitInterface subSet = new SuxBitString(chunk);
            leaves.add(new LeafNode(1, chunkSize, subSet));
        }

        return leaves;
    }

    private static DynamicBitVector buildTree(List<Node> level, int minCap, int maxCap) {
        // Keep on Building until all Nodes have been processed
        // i.e. until Root Node -> level.size() == 1; has been reached
        while (level.size() > 1) {

            // List of Nodes in current Level
            List<Node> nextLevel = new ArrayList<>();

            // Process all Child Nodes for the given Level
            // -> Cycle 1; Leaves
            // -> i == Index of current Child to be appended to Parent
            for (int i = 0; i < level.size(); i += maxCap) {

                // Current Parent Node
                InternalNode parent = new InternalNode(minCap, maxCap);

                // Append to Parent until Parents Capacity has been reached
                // or there are no more Nodes left to process
                int end = Math.min(i + maxCap, level.size());

                // Append Child to Parent
                for (int j = i; j < end; j++) {
                    parent.add(level.get(j));
                }

                // Append the Parent Node to next Level
                nextLevel.add(parent);
            }

            // This Cycles Parent Nodes become next Cycles Child Nodes
            level = nextLevel;
        }

        return new DynamicBitVector(level.getFirst());
    }
}
