package com.kihron.upcvariables.variablesManager;

public class Variable {

    private String name;
    private String value;

    public Variable(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return this.name;
    }

    public String getValue() {
        return value;
    }
}
