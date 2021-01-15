package com.kihron.upcvariables.elements.string;

import com.kihron.upcvariables.variablesManager.VariablesMain;
import me.TechsCode.UltraCustomizer.UltraCustomizer;
import me.TechsCode.UltraCustomizer.base.item.XMaterial;
import me.TechsCode.UltraCustomizer.scriptSystem.objects.*;
import me.TechsCode.UltraCustomizer.scriptSystem.objects.datatypes.DataType;

public class GetString extends Element {

    public GetString(UltraCustomizer ultraCustomizer) {
        super(ultraCustomizer);
    }

    @Override
    public String getName() {
        return "Get Variable (String)";
    }

    @Override
    public String getInternalName() {
        return "var-sg";
    }

    @Override
    public boolean isHidingIfNotCompatible() {
        return false;
    }

    @Override
    public XMaterial getMaterial() {
        return XMaterial.STRING;
    }

    @Override
    public String[] getDescription() {
        return new String[]{"Gets a stored global string."};
    }

    @Override
    public Argument[] getArguments(ElementInfo elementInfo) {
        return new Argument[]{new Argument("name", "Name", DataType.STRING, elementInfo)};
    }

    @Override
    public OutcomingVariable[] getOutcomingVariables(ElementInfo elementInfo) {
        return new OutcomingVariable[]{new OutcomingVariable("string", "String", DataType.STRING, elementInfo)};
    }

    @Override
    public Child[] getConnectors(ElementInfo elementInfo) {
        return new Child[] { new DefaultChild(elementInfo, "next") };
    }

    @Override
    public void run(ElementInfo info, ScriptInstance instance) {
        String name = (String) this.getArguments(info)[0].getValue(instance);
        try {
            String string = (String) VariablesMain.getInstance().get(name);

            if (string == null) {
                this.getOutcomingVariables(info)[0].register(instance, new DataRequester() {
                    @Override
                    public Object request() {
                        VariablesMain.getInstance().sendError(name, 0);
                        return "";
                    }
                });
            }
                else {
                    this.getOutcomingVariables(info)[0].register(instance, new DataRequester() {
                        @Override
                        public Object request() {
                            return string;
                        }
                    });
                }
        } catch (Exception ex) {
            this.getOutcomingVariables(info)[0].register(instance, new DataRequester() {
                public Object request() {
                    VariablesMain.getInstance().remove(name);
                    VariablesMain.getInstance().sendError(name, 1);
                    return "";
                }
            });
        }
        this.getConnectors(info)[0].run(instance);
    }
}
