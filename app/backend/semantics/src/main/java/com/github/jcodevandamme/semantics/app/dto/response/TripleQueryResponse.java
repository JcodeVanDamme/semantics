package com.github.jcodevandamme.semantics.app.dto.response;

import com.github.jcodevandamme.semantics.app.dto.util.TripleDto;

public record TripleQueryResponse (int count, TripleDto[] triples) {
}
