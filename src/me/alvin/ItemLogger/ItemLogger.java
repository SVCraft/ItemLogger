package me.alvin.ItemLogger;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class ItemLogger extends JavaPlugin {
    private List<Material> loggedMaterials = new ArrayList<>();
    private List<EntityType> loggedEntityTypes = new ArrayList<>();

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(new EventListener(this), this);

        // Materials

        getConfig().options().copyDefaults(true);
        if (getConfig().isSet("loggedMaterials") && getConfig().isList("loggedMaterials")) {
            List<String> loggedMaterials = getConfig().getStringList("loggedMaterials");

            for (String stringMaterial : loggedMaterials) {
                Material material = Material.getMaterial(stringMaterial);
                if (material == null || material == Material.AIR) {
                    getLogger().warning("Material \""+ stringMaterial + "\" not found");
                } else {
                    this.loggedMaterials.add(material);
                }
            }
        } else {
            this.loggedMaterials.add(Material.DIAMOND);
            this.loggedMaterials.add(Material.SADDLE);
            this.loggedMaterials.add(Material.NAME_TAG);
            this.loggedMaterials.add(Material.TOTEM_OF_UNDYING);
            this.loggedMaterials.add(Material.ELYTRA);
            this.loggedMaterials.add(Material.TRIDENT);
            this.loggedMaterials.add(Material.DIAMOND_SWORD);
            this.loggedMaterials.add(Material.DIAMOND_PICKAXE);
            this.loggedMaterials.add(Material.BOW);
            this.loggedMaterials.add(Material.ENCHANTING_TABLE);
            this.loggedMaterials.add(Material.SPAWNER);
            this.loggedMaterials.add(Material.ENDER_CHEST);
            this.loggedMaterials.add(Material.ANVIL);
            this.loggedMaterials.add(Material.CHIPPED_ANVIL);
            this.loggedMaterials.add(Material.DAMAGED_ANVIL);
            this.loggedMaterials.add(Material.TNT);

            List<String> stringLoggedMaterials = new ArrayList<>();
            for (Material loggedMaterial : this.loggedMaterials) {
                stringLoggedMaterials.add(loggedMaterial.toString());
            }
            getConfig().addDefault("loggedMaterials", stringLoggedMaterials);
            saveConfig();
        }

        String loggedMaterials = "";
        for (int i = 0; i < this.loggedMaterials.size(); i++) {
            Material loggedMaterial = this.loggedMaterials.get(i);

            boolean isLast = (i + 1) == this.loggedMaterials.size();
            loggedMaterials += loggedMaterial.toString() + (isLast ? "" : ", ");
        }
        getLogger().info("Logging these materials: "+ loggedMaterials);

        // EntityTypes

        if (getConfig().isSet("loggedEntityTypes") && getConfig().isList("loggedEntityTypes")) {
            List<String> loggedEntityTypes = getConfig().getStringList("loggedEntityTypes");

            for (String stringEntityType : loggedEntityTypes) {
                EntityType entityType;
                try {
                    entityType = EntityType.valueOf(stringEntityType);
                } catch(IllegalArgumentException | NullPointerException e) {
                    getLogger().warning("Entity type \""+ stringEntityType + "\" not found");
                    continue;
                }

                this.loggedEntityTypes.add(entityType);
            }
        } else {
            this.loggedEntityTypes.add(EntityType.VILLAGER);
            this.loggedEntityTypes.add(EntityType.WOLF);
            this.loggedEntityTypes.add(EntityType.CAT);

            List<String> stringLoggedEntityTypes = new ArrayList<>();
            for (EntityType loggedEntityType : this.loggedEntityTypes) {
                stringLoggedEntityTypes.add(loggedEntityType.toString());
            }
            getConfig().addDefault("loggedEntityTypes", stringLoggedEntityTypes);
            saveConfig();
        }

        getLogger().info("ItemLogger enabled");
    }

    @Override
    public void onDisable() {
        getLogger().info("ItemLogger disabled");
    }

    public List<Material> getLoggedMaterials() {
        return this.loggedMaterials;
    }

    public List<EntityType> getLoggedEntityTypes() {
        return this.loggedEntityTypes;
    }

    public void crash(String from, Throwable e) {
        getLogger().severe("Error in ItemLogger plugin from the "+ from +": "+ e.getMessage());
        e.printStackTrace();
        getLogger().severe("Shutting down plugin due to error");
        this.getPluginLoader().disablePlugin(this);
    }
}
