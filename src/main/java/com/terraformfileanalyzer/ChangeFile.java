package com.terraformfileanalyzer;

import java.util.List;

public class ChangeFile {

    private List<Change> changes;

    public ChangeFile() {

    }
    public void setChanges(List<Change> changes) {
        this.changes = changes;
    }

    public List<Change> getChanges() {
        return changes;
    }
   
}
