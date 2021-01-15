package com.kihron.upcvariables.elements.material;

import com.kihron.upcvariables.variablesManager.VariablesMain;
import me.TechsCode.UltraCustomizer.UltraCustomizer;
import me.TechsCode.UltraCustomizer.base.item.XMaterial;
import me.TechsCode.UltraCustomizer.scriptSystem.objects.*;
import me.TechsCode.UltraCustomizer.scriptSystem.objects.datatypes.DataType;

public class GetMaterial extends Element {

    public GetMaterial(UltraCustomizer ultraCustomizer) {
        super(ultraCustomizer);
    }

    @Override
    public String getName() {
        return "Get Variable (Material)";
    }

    @Override
    public String getInternalName() {
        return "var-mg";
    }

    @Override
    public boolean isHidingIfNotCompatible() {
        return false;
    }

    @Override
    public XMaterial getMaterial() {
        return XMaterial.IRON_INGOT;
    }

    @Override
    public String[] getDescription() {
        return new String[]{"Gets a stored global material."};
    }

    @Override
    public Argument[] getArguments(ElementInfo elementInfo) {
        return new Argument[]{new Argument("name", "Name", DataType.STRING, elementInfo)};
    }

    @Override
    public OutcomingVariable[] getOutcomingVariables(ElementInfo elementInfo) {
        return new OutcomingVariable[]{new OutcomingVariable("mat", "Material", DataType.MATERIAL, elementInfo)};
    }

    @Override
    public Child[] getConnectors(ElementInfo elementInfo) {
        return new Child[] { new DefaultChild(elementInfo, "next") };
    }

    @Override
    public void run(ElementInfo info, ScriptInstance instance) {
        String name = (String) this.getArguments(info)[0].getValue(instance);
        try {
            String material = (String) VariablesMain.getInstance().get(name);

            if (material == null) {
                this.getOutcomingVariables(info)[0].register(instance, new DataRequester() {
                    @Override
                    public Object request() {
                        VariablesMain.getInstance().sendError(name, 0);
                        return XMaterial.AIR;
                    }
                });
            }
            else {
                this.getOutcomingVariables(info)[0].register(instance, new DataRequester() {
                    @Override
                    public Object request() {
                        return XMaterial.fromNameString(material);
                    }
                });
            }
        } catch (Exception ex) {
            this.getOutcomingVariables(info)[0].register(instance, new DataRequester() {
                public Object request() {
                    VariablesMain.getInstance().remove(name);
                    VariablesMain.getInstance().sendError(name, 1);
                    return XMaterial.AIR;
                }
            });
        }
        this.getConnectors(info)[0].run(instance);
    }
}

