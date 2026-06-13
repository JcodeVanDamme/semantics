package com.github.jcodevandamme.semantics.app.dto.request;

import com.github.jcodevandamme.semantics.app.dto.util.TripleDto;

public record PutTriplesRequest(TripleDto original, TripleDto update) {
}
