/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.kumaisu.premisesevent.utility;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import io.github.kumaisu.premisesevent.Lib.Tools;
import io.github.kumaisu.premisesevent.config.Config;
import static io.github.kumaisu.premisesevent.config.Config.programCode;

/**
 *
 * @author sugichan
 */
public class DynmapControl {

    /**
     * Dynmap へ MarkerArea を登録する
     *
     * @param Owner
     * @param AreaCode
     * @param Loc
     */
    public static void SetDynmapArea( String Owner, String AreaCode, Location Loc ) {
        if ( Config.Field && Config.OnDynmap ) {
            String Command = "dmarker clearcorners";
            Bukkit.getServer().dispatchCommand( Bukkit.getConsoleSender(), Command );
            Tools.Prt( "Clear Corner : " + Command, Tools.consoleMode.max, programCode );
            String[] param = AreaCode.split( "-" );
            int bx = Integer.valueOf( param[0] );
            int bz = Integer.valueOf( param[1] );
            Tools.Prt( "Location(X) : [" + bx + "]", Tools.consoleMode.max, programCode );
            Tools.Prt( "Location(Z) : [" + bz + "]", Tools.consoleMode.max, programCode );
            int lx = ( bx * 16 ) + Config.Event_X1;
            int lz = ( bz * 16 ) + Config.Event_Z1;
            int hx = ( ( bx + 1 ) * 16 ) + Config.Event_X1;
            int hz = ( ( bz + 1 ) * 16 ) + Config.Event_Z1;

            Command = "dmarker addcorner " + lx + " 1 " + lz + " " + Config.Event_World;
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

            String locKey = "\"[" + bx + "-" + bz + "] " + Owner + " (" +
                ( int ) Loc.getX() + "," + ( int ) Loc.getY() + "," + ( int ) Loc.getZ() + ")\"";

            Command = "dmarker addarea id:" + bx + "-" + bz + " " + locKey;
            Bukkit.getServer().dispatchCommand( Bukkit.getConsoleSender(), Command );
            Tools.Prt( "Dynmap set : " + Command, Tools.consoleMode.max, programCode );
        }
    }

    /**
     * Dynmap から MarkerArea を削除する
     *
     * @param Code 
     */
    public static void DelDynmapArea( String Code ) {
        if ( Config.Field && Config.OnDynmap ) {
            String Command = "dmarker deletearea id:" + Code;
            Bukkit.getServer().dispatchCommand( Bukkit.getConsoleSender(), Command );
            Tools.Prt( "Dynmap del : " + Command, Tools.consoleMode.max, programCode );
        }
    }
}
