package com.kihron.upcvariables.mysql;

import me.TechsCode.UltraCustomizer.base.mysql.MySQLCredentials;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MySQL {

    private List<String> errorMessages;
    private MySQLCredentials mySQLCredentials;

    private MySQL(MySQLCredentials mySQLSettings) {
        this.errorMessages = new ArrayList<>();
        this.mySQLCredentials = mySQLSettings;
    }

    public static MySQL of(MySQLCredentials mySQLSettings) {
        return new MySQL(mySQLSettings);
    }

    public String update(String query) {
        try {
            Connection connection = getConnection();
            PreparedStatement p = connection.prepareStatement(query);
            p.execute();
            connection.close();
            p.close();
            return "Success";
        } catch(SQLException ex) {
            ex.printStackTrace();
            errorMessages.add(ex.getMessage());
            return ex.getMessage();
        }
    }

    public Connection getConnection() throws SQLException {
        String connectString = "jdbc:mysql://" + mySQLCredentials.getHostname() + ":" + mySQLCredentials.getPort() + "/" + mySQLCredentials.getDatabase() + "?useSSL=false&characterEncoding=utf-8&serverTimezone=UTC";
        return DriverManager.getConnection(connectString, mySQLCredentials.getUsername(), mySQLCredentials.getPassword());
    }

    public String getLatestErrorMessage() {
        if(errorMessages == null || errorMessages.size() == 0) return "";
        return errorMessages.get(errorMessages.size() - 1);
    }

    public List<String> getErrorMessages() {
        return errorMessages;
    }
}
