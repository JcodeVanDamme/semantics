package com.github.jcodevandamme.semantics.app.dto;

import com.github.jcodevandamme.semantics.app.dto.util.RDFObjectDTO;
import com.github.jcodevandamme.semantics.app.dto.util.TripleActionDto;
import com.github.jcodevandamme.semantics.app.dto.util.TripleDto;
import com.github.jcodevandamme.semantics.app.persistence.UpdateType;
import com.github.jcodevandamme.semantics.app.services.domain.logger.TripleAction;
import com.github.jcodevandamme.semantics.rdf.model.Triple;

import java.util.ArrayList;
import java.util.List;

public class DTOFactory {

    public static TripleDto[] tripleArr(List<Triple> triples) {
        TripleDto[] res = new TripleDto[triples.size()];
        for (int i = 0; i < triples.size(); i++) {
            res[i] = triple(triples.get(i));
        }
        return res;
    }

    public static TripleDto triple(Triple t) {
        return new TripleDto(
                new RDFObjectDTO((String) t.s().value(), false),
                new RDFObjectDTO((String) t.p().value(), false),
                new RDFObjectDTO((String) t.o().value(), t.o().isLiteral())
        );
    }

    public static TripleActionDto[] tripleActionsToDTO(List<TripleAction> actions) {
        TripleActionDto[] dtos = new TripleActionDto[actions.size()];
        for (int i = 0; i < actions.size(); i++) {
            dtos[i] = new TripleActionDto(
                    actionToString(actions.get(i).type()),
                    DTOFactory.triple(actions.get(i).triple())
            );
        }
        return dtos;
    }

    private static String actionToString(UpdateType t) {
        switch (t) {
            case ADD -> {
                return "Created";
            }
            case DELETE -> {
                return "Deleted";
            }
            default -> {
                return "Unknown";
            }
        }
    }
}
