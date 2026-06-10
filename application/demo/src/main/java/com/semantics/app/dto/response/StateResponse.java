package com.semantics.app.dto.response;

import com.semantics.app.dto.util.StateDto;

public record StateResponse(int count, StateDto[] states) {
}
