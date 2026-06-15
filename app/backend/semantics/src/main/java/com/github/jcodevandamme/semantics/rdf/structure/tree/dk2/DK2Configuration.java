package com.github.jcodevandamme.semantics.rdf.structure.tree.dk2;

/**
 * The type Dk 2 configuration.
 * @param chunkSize Size the static Bit-String will be partitioned into. Dictates the maximum Capacity of Leaf Nodes.
 * @param leafMinimumCapacity minimum number of Entries allowed in Leaf Nodes
 * @param internalMinimumCapacity minimum number of Entries allowed in internal Nodes
 * @param internalMaximumCapacity maximum number of Entries allowed in internal Nodes
 */
public record DK2Configuration(int chunkSize, int leafMinimumCapacity, int internalMinimumCapacity, int internalMaximumCapacity) {
}
