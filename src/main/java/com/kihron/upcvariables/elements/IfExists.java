package com.kihron.upcvariables.elements;

import com.kihron.upcvariables.variablesManager.VariablesMain;
import me.TechsCode.UltraCustomizer.UltraCustomizer;
import me.TechsCode.UltraCustomizer.base.item.XMaterial;
import me.TechsCode.UltraCustomizer.scriptSystem.objects.*;
import me.TechsCode.UltraCustomizer.scriptSystem.objects.datatypes.DataType;

public class IfExists extends Element {
    public IfExists(UltraCustomizer ultraCustomizer) {
        super(ultraCustomizer);
    }

    @Override
    public String getName() {
        return "Variable Exists";
    }

    @Override
    public String getInternalName() {
        return "var-ifexist";
    }

    @Override
    public boolean isHidingIfNotCompatible() {
        return false;
    }

    @Override
    public XMaterial getMaterial() {
        return XMaterial.LAVA_BUCKET;
    }

    @Override
    public String[] getDescription() {
        return new String[]{"Checks if a variable exists."};
    }

    @Override
    public Argument[] getArguments(ElementInfo elementInfo) {
        return new Argument[]{new Argument("name", "Name", DataType.STRING, elementInfo)};
    }

    @Override
    public OutcomingVariable[] getOutcomingVariables(ElementInfo elementInfo) {
        return new OutcomingVariable[]{new OutcomingVariable("result", "Result", DataType.BOOLEAN, elementInfo)};
    }

    @Override
    public Child[] getConnectors(ElementInfo elementInfo) {
        return new Child[] { new DefaultChild(elementInfo, "next") };
    }

    @Override
    public void run(ElementInfo info, ScriptInstance instance) {
        String name = (String) this.getArguments(info)[0].getValue(instance);
        try {
            this.getOutcomingVariables(info)[0].register(instance, new DataRequester() {
                @Override
                public Object request() {
                    if (VariablesMain.getInstance().useMySQL()) {
                        return VariablesMain.getInstance().get(name) != "";
                    } else {
                        return VariablesMain.storageFile.contains(name);
                    }
                }
            });
        } catch (Exception ex) {
            this.getOutcomingVariables(info)[0].register(instance, new DataRequester() {
                public Object request() {
                    ex.printStackTrace();
                    return false;
                }
            });
        }
        this.getConnectors(info)[0].run(instance);
    }
}
