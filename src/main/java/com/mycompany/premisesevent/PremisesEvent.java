/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.premisesevent;

import java.util.List;
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
        String blockName = getStoneName( block );
        block.setMetadata( "PLACED", new FixedMetadataValue( ( Plugin ) this, true ) );
        player.sendMessage( ChatColor.YELLOW + "Set Custom MetaData" );
        player.sendMessage( "You Loss " + blockName );
        player.sendMessage( " Point: " + config.getPoint( blockName ) );
    }
    
    @EventHandler
    public void onBlockBreak( BlockBreakEvent event ) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Material material = block.getType();
        player.sendMessage( ChatColor.GREEN + "Break to " + block.getState().getData().toString() + ChatColor.YELLOW + ( block.hasMetadata( "PLACED" ) ? "Placed":"Naturally" ) );
        String blockName = getStoneName( block );
        player.sendMessage( "You get " + blockName );
        player.sendMessage( " Point: " + config.getPoint( blockName ) );
        pc.addScore( config.getPoint( blockName ) );
        pc.addStoneCount( blockName );
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
                /<command> join PlayerName
                /<command> status [PlayerName]
                */
                switch ( args[0] ) {
                    case "join":
                        pc.save( p );
                        return true;
                    case "status":
                        if ( args.length > 1 ) {
                            Bukkit.getServer().getConsoleSender().sendMessage( ChatColor.RED + "Look other Player Status: " + args[1] );
                        } else {
                            Bukkit.getServer().getConsoleSender().sendMessage( ChatColor.RED + "Look other Player Status: This Player" );
                        }

                        sender.sendMessage( ChatColor.GREEN + "--------------------------------------------------" );
                        sender.sendMessage( ChatColor.AQUA + "Ores mined by: " + p.getDisplayName() );
                        sender.sendMessage( ChatColor.GOLD + "SCORE: " + ChatColor.WHITE + pc.getScore() );

                        List<String> SL = config.getStones();
                        for( int i = 0; i<SL.size(); i++ ) {
                            int sc = pc.getStoneCount( SL.get( i ) );
                            if ( sc>0 ) {
                                sender.sendMessage( ChatColor.GREEN + SL.get( i ) + ": " + ChatColor.YELLOW + sc );
                            }
                        }

                        /*
                        sender.sendMessage( ChatColor.AQUA + "Stone: " + p.getStatistic(Statistic.MINE_BLOCK, Material.STONE));
                        */
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

    public String getStoneName( Block b ) {
        String retStr = b.getType().toString();
        if ( b.getType().equals( Material.STONE ) ) {
            switch ( b.getData() ) {
                case 1:
                    retStr = "GRANITE";
                    break;
                case 3:
                    retStr = "DIORITE";
                    break;
                case 5:
                    retStr = "ANDESITE";
                    break;
            }
        }
        return retStr;
    }

}
