package com.github.jcodevandamme.semantics.app.services.domain.actor.data;

import java.util.ArrayList;
import java.util.List;

public class State {
    public String name;
    public String URI;
    public Ruler ruler;
    public String type;
    public int population;
    public List<Region> regions = new ArrayList<>();
    public List<MedState> mediatizatedStates = new ArrayList<>();
}
