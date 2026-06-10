package com.semantics.app.dto.response;

import com.semantics.app.dto.util.TripleActionDto;

public record TripleLogResponse(int count, TripleActionDto[] triples) {
}
