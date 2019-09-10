/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.premisesevent.config;

import java.util.Map;
import java.util.TreeMap;
import com.mycompany.kumaisulibraries.Tools;
import com.mycompany.kumaisulibraries.Tools.consoleMode;
import static com.mycompany.premisesevent.config.Config.programCode;

/**
 *
 * @author sugichan
 */
public class Messages {
    public static Map< String, String > PlayerMessage = new TreeMap< String, String >();

    public static String RepPlayer = "Player";      //  %player%            : player.getName()
    public static String RepMessage = "Message";    //  %message%           : Unique Message
    public static String RepTool = "Tool";          //  %tool%              : Tool Name
    public static String RepDigs = "1";             //  %digs%              : Tool DigSpeed
    public static String RepNDura = "0";            //  %nowDurability%     : now Durability
    public static String RepTDura = "0";            //  %targetDurability%  : mini Durability
    public static String RepScore = "0";            //  %score%             : Player Score
    public static String RepNames = "Name";         //  %name%              : Other Player.getName()
    public static String AreaCode = "0-0";          //  %AreaCode%          : AreaCode

    public static String ReplaceString( String key ) {
        Tools.Prt( "Message Key : " + key, consoleMode.max, programCode );
        String mainStr = PlayerMessage.get( key );
        mainStr = mainStr.replace( "%$", "§" );
        mainStr = mainStr.replace( "%player%", RepPlayer );
        mainStr = mainStr.replace( "%message%", RepMessage );
        mainStr = mainStr.replace( "%tool%", RepTool );
        mainStr = mainStr.replace( "%digs%", RepDigs );
        mainStr = mainStr.replace( "%nowDurability%", RepNDura );
        mainStr = mainStr.replace( "%targetDurability%", RepTDura );
        mainStr = mainStr.replace( "%score%", RepScore );
        mainStr = mainStr.replace( "%name%", RepNames );
        mainStr = mainStr.replace( "%AreaCode%", AreaCode );
        return mainStr;
    }
}
