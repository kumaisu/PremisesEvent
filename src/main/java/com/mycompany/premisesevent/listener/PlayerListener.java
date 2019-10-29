/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.premisesevent.listener;

import org.bukkit.plugin.Plugin;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import com.mycompany.kumaisulibraries.Tools;
import com.mycompany.premisesevent.Item.ItemControl;
import com.mycompany.premisesevent.Player.PlayerControl;
import com.mycompany.premisesevent.config.Config;
import com.mycompany.premisesevent.config.Messages;
import com.mycompany.premisesevent.database.AreaManager;
import static com.mycompany.premisesevent.PremisesEvent.pc;
import static com.mycompany.premisesevent.config.Config.programCode;

/**
 *
 * @author sugichan
 */
public class PlayerListener implements Listener {

    /**
     *
     * @param plugin
     */
    public PlayerListener( Plugin plugin ) {
        plugin.getServer().getPluginManager().registerEvents( this, plugin );
    }

    /**
     * プレイヤーのログイン時処理、参加者であれば、スコアをメモリにロードする
     * スコアファイルにプレゼントフラグがある場合は適宜プレゼント処理を行う
     * ※今の所スコアファイルのプレゼントフラグは直接編集でのみ変更可能
     *
     * @param event
     */
    @EventHandler
    public void onPlayerJoin( PlayerJoinEvent event ) {
        if ( Config.EventName.equals( "none" ) ) return;

        Player p = event.getPlayer();

        pc.put( p.getUniqueId(), new PlayerControl( p ) );
        pc.get( p.getUniqueId() ).load();

        if ( pc.get( p.getUniqueId() ).getEntry() != 1 ) {
            Tools.Prt( ChatColor.RED + p.getDisplayName() + " has not joined the this Event", programCode );
            return;
        }

        pc.get( p.getUniqueId() ).ScoreBoardEntry( p );
        Tools.Prt( ChatColor.AQUA + p.getDisplayName() + " is participating in the this Event", programCode );

        ItemControl ic = new ItemControl();
        if ( pc.get( p.getUniqueId() ).getPresentFlag() ) {
            Tools.Prt( p, Messages.ReplaceString( "DistArmor" ), Tools.consoleMode.normal, programCode );
            ic.ItemPresent( p );
        }
        if ( pc.get( p.getUniqueId() ).getUpdateFlag() ) {
            Tools.Prt( p, Messages.ReplaceString( "DistTool" ), Tools.consoleMode.normal, programCode );
            Config.tools.keySet().forEach( ( key ) -> {
                Tools.Prt( ChatColor.GREEN + "Config Tool Name : " + key, programCode );
                ic.ToolPresent( p, Material.getMaterial( key ), Config.tools.get( key ), Config.EventToolName );
            } );
        }
    }

    /**
     * プレイヤーのログアウト時処理、参加者であればメモリのスコアをスコアファイルに保存する
     *
     * @param event
     */
    @EventHandler
    public void onPlayerQuit( PlayerQuitEvent event ) {
        if ( Config.EventName.equals( "none" ) ) return;

        Player player = event.getPlayer();
        if ( pc.get( player.getUniqueId() ).getEntry() != 0 ) {
            Tools.Prt( ChatColor.AQUA + player.getDisplayName() + ChatColor.WHITE + " logged out, Saved the Score", programCode );

            pc.get( player.getUniqueId() ).save();
        } else {
            Tools.Prt( ChatColor.AQUA + player.getDisplayName() + ChatColor.LIGHT_PURPLE + " logged out, not Save", programCode );
        }
        pc.remove( player.getUniqueId() );
    }
    
    /**
     * エリア内の移動を監視
     *
     * @param event 
     */
    @EventHandler
    public void onPlayerMove( PlayerMoveEvent event ) {
        if ( Config.EventName.equals( "none" ) ) return;

        Player player = event.getPlayer();
        if ( Config.Field && !AreaManager.CheckArea( player.getLocation() ) ) return;

        //  イベント参加判定
        if ( pc.get( player.getUniqueId() ).getEntry() == 1 ) {
            if ( Config.Field ) AreaManager.PackAreaCode( player.getLocation() );
            pc.get( player.getUniqueId() ).PrintArea( player, Messages.AreaCode );
        }
    }
}