package com.kihron.upcvariables.guis;

import com.kihron.upcvariables.mysql.MySQL;
import com.kihron.upcvariables.variablesManager.Variable;
import com.kihron.upcvariables.variablesManager.VariableStorageFile;
import com.kihron.upcvariables.variablesManager.VariablesMain;
import me.TechsCode.UltraCustomizer.UltraCustomizer;
import me.TechsCode.UltraCustomizer.base.gui.Button;
import me.TechsCode.UltraCustomizer.base.gui.Model;
import me.TechsCode.UltraCustomizer.base.gui.pageableview.BasicSearch;
import me.TechsCode.UltraCustomizer.base.gui.pageableview.PageableGUI;
import me.TechsCode.UltraCustomizer.base.gui.pageableview.SearchFeature;
import me.TechsCode.UltraCustomizer.base.item.ItemSerializer;
import me.TechsCode.UltraCustomizer.base.item.XMaterial;
import me.TechsCode.UltraCustomizer.base.visual.Animation;
import me.TechsCode.UltraCustomizer.base.visual.Colors;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class VariablesGUI extends PageableGUI<Variable> {

    UltraCustomizer uc;

    private final String VARIABLES_TABLE = "Variables";

    private VariableStorageFile getStorageFile() {
        return VariablesMain.storageFile;
    }

    public static Map<String, String> variables = new HashMap<>();

    VariablesGUI(Player p, UltraCustomizer customizer) {
        super(p, customizer);
        uc = customizer;
    }

    @Override
    public String getTitle() {
        return "Variables Manager";
    }

    private void totalVariables(Button button) {
        List<String> lore = new ArrayList<>();
        int size = 0;

        if (VariablesMain.getInstance().useMySQL()) {
            MySQL mySQL = VariablesMain.getMySQL();
            try {
                Connection connection = mySQL.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT COUNT(*) FROM " + VARIABLES_TABLE + ";");
                ResultSet rs = preparedStatement.executeQuery();
                while (rs.next()) {
                    size = rs.getInt(1);
                }
                rs.close();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            size = getStorageFile().getKeys().size();
        }

        lore.add("§bTotal§8 \u00BB §f" + size);

        if (VariablesMain.getInstance().useMySQL()) {
            lore.add("§bStorage§8 \u00BB §fMySQL");
        } else {
            lore.add("§bStorage§8 \u00BB §fLocal");
        }

        lore.add("");
        lore.add("§eClick §7to toggle");
        lore.add("§7Storage Type!");

        button.material(XMaterial.PAPER)
                .name(Animation.fading("Variables Info", Colors.Aqua, Colors.WHITE))
                .lore(lore);

        button.action(ActionType -> {
                if (!VariablesMain.getInstance().hasMySQL()) {
                    p.sendMessage("§6§lVariables§8 » §7There is no available MySQL linked!");
                } else if (!VariablesMain.getInstance().useMySQL()) {
                    VariablesMain.sqlStorageFile.set("MySQL", "true");
                    VariablesMain.getInstance().convertToMySQL();
                } else {
                    VariablesMain.sqlStorageFile.set("MySQL", "false");
                    VariablesMain.getInstance().convertToLocal();
                }
        });
    }

    private void deleteAll(Button button) {
        button.material(XMaterial.ENDER_CHEST)
                .name(Animation.fading("Delete All", Colors.Red, Colors.WHITE))
                .lore("§cPress Q §7to Delete All!");
        button.action(ActionType -> {
            if (ActionType.name().contains("Q")) {
                if (VariablesMain.getInstance().useMySQL()) {
                    for (String var: variables.keySet()) {
                        VariablesMain.getInstance().remove(var);
                    }
                    variables.clear();
                } else {
                    for (String var: VariablesMain.storageFile.getKeys()) {
                        VariablesMain.getInstance().remove(var);
                    }
                }
            }
        });
    }

    public abstract void onBack();

    public Variable[] getObjects() {
        Variable[] variableObjects = new Variable[0];
        if (VariablesMain.getInstance().useMySQL()) {
            MySQL mySQL = VariablesMain.getMySQL();
            try {
                Connection connection = mySQL.getConnection();
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
        return variableObjects;
    }

    public SearchFeature<Variable> getSearch() {
        return new BasicSearch<Variable>() {
            @Override
            public String[] getSearchableText(Variable variable) {
                return new String[]{variable.getName()};
            }
        };
    }

    public void construct(Button button, Variable variable) {
        List<String> lore = new ArrayList<>();
        lore.add("§ePress Q §7to delete!");
        lore.add("");
        lore.add("§e§lValue");

        String s = variable.getValue();
        if (s.contains("rO0ABXN") && VariablesMain.getInstance().isValid(s)) {
            s = ItemSerializer.itemFrom64(s).getType().toString();
        } else if (s.length() > 10) {
            s = s.substring(0, 10) + "§7...";
        }

        lore.add(("§7- §f" + s));

        button.material(XMaterial.BOOK)
                .name("§6§l" + variable.getName())
                .lore(lore);

        button.action(ActionType -> {
            if (ActionType.name().contains("Q")) {
                if (VariablesMain.getInstance().useMySQL()) {
                    variables.remove(variable.getName());
                }
                VariablesMain.getInstance().remove(variable.getName());
            }
        });
    }

    @Override
    protected void construct(Model info) {
        super.construct(info);
        info.button(47, this::totalVariables);
        info.button(53, this::deleteAll);
    }
}
