/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.kumaisu.premisesevent.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import io.github.kumaisu.premisesevent.Lib.Tools;
import io.github.kumaisu.premisesevent.PremisesEvent;
import io.github.kumaisu.premisesevent.config.Config;
import io.github.kumaisu.premisesevent.config.Messages;
import io.github.kumaisu.premisesevent.database.AreaManager;
import static io.github.kumaisu.premisesevent.config.Config.programCode;
import io.github.kumaisu.premisesevent.database.Database;

/**
 *
 * @author sugichan
 */
public class AreaCommand implements CommandExecutor {

     private final PremisesEvent instance;

     public AreaCommand( PremisesEvent instance ) {
         this.instance = instance;
     }

     /**
      * エリアコマンドヘルプ
      *
      * @param player 
      */
     private void help( Player player ) {
        Tools.Prt( player, ChatColor.GREEN + "/Area Command List", programCode );
        Tools.Prt( player, ChatColor.YELLOW + "info [AreaCode]        : " + ChatColor.WHITE + "Area Information", programCode );
        Tools.Prt( player, ChatColor.YELLOW + "add [AreaCode] [Owner] : " + ChatColor.WHITE + "Registration", programCode );
        Tools.Prt( player, ChatColor.YELLOW + "del [AreaCode]         : " + ChatColor.WHITE + "Delete Owner", programCode );
        Tools.Prt( player, ChatColor.YELLOW + "check [AreaCode]       : " + ChatColor.WHITE + "Area Total Score", programCode );
        Tools.Prt( player, ChatColor.YELLOW + "regist [Owner]         : " + ChatColor.WHITE + "Player Regist Area Count", programCode );
        Tools.Prt( player, ChatColor.YELLOW + "list [Owner]           : " + ChatColor.WHITE + "Holding Area List", programCode );
        Tools.Prt( player, ChatColor.YELLOW + "AllClear : " + ChatColor.WHITE + "Clear All AreaData", programCode );
        Tools.Prt( player, ChatColor.YELLOW + "help     : " + ChatColor.WHITE + "Command List", programCode );
     }

    /**
     * コマンドの入力に対する処理
     *
     * @param sender
     * @param cmd
     * @param commandLabel
     * @param args
     * @return
     */
    @Override
    public boolean onCommand( CommandSender sender, Command cmd, String commandLabel, String[] args ) {
        Player player = ( sender instanceof Player ? ( Player ) sender:null );

        if ( ( player != null ) && ( !player.hasPermission( "Premises.admin" ) ) ) { return false; }
        if ( !Config.Field ) {
            Tools.Prt( player, ChatColor.RED + "Missing field specification", Tools.consoleMode.max, programCode );
            return false;
        }

        String commandString = "help";
        if ( args.length > 0 ) commandString = args[0];

        switch ( commandString ) {
            case "info":
                if ( args.length > 1 ) {
                    String infoArea = args[1];
                    
                    if ( AreaManager.GetSQL( infoArea ) ) {
                        Tools.Prt( player, ChatColor.GREEN + "AreaCode:" + ChatColor.YELLOW + Database.AreaCode, programCode );
                        Tools.Prt( player, ChatColor.GREEN + "Owner:" + ChatColor.YELLOW + Database.Owner, programCode );
                        Tools.Prt( player, ChatColor.GREEN + "Location:" + ChatColor.YELLOW + Database.Location.toString(), programCode );
                        Tools.Prt( player, ChatColor.GREEN + "Block:" + ChatColor.YELLOW + Database.Block, programCode );
                        Tools.Prt( player, ChatColor.GREEN + "Date:" + ChatColor.YELLOW + Database.GetDate, programCode );
                    } else { Tools.Prt( player, ChatColor.RED + "存在しないエリアコードです[" + infoArea + "]", programCode ); }
                    return true;
                } else { return false; }
            case "add":
                if ( args.length > 2 ) {
                    Messages.AreaCode = args[1];
                    AreaManager.ManualAddRegist( player, args[2] );
                }
                return true;
            case "del":
                if ( args.length > 1 ) {
                    AreaManager.DelRegister( player, args[1] );
                    return true;
                } else return false;
            case "list":
                AreaManager.AreaList( player, ( ( args.length > 1 ) ? args[1] : "" ) );
                return true;
            case "check":
                if ( args.length > 1 ) {
                    Tools.Prt( player, "Checked Area [" + args[1] + "] = " + AreaManager.AreaCount( args[1] ) + " Point.", programCode );
                    return true;
                } else return false;
            case "regist":
                if ( args.length > 1 ) {
                    Tools.Prt( player, args[1] + "'s Registerd : " + AreaManager.GetRegistCount( args[1] ), programCode );
                    return true;
                } else return false;
            case "help":
                help( player );
                return true;
            case "AllClear":
                AreaManager.AllClear( player );
                return true;
            default:
                break;
        }

        Tools.Prt( player, ChatColor.RED + "[Premises] Area Unknown Command [" + commandString + "]", Tools.consoleMode.full, programCode );
        help( player );
        return false;
    }
}
