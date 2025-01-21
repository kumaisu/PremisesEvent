/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.kumaisu.premisesevent.listener;

import org.bukkit.plugin.Plugin;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.Material;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import io.github.kumaisu.premisesevent.Lib.Tools;
import io.github.kumaisu.premisesevent.config.Config;
import io.github.kumaisu.premisesevent.config.Messages;
import io.github.kumaisu.premisesevent.database.AreaManager;
import io.github.kumaisu.premisesevent.utility.BukkitTool;
import static io.github.kumaisu.premisesevent.PremisesEvent.config;
import static io.github.kumaisu.premisesevent.PremisesEvent.pc;
import static io.github.kumaisu.premisesevent.config.Config.programCode;
import io.github.kumaisu.premisesevent.database.Database;

/**
 *
 * @author sugichan
 */
public class PlaceListener implements Listener {

    /**
     *
     * @param plugin
     */
    public PlaceListener( Plugin plugin ) {
        plugin.getServer().getPluginManager().registerEvents( this, plugin );
    }

    /**
     * ブロックを設置した時の処理
     * ポイントブロックを置いた場合は、スコアからポイントをマイナスする
     * ※自然生成か設置かを判断するための試作をしているが機能していない
     *
     * @param event
     */
    @EventHandler
    public void onBlockPlace( BlockPlaceEvent event ) {
        if ( Config.EventName.equals( "none" ) ) return;

        Player player = event.getPlayer();
        if ( !player.getLocation().getWorld().getName().equals( Config.Event_World ) ) return;

        Messages.RepPlayer = player.getName();
        Block block = event.getBlock();
        String blockName = BukkitTool.getStoneName( block );

        if ( Config.CreativeCount && player.getGameMode() == GameMode.CREATIVE ) return;
        if ( Config.Field && !AreaManager.CheckArea( block.getLocation() ) ) return;

        if ( pc.get( player.getUniqueId() ).getEntry() != 1 ) {
            if ( !Config.placeFree ) event.setCancelled( true );
            return;
        }

        //  イベント保護
        if ( Config.Field && ( Config.PlayerAlarm != Config.UpperMode.None ) ) {
            if ( !Config.MarkReleaseBlock || Database.Block.equals( block.getType().name() ) ) {
                AreaManager.AreaRelease( player, block );
            }
        }

        if ( Config.stones.contains( blockName ) == false ) {
            if ( !Config.placeSpecified ) {
                Tools.Prt( player, Messages.GetString( "NGBlockPlace" ), Tools.consoleMode.full, programCode );
                event.setCancelled( true );
            }
            Tools.Prt(
                ChatColor.AQUA + player.getDisplayName() +
                ChatColor.GREEN + " [" +
                ChatColor.GOLD + blockName +
                ChatColor.GREEN + "] is not a target",
                Tools.consoleMode.full, programCode
            );
            return;
        }

        if ( ( Config.zeroPlace ) || ( config.getPoint( blockName ) == 0 ) || ( pc.get( player.getUniqueId() ).getStoneCount( blockName ) > 0 ) ) {
            Tools.Prt( player.getDisplayName() + " Loss " + blockName + " Point: " + config.getPoint( blockName ), Tools.consoleMode.max, programCode );
            if ( config.getPoint( blockName ) != 0 ) {
                pc.get( player.getUniqueId() ).addScore( player, - config.getPoint( blockName ) );
                pc.get( player.getUniqueId() ).subStoneCount( blockName, ( config.getPoint( blockName ) < 0 ) );
                player.setPlayerListName(
                    pc.get( player.getUniqueId() ).getListName() + " " +
                    ChatColor.YELLOW + String.format( "%8d", pc.get( player.getUniqueId() ).getScore() )
                );
            }
        } else {
            if ( !Config.placeFree ) {
                Tools.Prt( player, Messages.GetString( "NoMorePlace" ), Tools.consoleMode.full, programCode );
                if ( Config.titlePrint ) {
                    player.sendTitle(
                        Messages.GetString( "NoMoreTitleM" ),
                        Messages.GetString( "NoMoreTitleS" ),
                        0, 100, 0 );
                }
                event.setCancelled( true );
            }
        }
    }

    /**
     * 看板設置時に文章を記載した時に発生するイベント
     *
     * @param event
     */
    @EventHandler
    public void onSignChange( SignChangeEvent event ) {
        Player player = event.getPlayer();
        Material material = event.getBlock().getType();

        if ( ( !Config.SignPlace ) && ( !player.hasPermission( "Premises.admin" ) ) ) return;

        Tools.Prt( ChatColor.YELLOW + "get Sign Change Envent Material = " + material.name(), Tools.consoleMode.max, programCode );
        for ( int i = 0; i < 4; i++ ) { Tools.Prt( ChatColor.YELLOW + "Old Sign " + i + " : " + event.getLine( i ), Tools.consoleMode.max, programCode ); }

        switch ( event.getLine( 0 ) ) {
            case "[P-Get]":
            case "[P-Join]":
            case "[P-Status]":
            case "[P-Update]":
            case "[P-TOP]":
            case "[P-LIST]":
                //  指定
                event.setLine( 3, ChatColor.DARK_PURPLE + "Premises" + ChatColor.RED + " " );
                break;
            default:
        }
    }
}
