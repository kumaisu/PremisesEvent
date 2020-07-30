/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.premisesevent.command;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.OfflinePlayer;
import com.mycompany.kumaisulibraries.Tools;
import com.mycompany.premisesevent.Item.ItemControl;
import com.mycompany.premisesevent.Player.PlayerControl;
import com.mycompany.premisesevent.Player.PlayerStatus;
import com.mycompany.premisesevent.Player.TopList;
import com.mycompany.premisesevent.PremisesEvent;
import com.mycompany.premisesevent.config.Config;
import com.mycompany.premisesevent.config.Messages;
import com.mycompany.premisesevent.config.MessagesManager;
import com.mycompany.premisesevent.database.Database;
import com.mycompany.premisesevent.database.SQLControl;
import com.mycompany.premisesevent.database.AreaManager;
import com.mycompany.premisesevent.utility.BukkitTool;
import static com.mycompany.premisesevent.PremisesEvent.pc;
import static com.mycompany.premisesevent.config.Config.programCode;

/**
 *
 * @author sugichan
 */
public class PECommand implements CommandExecutor {

     private final PremisesEvent instance;

     public PECommand( PremisesEvent instance ) {
         this.instance = instance;
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
        boolean hasPermission = ( ( player == null ) ? true:player.hasPermission( "Premises.admin" ) );

        String commandString = "";
        String itemName = "";
        if ( args.length > 0 ) commandString = args[0];
        if ( args.length > 1 ) itemName = args[1];

        switch ( commandString ) {
            case "pstatus":
                if ( hasPermission ) {
                    instance.config.Status( player );
                    return true;
                } else return false;
            case "message":
                MessagesManager.getStatus( player );
                return true;
            case "reload":
                if ( hasPermission ) {
                    instance.config.load();
                    if ( Config.Field ) {
                        if ( Database.dataSource != null ) {
                            Database.dataSource.close();
                        }
                        Tools.Prt( "Open SQLite Database : " + Config.databaseName, Tools.consoleMode.max, programCode );
                        SQLControl.connect();
                        SQLControl.TableUpdate();
                    }
                    return true;
                } else return false;
            case "messagereload":
                if ( hasPermission ) {
                    instance.messe.load();
                    return true;
                } else return false;
            case "csv":
                if ( hasPermission ) {
                    TopList TL = new TopList( instance.getDataFolder().toString() );
                    try {
                        TL.ToCSV( Config.stones );
                    } catch ( IOException ex ) {
                        Logger.getLogger( PremisesEvent.class.getName() ).log( Level.SEVERE, null, ex );
                    }
                    return true;
                } else return false;
            case "give":
                if ( hasPermission ) {
                    if ( args.length == 4 ) { return GiveScore( player, args[2], args[3] ); }
                    return true;
                } else return false;
            case "Console":
                if ( hasPermission ) {
                    if ( !Tools.setDebug( itemName, programCode ) ) {
                        Tools.entryDebugFlag( programCode, Tools.consoleMode.normal );
                        Tools.Prt( ChatColor.RED + "Config Debugモードの指定値が不正なので、normal設定にしました", programCode );
                    }
                    Tools.Prt( player,
                        ChatColor.GREEN + "System Debug Mode is [ " +
                        ChatColor.RED + Tools.consoleFlag.get( programCode ) +
                        ChatColor.GREEN + " ]",
                        programCode
                    );
                    return true;
                } else return false;
            case "stones":
                if ( hasPermission ) {
                    instance.config.getStoneList( player );
                    return true;
                }
            case "toplist":
                if ( player != null && pc.get( player.getUniqueId() ).getEntry() != 0 ) pc.get( player.getUniqueId() ).save();
                Bukkit.getServer().getOnlinePlayers().stream().filter( ( onPlayer ) -> (
                    pc.get( onPlayer.getUniqueId() ).getEntry() == 1 ) ).forEachOrdered( ( onPlayer ) -> {
                        Tools.Prt(
                            onPlayer.getDisplayName() +
                            " is Online Event[" +
                            ( ( pc.get( onPlayer.getUniqueId() ).getEntry() == 1 ) ? "true":"false" ) +
                            "]",
                            programCode
                        );
                        pc.get( onPlayer.getUniqueId() ).save();
                    }
                );
                TopList TL = new TopList( instance.getDataFolder().toString() );
                TL.Top( player, Tools.consoleMode.max );
                return true;
            case "pointtip":
                Config.PointTip = !Config.PointTip;
                Tools.Prt( player, ChatColor.WHITE + "PointTip表示 : " + ChatColor.YELLOW + ( Config.PointTip ? "あり":"なし" ), programCode );
                return true;
            case "help":
                Tools.Prt( player, ChatColor.GREEN + "/premises Command List", programCode );
                Tools.Prt( player, ChatColor.YELLOW +   "join               : " + ChatColor.WHITE + "イベント参加", programCode );
                Tools.Prt( player, ChatColor.YELLOW +   "status [Player]    : " + ChatColor.WHITE + "イベントスコア参照", programCode );
                Tools.Prt( player, ChatColor.YELLOW +   "get [Material]     : " + ChatColor.WHITE + "イベントツール再配布", programCode );
                Tools.Prt( player, ChatColor.YELLOW +   "update             : " + ChatColor.WHITE + "イベントツールアップデート", programCode );
                Tools.Prt( player, ChatColor.YELLOW +   "toplist            : " + ChatColor.WHITE + "参加者ランキング", programCode );
                if ( hasPermission ) {
                    Tools.Prt( player, ChatColor.GREEN + "Admin Command's", programCode );
                    Tools.Prt( player, ChatColor.AQUA + "check              : " + ChatColor.WHITE + "所持アイテムチェック", programCode );
                    Tools.Prt( player, ChatColor.AQUA + "launch             : " + ChatColor.WHITE + "花火", programCode );
                    Tools.Prt( player, ChatColor.AQUA + "csv                : " + ChatColor.WHITE + "ランキングのファイル出力", programCode );
                    Tools.Prt( player, ChatColor.AQUA + "give Player Score  : " + ChatColor.WHITE + "スコアの調整", programCode );
                    Tools.Prt( player, ChatColor.AQUA + "Console [Mode]     : " + ChatColor.WHITE + "コンソールデバッグ設定 [max,full,normal,none]", programCode );
                    Tools.Prt( player, ChatColor.AQUA + "reload             : " + ChatColor.WHITE + "Configリロード", programCode );
                    Tools.Prt( player, ChatColor.AQUA + "stones             : " + ChatColor.WHITE + "ストーンスコア", programCode );
                    Tools.Prt( player, ChatColor.AQUA + "pstatus            : " + ChatColor.WHITE + "Configステータス", programCode );
                    Tools.Prt( player, ChatColor.AQUA + "message            : " + ChatColor.WHITE + "Message一覧", programCode );
                    Tools.Prt( player, ChatColor.AQUA + "pointtip           : " + ChatColor.WHITE + "破壊ポイント表示可否", programCode );
                }
                return true;
            default:
                break;
        }

        if ( sender instanceof Player ) {
            Messages.RepPlayer = player.getName();
            switch ( commandString ) {
                case "join":
                    return pc.get( player.getUniqueId() ).JoinPlayer( player );
                case "get":
                    return pc.get( player.getUniqueId() ).getEventItem( player, itemName );
                case "update":
                    boolean force = ( itemName.equals( "force" ) );
                    if ( pc.get( player.getUniqueId() ).getEntry() == 1 ) pc.get( player.getUniqueId() ).ToolUpdate( player, force );
                    return true;
                case "status":
                    return PlayerStatus.Print( player, itemName );
                case "list":
                    AreaManager.GetRegist( player.getName(), true, player );
                    return true;
                case "check":
                    if ( hasPermission ) {
                        ItemControl ic = new ItemControl();
                        ic.ShowItemStatus( player );
                        return true;
                    } else return false;
                case "launch":
                    if ( hasPermission) {
                        BukkitTool.launchFireWorks( player.getLocation() );
                        return true;
                    } else return false;
                default:
                    break;
            }
        } else Tools.Prt( Messages.GetString( "ConsoleOnly" ), programCode );

        Tools.Prt( player, ChatColor.RED + "[Premises] Unknown Command [" + commandString + "]", Tools.consoleMode.full, programCode );
        return false;
    }

