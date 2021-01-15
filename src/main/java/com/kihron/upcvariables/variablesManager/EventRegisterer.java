package com.kihron.upcvariables.variablesManager;

import me.TechsCode.UltraCustomizer.scriptSystem.ElementRegistration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;

public class EventRegisterer {


    public void onEnable() {
        Bukkit.getServer().getPluginManager().registerEvents(VariablesMain.getInstance(), (Plugin) ElementRegistration.getElementFromName("GetBoolean").plugin);
    }

    public void onDisable() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getOpenInventory().getTitle().contains("Variables Manager"))
                player.closeInventory();
            HandlerList.unregisterAll(VariablesMain.getInstance());
        }
    }
}
