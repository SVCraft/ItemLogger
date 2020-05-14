package me.alvin.ItemLogger;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

class EventListener implements Listener {
    private ItemLogger itemLogger;

    EventListener(ItemLogger itemLogger) {
        this.itemLogger = itemLogger;
    }

    private String formatLocation(Location location) {
        return location.getBlockX() +" "
                + location.getBlockY() +" "
                + location.getBlockZ()
                + (location.getWorld() != null && location.getWorld() != Bukkit.getWorlds().get(0) ? (" "+ location.getWorld().getName()) : "")
                + ((location.getWorld() != null && location.getWorld().getEnvironment() != World.Environment.NORMAL) ? (" "+ location.getWorld().getEnvironment().toString()) : "");
    }

    private String formatItem(ItemStack item) {
        return item.getAmount() + "x " + item.getType().toString() + ((item.getItemMeta() != null && item.getItemMeta().hasDisplayName()) ? "("+ item.getItemMeta().getDisplayName() +")" : "");
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClick(InventoryClickEvent event) {
        try {
            if ((event.getCurrentItem() != null && itemLogger.getLoggedMaterials().contains(event.getCurrentItem().getType()))
                   || event.getCursor() != null && itemLogger.getLoggedMaterials().contains(event.getCursor().getType())) {
                ItemStack item = event.getCurrentItem() != null ? event.getCurrentItem() : event.getCursor();
                if (item.getType() == Material.AIR) return;

                String msg = event.getWhoClicked().getName() + " clicked on " + formatItem(item) + ". ";
                msg += "The clicker is at " + formatLocation(event.getWhoClicked().getLocation()) + ". ";
                if (event.getClickedInventory() != null && event.getClickedInventory().getLocation() != null) {
                    msg += "The clicked inventory is at " + formatLocation(event.getClickedInventory().getLocation()) + ". ";
                }
                if (event.getCurrentItem() != null)
                    msg += "Current item. ";
                else
                    msg += "Cursor item. ";
                msg += event.getAction().toString() +". ";
                msg += event.getClick().toString() +". ";
                if (event.getClickedInventory() != null && event.getClickedInventory().getHolder() != null) msg += event.getClickedInventory().getHolder().getClass().getName() + ". ";

                itemLogger.getLogger().info(msg);
            }
        } catch (Throwable e) {
            itemLogger.crash("InventoryClickEvent EventListener", e);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        try {
            if (itemLogger.getLoggedMaterials().contains(event.getItemDrop().getItemStack().getType())) {
                ItemStack item = event.getItemDrop().getItemStack();
                if (item.getType() == Material.AIR) return;

                String msg = event.getPlayer().getName() + " dropped " + formatItem(item) + ". ";

                msg += "The player that dropped is at " + formatLocation(event.getPlayer().getLocation()) + ". ";

                itemLogger.getLogger().info(msg);
            }
        } catch (Throwable e) {
            itemLogger.crash("PlayerDropItemEvent EventListener", e);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityPickupItem(EntityPickupItemEvent event) {
        try {
            if (itemLogger.getLoggedMaterials().contains(event.getItem().getItemStack().getType())) {
                ItemStack item = event.getItem().getItemStack();
                if (item.getType() == Material.AIR) return;

                String msg = event.getEntity().getName() + "("+ event.getEntityType().toString() +") picked up " + formatItem(item) + ". ";
                msg += "The entity that picked up is at " + formatLocation(event.getEntity().getLocation()) + ". ";

                itemLogger.getLogger().info(msg);
            }
        } catch (Throwable e) {
            itemLogger.crash("EntityPickupItemEvent EventListener", e);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onItemDespawn(ItemDespawnEvent event) {
        try {
            if (itemLogger.getLoggedMaterials().contains(event.getEntity().getItemStack().getType())) {
                ItemStack item = event.getEntity().getItemStack();
                if (item.getType() == Material.AIR) return;

                String msg = "Item despawned: " + formatItem(item) + ". ";
                msg += "At: " + formatLocation(event.getLocation()) + ". ";

                itemLogger.getLogger().info(msg);
            }
        } catch (Throwable e) {
            itemLogger.crash("ItemDespawnEvent EventListener", e);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDamage(EntityDamageEvent event) {
        try {
            if (event.getEntity() instanceof Item) {
                Item entity = (Item) event.getEntity();
                if (itemLogger.getLoggedMaterials().contains(entity.getItemStack().getType())) {
                    ItemStack item = entity.getItemStack();
                    if (item.getType() == Material.AIR) return;

                    String msg = "Item being damaged: " + formatItem(item) + ". ";
                    msg += "At: " + formatLocation(entity.getLocation()) + ". ";
                    msg += "Cause: "+ event.getCause().toString() +". ";

                    itemLogger.getLogger().info(msg);
                }
            }
        } catch (Throwable e) {
            itemLogger.crash("EntityDamageEvent EventListener", e);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event) {
        try {
            if (itemLogger.getLoggedMaterials().contains(event.getBlock().getType())) {
                String msg = event.getPlayer().getName() + " mined "+ event.getBlock().getType() +" ";
                msg += "at "+ formatLocation(event.getBlock().getLocation()) +". ";
                msg += "The player was at "+ formatLocation(event.getPlayer().getLocation()) + " when the block was broken. ";
                msg += "The player is holding "+ formatItem(event.getPlayer().getInventory().getItemInMainHand()) +" in their main hand. ";

                itemLogger.getLogger().info(msg);
            }
        } catch (Throwable e) {
            itemLogger.crash("BlockBreakEvent EventListener", e);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent event) {
        try {
            if (itemLogger.getLoggedMaterials().contains(event.getBlock().getType())) {
                String msg = event.getPlayer().getName() + " placed " + event.getBlock().getType() + " ";
                msg += "at "+ formatLocation(event.getBlock().getLocation());
                msg += "The player is at "+ formatLocation(event.getPlayer().getLocation());

                itemLogger.getLogger().info(msg);
            }
        } catch (Throwable e) {
            itemLogger.crash("BlockPlaceEvent EventListener", e);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerPortal(PlayerPortalEvent event) {
        try {
            String msg = event.getPlayer().getName() + " traveled in a portal. ";
            msg += "From " + formatLocation(event.getFrom()) + ". ";
            msg += "To " + formatLocation(event.getTo()) + ". ";
            msg += "Cause: " + event.getCause() + ". ";

            itemLogger.getLogger().info(msg);
        } catch (Throwable e) {
            itemLogger.crash("PlayerPortalEvent EventListener", e);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityExplode(EntityExplodeEvent event) {
        try {
            if (event.getEntityType() == EntityType.PRIMED_TNT || event.getEntityType() == EntityType.CREEPER) {
                String msg = event.getEntityType().toString() +" exploded ";
                msg += "at "+ formatLocation(event.getLocation()) + ". ";
                msg += "yield "+ event.getYield() +". ";

                itemLogger.getLogger().info(msg);

                if (event.getLocation().getWorld() != null) {
                    itemLogger.getLogger().info("Nearby players:");

                    Collection<Entity> nearbyEntities = event.getLocation().getWorld().getNearbyEntities(event.getLocation(), 100, 100, 100);
                    for (Entity entity : nearbyEntities) {
                        if (entity instanceof Player) {
                            Player player = (Player) entity;
                            itemLogger.getLogger().info(player.getName() + " at "+ formatLocation(player.getLocation()));
                        }
                    }

                }
            }
        } catch (Throwable e) {
            itemLogger.crash("PlayerPortalEvent EventListener", e);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDeath(EntityDeathEvent event) {
        try {
            if (itemLogger.getLoggedEntityTypes().contains(event.getEntityType())) {
                String msg = "A "+ event.getEntityType().toString() + "("+ ((event.getEntity().getCustomName() != null) ? event.getEntity().getCustomName() : event.getEntity().getName()) +") was killed. ";
                if (event.getEntity().getKiller() != null) msg += "Killed by "+ event.getEntity().getKiller().getName() +". ";
                msg += "Died at "+ formatLocation(event.getEntity().getLocation()) +". ";
                if (event.getEntity().getKiller() != null) msg += "Killer was at "+ formatLocation(event.getEntity().getKiller().getLocation()) +". ";
                if (event.getEntity().getLastDamageCause() != null) msg += "Last damage cause "+ event.getEntity().getLastDamageCause().getCause().toString() + ". ";

                itemLogger.getLogger().info(msg);
            }
        } catch (Throwable e) {
            itemLogger.crash("EntityDeathEvent EventListener", e);
        }
    }
}
