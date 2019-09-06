/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.premisesevent.utility;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import com.mycompany.kumaisulibraries.Tools;
import com.mycompany.premisesevent.config.Config;
import static com.mycompany.premisesevent.config.Config.programCode;

/**
 *
 * @author sugichan
 */
public class DynmapControl {

    /**
     * Dynmap へ MarkerArea を登録する
     *
     * @param player
     * @param bx
     * @param bz 
     */
    public static void SetDynmapArea( Player player, int bx, int bz ) {
        Bukkit.getServer().dispatchCommand( Bukkit.getConsoleSender(), "dmarker clearcors" );
        int lx = ( ( bx * 16 ) + Config.Event_X1 );
        int lz = ( ( bz * 16 ) + Config.Event_Z1 );
        int hx = ( ( ( bx + 1 ) * 16 ) + Config.Event_X1 );
        int hz = ( ( ( bz + 1 ) * 16 ) + Config.Event_Z1 );

        String Command = "dmarker addcorner " + lx + " 1 " + lz + " " + Config.Event_World;
        Bukkit.getServer().dispatchCommand( Bukkit.getConsoleSender(), Command );
        Tools.Prt( "Dynmap set : " + Command, Tools.consoleMode.max, programCode );

        Command = "dmarker addcorner " + lx + " 1 " + hz + " " + Config.Event_World;
        Bukkit.getServer().dispatchCommand( Bukkit.getConsoleSender(), Command );
        Tools.Prt( "Dynmap set : " + Command, Tools.consoleMode.max, programCode );

        Command = "dmarker addcorner " + hx + " 1 " + hz + " " + Config.Event_World;
        Bukkit.getServer().dispatchCommand( Bukkit.getConsoleSender(), Command );
        Tools.Prt( "Dynmap set : " + Command, Tools.consoleMode.max, programCode );

        Command = "dmarker addcorner " + hx + " 1 " + lz + " " + Config.Event_World;
        Bukkit.getServer().dispatchCommand( Bukkit.getConsoleSender(), Command );
        Tools.Prt( "Dynmap set : " + Command, Tools.consoleMode.max, programCode );

        Command = "dmarker addarea id:" + bx + "-" + bz + " " + player.getName();
        Bukkit.getServer().dispatchCommand( Bukkit.getConsoleSender(), Command );
        Tools.Prt( "Dynmap set : " + Command, Tools.consoleMode.max, programCode );
    }

    /**
     * Dynmap から MarkerArea を削除する
     *
     * @param Code 
     */
    public static void DelDynmapArea( String Code ) {
        if ( Config.OnDynmap ) {
            String Command = "dmarker deletearea id:" + Code;
            Bukkit.getServer().dispatchCommand( Bukkit.getConsoleSender(), Command );
            Tools.Prt( "Dynmap set : " + Command, Tools.consoleMode.max, programCode );
        }
    }
}
