package com.kihron.upcvariables.elements.item;

import com.kihron.upcvariables.variablesManager.VariablesMain;
import me.TechsCode.UltraCustomizer.UltraCustomizer;
import me.TechsCode.UltraCustomizer.base.item.ItemSerializer;
import me.TechsCode.UltraCustomizer.base.item.XMaterial;
import me.TechsCode.UltraCustomizer.scriptSystem.objects.*;
import me.TechsCode.UltraCustomizer.scriptSystem.objects.datatypes.DataType;
import org.bukkit.inventory.ItemStack;

public class StoreItem extends Element {

    public StoreItem(UltraCustomizer ultraCustomizer) {
        super(ultraCustomizer);
    }

    @Override
    public String getName() {
        return "Store Variable (Item)";
    }

    @Override
    public String getInternalName() {
        return "var-is";
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
        return new String[]{"Stores an item as a global variable."};
    }

    @Override
    public Argument[] getArguments(ElementInfo elementInfo) {
        return new Argument[]{new Argument("name", "Name", DataType.STRING, elementInfo), new Argument("item", "Item", DataType.ITEM, elementInfo)};
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
            ItemStack itemStack = (ItemStack) this.getArguments(info)[1].getValue(instance);
            if (itemStack == null) {
                System.out.println("Could not store variable");
            } else {
                VariablesMain.getInstance().put(name, ItemSerializer.itemTo64(itemStack));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            this.getOutcomingVariables(info)[0].register(instance, new DataRequester() {
                public Object request() {
                    return null;
                }
            });
        }
        this.getConnectors(info)[0].run(instance);
    }
}