    /**
     * 参加者のスコアーを操作する処理
     *
     * @param player
     * @param name
     * @param score
     * @return
     */
    private boolean GiveScore( Player player, String name, String score ) {
        int scoreNum;
        Player scorePlayer;
        boolean createStat = false;
        boolean retStat;
        Messages.RepPlayer = name;

        try {
            scoreNum = Integer.parseInt( score );
        } catch ( NumberFormatException e ) {
            Tools.Prt( player, Messages.GetString( "ValueIncorrect" ), programCode );
            return false;
        }

        if ( player.getName().equals( name ) ) {
            scorePlayer = player;
        } else {
            Player checkPlayer = Bukkit.getServer().getPlayer( name );
            if ( checkPlayer != null ) {
                //  online
                scorePlayer = checkPlayer;
            } else {
                //  offline
                OfflinePlayer op = Bukkit.getServer().getOfflinePlayer( name );
                if ( op.hasPlayedBefore() ) {
                    scorePlayer = op.getPlayer();
                    pc.put( scorePlayer.getUniqueId(), new PlayerControl( player ) );
                    pc.get( scorePlayer.getUniqueId() ).load();
                    createStat = true;
                } else {
                    Tools.Prt( player, Messages.GetString( "NoPlayer" ), programCode );
                    return false;
                }
            }
        }

        if ( pc.get( scorePlayer.getUniqueId() ).getEntry() == 1 ) {
            pc.get( scorePlayer.getUniqueId() ).addScore( null, scoreNum );
            pc.get( scorePlayer.getUniqueId() ).save();
            retStat = true;
        } else {
            Tools.Prt( player, Messages.GetString( "NoJoin" ), programCode );
            retStat = false;
        }

        if ( createStat ) pc.remove( scorePlayer.getUniqueId() );
        return retStat;
    }

    private boolean PlayerStatus(Player player, String itemName) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
