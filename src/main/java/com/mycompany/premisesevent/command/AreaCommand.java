/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.premisesevent.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.mycompany.kumaisulibraries.Tools;
import com.mycompany.premisesevent.PremisesEvent;
import com.mycompany.premisesevent.database.AreaManager;
import static com.mycompany.premisesevent.config.Config.programCode;

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
        Tools.Prt( player, ChatColor.YELLOW + "list     : " + ChatColor.WHITE + "Holding Area List", programCode );
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

        String commandString = "help";
        if ( args.length > 0 ) commandString = args[0];

        switch ( commandString ) {
            case "list":
                AreaManager.AreaList( player );
                return true;
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
