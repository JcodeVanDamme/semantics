package com.github.jcodevandamme.semantics.app.services.domain.logger;

import com.github.jcodevandamme.semantics.app.persistence.UpdateType;
import com.github.jcodevandamme.semantics.rdf.model.Triple;

public record TripleAction(UpdateType type, Triple triple) {
}
