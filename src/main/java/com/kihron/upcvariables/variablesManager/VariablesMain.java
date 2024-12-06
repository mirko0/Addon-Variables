package com.kihron.upcvariables.variablesManager;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kihron.upcvariables.guis.OverviewVars;
import com.kihron.upcvariables.guis.VariablesGUI;
import com.kihron.upcvariables.mysql.MySQL;
import com.kihron.upcvariables.mysql.MySQLStorageFile;
import com.kihron.upcvariables.papi.VariablesExtension;
import me.TechsCode.UltraCustomizer.UltraCustomizer;
import me.TechsCode.UltraCustomizer.base.item.ItemSerializer;
import me.TechsCode.UltraCustomizer.base.mysql.MySQLCredentials;
import me.TechsCode.UltraCustomizer.gui.Overview;
import me.TechsCode.UltraCustomizer.scriptSystem.ElementRegistration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Stream;

public class VariablesMain implements Listener {

    public static final UltraCustomizer ULTRA_CUSTOMIZER = ElementRegistration.getElementFromName("var-bg").plugin;
    private final String VARIABLES_TABLE = "Variables";
    public static MySQLStorageFile sqlStorageFile = new MySQLStorageFile(new HashMap<>());

    private VariablesMain() {
        Bukkit.getServer().getPluginManager().registerEvents(this, ULTRA_CUSTOMIZER.getBootstrap());
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new VariablesExtension(ULTRA_CUSTOMIZER).register();
        }
    }

    private static final VariablesMain INSTANCE = new VariablesMain();

    public static VariablesMain getInstance() {
        return INSTANCE;
    }

    public static VariableStorageFile storageFile = new VariableStorageFile(new HashMap<>());

    public static MySQL getMySQL() {
        if(!sqlStorageFile.contains("MySQL")) {
            sqlStorageFile.set("MySQL", "false");
        }
        MySQLCredentials mySQLCredentials = getInstance().createSettings();
        return MySQL.of(mySQLCredentials);
    }

    public boolean hasMySQL() {
        File f = new File("plugins/UltraCustomizer/Registry.json");

        boolean result = false;
        try {
                Scanner scanner = new Scanner(f);
                String line = scanner.nextLine();
                String info;

                if (line.startsWith("mysql: ")) {
                    Base64.Decoder decoder = Base64.getDecoder();
                    info = new String(decoder.decode(line.substring(7)));
                    result = info.contains("{\"hostname\":\"\"");
                } else {
                    return false;
//                    throw new RuntimeException("Something went wrong: " + line);
                }
        } catch (FileNotFoundException ignored) {
        }
        return !result;
    }

    public boolean useMySQL() {
        if (hasMySQL()) {
            MySQL mySQL = VariablesMain.getMySQL();
            String VARIABLES_TABLE = "Variables";
            mySQL.update("CREATE TABLE IF NOT EXISTS " + VARIABLES_TABLE + " (varname VARCHAR(250), varvalue VARCHAR(1000));");
            return "true".equals(sqlStorageFile.get("MySQL"));
        } else {
            return false;
        }
    }

    public void convertToMySQL() {
        for (String key: storageFile.getKeys()) {
            put(key, storageFile.get(key));
            storageFile.set(key, null);
        }
    }

    public void convertToLocal() {
        MySQL mySQL = getMySQL();
        for (String key: VariablesGUI.variables.keySet()) {
            storageFile.set(key, VariablesGUI.variables.get(key));
        }
        mySQL.update("DROP TABLE " + VARIABLES_TABLE);
        VariablesGUI.variables.clear();
    }

    public MySQLCredentials createSettings() {
        String host = "";
        String port = "";
        String database = "";
        String username = "";
        String password = "";

        File f = new File("plugins/UltraCustomizer/Registry.json");

        if ((f.exists() && !f.isDirectory())) {
            try {
                Scanner scanner = new Scanner(f);
                String line = scanner.nextLine();
                String info;

                if (line.startsWith("mysql: ")) {
                    Base64.Decoder decoder = Base64.getDecoder();
                    info = new String(decoder.decode(line.substring(7)));
                } else {
                    throw new RuntimeException("Something went wrong: " + line);
                }

                JsonParser jsonParser = new JsonParser();
                JsonObject result = jsonParser.parse(info).getAsJsonObject();

                if(Stream.of("hostname","port","database","username","password").allMatch(result::has)) {
                    host = result.get("hostname").getAsString();
                    port = result.get("port").getAsString();
                    database = result.get("database").getAsString();
                    username = result.get("username").getAsString();
                    password = result.get("password").getAsString();
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return new MySQLCredentials(host, port, database, username, password);
    }

    public void put(String key, Object value){
        if (VariablesMain.getInstance().useMySQL()) {
            MySQL mySQL = getMySQL();
            try {
                Connection connection = mySQL.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT varvalue FROM " + VARIABLES_TABLE + " WHERE `varname`='" + key.replace("'", "''") + "'");
                ResultSet valueCheck = preparedStatement.executeQuery();
                if (valueCheck.next()) {
                    if (valueCheck.getString(1) != null) {
                        mySQL.update("DELETE FROM " + VARIABLES_TABLE + " WHERE `varname`='" + key.replace("'", "''") + "'");
                    }
                }
                mySQL.update("INSERT INTO " + VARIABLES_TABLE + " (varname, varvalue) VALUES ('" + key + "', '" + value + "');");
                connection.close();
                valueCheck.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            storageFile.set(key, value);
        }
    }

    public Object get(String key) {
        if (VariablesMain.getInstance().useMySQL()) {
            MySQL mySQL = getMySQL();
            try {
                Connection connection = mySQL.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT varvalue FROM " + VARIABLES_TABLE + " WHERE `varname`='" + key.replace("'", "''") + "'");
                ResultSet value = preparedStatement.executeQuery();
                String result = "";
                if (value.next()) {
                    result = value.getString(1);
                }
                connection.close();
                value.close();
                return result;
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return storageFile.get(key);
        }
    }

    public void remove(String key) {
        if (VariablesMain.getInstance().useMySQL()) {
            MySQL mySQL = getMySQL();
            mySQL.update("DELETE FROM " + VARIABLES_TABLE + " WHERE `varname`='" + key.replace("'", "''") + "'");
        } else {
            storageFile.set(key, null);
        }
    }

    public void sendError(String name, Integer type) {
        if (type == 0) {
            System.out.println(("§6§lVariables§8 » §7The variable \" §6" + name + "§7\" does not exist!"));
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.hasPermission("ucplus.warn"))
                    p.sendMessage("§6§lVariables§8 » §7The variable \" §6" + name + "§7\" does not exist!");
            }
        } else if (type == 1) {
            System.out.println(("§6§lVariables§8 » §7The variable \" §6" + name + "§7\" had a problem and was removed!"));
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.hasPermission("ucplus.warn"))
                    p.sendMessage("§6§lVariables§8 » §7The variable \" §6" + name + "§7\" had a problem and was removed!");
            }
        }
    }

    private final List<UUID> running = new ArrayList<>();
    private final HashMap<UUID, Long> time = new HashMap<>();
    private final List<String> title = new ArrayList<>();

    @EventHandler
    public void onInvOpen(InventoryOpenEvent e) {
        try {
            title.add(e.getView().getTitle());
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (title.size() >= 2 && title.get(title.size() - 2).contains("Ultra Customizer")) {
                        if (e.getView().getTitle().contains("Overview > Addons") && e.getInventory().getSize() == (9 * 4) && !running.contains(e.getPlayer().getUniqueId())) {
                            running.add(e.getPlayer().getUniqueId());
                            time.put(e.getPlayer().getUniqueId(), System.currentTimeMillis());
                            new OverviewVars((Player) e.getPlayer(), ULTRA_CUSTOMIZER) {
                                @Override
                                public void onBack() {
                                    new Overview((Player) e.getPlayer(), ULTRA_CUSTOMIZER);
                                }
                            };
                        }
                        title.clear();
                    }else if (title.size() > 3) {
                        title.clear();
                    }
                }
            }.runTaskLater(ULTRA_CUSTOMIZER.getBootstrap(), 1);
        } catch (Exception exception) {
            UltraCustomizer.getInstance().log("Error with Inventory Open Event [variables}");
            exception.printStackTrace();
        }
    }

    @EventHandler
    public void onInvClose(InventoryCloseEvent e) {
        if(time.containsKey(e.getPlayer().getUniqueId())) {
            if(System.currentTimeMillis() - time.get(e.getPlayer().getUniqueId()) > 10) {
                running.remove(e.getPlayer().getUniqueId());
            }
        }
    }

    public Boolean isValid(String string) {
        try {
            ItemStack itemStack = ItemSerializer.itemFrom64(string);
            return itemStack != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
