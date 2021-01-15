package com.kihron.upcvariables.elements.item;

import com.kihron.upcvariables.variablesManager.VariablesMain;
import me.TechsCode.UltraCustomizer.UltraCustomizer;
import me.TechsCode.UltraCustomizer.base.item.ItemSerializer;
import me.TechsCode.UltraCustomizer.base.item.XMaterial;
import me.TechsCode.UltraCustomizer.scriptSystem.objects.*;
import me.TechsCode.UltraCustomizer.scriptSystem.objects.datatypes.DataType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GetItem extends Element {

    public GetItem(UltraCustomizer ultraCustomizer) {
        super(ultraCustomizer);
    }

    @Override
    public String getName() {
        return "Get Variable (Item)";
    }

    @Override
    public String getInternalName() {
        return "var-ig";
    }

    @Override
    public boolean isHidingIfNotCompatible() {
        return false;
    }

    @Override
    public XMaterial getMaterial() {
        return XMaterial.GOLD_INGOT;
    }

    @Override
    public String[] getDescription() {
        return new String[]{"Gets a stored global item."};
    }

    @Override
    public Argument[] getArguments(ElementInfo elementInfo) {
        return new Argument[]{new Argument("name", "Name", DataType.STRING, elementInfo)};
    }

    @Override
    public OutcomingVariable[] getOutcomingVariables(ElementInfo elementInfo) {
        return new OutcomingVariable[]{new OutcomingVariable("item", "Item", DataType.ITEM, elementInfo)};
    }

    @Override
    public Child[] getConnectors(ElementInfo elementInfo) {
        return new Child[] { new DefaultChild(elementInfo, "next") };
    }

    @Override
    public void run(ElementInfo info, ScriptInstance instance) {
        String name = (String) this.getArguments(info)[0].getValue(instance);
        ItemStack error = new ItemStack(Material.COCOA_BEANS);
        ItemMeta meta = error.getItemMeta();
        assert meta != null;
        meta.setDisplayName(ChatColor.RED + "There was an error with the variable!");
        error.setItemMeta(meta);
        try {
            String itemStack = (String) VariablesMain.getInstance().get(name);

                this.getOutcomingVariables(info)[0].register(instance, new DataRequester() {
                    @Override
                    public Object request() {
                        if (itemStack == null) {
                            VariablesMain.getInstance().sendError(name, 0);
                            return error;
                        } else {
                            Boolean valid = VariablesMain.getInstance().isValid(itemStack);
                            if (valid) {
                                return ItemSerializer.itemFrom64(itemStack);
                            } else {
                                VariablesMain.getInstance().remove(name);
                                VariablesMain.getInstance().sendError(name, 1);
                                return error;
                            }
                        }
                    }
                });
        } catch (Exception ex) {
            this.getOutcomingVariables(info)[0].register(instance, new DataRequester() {
                public Object request() {
                    return error;
                }
            });
        }
        this.getConnectors(info)[0].run(instance);
    }
}
