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

        Messages.PlayerMessage.put( "DistArmor", UKData.getString( "DistArmor", "error" ) );
        Messages.PlayerMessage.put( "DistTool", UKData.getString( "DistTool", "error" ) );
        Messages.PlayerMessage.put( "NGBlockPlace", UKData.getString( "NGBlockPlace", "error" ) );
        Messages.PlayerMessage.put( "NoMorePlace", UKData.getString( "NoMorePlace", "error" ) );
        Messages.PlayerMessage.put( "NoMoreTitleM", UKData.getString( "NoMoreTitleM", "error" ) );
        Messages.PlayerMessage.put( "NoMoreTitleS", UKData.getString( "NoMoreTitleS", "error" ) );
        Messages.PlayerMessage.put( "RequestJoin", UKData.getString( "RequestJoin", "error" ) );
        Messages.PlayerMessage.put( "OmitPlayer", UKData.getString( "OmitPlayer", "error" ) );
        Messages.PlayerMessage.put( "RequestTool", UKData.getString( "RequestTool", "error" ) );
        Messages.PlayerMessage.put( "NoBreak", UKData.getString( "NoBreak", "error" ) );
        Messages.PlayerMessage.put( "DontBreak", UKData.getString( "DontBreak", "error" ) );
        Messages.PlayerMessage.put( "NoBreakTitleM", UKData.getString( "NoBreakTitleM", "error" ) );
        Messages.PlayerMessage.put( "NoBreakTitleS", UKData.getString( "NoBreakTitleS", "error" ) );
        Messages.PlayerMessage.put( "ToolWarning", UKData.getString( "ToolWarning", "error" ) );
        Messages.PlayerMessage.put( "ToolWarningTitleM", UKData.getString( "ToolWarningTitleM", "error" ) );
        Messages.PlayerMessage.put( "ToolWarningTitleS", UKData.getString( "ToolWarningTitleS", "error" ) );
        Messages.PlayerMessage.put( "PresentArmor", UKData.getString( "PresentArmor", "error" ) );
        Messages.PlayerMessage.put( "PresentTool", UKData.getString( "PresentTool", "error" ) );
        Messages.PlayerMessage.put( "UpdateTool", UKData.getString( "UpdateTool", "error" ) );
        Messages.PlayerMessage.put( "AlreadyJoin", UKData.getString( "AlreadyJoin", "error" ) );
        Messages.PlayerMessage.put( "AlreadyJoinBroadcast", UKData.getString( "AlreadyJoinBroadcast", "error" ) );
        Messages.PlayerMessage.put( "RefusalJoin", UKData.getString( "RefusalJoin", "error" ) );
        Messages.PlayerMessage.put( "RefusalJoinBroadcast", UKData.getString( "RefusalJoinBroadcast", "error" ) );
        Messages.PlayerMessage.put( "Join", UKData.getString( "Join", "error" ) );
        Messages.PlayerMessage.put( "JoinBroadcast", UKData.getString( "JoinBroadcast", "error" ) );
        Messages.PlayerMessage.put( "OnlyJoin", UKData.getString( "OnlyJoin", "error" ) );
        Messages.PlayerMessage.put( "NoInventory", UKData.getString( "NoInventory", "error" ) );
        Messages.PlayerMessage.put( "NoEnoughScore", UKData.getString( "NoEnoughScore", "error" ) );
        Messages.PlayerMessage.put( "HaveTool", UKData.getString( "HaveTool", "error" ) );
        Messages.PlayerMessage.put( "NowDurable", UKData.getString( "NowDurable", "error" ) );
        Messages.PlayerMessage.put( "NotToolName", UKData.getString( "NotToolName", "error" ) );
        Messages.PlayerMessage.put( "NoEventTool", UKData.getString( "NoEventTool", "error" ) );
        Messages.PlayerMessage.put( "NotUpdate", UKData.getString( "NotUpdate", "error" ) );
        Messages.PlayerMessage.put( "Achievement", UKData.getString( "Achievement", "error" ) );
        Messages.PlayerMessage.put( "NoBCAchive", UKData.getString( "NoBCAchive", "error" ) );
        Messages.PlayerMessage.put( "ConsoleOnly", UKData.getString( "ConsoleOnly", "error" ) );
        Messages.PlayerMessage.put( "ValueIncorrect", UKData.getString( "ValueIncorrect", "error" ) );
        Messages.PlayerMessage.put( "NoPlayer", UKData.getString( "NoPlayer", "error" ) );
        Messages.PlayerMessage.put( "NoJoin", UKData.getString( "NoJoin", "error" ) );
        Messages.PlayerMessage.put( "NoOwnerArea", UKData.getString( "NoOwnerArea", "error" ) );
        Messages.PlayerMessage.put( "OwnerArea", UKData.getString( "OwnerArea", "error" ) );
        Messages.PlayerMessage.put( "GetAreaM", UKData.getString( "GetAreaM", "error" ) );
        Messages.PlayerMessage.put( "GetAreaM2", UKData.getString( "GetAreaM2", "error" ) );
        Messages.PlayerMessage.put( "GetAreaS", UKData.getString( "GetAreaS", "error" ) );
        Messages.PlayerMessage.put( "FreeArea", UKData.getString( "FreeArea", "error" ) );
        Messages.PlayerMessage.put( "WarningMsg", UKData.getString( "WarningMsg", "error" ) );
        Messages.PlayerMessage.put( "WarnTitleM", UKData.getString( "WarnTitleM", "error" ) );
        Messages.PlayerMessage.put( "WarnTitleS", UKData.getString( "WarnTitleS", "error" ) );
        Messages.PlayerMessage.put( "FraudMode", UKData.getString( "FraudMode", "error" ) );
        Messages.PlayerMessage.put( "FraudEvent", UKData.getString( "FraudEvent", "error" ) );
        Messages.PlayerMessage.put( "FraudUpper", UKData.getString( "FraudUpper", "error" ) );
        Messages.PlayerMessage.put( "EventList", UKData.getString( "EventList", "error" ) );
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
            String mainStr = ChatColor.YELLOW + Messages.PlayerMessage.get( gn );
            mainStr = mainStr.replace( "%player%", ChatColor.AQUA + "%player%" + ChatColor.YELLOW );
            mainStr = mainStr.replace( "%message%", ChatColor.AQUA + "%message%" + ChatColor.YELLOW );
            mainStr = mainStr.replace( "%tool%", ChatColor.AQUA + "%tool%" + ChatColor.YELLOW );
            mainStr = mainStr.replace( "%digs%", ChatColor.AQUA + "%digs%" + ChatColor.YELLOW );
            mainStr = mainStr.replace( "%nowDurability%", ChatColor.AQUA + "%nowDurability%" + ChatColor.YELLOW );
            mainStr = mainStr.replace( "%targetDurability%", ChatColor.AQUA + "%targetDurability%" + ChatColor.YELLOW );
            mainStr = mainStr.replace( "%score%", ChatColor.AQUA + "%score%" + ChatColor.YELLOW );
            mainStr = mainStr.replace( "%AreaCode%", ChatColor.AQUA + "%AreaCode%" + ChatColor.YELLOW );
            Tools.Prt( player, ChatColor.WHITE + gn + " : " + mainStr, programCode );
        } );
        Tools.Prt( player, ChatColor.GREEN + "=========================", programCode );
    }
}
