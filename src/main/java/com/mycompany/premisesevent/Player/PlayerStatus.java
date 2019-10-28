/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.premisesevent.Player;

import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import com.mycompany.kumaisulibraries.Tools;
import com.mycompany.premisesevent.config.Messages;
import static com.mycompany.premisesevent.PremisesEvent.pc;
import static com.mycompany.premisesevent.config.Config.programCode;

/**
 *
 * @author sugichan
 */
public class PlayerStatus {
    /**
     * イベント参加者のステータス表示する処理
     *
     * @param player
     * @param Other
     * @return
     */
    public static boolean Print( Player player, String Other ) {
        if ( pc.get( player.getUniqueId() ).getEntry() == 1 ) {
            if ( "".equals( Other ) ) {
                pc.get( player.getUniqueId() ).save();
                pc.get( player.getUniqueId() ).getStatus( player );
            } else {
                UUID uuid;

                Tools.Prt( player, ChatColor.RED + "Look other Player : " + Other, Tools.consoleMode.print, programCode );

                OfflinePlayer op = Bukkit.getServer().getOfflinePlayer( Other );
                if ( op.hasPlayedBefore() ) {
                    uuid = op.getUniqueId();
                    pc.put( uuid, new PlayerControl( op ) );
                    pc.get( uuid ).load();
                } else {
                    Tools.Prt( player, ChatColor.RED + "This Player is not joined to server.", programCode );
                    return false;
                }
                pc.get( uuid ).getStatus( player );
                if ( player.getUniqueId() != uuid ) pc.remove( uuid );
            }
            return true;
        } else {
            Messages.RepPlayer = player.getName();
            Tools.Prt( player, Messages.ReplaceString( "NoJoin" ), programCode );
            return false;
        }
    }
}
