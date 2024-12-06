package com.kihron.upcvariables.guis;

import me.TechsCode.UltraCustomizer.ColorPalette;
import me.TechsCode.UltraCustomizer.UltraCustomizer;
import me.TechsCode.UltraCustomizer.base.addons.gui.AddonsMarketplaceListView;
import me.TechsCode.UltraCustomizer.base.addons.gui.InstalledAddonsView;
import me.TechsCode.UltraCustomizer.base.gui.Button;
import me.TechsCode.UltraCustomizer.base.gui.GUI;
import me.TechsCode.UltraCustomizer.base.gui.Model;
import me.TechsCode.UltraCustomizer.base.item.XMaterial;
import me.TechsCode.UltraCustomizer.base.legacy.Common;
import me.TechsCode.UltraCustomizer.base.visual.Animation;
import me.TechsCode.UltraCustomizer.base.visual.Color;
import me.TechsCode.UltraCustomizer.base.visual.Colors;
import org.bukkit.entity.Player;

public abstract class OverviewVars extends GUI {
    private final UltraCustomizer plugin;

    public OverviewVars(Player var1, UltraCustomizer var2) {
        super(var1, var2);
        this.plugin = var2;
    }

    public abstract void onBack();

    public String getCurrentTitle() {
        return "Overview > Addons";
    }

    public void construct(Model var1) {
        var1.setSlots(36);
        var1.button(12, this::MarketplaceButton);
        var1.button(16, this::InstalledAddonsButton);
        var1.button(36, this::openVariablesGUI);
        var1.button(32, (var1x) -> {
            Common.BackButton(var1x, (var1y) -> this.onBack());
        });
    }

    private void MarketplaceButton(Button var1) {
        boolean var2 = this.plugin.getAddonManager().isCloudOnline();
        var1.material(XMaterial.EMERALD).name(Animation.wave("Marketplace", new Color[]{var2 ? ColorPalette.MAIN : Colors.Red, Colors.WHITE})).lore(new String[]{var2 ? "§7Click to browse our §eMarketplace" : "§6Our Marketplace is currently §cunavailable", "", "§7Our Marketplace provides you", "§7with various addons that are", "§7downloadable with just one click!"});
        var1.action((var2x) -> {
            if (var2) {
                AddonsMarketplaceListView var10001 = new AddonsMarketplaceListView(this.p, this.plugin) {
                    public void onBack() {
                        OverviewVars.this.reopen();
                    }
                };
            }
        });
    }

    private void InstalledAddonsButton(Button var1) {
        var1.material(XMaterial.HOPPER_MINECART).name(Animation.wave("Installed", new Color[]{ColorPalette.MAIN, Colors.WHITE})).lore(new String[]{"§7Click to view §eInstalled Addons", "", "§7Amount: §e" + this.plugin.getAddonManager().getInstalledAddons().size(), "", "§7View, edit, and remove all installed addons.", "§7Don't want an addon anymore? Then delete it!"});
        var1.action((var1x) -> {
            InstalledAddonsView var10001 = new InstalledAddonsView(this.p, this.plugin) {
                public void onBack() {
                    OverviewVars.this.reopen();
                }
            };
        });
    }

    public void openVariablesGUI(Button button) {
        button.material(XMaterial.PAPER).name(Animation.wave("Variables Manager", new Color[]{ColorPalette.MAIN, Colors.WHITE})).lore(new String[]{"§7" + "Click to manipulate variables!"});
        button.action((action) -> new VariablesGUI(this.p, this.plugin) {
            @Override
            public void onBack() {
                OverviewVars.this.reopen();
            }
        });
    }
}
