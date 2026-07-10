package com.github.jcodevandamme.semantics.rdf.structure.tree.dk2.dynamicbitvector;

/**
 * Dynamic-Bit-Vector Configuration Parameters passed by the DK2Builder to the underlying Dynamic-Bit-Vectors.
 *
 * @param chunkSize Size the static Bit-String will be partitioned into. Dictates the maximum Capacity of Leaf Nodes.
 * @param leafMinimumCapacity minimum number of Entries allowed in Leaf Nodes
 * @param internalMinimumCapacity minimum number of Entries allowed in internal Nodes
 * @param internalMaximumCapacity maximum number of Entries allowed in internal Nodes
 */
public record DynamicBitVectorConfiguration(int chunkSize, int leafMinimumCapacity, int internalMinimumCapacity, int internalMaximumCapacity) {
}
