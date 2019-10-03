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
import org.bukkit.block.Block;

/**
 *
 * @author sugichan
 */
public class DynmapControl {

    /**
     * Dynmap へ MarkerArea を登録する
     *
     * @param player
     * @param AreaCode
     * @param block 
     */
    public static void SetDynmapArea( Player player, String AreaCode, Block block ) {
        Bukkit.getServer().dispatchCommand( Bukkit.getConsoleSender(), "dmarker clearcors" );
        String[] param = AreaCode.split( "-" );
        int bx = Integer.valueOf( param[0] );
        int bz = Integer.valueOf( param[1] );
        Tools.Prt( "Location(X) : [" + bx + "]", Tools.consoleMode.max, programCode );
        Tools.Prt( "Location(Z) : [" + bz + "]", Tools.consoleMode.max, programCode );
        int lx = ( bx * 16 ) + Config.Event_X1;
        int lz = ( bz * 16 ) + Config.Event_Z1;
        int hx = ( ( bx + 1 ) * 16 ) + Config.Event_X1;
        int hz = ( ( bz + 1 ) * 16 ) + Config.Event_Z1;

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

        String locKey = "\"[" + bx + "-" + bz + "] " +
                player.getName() + " (" +
                ( int ) block.getLocation().getX() + "," + ( int ) block.getLocation().getY() + "," + ( int ) block.getLocation().getZ() + ")\"";

        Command = "dmarker addarea id:" + bx + "-" + bz + " " + locKey;
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
