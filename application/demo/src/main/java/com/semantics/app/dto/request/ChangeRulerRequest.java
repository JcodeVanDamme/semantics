package com.semantics.app.dto.request;

import jakarta.annotation.Nonnull;

public record ChangeRulerRequest(String state, String ruler) {
}
