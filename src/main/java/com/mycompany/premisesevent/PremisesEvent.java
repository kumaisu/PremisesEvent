/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.premisesevent;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author sugichan
 */
public class PremisesEvent extends JavaPlugin implements Listener {

    private Plugin plugin;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents( this, this );
        plugin = (Plugin) this;
    }

    @EventHandler
    public void onBlockPlace( BlockPlaceEvent event ) {
        Player player = event.getPlayer();
        // event.getBlockReplacedState().setMetadata( "PLACED", new FixedMetadataValue( plugin, true ) );
        Block block = event.getBlock();
        block.setMetadata( "PLACED", new FixedMetadataValue( plugin, true ) );
        player.sendMessage( ChatColor.YELLOW + "Set Custom MetaData" );
    }
    
    @EventHandler
    public void onBlockBreak( BlockBreakEvent event ) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        player.sendMessage( ChatColor.GREEN + "Break to " + block.getType().name() );
        player.sendMessage( ChatColor.GREEN + block.getState().getData().toString() );
        //  block.getMetadata( "PLACED" ); // Check if it is null. If so, it was generated naturally / not placed by the player
        player.sendMessage( ChatColor.GREEN + ( block.hasMetadata( "PLACED" ) ? "Placed":"Naturally" ) );
    }
    
    @Override
    public boolean onCommand(CommandSender sender,Command cmd, String commandLabel, String[] args) {
        Player p = ( Player )sender;
        if ( cmd.getName().equalsIgnoreCase( "Premises" ) ) {
            if ( args.length > 0 ) {
                /*
                /<command> get PlayerName
                /<command> join PlayerName
                /<command> status [PlayerName]
                /<command> stat
                */
                switch ( args[0] ) {
                    case "get":
                        return true;
                    case "join":
                        return true;
                    case "status":
                        sender.sendMessage( ChatColor.GREEN + ("--------------------------------------------------"));
                        sender.sendMessage( ChatColor.AQUA + "                     Ores mined by: " + p.getDisplayName() );
                        sender.sendMessage( ChatColor.AQUA + "CobbleStone: " + p.getStatistic(Statistic.MINE_BLOCK, Material.COBBLESTONE));
                        sender.sendMessage( ChatColor.AQUA + "Stone: " + p.getStatistic(Statistic.MINE_BLOCK, Material.STONE));
                        sender.sendMessage( ChatColor.AQUA + "Gravel: " + p.getStatistic(Statistic.MINE_BLOCK, Material.GRAVEL));
                        sender.sendMessage( ChatColor.AQUA + "NetherRack: " + p.getStatistic(Statistic.MINE_BLOCK, Material.NETHERRACK));
                        sender.sendMessage( ChatColor.AQUA + "NetherBrick: " + p.getStatistic(Statistic.MINE_BLOCK, Material.NETHER_BRICK));
                        sender.sendMessage( ChatColor.AQUA + "QUARTZ Ore: " + p.getStatistic(Statistic.MINE_BLOCK, Material.QUARTZ_ORE));
                        sender.sendMessage( ChatColor.AQUA + "SOUL_SAND: " + p.getStatistic(Statistic.MINE_BLOCK, Material.SOUL_SAND));
                        /*
                        sender.sendMessage( ChatColor.AQUA + "NetherWarte: " + p.getStatistic(Statistic.MINE_BLOCK, Material.NETHER_WARTS));
                        */
                        sender.sendMessage( ChatColor.AQUA + "Dirt: " + p.getStatistic(Statistic.MINE_BLOCK, Material.DIRT));
                        sender.sendMessage( ChatColor.AQUA + "Grass: " + p.getStatistic(Statistic.MINE_BLOCK, Material.GRASS));
                        sender.sendMessage( ChatColor.AQUA + "Diamond Ore: " + p.getStatistic(Statistic.MINE_BLOCK, Material.DIAMOND_ORE));
                        sender.sendMessage( ChatColor.AQUA + "Emerald Ore: " + p.getStatistic(Statistic.MINE_BLOCK, Material.EMERALD_ORE));
                        sender.sendMessage( ChatColor.AQUA + "Iron Ore: " + p.getStatistic(Statistic.MINE_BLOCK, Material.IRON_ORE));
                        sender.sendMessage( ChatColor.AQUA + "Gold Ore: " + p.getStatistic(Statistic.MINE_BLOCK, Material.GOLD_ORE));
                        sender.sendMessage( ChatColor.AQUA + "Redstone Ore: " + p.getStatistic(Statistic.MINE_BLOCK, Material.REDSTONE_ORE));
                        sender.sendMessage( ChatColor.AQUA + "Coal Ore: " + p.getStatistic(Statistic.MINE_BLOCK, Material.COAL_ORE));
                        sender.sendMessage( ChatColor.AQUA + "Lapis Ore: " + p.getStatistic(Statistic.MINE_BLOCK, Material.LAPIS_ORE));
                        sender.sendMessage( ChatColor.GREEN + ("--------------------------------------------------"));
                        return true;
                    default:
                        sender.sendMessage( ChatColor.RED + "[Premises] Unknown Command" );
                        return false;
                }
            }
        }
        return false;
    }


}
