package com.xiaomi.be.persistence;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ProcessorState implements Serializable {
    private List<String> events;

    public ProcessorState() {
        this(new ArrayList<String>());
    }

    public ProcessorState(List<String> events) {
        this.events = events;
    }

    public void update(ProcessorActor.Evt evt) {
        events.add(evt.data);
    }

    public ProcessorState copy(){
        return new ProcessorState(new ArrayList<>(events));
    }

    public int size() {
        return events == null ? 0 : events.size();
    }
}
