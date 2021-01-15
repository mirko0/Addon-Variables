package com.kihron.upcvariables.elements.number;

import com.kihron.upcvariables.variablesManager.VariablesMain;
import me.TechsCode.UltraCustomizer.UltraCustomizer;
import me.TechsCode.UltraCustomizer.base.item.XMaterial;
import me.TechsCode.UltraCustomizer.scriptSystem.objects.*;
import me.TechsCode.UltraCustomizer.scriptSystem.objects.datatypes.DataType;

public class GetNumber extends Element {

    public GetNumber(UltraCustomizer ultraCustomizer) {
        super(ultraCustomizer);
    }

    @Override
    public String getName() {
        return "Get Variable (Number)";
    }

    @Override
    public String getInternalName() {
        return "var-ng";
    }

    @Override
    public boolean isHidingIfNotCompatible() {
        return false;
    }

    @Override
    public XMaterial getMaterial() {
        return XMaterial.REDSTONE;
    }

    @Override
    public String[] getDescription() {
        return new String[]{"Gets a stored global number."};
    }

    @Override
    public Argument[] getArguments(ElementInfo elementInfo) {
        return new Argument[]{new Argument("name", "Name", DataType.STRING, elementInfo)};
    }

    @Override
    public OutcomingVariable[] getOutcomingVariables(ElementInfo elementInfo) {
        return new OutcomingVariable[]{new OutcomingVariable("num", "Number", DataType.NUMBER, elementInfo)};
    }

    @Override
    public Child[] getConnectors(ElementInfo elementInfo) {
        return new Child[] { new DefaultChild(elementInfo, "next") };
    }

    @Override
    public void run(ElementInfo info, ScriptInstance instance) {
        String name = (String) this.getArguments(info)[0].getValue(instance);
        try {
            Double number = (Double) VariablesMain.getInstance().get(name);

            if (number == null) {
                this.getOutcomingVariables(info)[0].register(instance, new DataRequester() {
                    @Override
                    public Object request() {
                        VariablesMain.getInstance().sendError(name, 0);
                        return (long) 0;
                    }
                });
            }
            else {
                this.getOutcomingVariables(info)[0].register(instance, new DataRequester() {
                    @Override
                    public Object request() {
                        return number.longValue();
                    }
                });
            }
        } catch (Exception ex) {
            this.getOutcomingVariables(info)[0].register(instance, new DataRequester() {
                public Object request() {
                    VariablesMain.getInstance().remove(name);
                    VariablesMain.getInstance().sendError(name, 1);
                    return 0;
                }
            });
        }
        this.getConnectors(info)[0].run(instance);
    }
}
