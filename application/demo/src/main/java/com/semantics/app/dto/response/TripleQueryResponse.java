package com.semantics.app.dto.response;

import com.semantics.app.dto.util.TripleDto;

public record TripleQueryResponse (int count, TripleDto[] triples) {
}
