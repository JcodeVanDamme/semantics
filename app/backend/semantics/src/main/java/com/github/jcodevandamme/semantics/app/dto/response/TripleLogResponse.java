package com.github.jcodevandamme.semantics.app.dto.response;

import com.github.jcodevandamme.semantics.app.dto.util.TripleActionDto;

public record TripleLogResponse(int count, TripleActionDto[] triples) {
}
