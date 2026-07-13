package com.github.jcodevandamme.semantics.app.dto.util;

import com.github.jcodevandamme.semantics.app.services.domain.logger.DomainAction;

import java.time.Instant;

public record HistoryDto(DomainAction action, Instant timeStamp, TripleActionDto[] triples) {
}
