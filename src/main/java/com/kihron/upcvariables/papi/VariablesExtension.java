package com.kihron.upcvariables.papi;

import com.kihron.upcvariables.mysql.MySQL;
import com.kihron.upcvariables.variablesManager.Variable;
import com.kihron.upcvariables.variablesManager.VariableStorageFile;
import com.kihron.upcvariables.variablesManager.VariablesMain;
import me.TechsCode.UltraCustomizer.UltraCustomizer;
import me.TechsCode.UltraCustomizer.base.item.ItemSerializer;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class VariablesExtension extends PlaceholderExpansion {

    public static Map<String, String> variables = new HashMap<>();

    private VariableStorageFile getStorageFile() {
        return VariablesMain.storageFile;
    }

    private UltraCustomizer plugin;

    public VariablesExtension(UltraCustomizer plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean persist(){
        return true;
    }

    @Override
    public boolean canRegister(){
        return true;
    }

    @Override
    public String getAuthor(){
        return "Kihron";
    }

    @Override
    public String getIdentifier(){
        return "ucvar";
    }

    @Override
    public String getVersion(){
        return "1.0.0";
    }

    @Override
    public String onRequest(OfflinePlayer player, String identifier){
        if(player == null){
            return "";
        }

        Variable[] variableObjects = new Variable[0];
        if (VariablesMain.getInstance().useMySQL()) {
            MySQL mySQL = VariablesMain.getMySQL();
            try {
                Connection connection = mySQL.getConnection();
                String VARIABLES_TABLE = "Variables";
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM " + VARIABLES_TABLE + ";");
                ResultSet rs = preparedStatement.executeQuery();
                while (rs.next()) variables.put(rs.getString("varname"), rs.getString("varvalue"));
                rs.close();
                connection.close();

                variableObjects = variables.entrySet().stream()
                        .map(entry -> new Variable(entry.getKey(), entry.getValue()))
                        .toArray(Variable[]::new);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            variableObjects = getStorageFile().getKeys().stream()
                    .map(entry -> new Variable(entry, String.valueOf(getStorageFile().get(entry))))
                    .toArray(Variable[]::new);
        }

        for (Variable variable: variableObjects) {
            String name = variable.getName();
            String s = variable.getValue();
            if (s.contains("rO0ABXN") && VariablesMain.getInstance().isValid(s)) {
                s = ItemSerializer.itemFrom64(s).getType().toString();
            } else if (s.length() > 30) {
                s = s.substring(0, 30) + "§7...";
            }

            if (identifier.contains("$")) {
                identifier = PlaceholderAPI.setPlaceholders(player, identifier.replaceAll("\\$", "%"));
            }

            if (identifier.equals(name)) {
                s = PlaceholderAPI.setPlaceholders(player, s);
                return s;
            }
        }
        return null;
    }
}
