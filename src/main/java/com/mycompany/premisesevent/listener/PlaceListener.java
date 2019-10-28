/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.premisesevent.listener;

import org.bukkit.plugin.Plugin;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import com.mycompany.kumaisulibraries.BukkitTool;
import com.mycompany.kumaisulibraries.Tools;
import com.mycompany.premisesevent.config.Config;
import com.mycompany.premisesevent.config.Messages;
import com.mycompany.premisesevent.database.AreaManager;
import static com.mycompany.premisesevent.PremisesEvent.config;
import static com.mycompany.premisesevent.PremisesEvent.pc;
import static com.mycompany.premisesevent.config.Config.programCode;

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
        Block block = event.getBlock();
        String blockName = BukkitTool.getStoneName( block );

        if ( Config.CreativeCount && player.getGameMode() == GameMode.CREATIVE ) return;

        if ( Config.Field && !AreaManager.CheckArea( block.getLocation() ) ) return;

        if ( pc.get( player.getUniqueId() ).getEntry() != 1 ) {
            if ( !Config.placeFree ) event.setCancelled( true );
            return;
        }

        //  イベント保護
        if ( Config.Field && Config.PlayerAlarm ) {
            AreaManager.AreaRelease( player, block );
        }

        if ( Config.stones.contains( blockName ) == false ) {
            if ( !Config.placeSpecified ) {
                Tools.Prt( player, Messages.ReplaceString( "NGBlockPlace" ), Tools.consoleMode.full, programCode );
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
                    ChatColor.WHITE + String.format( "%-12s", player.getDisplayName() ) + " " +
                    ChatColor.YELLOW + String.format( "%8d", pc.get( player.getUniqueId() ).getScore() )
                );
            }
        } else {
            if ( !Config.placeFree ) {
                Tools.Prt( player, Messages.ReplaceString( "NoMorePlace" ), Tools.consoleMode.full, programCode );
                if ( Config.titlePrint ) {
                    player.sendTitle(
                        Messages.ReplaceString( "NoMoreTitleM" ),
                        Messages.ReplaceString( "NoMoreTitleS" ),
                        0, 100, 0 );
                }
                event.setCancelled( true );
            }
        }
    }
}
