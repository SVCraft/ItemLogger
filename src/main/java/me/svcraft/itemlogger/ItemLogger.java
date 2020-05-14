package me.svcraft.itemlogger;

import me.svcraft.minigames.config.Config;
import me.svcraft.minigames.plugin.SVCraftPlugin;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.EntityType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ItemLogger extends SVCraftPlugin {
    private List<Material> loggedMaterials = new ArrayList<>();
    private List<EntityType> loggedEntityTypes = new ArrayList<>();

    @Override
    public void onPluginEnable() {
        this.registerPerWorldEvents(new EventListener(this));
        this.registerCommand("itemlogger", new Command(this));

        try {
            this.reload();
        } catch (IOException | InvalidConfigurationException e) {
            this.crash("ItemLogger#reload", e);
        }

        getLogger().info("ItemLogger enabled");
    }

    @Override
    public void onPluginDisable() {
        getLogger().info("ItemLogger disabled");
    }

    @Override
    public void reload() throws IOException, InvalidConfigurationException {
        super.reload();

        Config config = this.getConfigManager().getConfig("config");

        // Materials

        this.loggedMaterials.clear();

        if (config.isSet("loggedMaterials") && config.isList("loggedMaterials")) {
            List<String> loggedMaterials = config.getStringList("loggedMaterials");

            for (String stringMaterial : loggedMaterials) {
                Material material = Material.getMaterial(stringMaterial);
                if (material == null || material == Material.AIR) {
                    this.getLogger().warning("Material \""+ stringMaterial + "\" not found");
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
            config.set("loggedMaterials", stringLoggedMaterials);
            config.save();
        }

        StringBuilder loggedMaterials = new StringBuilder();
        for (int i = 0; i < this.loggedMaterials.size(); i++) {
            Material loggedMaterial = this.loggedMaterials.get(i);

            boolean isLast = (i + 1) == this.loggedMaterials.size();
            loggedMaterials.append(loggedMaterial.toString()).append(isLast ? "" : ", ");
        }
        this.getLogger().info("Logging these materials: "+ loggedMaterials);

        // EntityTypes

        this.loggedEntityTypes.clear();

        if (config.isSet("loggedEntityTypes") && config.isList("loggedEntityTypes")) {
            List<String> loggedEntityTypes = config.getStringList("loggedEntityTypes");

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
            config.set("loggedEntityTypes", stringLoggedEntityTypes);
            config.save();
        }

        StringBuilder loggedEntityTypes = new StringBuilder();
        for (int i = 0; i < this.loggedEntityTypes.size(); i++) {
            EntityType loggedEntityType = this.loggedEntityTypes.get(i);

            boolean isLast = (i + 1) == this.loggedEntityTypes.size();
            loggedEntityTypes.append(loggedEntityType.toString()).append(isLast ? "" : ", ");
        }
        this.getLogger().info("Logging these entity types: "+ loggedEntityTypes);
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
