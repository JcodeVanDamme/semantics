package com.github.jcodevandamme.semantics.app.dto.response;

import com.github.jcodevandamme.semantics.app.dto.util.StateDto;
public record StateResponse(int count, StateDto[] states) {
}
