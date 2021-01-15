package com.kihron.upcvariables.variablesManager;

import me.TechsCode.UltraCustomizer.base.item.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class StorageFile {

    private final String name;
    private String fileExtension = "dat";
    private File file;
    private YamlConfiguration config;

    public StorageFile(String name, HashMap<String, Object> list) {
        this.name = name;
        load();
        setup(list);
    }

    public StorageFile(String name, HashMap<String, Object> list, String fileExtension) {
        this.name = name;
        this.fileExtension = fileExtension;
        load();
        setup(list);
    }

    public StorageFile(String name) {
        this.name = name;
        load();
    }

    public StorageFile(String name, String fileExtension) {
        this.name = name;
        this.fileExtension = fileExtension;
        load();
    }

    public String getName() {
        return name;
    }

    private void load() {
        this.file = new File("plugins/UltraCustomizer" + "/Storage/" + getName() + "." + this.fileExtension);
        if(!this.file.exists()) {
            this.file.getParentFile().mkdirs();
            try {
                this.file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.config = YamlConfiguration.loadConfiguration(this.file);
    }

    private void setup(HashMap<String, Object> map) {
        for(Map.Entry<String, Object> m : map.entrySet()) {
            String key = m.getKey();
            Object value = m.getValue();
            if(!config.contains(key)) {
                config.set(key, value);
                try {
                    config.save(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String getString(String s) {
        if(!config.contains(s)) return "";
        return config.getString(s);
    }

    public double getDouble(String s) {
        if(!config.contains(s)) return 0d;
        return config.getDouble(s);
    }

    public Float getFloat(String s) {
        if(!config.contains(s)) return 0f;
        return (float)config.getDouble(s);
    }

    public Object get(String s) {
        if(!config.contains(s)) return "";
        return config.get(s);
    }

    public ItemStack getItemStack(String s) {
        if(!contains(s)) return null;
        if(get(s) == null) return null;
        return config.getItemStack(s);
    }

    public XMaterial getMaterial(String s) {
        if(!contains(s)) return null;
        if(get(s) == null) return null;
        return XMaterial.fromNameString(getString(s));
    }

    public List<ItemStack> getItems(String s) {
        if(!contains(s)) return null;
        if(get(s) == null) return null;
        return (List<ItemStack>)config.getList(s);
    }

    public List<ItemStack> getItems(String s, List<ItemStack> d) {
        if(!contains(s)) return d;
        if(get(s) == null) return d;
        return (List<ItemStack>)config.getList(s);
    }

    public HashMap<Integer, ItemStack> getInventory(String s) {
        HashMap<Integer, ItemStack> items = new HashMap<>();
        for(String keys : Objects.requireNonNull(config.getConfigurationSection(s)).getKeys(false)) {
            ItemStack is = (ItemStack)get(s + "." + keys);
            if(is != null) items.put(Integer.parseInt(keys), is);
        }
        return items;
    }

    public Location getLocation(String name) {
        World world = Bukkit.getWorld(getString(name + ".World"));
        double x = getFloat(name + ".X");
        double y = getFloat(name + ".Y");
        double z = getFloat(name + ".Z");
        float yaw = (contains(name + ".Yaw") ? getFloat(name + ".Yaw") : 0.00F);
        float pitch = (contains(name + ".Pitch") ? getFloat(name + ".Pitch") : 0.00F);
        return new Location(world, x, y,z, yaw, pitch);
    }

    public boolean contains(String name) {
        return config.contains(name);
    }

    public void setLocation(String name, Location location) {
        set(name + ".World", location.getWorld().getName());
        set(name + ".X", location.getX());
        set(name + ".Y", location.getY());
        set(name + ".Z", location.getZ());
        set(name + ".Yaw", location.getYaw());
        set(name + ".Pitch", location.getPitch());
    }

    public void setItemStack(String s, ItemStack stack) {
        set(s, stack.serialize());
    }

    public void setItems(String s, List<ItemStack> items) {
        set(s, items);
    }

    public void setInventory(String s, Inventory inv) {
        for(int i = 0; i < inv.getSize(); i++) {
            if(inv.getContents()[i] != null) {
                set(s + "." + i, inv.getContents()[i]);
            }
        }
    }

    public void setInventory(String s, ItemStack[] inv) {
        for(int i = 0; i < inv.length; i++) {
            if(inv[i] != null) {
                set(s + "." + i, inv[i]);
            }
        }
    }

    public void set(String s, Object s2) {
        config.set(s, s2);
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public File getFile() {
        return file;
    }

    public YamlConfiguration getConfig() {
        return config;
    }

    public Set<String> getKeys() {
        return config.getKeys(false);
    }
}
