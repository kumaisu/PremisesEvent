/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.kumaisu.premisesevent.listener;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import io.github.kumaisu.premisesevent.Lib.Tools;
import io.github.kumaisu.premisesevent.config.Config;
import io.github.kumaisu.premisesevent.config.Messages;
import io.github.kumaisu.premisesevent.database.Database;
import io.github.kumaisu.premisesevent.database.AreaManager;
import io.github.kumaisu.premisesevent.utility.BukkitTool;
import static io.github.kumaisu.premisesevent.PremisesEvent.configManager;
import static io.github.kumaisu.premisesevent.PremisesEvent.playerControlMap;
import static io.github.kumaisu.premisesevent.config.Config.programCode;

/**
 *
 * @author sugichan
 */
public class BreakListener implements Listener {

    public Plugin plugin;

    /**
     *
     * @param plugin
     */
    public BreakListener( Plugin plugin ) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents( this, plugin );
    }

    public void spawnScoreStand( Location location, int score ) {
        Location adjustedLocation = location.add(Config.pt_x, Config.pt_y, Config.pt_z);
        ArmorStand armorStand = spawnArmorStand(adjustedLocation, score);
        scheduleArmorStandRemoval(armorStand, Config.pt_delay);
    }

    private ArmorStand spawnArmorStand(Location location, int score) {
        ArmorStand armorStand = location.getWorld().spawn(location, ArmorStand.class);
        armorStand.setGravity(true);
        armorStand.setSmall(true);
        armorStand.setBasePlate(false);
        armorStand.setCustomName("§a" + score);
        armorStand.setCustomNameVisible(true);
        armorStand.setVisible(false);
        armorStand.setInvulnerable(true);
        return armorStand;
    }

    private void scheduleArmorStandRemoval(ArmorStand armorStand, int delay) {
        Bukkit.getServer().getScheduler().runTaskLater(plugin, armorStand::remove, delay);
    }

    /**
     * ブロックが破壊された時の処理
     *
     * @param event
     */
    @EventHandler
    public void onBlockBreak( BlockBreakEvent event ) {
        if (isEventInactive() || isPlayerExempt(event)) return;

        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (block.getType().toString().contains("SIGN")) {
            handleSignBlock(player, block);
        }

        if (isRestrictedTool(player)) {
            cancelEventWithMessage(event, player, Messages.GetString("NoEventTool"));
        }

        if (!isAreaValid(player, block)) {
            cancelEventWithWarning(event, player, Messages.GetString("OverRegist"));
        }
    }

    private boolean isEventInactive() {
        return "none".equals(Config.EventName);
    }

    private boolean isPlayerExempt(BlockBreakEvent event) {
        Player player = event.getPlayer();
        return !player.getWorld().getName().equals(Config.Event_World)
                || (Config.CreativeCount && player.getGameMode() == GameMode.CREATIVE);
    }

    private static final String REQUIRED_SIGN_TEXT = ChatColor.DARK_PURPLE + "Premises" + ChatColor.RED + " ";

    private String getSignLine(Sign sign, int index) {
        String[] lines = sign.getLines();
        if (index < 0 || index >= 4) {
            throw new IllegalArgumentException("Index out of bounds for Sign lines: " + index);
        }
        return lines[index] != null ? lines[index] : "";
    }

    private void handleSignBlock(Player player, Block block) {
        Sign sign = (Sign) block.getState();
        if (!Config.SignPlace && !player.hasPermission("Premises.admin")) {
            String signContentOnLine3 = getSignLine(sign, 3); // Using the helper method
            if (REQUIRED_SIGN_TEXT.equals(signContentOnLine3)) {
                Tools.Prt(player, Messages.GetString("SignBreakNotAllowed"), Tools.consoleMode.full, Config.programCode);
            }
        }
    }
    private boolean isRestrictedTool(Player player) {
        ItemStack item = player.getInventory().getItemInMainHand();
        return Config.breakTool && item.getType() != Material.TORCH
                && (!item.getItemMeta().hasDisplayName() || !item.getItemMeta().getDisplayName().equalsIgnoreCase(Config.EventToolName));
    }

    private void cancelEventWithMessage(BlockBreakEvent event, Player player, String message) {
        Tools.Prt(player, message, Tools.consoleMode.full, Config.programCode);
        event.setCancelled(true);
    }

    private boolean isAreaValid(Player player, Block block) {
        if (!Config.Field || Config.PlayerAlarm == Config.UpperMode.None) {
            return true;
        }

        if (!AreaManager.GetSQL(AreaManager.PackAreaCode(block.getLocation()))) {
            return canRegisterMoreAreas(player);
        }

        return isAreaOwner(player);
    }

    private void cancelEventWithWarning(BlockBreakEvent event, Player player, String message) {
        Tools.Prt(player, message, Tools.consoleMode.full, Config.programCode);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 30, 2));
        event.setCancelled(true);
    }

    private boolean canRegisterMoreAreas(Player player) {
        return Config.MAX_REGIST <= 0 || AreaManager.GetRegistCount(player.getName()) < Config.MAX_REGIST;
    }

    private boolean isAreaOwner(Player player) {
        return Config.PlayerAlarm != Config.UpperMode.Block || Database.Owner.equals(player.getName());
    }

}
