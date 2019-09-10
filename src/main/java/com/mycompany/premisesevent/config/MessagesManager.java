/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.premisesevent.config;

import java.io.File;
import java.io.IOException;
import java.util.TreeMap;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import com.mycompany.kumaisulibraries.Tools;
import static com.mycompany.premisesevent.config.Config.programCode;

/**
 *
 * @author sugichan
 */
public class MessagesManager {
    private final Plugin plugin;

    private final String resourceFile = "message.yml";
    private final File UKfile;
    private FileConfiguration UKData; // = new YamlConfiguration();

    public MessagesManager( Plugin plugin ) {
        this.plugin = plugin;
        UKfile = new File( plugin.getDataFolder(), resourceFile );
        load();
    }

    /**
     * 設定をロードします
     */
    public void load() {
        if ( !UKfile.exists() ) { plugin.saveResource( resourceFile, false ); }
        if ( UKData == null ) { UKData = YamlConfiguration.loadConfiguration( UKfile ); }

        Tools.Prt( "Messages Loading now...", programCode );

        Messages.PlayerMessage = new TreeMap<>();

        Messages.PlayerMessage.put( "DistArmor", UKData.getString( "DistArmor" ) );
        Messages.PlayerMessage.put( "DistTool", UKData.getString( "DistTool" ) );
        Messages.PlayerMessage.put( "NGBlockPlace", UKData.getString( "NGBlockPlace" ) );
        Messages.PlayerMessage.put( "NoMorePlace", UKData.getString( "NoMorePlace" ) );
        Messages.PlayerMessage.put( "NoMoreTitleM", UKData.getString( "NoMoreTitleM" ) );
        Messages.PlayerMessage.put( "NoMoreTitleS", UKData.getString( "NoMoreTitleS" ) );
        Messages.PlayerMessage.put( "RequestJoin", UKData.getString( "RequestJoin" ) );
        Messages.PlayerMessage.put( "OmitPlayer", UKData.getString( "OmitPlayer" ) );
        Messages.PlayerMessage.put( "RequestTool", UKData.getString( "RequestTool" ) );
        Messages.PlayerMessage.put( "NoBreak", UKData.getString( "NoBreak" ) );
        Messages.PlayerMessage.put( "DontBreak", UKData.getString( "DontBreak" ) );
        Messages.PlayerMessage.put( "NoBreakTitleM", UKData.getString( "NoBreakTitleM" ) );
        Messages.PlayerMessage.put( "NoBreakTitleS", UKData.getString( "NoBreakTitleS" ) );
        Messages.PlayerMessage.put( "ToolWarning", UKData.getString( "ToolWarning" ) );
        Messages.PlayerMessage.put( "ToolWarningTitleM", UKData.getString( "ToolWarningTitleM" ) );
        Messages.PlayerMessage.put( "ToolWarningTitleS", UKData.getString( "ToolWarningTitleS" ) );
        Messages.PlayerMessage.put( "PresentArmor", UKData.getString( "PresentArmor" ) );
        Messages.PlayerMessage.put( "PresentTool", UKData.getString( "PresentTool" ) );
        Messages.PlayerMessage.put( "UpdateTool", UKData.getString( "UpdateTool" ) );
        Messages.PlayerMessage.put( "AlreadyJoin", UKData.getString( "AlreadyJoin" ) );
        Messages.PlayerMessage.put( "AlreadyJoinBroadcast", UKData.getString( "AlreadyJoinBroadcast" ) );
        Messages.PlayerMessage.put( "RefusalJoin", UKData.getString( "RefusalJoin" ) );
        Messages.PlayerMessage.put( "RefusalJoinBroadcast", UKData.getString( "RefusalJoinBroadcast" ) );
        Messages.PlayerMessage.put( "Join", UKData.getString( "Join" ) );
        Messages.PlayerMessage.put( "JoinBroadcast", UKData.getString( "JoinBroadcast" ) );
        Messages.PlayerMessage.put( "OnlyJoin", UKData.getString( "OnlyJoin" ) );
        Messages.PlayerMessage.put( "NoInventory", UKData.getString( "NoInventory" ) );
        Messages.PlayerMessage.put( "NoEnoughScore", UKData.getString( "NoEnoughScore" ) );
        Messages.PlayerMessage.put( "HaveTool", UKData.getString( "HaveTool" ) );
        Messages.PlayerMessage.put( "NowDurable", UKData.getString( "NowDurable" ) );
        Messages.PlayerMessage.put( "NotToolName", UKData.getString( "NotToolName" ) );
        Messages.PlayerMessage.put( "NoEventTool", UKData.getString( "NoEventTool" ) );
        Messages.PlayerMessage.put( "NotUpdate", UKData.getString( "NotUpdate" ) );
        Messages.PlayerMessage.put( "Achievement", UKData.getString( "Achievement" ) );
        Messages.PlayerMessage.put( "ConsoleOnly", UKData.getString( "ConsoleOnly" ) );
        Messages.PlayerMessage.put( "ValueIncorrect", UKData.getString( "ValueIncorrect" ) );
        Messages.PlayerMessage.put( "NoPlayer", UKData.getString( "NoPlayer" ) );
        Messages.PlayerMessage.put( "NoJoin", UKData.getString( "NoJoin" ) );
        Messages.PlayerMessage.put( "NoOwnerArea", UKData.getString( "NoOwnerArea" ) );
        Messages.PlayerMessage.put( "OwnerArea", UKData.getString( "OwnerArea" ) );
        Messages.PlayerMessage.put( "GetAreaM", UKData.getString( "GetAreaM" ) );
        Messages.PlayerMessage.put( "GetAreaM2", UKData.getString( "GetAreaM2" ) );
        Messages.PlayerMessage.put( "GetAreaS", UKData.getString( "GetAreaS" ) );
        Messages.PlayerMessage.put( "FreeArea", UKData.getString( "FreeArea" ) );
        Messages.PlayerMessage.put( "WarningMsg", UKData.getString( "WarningMsg" ) );
        Messages.PlayerMessage.put( "WarnTitleM", UKData.getString( "WarnTitleM" ) );
        Messages.PlayerMessage.put( "WarnTitleS", UKData.getString( "WarnTitleS" ) );
        Messages.PlayerMessage.put( "FraudMode", UKData.getString( "FraudMode" ) );
        Messages.PlayerMessage.put( "FraudEvent", UKData.getString( "FraudEvent" ) );
        Messages.PlayerMessage.put( "FraudUpper", UKData.getString( "FraudUpper" ) );
        Messages.PlayerMessage.put( "EventList", UKData.getString( "EventList" ) );
    }

    /**
     * これは使うと、個別設定が消えるので、サンプル
     */
    public void save() {
        Messages.PlayerMessage.keySet().forEach ( ( gn ) -> {
            UKData.set( gn, Messages.PlayerMessage.get( gn ) );
        } );

        try {
            UKData.save( UKfile );
        }
        catch ( IOException e ) {
            Tools.Prt( ChatColor.RED + "Could not save MotD.yaml File.", programCode );
        }
    }

    /**
     * MotD Message Setting Print
     *
     * @param player
     */
    public static void getStatus( Player player ) {
        Tools.Prt( player, ChatColor.GREEN + "=== Premises Messages ===", programCode );
        Messages.PlayerMessage.keySet().forEach ( ( gn ) -> {
            Tools.Prt( player, ChatColor.WHITE + gn + " : " + ChatColor.YELLOW + Messages.PlayerMessage.get( gn ), programCode );
        } );
        Tools.Prt( player, ChatColor.GREEN + "=========================", programCode );
    }
}
