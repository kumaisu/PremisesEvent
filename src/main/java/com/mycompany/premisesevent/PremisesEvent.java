/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.premisesevent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author sugichan
 */
public class PremisesEvent extends JavaPlugin implements Listener {

    private Config config;
    private final Map<UUID, PlayerControl> pc = new HashMap<>();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents( this, this );
        config = new Config( this );
    }

    @EventHandler
    public void onPlayerJoin( PlayerJoinEvent e){
        Player p = e.getPlayer();

        pc.put( p.getUniqueId(), new PlayerControl( ( Plugin ) this ) );
        pc.get( p.getUniqueId() ).load( p );
        
        if ( pc.get( p.getUniqueId() ).getEntry() ) {
            Bukkit.getServer().getConsoleSender().sendMessage( ChatColor.AQUA + p.getDisplayName() + " is participating in the this Event" );
        } else {
            Bukkit.getServer().getConsoleSender().sendMessage( ChatColor.RED + p.getDisplayName() + " has not joined the this Event" );
        }
    }
    
    @EventHandler
    public void onBlockPlace( BlockPlaceEvent event ) {
        Player player = event.getPlayer();
        if ( pc.get( player.getUniqueId() ).getEntry() ) {
            Block block = event.getBlock();
            String blockName = getStoneName( block );
            block.setMetadata( "PLACED", new FixedMetadataValue( ( Plugin ) this, true ) );

            if ( config.getStones().contains( blockName ) ) {
                //  Bukkit.getServer().getConsoleSender().sendMessage( player.getDisplayName() + " Loss " + blockName + " Point: " + config.getPoint( blockName ) );
                pc.get( player.getUniqueId() ).addScore( - config.getPoint( blockName ) );
                pc.get( player.getUniqueId() ).save( player );
                player.setPlayerListName( ChatColor.YELLOW + String.valueOf( pc.get( player.getUniqueId() ).getScore() ) + ChatColor.WHITE + " " + player.getDisplayName() );
            } else {
                //  Bukkit.getServer().getConsoleSender().sendMessage( ChatColor.LIGHT_PURPLE + "This block is not a target" );
            }
        }
    }
    
    @EventHandler
    public void onBlockBreak( BlockBreakEvent event ) {
        Player player = event.getPlayer();
        if ( pc.get( player.getUniqueId() ).getEntry() ) {
            Block block = event.getBlock();
            Material material = block.getType();
            String blockName = getStoneName( block );

            if ( config.getStones().contains( blockName ) ) {
                //  Bukkit.getServer().getConsoleSender().sendMessage( player.getDisplayName() + " get " + blockName + " Point: " + config.getPoint( blockName ) + ChatColor.YELLOW + " (" + ( block.hasMetadata( "PLACED" ) ? "Placed":"Naturally" ) + ")" );
                pc.get( player.getUniqueId() ).addScore( config.getPoint( blockName ) );
                pc.get( player.getUniqueId() ).addStoneCount( blockName );
                pc.get( player.getUniqueId() ).save( player );
                player.setPlayerListName( ChatColor.YELLOW + String.valueOf( pc.get( player.getUniqueId() ).getScore() ) + ChatColor.WHITE + " " + player.getDisplayName() );
            //  } else {
                //  Bukkit.getServer().getConsoleSender().sendMessage( ChatColor.LIGHT_PURPLE + "This block is not a target" );
            }

        }
    }
    
    @Override
    public boolean onCommand(CommandSender sender,Command cmd, String commandLabel, String[] args) {
        if ( !( sender instanceof Player ) ) {
            Bukkit.getServer().getConsoleSender().sendMessage( ChatColor.RED + "コマンドはコンソールから操作できません" );
            return false;
        }
        ItemControl ic = new ItemControl( this );
        Player p = (Player)sender;

        if ( cmd.getName().equalsIgnoreCase( "Premises" ) ) {
            if ( args.length > 0 ) {
                /*
                /<command> join PlayerName
                /<command> status [PlayerName]
                */
                switch ( args[0] ) {
                    case "Present":
                        ic.ItemUpdate( p, null );
                        return true;
                    case "join":
                        if ( !pc.get( p.getUniqueId() ).getEntry() ) {
                            pc.get( p.getUniqueId() ).JoinPlayer( p );
                            ic.ItemPresent( p );
                            ic.ItemUpdate( p, null );
                        }
                        return true;
                    case "status":
                        if ( args.length > 1 ) {
                            Bukkit.getServer().getConsoleSender().sendMessage( ChatColor.RED + "Look other Player Status: " + args[1] );
                            // pc.load( 設定したプレイヤー構造体を取得する方法探す );
                        } else {
                            Bukkit.getServer().getConsoleSender().sendMessage( ChatColor.RED + "Look other Player Status: This Player" );
                            pc.get( p.getUniqueId() ).load( p );
                        }

                        sender.sendMessage( ChatColor.GREEN + "--------------------------------------------------" );
                        sender.sendMessage( ChatColor.AQUA + "Ores mined by: " + p.getDisplayName() );
                        sender.sendMessage( ChatColor.GOLD + "SCORE: " + ChatColor.WHITE + pc.get( p.getUniqueId() ).getScore() );

                        List<String> SL = config.getStones();
                        for( int i = 0; i<SL.size(); i++ ) {
                            int sc = pc.get( p.getUniqueId() ).getStoneCount( SL.get( i ) );
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
