package org.tientt.models.entities;

public enum FileType {

    REGULAR_FILE("-"), DIRECTORY("d"), ROOT("r");

    private final String text;

    FileType(String text) {
        this.text = text;
    }

    public String getValue(){
        return text;
    }

    @Override
    public String toString() {
        return text;
    }
}
