package com.github.jcodevandamme.semantics.app.dto.util;

public record StateDto(String name, String stateType,int population,  RulerDto ruler, RegionDto[] regions, MediatizatedStateDto[] mediatizatedStates) {
}
