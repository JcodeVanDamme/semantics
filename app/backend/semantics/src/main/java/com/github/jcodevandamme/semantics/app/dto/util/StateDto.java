package com.github.jcodevandamme.semantics.app.dto.util;

public record StateDto(String name, RulerDto ruler, MediatizatedStatesDto mediatizatedStates, RegionsDto regions, int population, String stateType) {
}
