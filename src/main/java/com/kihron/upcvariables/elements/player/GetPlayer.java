package com.kihron.upcvariables.elements.player;


import com.kihron.upcvariables.variablesManager.VariablesMain;
import me.TechsCode.UltraCustomizer.UltraCustomizer;
import me.TechsCode.UltraCustomizer.base.item.XMaterial;
import me.TechsCode.UltraCustomizer.scriptSystem.objects.*;
import me.TechsCode.UltraCustomizer.scriptSystem.objects.datatypes.DataType;
import org.bukkit.Bukkit;

public class GetPlayer extends Element {

    public GetPlayer(UltraCustomizer ultraCustomizer) {
        super(ultraCustomizer);
    }

    @Override
    public String getName() {
        return "Get Variable (Player)";
    }

    @Override
    public String getInternalName() {
        return "var-pg";
    }

    @Override
    public boolean isHidingIfNotCompatible() {
        return false;
    }

    @Override
    public XMaterial getMaterial() {
        return XMaterial.PLAYER_HEAD;
    }

    @Override
    public String[] getDescription() {
        return new String[]{"Gets a stored global player."};
    }

    @Override
    public Argument[] getArguments(ElementInfo elementInfo) {
        return new Argument[]{new Argument("name", "Name", DataType.STRING, elementInfo)};
    }

    @Override
    public OutcomingVariable[] getOutcomingVariables(ElementInfo elementInfo) {
        return new OutcomingVariable[]{new OutcomingVariable("player", "Player", DataType.PLAYER, elementInfo)};
    }

    @Override
    public Child[] getConnectors(ElementInfo elementInfo) {
        return new Child[] { new DefaultChild(elementInfo, "next") };
    }

    @Override
    public void run(ElementInfo info, ScriptInstance instance) {
        String name = (String) this.getArguments(info)[0].getValue(instance);
        try {
            String player = (String) VariablesMain.getInstance().get(name);

            if (player == null) {
                this.getOutcomingVariables(info)[0].register(instance, new DataRequester() {
                    @Override
                    public Object request() {
                        VariablesMain.getInstance().sendError(name, 0);
                        return Bukkit.getPlayer("");
                    }
                });
            }
            else {
                this.getOutcomingVariables(info)[0].register(instance, new DataRequester() {
                    @Override
                    public Object request() {
                        return Bukkit.getPlayer(player);
                    }
                });
            }
        } catch (Exception ex) {
            this.getOutcomingVariables(info)[0].register(instance, new DataRequester() {
                public Object request() {
                    VariablesMain.getInstance().remove(name);
                    VariablesMain.getInstance().sendError(name, 1);
                    return Bukkit.getPlayer("");
                }
            });
        }
        this.getConnectors(info)[0].run(instance);
    }
}
