/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.premisesevent.listener;

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
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import com.mycompany.kumaisulibraries.Tools;
import com.mycompany.premisesevent.config.Config;
import com.mycompany.premisesevent.config.Messages;
import com.mycompany.premisesevent.database.AreaManager;
import com.mycompany.premisesevent.utility.BukkitTool;
import static com.mycompany.premisesevent.PremisesEvent.config;
import static com.mycompany.premisesevent.PremisesEvent.pc;
import static com.mycompany.premisesevent.config.Config.programCode;

/**
 *
 * @author sugichan
 */
public class BreakListener implements Listener {

    /**
     *
     * @param plugin
     */
    public BreakListener( Plugin plugin ) {
        plugin.getServer().getPluginManager().registerEvents( this, plugin );
    }

    /**
     * ブロックが破壊された時の処理
     *
     * @param event
     */
    @EventHandler
    public void onBlockBreak( BlockBreakEvent event ) {
        if ( Config.EventName.equals( "none" ) ) return;

        Player player = event.getPlayer();

        if ( Config.CreativeCount && player.getGameMode() == GameMode.CREATIVE ) return;
        if ( Config.Field && !AreaManager.CheckArea( event.getBlock().getLocation() ) ) return;

        //  イベント参加判定
        switch ( pc.get( player.getUniqueId() ).getEntry() ) {
            case 0:
                if ( !Config.breakFree ) {
                    Tools.Prt( player, Messages.ReplaceString( "RequestJoin" ), Tools.consoleMode.normal, programCode );
                    event.setCancelled( true );
                }
                return;
            case 1:
                // 参加者なので、スルー
                break;
            case 2:
                Tools.Prt( player, Messages.ReplaceString( "OmitPlayer" ), Tools.consoleMode.normal, programCode );
                event.setCancelled( true );
                return;
        }

        Block block = event.getBlock();
        String blockName = BukkitTool.getStoneName( block );
        ItemStack item = player.getInventory().getItemInMainHand();

        //  禁止判定
        Location loc = block.getLocation();
        loc.setY( loc.getY() + 1 );
        Block checkBlock = loc.getBlock();
        if ( !player.hasPermission( "Premises.warning" ) && Config.Field ) {
            switch ( Config.UpperBlock ) {
                case Block:
                    if ( AreaManager.WarningCheck( player, checkBlock ) ) {
                        event.setCancelled( true );
                        return;
                    }
                    break;
                case Warning:
                    if ( AreaManager.WarningCheck( player, checkBlock ) ) {
                        player.addPotionEffect( new PotionEffect( PotionEffectType.SLOW_DIGGING, 200, 2 ) );
                    }
                default:
            }
        }

        //  ツール判定
        if ( Config.breakTool && ( item.getType() != Material.TORCH ) ) {
            if (
                    ( item.getType() == Material.AIR ) ||
                    ( !item.getItemMeta().hasDisplayName() ) ||
                    ( !item.getItemMeta().getDisplayName().equalsIgnoreCase( Config.EventToolName ) )
               ) {
                Tools.Prt( player, Messages.ReplaceString( "NoEventTool" ), Tools.consoleMode.full, programCode );
                event.setCancelled( true );
                return;
            }
        }

        //  エリア関連チェック
        if ( Config.Field && Config.PlayerAlarm ) { AreaManager.AreaCheck( player, block ); }

        //  機能看板処理
        if ( blockName.contains( "SIGN" ) ) {
            Tools.Prt( "this is Sign Block : " + blockName, Tools.consoleMode.max, programCode );
            Sign sign = ( Sign ) block.getState();
            if (
                ( !Config.SignPlace ) &&
                ( !player.hasPermission( "Premises.admin" ) ) &&
                ( sign.getLine( 3 ).equals( ChatColor.DARK_PURPLE + "Premises" + ChatColor.RED  + " " ) )
            ) {
                event.setCancelled( true );
                return;
            }
        }

        //  ブロック処理
        if ( Config.stones.contains( blockName ) ) {
            Tools.Prt(
                player.getDisplayName() + " get " + blockName + " Point: " + config.getPoint( blockName ),
                Tools.consoleMode.max, programCode
            );

            if ( config.getPoint( blockName )<0 ) {
                Tools.Prt(
                    ChatColor.RED + "Warning " +
                    ChatColor.AQUA + player.getDisplayName() +
                    ChatColor.RED + " broke a " +
                    ChatColor.YELLOW + blockName,
                    Tools.consoleMode.normal, programCode
                );

                switch( Config.difficulty ) {
                    case Easy:
                        Tools.Prt( player, Messages.ReplaceString( "NoBreak" ), Tools.consoleMode.full, programCode );
                        event.setCancelled( true );
                        return;
                    case Normal:
                        Tools.Prt( player, Messages.ReplaceString( "DontBreak" ), Tools.consoleMode.full, programCode );
                        if ( Config.titlePrint ) {
                            player.sendTitle(
                                Messages.ReplaceString( "NoBreakTitleM" ),
                                Messages.ReplaceString( "NoBreakTitleS" ),
                                0, 100, 0 );
                        }
                        break;
                    case Hard:
                        break;
                }
            }

            if ( config.getPoint( blockName ) != 0 ){
                pc.get( player.getUniqueId() ).addStoneCount( blockName, ( config.getPoint( blockName ) < 0 ) );
                pc.get( player.getUniqueId() ).addScore( player, config.getPoint( blockName ) );
                player.setPlayerListName(
                    ChatColor.WHITE + String.format( "%-12s", player.getDisplayName() ) + " " +
                    ChatColor.YELLOW + String.format( "%8d", pc.get( player.getUniqueId() ).getScore() )
                );
            }

        } else {
            Tools.Prt(
                ChatColor.AQUA + player.getDisplayName() +
                ChatColor.LIGHT_PURPLE + " [" +
                ChatColor.GOLD + block.getType().toString() +
                ChatColor.LIGHT_PURPLE + "] is not a target",
                Tools.consoleMode.full, programCode );
        }

        if ( item.getType() == Material.AIR ) return;

        if ( item.getItemMeta().hasDisplayName() ) {
            if ( item.getItemMeta().getDisplayName().equalsIgnoreCase( Config.EventToolName ) ) {
                if ( ( item.getType().getMaxDurability() * Config.Repair ) <= item.getDurability() ) {
                    Tools.Prt( player, Messages.ReplaceString( "ToolWarning" ), Tools.consoleMode.max, programCode );
                    if ( Config.titlePrint ) {
                        player.sendTitle(
                            Messages.ReplaceString( "ToolWarningTitleM" ),
                            Messages.ReplaceString( "ToolWarningTitleS" ),
                            0, 50, 0
                        );
                    }
                }
            }
        }
    }
}
