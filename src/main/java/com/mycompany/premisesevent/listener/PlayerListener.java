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

        Player player = event.getPlayer();
        Messages.RepPlayer = player.getName();

        pc.put( player.getUniqueId(), new PlayerControl( player ) );
        pc.get( player.getUniqueId() ).load();

        if ( pc.get( player.getUniqueId() ).getEntry() != 1 ) {
            Tools.Prt( ChatColor.RED + player.getDisplayName() + " has not joined the this Event", programCode );
            return;
        }

        pc.get( player.getUniqueId() ).ScoreBoardEntry( player );
        Tools.Prt( ChatColor.AQUA + player.getDisplayName() + " is participating in the this Event", programCode );

        ItemControl ic = new ItemControl();
        if ( pc.get( player.getUniqueId() ).getPresentFlag() ) {
            Tools.Prt( player, Messages.GetString( "DistArmor" ), Tools.consoleMode.normal, programCode );
            ic.ItemPresent( player );
        }
        if ( pc.get( player.getUniqueId() ).getUpdateFlag() ) {
            Tools.Prt( player, Messages.GetString( "DistTool" ), Tools.consoleMode.normal, programCode );
            Config.tools.keySet().forEach( ( key ) -> {
                Tools.Prt( ChatColor.GREEN + "Config Tool Name : " + key, programCode );
                ic.ToolPresent( player, Material.getMaterial( key ), Config.tools.get( key ), Config.EventToolName );
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
        if ( !player.getLocation().getWorld().getName().equals( Config.Event_World ) ) return;

        //  イベント参加判定
        if ( pc.get( player.getUniqueId() ).getEntry() == 1 ) {
            if ( Config.Field ) {
                if ( !AreaManager.CheckArea( player.getLocation() ) ) return;
                Messages.AreaCode = AreaManager.PackAreaCode( player.getLocation() );
            }
            pc.get( player.getUniqueId() ).PrintArea( player, Messages.AreaCode );
        }
    }
}
