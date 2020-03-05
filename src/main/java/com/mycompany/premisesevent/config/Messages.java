/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.premisesevent.config;

import java.util.Map;
import java.util.TreeMap;
import com.mycompany.kumaisulibraries.Tools;
import static com.mycompany.premisesevent.config.Config.programCode;

/**
 *
 * @author sugichan
 */
public class Messages {
    public static Map< String, String > PlayerMessage = new TreeMap< String, String >();

    public static String RepPlayer = "%$4Player(error)";    //  %player%            : player.getName()
    public static String RepMessage = "%$4Message(error)";  //  %message%           : Unique Message
    public static String RepTool = "%$4Tool(error)";        //  %tool%              : Tool Name
    public static String RepDigs = "%$41(error)";           //  %digs%              : Tool DigSpeed
    public static String RepNDura = "%$4#(error)";          //  %nowDurability%     : now Durability
    public static String RepTDura = "%$4#(error)";          //  %targetDurability%  : mini Durability
    public static String RepScore = "%$4#(error)";          //  %score%             : Player Score
    public static String AreaCode = "%$4#-#(error)";        //  %AreaCode%          : AreaCode

    public static String Replace( String word ) {
        word = word.replace( "%player%", RepPlayer );
        word = word.replace( "%message%", RepMessage );
        word = word.replace( "%tool%", RepTool );
        word = word.replace( "%digs%", RepDigs );
        word = word.replace( "%nowDurability%", RepNDura );
        word = word.replace( "%targetDurability%", RepTDura );
        word = word.replace( "%score%", RepScore );
        word = word.replace( "%AreaCode%", AreaCode );
        word = word.replace( "%$", "§" );
        return word;
    }
 
    public static String GetString( String key ) {
        Tools.Prt( "Message Key : " + key, Tools.consoleMode.max, programCode );
        return Replace( PlayerMessage.get( key ) );
    }
}
