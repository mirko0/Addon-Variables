package com.kihron.upcvariables.variablesManager;

import java.util.HashMap;

public class VariableStorageFile extends StorageFile {

    public VariableStorageFile(HashMap<String, Object> variables) {
        super("Variables", new HashMap<>(variables));
    }
}
