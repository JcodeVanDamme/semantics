package com.semantics.app.dto.util;

import com.semantics.app.dto.util.MediatizatedStatesDto;
import com.semantics.app.dto.util.RegionsDto;
import com.semantics.app.dto.util.RulerDto;

public record StateDto(String name, RulerDto ruler, MediatizatedStatesDto mediatizatedStates, RegionsDto regions, int population, String stateType) {
}
