package com.semantics.app.dto.request;

import com.semantics.app.dto.util.TripleDto;
import jakarta.annotation.Nonnull;

public record PutTriplesRequest(TripleDto original, TripleDto update) {
}
