package com.kihron.upcvariables.elements.booleans;

import com.kihron.upcvariables.variablesManager.VariablesMain;
import me.TechsCode.UltraCustomizer.UltraCustomizer;
import me.TechsCode.UltraCustomizer.base.item.XMaterial;
import me.TechsCode.UltraCustomizer.scriptSystem.objects.*;
import me.TechsCode.UltraCustomizer.scriptSystem.objects.datatypes.DataType;

public class GetBoolean extends Element {

    public GetBoolean(UltraCustomizer ultraCustomizer) {
        super(ultraCustomizer);
    }

    @Override
    public String getName() {
        return "Get Variable (Boolean)";
    }

    @Override
    public String getInternalName() {
        return "var-bg";
    }

    @Override
    public boolean isHidingIfNotCompatible() {
        return false;
    }

    @Override
    public XMaterial getMaterial() {
        return XMaterial.COMPARATOR;
    }

    @Override
    public String[] getDescription() {
        return new String[]{"Gets a stored global boolean."};
    }

    @Override
    public Argument[] getArguments(ElementInfo elementInfo) {
        return new Argument[]{new Argument("name", "Name", DataType.STRING, elementInfo)};
    }

    @Override
    public OutcomingVariable[] getOutcomingVariables(ElementInfo elementInfo) {
        return new OutcomingVariable[]{new OutcomingVariable("boolean", "Boolean", DataType.BOOLEAN, elementInfo)};
    }

    @Override
    public Child[] getConnectors(ElementInfo elementInfo) {
        return new Child[] { new DefaultChild(elementInfo, "next") };
    }

    @Override
    public void run(ElementInfo info, ScriptInstance instance) {
        String name = (String) this.getArguments(info)[0].getValue(instance);
        try {
            Boolean b = Boolean.parseBoolean(String.valueOf(VariablesMain.getInstance().get(name)));

            if (b == null) {
                VariablesMain.getInstance().sendError(name, 0);
                this.getOutcomingVariables(info)[0].register(instance, new DataRequester() {
                    @Override
                    public Object request() {
                        VariablesMain.getInstance().sendError(name, 0);
                        return false;
                    }
                });
            }
            else {
                this.getOutcomingVariables(info)[0].register(instance, new DataRequester() {
                    @Override
                    public Object request() {
                        return b;
                    }
                });
            }
        } catch (Exception ex) {
            this.getOutcomingVariables(info)[0].register(instance, new DataRequester() {
                public Object request() {
                    VariablesMain.getInstance().remove(name);
                    VariablesMain.getInstance().sendError(name, 1);
                    return false;
                }
            });
        }
        this.getConnectors(info)[0].run(instance);
    }
}
