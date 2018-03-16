/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.premisesevent;

import org.bukkit.Bukkit;
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

    private Config config;
    private PlayerControl pc;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents( this, this );
        config = new Config( this );
        pc = new PlayerControl( ( Plugin ) this );
    }

    @EventHandler
    public void onBlockPlace( BlockPlaceEvent event ) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        block.setMetadata( "PLACED", new FixedMetadataValue( ( Plugin ) this, true ) );
        player.sendMessage( ChatColor.YELLOW + "Set Custom MetaData" );
    }
    
    @EventHandler
    public void onBlockBreak( BlockBreakEvent event ) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Material material = block.getType();
        String getsn = block.getType().toString();
        player.sendMessage( ChatColor.GREEN + "Break to " + block.getState().getData().toString() + ChatColor.YELLOW + ( block.hasMetadata( "PLACED" ) ? "Placed":"Naturally" ) );
        if ( block.getType().equals(Material.STONE) ) {
            switch ( block.getData() ) {
                case 1:
                    player.sendMessage( ChatColor.AQUA + "Hey! Hey! get Granite");
                    getsn = "GRANITE";
                    break;
                case 3:
                    player.sendMessage( ChatColor.AQUA + "Hey! Hey! get Diorite");
                    getsn = "DIORITE";
                    break;
                case 5:
                    player.sendMessage( ChatColor.AQUA + "Hey! Hey! get Andesite");
                    getsn = "ANDESITE";
                    break;
                default:
                    player.sendMessage( ChatColor.AQUA + "Hey! Hey! get Other Stone");
            }
        }
        player.sendMessage( "You get " + getsn + " Point: " + config.getPoint( getsn ) );
    }
    
    @Override
    public boolean onCommand(CommandSender sender,Command cmd, String commandLabel, String[] args) {
        if ( !( sender instanceof Player ) ) {
            Bukkit.getServer().getConsoleSender().sendMessage( ChatColor.RED + "コマンドはコンソールから操作できません" );
            return false;
        }
        Player p = (Player)sender;
        if ( cmd.getName().equalsIgnoreCase( "Premises" ) ) {
            if ( args.length > 0 ) {
                /*
                /<command> get PlayerName
                /<command> join PlayerName
                /<command> status [PlayerName]
                */
                switch ( args[0] ) {
                    case "get":
                        pc.load( p );
                        return true;
                    case "join":
                        pc.save( p );
                        return true;
                    case "status":
                        if ( !( args[1] == null ) ) {
                            Bukkit.getServer().getConsoleSender().sendMessage( ChatColor.RED + "Look other Player Status: " + args[1] );
                        }
                        sender.sendMessage( ChatColor.GREEN + "--------------------------------------------------" );
                        sender.sendMessage( ChatColor.AQUA + "                     Ores mined by: " + p.getDisplayName() );
                        sender.sendMessage( ChatColor.AQUA + "CobbleStone: " + p.getStatistic(Statistic.MINE_BLOCK, Material.COBBLESTONE));
                        sender.sendMessage( ChatColor.AQUA + "Stone: " + p.getStatistic(Statistic.MINE_BLOCK, Material.STONE));
                        sender.sendMessage( ChatColor.AQUA + "Gravel: " + p.getStatistic(Statistic.MINE_BLOCK, Material.GRAVEL));
                        sender.sendMessage( ChatColor.AQUA + "NetherRack: " + p.getStatistic(Statistic.MINE_BLOCK, Material.NETHERRACK));
                        sender.sendMessage( ChatColor.AQUA + "NetherBrick: " + p.getStatistic(Statistic.MINE_BLOCK, Material.NETHER_BRICK));
                        sender.sendMessage( ChatColor.AQUA + "QUARTZ Ore: " + p.getStatistic(Statistic.MINE_BLOCK, Material.QUARTZ_ORE));
                        sender.sendMessage( ChatColor.AQUA + "SOUL_SAND: " + p.getStatistic(Statistic.MINE_BLOCK, Material.SOUL_SAND));
                        sender.sendMessage( ChatColor.AQUA + "Dirt: " + p.getStatistic(Statistic.MINE_BLOCK, Material.DIRT));
                        sender.sendMessage( ChatColor.AQUA + "Grass: " + p.getStatistic(Statistic.MINE_BLOCK, Material.GRASS));
                        sender.sendMessage( ChatColor.AQUA + "Diamond Ore: " + p.getStatistic(Statistic.MINE_BLOCK, Material.DIAMOND_ORE));
                        sender.sendMessage( ChatColor.AQUA + "Emerald Ore: " + p.getStatistic(Statistic.MINE_BLOCK, Material.EMERALD_ORE));
                        sender.sendMessage( ChatColor.AQUA + "Iron Ore: " + p.getStatistic(Statistic.MINE_BLOCK, Material.IRON_ORE));
                        sender.sendMessage( ChatColor.AQUA + "Gold Ore: " + p.getStatistic(Statistic.MINE_BLOCK, Material.GOLD_ORE));
                        sender.sendMessage( ChatColor.AQUA + "Redstone Ore: " + p.getStatistic(Statistic.MINE_BLOCK, Material.REDSTONE_ORE));
                        sender.sendMessage( ChatColor.AQUA + "Coal Ore: " + p.getStatistic(Statistic.MINE_BLOCK, Material.COAL_ORE));
                        sender.sendMessage( ChatColor.AQUA + "Lapis Ore: " + p.getStatistic(Statistic.MINE_BLOCK, Material.LAPIS_ORE));
                        sender.sendMessage( ChatColor.GREEN + "--------------------------------------------------" );
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
