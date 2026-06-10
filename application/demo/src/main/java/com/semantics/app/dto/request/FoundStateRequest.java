package com.semantics.app.dto.request;

import jakarta.annotation.Nonnull;
import org.jspecify.annotations.NonNull;

public record FoundStateRequest(String stateName, String stateType, String rulerName) {
}
