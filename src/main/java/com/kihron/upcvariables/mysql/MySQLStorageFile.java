package com.kihron.upcvariables.mysql;

import com.kihron.upcvariables.variablesManager.StorageFile;

import java.util.HashMap;

public class MySQLStorageFile extends StorageFile {
    public MySQLStorageFile(HashMap<String, Object> defaults) {
        super("Variables-MySQL", new HashMap<>(defaults));
    }
}
