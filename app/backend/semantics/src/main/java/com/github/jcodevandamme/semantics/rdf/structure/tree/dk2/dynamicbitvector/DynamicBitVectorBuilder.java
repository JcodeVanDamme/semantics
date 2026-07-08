package com.github.jcodevandamme.semantics.rdf.structure.tree.dk2.dynamicbitvector;

import com.github.jcodevandamme.semantics.rdf.structure.bitstring.BitInterface;
import com.github.jcodevandamme.semantics.rdf.structure.bitstring.RoaringBitString;

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
     * @param config Configuration Parameters for the Tree
     * @return the generated DynamicBitVector
     */
    public static DynamicBitVector build(BitInterface bitString, DynamicBitVectorConfiguration config) {
        List<Node> leaves = generateLeaves(bitString, config.chunkSize(), config.leafMinimumCapacity());
        return buildTree(leaves, config);
    }
    private static List<Node> generateLeaves(BitInterface bitString, int chunkSize, int minCap) {
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

            BitInterface subSet = new RoaringBitString(chunk);
            leaves.add(new LeafNode(minCap, chunkSize, subSet));
        }

        return leaves;
    }

    private static DynamicBitVector buildTree(List<Node> level, DynamicBitVectorConfiguration config) {
        // Keep on Building until all Nodes have been processed
        // i.e. until Root Node -> level.size() == 1; has been reached
        while (level.size() > 1) {

            // List of Nodes in current Level
            List<Node> nextLevel = new ArrayList<>();

            // Process all Child Nodes for the given Level
            // -> Cycle 1; Leaves
            // -> i == Index of current Child to be appended to Parent
            for (int i = 0; i < level.size(); i += config.internalMaximumCapacity()) {

                // Current Parent Node
                InternalNode parent = new InternalNode(config.internalMinimumCapacity(), config.internalMaximumCapacity());

                // Append to Parent until Parents Capacity has been reached
                // or there are no more Nodes t1 to process
                int end = Math.min(i + config.internalMaximumCapacity(), level.size());

                // Append Child to Parent
                int childIdx = 0;
                for (int j = i; j < end; j++) {
                    Node child = level.get(j);
                    parent.add(child, parent.entries().size());
                    child.setParent(parent, childIdx);
                    childIdx++;
                }

                // Append the Parent Node to next Level
                nextLevel.add(parent);
            }

            // This Cycles Parent Nodes become next Cycles Child Nodes
            level = nextLevel;
        }
        if (level.isEmpty()) {
            return generateEmptyTree(config);
        }
        return new DynamicBitVector(level.getFirst(), config);
    }

    private static DynamicBitVector generateEmptyTree(DynamicBitVectorConfiguration config) {
        List<Boolean> emptyRootBits = new ArrayList<>();
        for (int i = 0; i < config.chunkSize(); i++) {
            emptyRootBits.add(false);
        }
        LeafNode root = new LeafNode(
                config.leafMinimumCapacity(),
                config.chunkSize(),
                new RoaringBitString(emptyRootBits)
        );
        return new DynamicBitVector(root, config);
    }
}
