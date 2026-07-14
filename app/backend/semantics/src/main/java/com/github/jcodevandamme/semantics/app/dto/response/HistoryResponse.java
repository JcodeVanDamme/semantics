package com.github.jcodevandamme.semantics.app.dto.response;

import com.github.jcodevandamme.semantics.app.dto.util.HistoryDto;

public record HistoryResponse(HistoryDto... history) {
}
