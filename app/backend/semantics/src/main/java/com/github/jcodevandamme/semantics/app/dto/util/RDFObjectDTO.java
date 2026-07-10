package com.github.jcodevandamme.semantics.app.dto.util;

public record RDFObjectDTO(String value, Boolean isLiteral) {
    public RDFObjectDTO {
        if (isLiteral == null) {
            isLiteral = false;
        }
    }
}
