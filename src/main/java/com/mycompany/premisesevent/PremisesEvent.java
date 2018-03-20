/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.premisesevent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
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
        pc.get( p.getUniqueId() ).setDisplayName( p.getDisplayName() );
        pc.get( p.getUniqueId() ).setUUID( p.getUniqueId() );
        pc.get( p.getUniqueId() ).load();
        
        if ( pc.get( p.getUniqueId() ).getEntry() ) {
            pc.get( p.getUniqueId() ).ScoreBoardEntry( p );
            Bukkit.getServer().getConsoleSender().sendMessage( ChatColor.AQUA + p.getDisplayName() + " is participating in the this Event" );
        } else {
            Bukkit.getServer().getConsoleSender().sendMessage( ChatColor.RED + p.getDisplayName() + " has not joined the this Event" );
        }
    }
    
    @EventHandler
    public void onBlockPlace( BlockPlaceEvent event ) {
        Player player = event.getPlayer();
        if ( !pc.get( player.getUniqueId() ).getEntry() ) return;

        Block block = event.getBlock();
        String blockName = getStoneName( block );
        block.setMetadata( "PLACED", new FixedMetadataValue( ( Plugin ) this, true ) );

        if ( config.getStones().contains( blockName ) ) {
            //  Bukkit.getServer().getConsoleSender().sendMessage( player.getDisplayName() + " Loss " + blockName + " Point: " + config.getPoint( blockName ) );
            pc.get( player.getUniqueId() ).addScore( - config.getPoint( blockName ) );
            pc.get( player.getUniqueId() ).save();
            player.setPlayerListName( ChatColor.YELLOW + String.valueOf( pc.get( player.getUniqueId() ).getScore() ) + ChatColor.WHITE + " " + player.getDisplayName() );
        } else {
            //  Bukkit.getServer().getConsoleSender().sendMessage( ChatColor.LIGHT_PURPLE + "This block is not a target" );
        }
    }
    
    @EventHandler
    public void onBlockBreak( BlockBreakEvent event ) {
        Player player = event.getPlayer();
        if ( !pc.get( player.getUniqueId() ).getEntry() ) return;

        Block block = event.getBlock();
        Material material = block.getType();
        String blockName = getStoneName( block );

        if ( config.getStones().contains( blockName ) ) {
            //  Bukkit.getServer().getConsoleSender().sendMessage( player.getDisplayName() + " get " + blockName + " Point: " + config.getPoint( blockName ) + ChatColor.YELLOW + " (" + ( block.hasMetadata( "PLACED" ) ? "Placed":"Naturally" ) + ")" );
            pc.get( player.getUniqueId() ).addScore( config.getPoint( blockName ) );
            pc.get( player.getUniqueId() ).addStoneCount( blockName );
            pc.get( player.getUniqueId() ).save();
            player.setPlayerListName( ChatColor.WHITE + player.getDisplayName() + " " + ChatColor.YELLOW + String.valueOf( pc.get( player.getUniqueId() ).getScore() ) );
        //  } else {
            //  Bukkit.getServer().getConsoleSender().sendMessage( ChatColor.LIGHT_PURPLE + "This block is not a target" );
        }
    }
    
    @EventHandler //    看板ブロックを右クリック
    public void onSignClick( PlayerInteractEvent event ) {

        if ( event.getAction() != Action.RIGHT_CLICK_BLOCK ) return;

        Player player = event.getPlayer();
        if ( !pc.get( player.getUniqueId() ).getEntry() ) return;

        Block clickedBlock = event.getClickedBlock();
        Material material = clickedBlock.getType();
        if ( material == Material.SIGN_POST || material == Material.WALL_SIGN ) {
            Sign sign = (Sign) clickedBlock.getState();
            switch ( sign.getLine(0) ) {
                case "[Premises]":
                    ItemStack item = player.getInventory().getItemInMainHand();
                    if ( item.getItemMeta().hasDisplayName() ) {
                        if ( item.getItemMeta().getDisplayName().equalsIgnoreCase( "§bイベントつるはし" ) ) {
                            double CheckDurability = ( item.getType().getMaxDurability() * 0.9 );
                            if ( CheckDurability <= item.getDurability() ) {
                                ItemControl ic = new ItemControl( this );
                                player.getInventory().setItemInMainHand( null );
                                ic.ItemUpdate( player, item );
                            } else {
                                player.sendMessage(
                                    ChatColor.YELLOW + "ツール耐久値は " +
                                    ChatColor.WHITE + ( item.getType().getMaxDurability() - item.getDurability() ) +
                                    ChatColor.YELLOW + " なので " +
                                    ChatColor.WHITE + ( (int) ( item.getType().getMaxDurability() - CheckDurability ) ) +
                                    ChatColor.YELLOW + " 以下にしてね"
                                );
                            }
                        }
                    }
                    break;
                case "[P-Join]":
                    Join( player );
                    break;
                case "[P-Status]":
                    pc.get( player.getUniqueId() ).getStatus( player );
                    break;
                default:
            }
        }
    }

    @Override
    public boolean onCommand( CommandSender sender, Command cmd, String commandLabel, String[] args ) {
        if ( !( sender instanceof Player ) ) {
            Bukkit.getServer().getConsoleSender().sendMessage( ChatColor.RED + "コマンドはコンソールから操作できません" );
            return false;
        }
        Player p = ( Player ) sender;

        if ( cmd.getName().equalsIgnoreCase( "Premises" ) ) {
            if ( args.length > 0 ) {
                /*
                /<command> join
                /<command> status [PlayerName]
                /<command> take
                */
                switch ( args[0] ) {
                    case "take":
                        if ( pc.get( p.getUniqueId() ).getScore() > 2000 ) {
                            ItemControl ic = new ItemControl( this );
                            ic.ItemUpdate( p, null );
                            pc.get( p.getUniqueId() ).addScore( -2000 );
                            return true;
                        } else {
                            p.sendMessage( ChatColor.RED + "Scoreが足りないので配布できません" );
                            return false;
                        }
                    case "join":
                        return Join( p );
                    case "status":
                        UUID uuid;

                        if ( args.length > 1 ) {
                            Bukkit.getServer().getConsoleSender().sendMessage( ChatColor.RED + "Look other Player : " + args[1] );

                            OfflinePlayer op = Bukkit.getServer().getOfflinePlayer( args[1] );
                            if ( op.hasPlayedBefore() ) {
                                uuid = op.getUniqueId();
                                pc.put( uuid, new PlayerControl( ( Plugin ) this ) );
                                pc.get( uuid ).setDisplayName( op.getName() );
                                pc.get( uuid ).setUUID( op.getUniqueId() );
                                pc.get( uuid ).load();
                            } else {
                                p.sendMessage( ChatColor.RED + "This Player is not joined to server." );
                                return false;
                            }
                        } else {
                            uuid = p.getUniqueId();
                        }
                        
                        pc.get( uuid ).getStatus( p );

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

    public boolean Join( Player p ) {
        if ( !Arrays.asList( p.getInventory().getStorageContents() ).contains( null ) ) {
            p.sendMessage( ChatColor.RED + "参加アイテム配布用のためインベントリに空きが必要です" );
            return false;
        }
        if ( !pc.get( p.getUniqueId() ).getEntry() ) {
            pc.get( p.getUniqueId() ).JoinPlayer( p );
            ItemControl ic = new ItemControl( this );
            ic.ItemPresent( p );
            ic.ItemUpdate( p, null );
        }
        return true;
    }
}
