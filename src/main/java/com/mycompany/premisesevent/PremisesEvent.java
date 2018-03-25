/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.premisesevent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
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
import org.bukkit.event.player.PlayerQuitEvent;
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
    public void onPlayerJoin( PlayerJoinEvent event ){
        Player p = event.getPlayer();
        
        pc.put( p.getUniqueId(), new PlayerControl( ( Plugin ) this ) );
        pc.get( p.getUniqueId() ).setDisplayName( p.getDisplayName() );
        pc.get( p.getUniqueId() ).setUUID( p.getUniqueId() );
        pc.get( p.getUniqueId() ).load();
        
        if ( pc.get( p.getUniqueId() ).getEntry() ) {
            pc.get( p.getUniqueId() ).ScoreBoardEntry( p );
            Bukkit.getServer().getConsoleSender().sendMessage( ChatColor.AQUA + p.getDisplayName() + " is participating in the this Event" );

            ItemControl ic = new ItemControl( this );
            if ( pc.get( p.getUniqueId() ).getPresentFlag() ) {
                Bukkit.getServer().getConsoleSender().sendMessage( ChatColor.YELLOW + p.getDisplayName() + " for RePresent Amor" );
                p.sendMessage( ChatColor.YELLOW + "イベント装備の再配布" );
                ic.ItemPresent( p );
            }
            if ( pc.get( p.getUniqueId() ).getUpdateFlag() ) {
                Bukkit.getServer().getConsoleSender().sendMessage( ChatColor.YELLOW + p.getDisplayName() + " for RePresent Tool" );
                p.sendMessage( ChatColor.YELLOW + "イベントツールの再配布" );
                ic.ItemUpdate( p, null );
            }
        
            //  暫定で強制的に再配布フラグを消すため
            //  saveは、ログアウト時1回だけにする方が良いと思ってるんだけど、、検討不足
            //  サーバーダウン時の対応で、onDisableでもセーブする必要あるかな?
            //  pc.get( p.getUniqueId() ).save();
        } else {
            Bukkit.getServer().getConsoleSender().sendMessage( ChatColor.RED + p.getDisplayName() + " has not joined the this Event" );
        }
    }
    
    @EventHandler
    public void onPlayerQuit( PlayerQuitEvent event ) {
        Player player = event.getPlayer();
        Bukkit.getServer().getConsoleSender().sendMessage( ChatColor.YELLOW + "[Premises] " + player.getDisplayName() + " logged out, Saved the Score" );

        pc.get( player.getUniqueId() ).save();
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
            //  ログアウト時に一括保存に変更した
            //  pc.get( player.getUniqueId() ).save();
        //  } else {
            //  Bukkit.getServer().getConsoleSender().sendMessage( ChatColor.LIGHT_PURPLE + "This block is not a target" );
        }

        player.setPlayerListName( ChatColor.WHITE + String.format( "%-12s", player.getDisplayName() ) + " " + ChatColor.YELLOW + String.format( "%8d", pc.get( player.getUniqueId() ).getScore() ) );
    }
    
    @EventHandler
    public void onBlockBreak( BlockBreakEvent event ) {
        Player player = event.getPlayer();
        if ( !pc.get( player.getUniqueId() ).getEntry() ) return;

        Block block = event.getBlock();
        Material material = block.getType();
        String blockName = getStoneName( block );

        if ( config.getStones().contains( blockName ) ) {
            /* これを表示すると、サーバーコンソールがこのログで埋まってしまうので注意
            Bukkit.getServer().getConsoleSender().sendMessage(
                player.getDisplayName() + " get " + blockName +
                " Point: " + config.getPoint( blockName ) +
                ChatColor.YELLOW + " (" + ( block.hasMetadata( "PLACED" ) ? "Placed":"Naturally" ) + ")"
            );
            */
            pc.get( player.getUniqueId() ).addScore( config.getPoint( blockName ) );
            pc.get( player.getUniqueId() ).addStoneCount( blockName );
            //  ログアウト時に一括保存に変更した
            //  pc.get( player.getUniqueId() ).save();
        } else {
            Bukkit.getServer().getConsoleSender().sendMessage( ChatColor.LIGHT_PURPLE + "This [" + ChatColor.GOLD + block.getType().toString() + ChatColor.LIGHT_PURPLE + "] is not a target" );
        }

        player.setPlayerListName( ChatColor.WHITE + String.format( "%-12s", player.getDisplayName() ) + " " + ChatColor.YELLOW + String.format( "%8d", pc.get( player.getUniqueId() ).getScore() ) );

        ItemStack item = player.getInventory().getItemInMainHand();
        
        if ( item.getItemMeta().hasDisplayName() ) {
            if ( item.getItemMeta().getDisplayName().equalsIgnoreCase( "§bイベントつるはし" ) ) {
                if ( ( item.getType().getMaxDurability() * 0.9 ) <= item.getDurability() ) player.sendMessage( ChatColor.RED + "ツールの耐久値がヤバイですよ" );
            }
        }
    }
    
    public void ToolUpdate( Player player ) {

        if ( player.getInventory().getItemInMainHand().getType() == Material.AIR ) return;

        ItemStack item = player.getInventory().getItemInMainHand();

        if ( item.getItemMeta().hasDisplayName() ) {
            if ( item.getItemMeta().getDisplayName().equalsIgnoreCase( "§bイベントつるはし" ) ) {
                double CheckDurability = ( item.getType().getMaxDurability() * 0.9 );
                if ( CheckDurability <= item.getDurability() ) {
                    ItemControl ic = new ItemControl( this );
                    player.getInventory().setItemInMainHand( null );
                    ic.ItemUpdate( player, item );
                    Bukkit.getServer().getConsoleSender().sendMessage( ChatColor.GOLD + player.getDisplayName() + " Tool Update !!" );
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
    }
    
    @EventHandler //    看板ブロックを右クリック
    public void onSignClick( PlayerInteractEvent event ) {
               
        if ( event.getAction() != Action.RIGHT_CLICK_BLOCK ) return;
        
        Player player = event.getPlayer();
        Block clickedBlock = event.getClickedBlock();
        Material material = clickedBlock.getType();
        if ( material == Material.SIGN_POST || material == Material.WALL_SIGN ) {
            Sign sign = (Sign) clickedBlock.getState();
            switch ( sign.getLine(0) ) {
                case "[P-Get]":
                    if ( pc.get( player.getUniqueId() ).getEntry() ) pc.get( player.getUniqueId() ).itemget( player );
                    break;
                case "[P-Join]":
                    pc.get( player.getUniqueId() ).JoinPlayer( player );
                    break;
                case "[P-Status]":
                    if ( pc.get( player.getUniqueId() ).getEntry() ) pc.get( player.getUniqueId() ).getStatus( player );
                    break;
                case "[P-Update]":
                    if ( pc.get( player.getUniqueId() ).getEntry() ) ToolUpdate( player );
                    break;
                default:
            }
        }
    }

    @Override
    public boolean onCommand( CommandSender sender, Command cmd, String commandLabel, String[] args ) {
        Player player = ( sender instanceof Player ? ( Player ) sender:null );

        if ( cmd.getName().equalsIgnoreCase( "toplist" ) ) {

            Bukkit.getServer().getOnlinePlayers().stream().filter( ( onPlayer ) -> ( pc.get( onPlayer.getUniqueId() ).getEntry() ) ).forEachOrdered( ( onPlayer ) -> {
                Bukkit.getServer().getConsoleSender().sendMessage( onPlayer.getDisplayName() + "is Online Event[" + ( pc.get( onPlayer.getUniqueId() ).getEntry() ? "true":"false" ) + "]" );
                pc.get( onPlayer.getUniqueId() ).save();
            } );

            TopList TL = new TopList( this );
            TL.Top( player );
            return true;
        }

        if ( player == null ) {
            Bukkit.getServer().getConsoleSender().sendMessage( ChatColor.RED + "コマンドはコンソールから操作できません" );
            return false;
        }

        if ( cmd.getName().equalsIgnoreCase( "Premises" ) ) {
            if ( args.length > 0 ) {
                /*
                /<command> join
                /<command> status [PlayerName]
                /<command> take
                */
                switch ( args[0] ) {
                    case "get":
                        if ( pc.get( player.getUniqueId() ).getEntry() ) return pc.get( player.getUniqueId() ).itemget( player );
                        return true;
                    case "update":
                        if ( pc.get( player.getUniqueId() ).getEntry() ) ToolUpdate( player );
                        return true;
                    case "join":
                        return pc.get( player.getUniqueId() ).JoinPlayer( player );
                    case "status":
                        if ( pc.get( player.getUniqueId() ).getEntry() ) {
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
                                    player.sendMessage( ChatColor.RED + "This Player is not joined to server." );
                                    return false;
                                }
                            } else {
                                uuid = player.getUniqueId();
                            }
                        
                            pc.get( uuid ).getStatus( player );
                        }

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
