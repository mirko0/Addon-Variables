package com.kihron.upcvariables.elements.player;

import com.kihron.upcvariables.variablesManager.VariablesMain;
import me.TechsCode.UltraCustomizer.UltraCustomizer;
import me.TechsCode.UltraCustomizer.base.item.XMaterial;
import me.TechsCode.UltraCustomizer.scriptSystem.objects.*;
import me.TechsCode.UltraCustomizer.scriptSystem.objects.datatypes.DataType;
import org.bukkit.entity.Player;

public class StorePlayer extends Element {

    public StorePlayer(UltraCustomizer ultraCustomizer) {
        super(ultraCustomizer);
    }

    @Override
    public String getName() {
        return "Store Variable (Player)";
    }

    @Override
    public String getInternalName() {
        return "var-ps";
    }

    @Override
    public boolean isHidingIfNotCompatible() {
        return false;
    }

    @Override
    public XMaterial getMaterial() {
        return XMaterial.WRITABLE_BOOK;
    }

    @Override
    public String[] getDescription() {
        return new String[]{"Stores a player as a global variable."};
    }

    @Override
    public Argument[] getArguments(ElementInfo elementInfo) {
        return new Argument[]{new Argument("name", "Name", DataType.STRING, elementInfo), new Argument("player", "Player", DataType.PLAYER, elementInfo)};
    }

    @Override
    public OutcomingVariable[] getOutcomingVariables(ElementInfo elementInfo) {
        return new OutcomingVariable[0];
    }

    @Override
    public Child[] getConnectors(ElementInfo elementInfo) {
        return new Child[] { new DefaultChild(elementInfo, "next") };
    }

    @Override
    public void run(ElementInfo info, ScriptInstance instance) {
        try {
            String name = (String) this.getArguments(info)[0].getValue(instance);
            Player player = (Player) this.getArguments(info)[1].getValue(instance);

            VariablesMain.getInstance().put(name, player.getName());

        } catch (Exception ex) {
            ex.printStackTrace();
//            this.getOutcomingVariables(info)[0].register(instance, new DataRequester() {
//                public Object request() {
//                    return null;
//                }
//            });
        }
        this.getConnectors(info)[0].run(instance);
    }
}
