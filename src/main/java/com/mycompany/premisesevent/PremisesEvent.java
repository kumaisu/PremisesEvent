/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.premisesevent;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import com.mycompany.kumaisulibraries.Utility;
import com.mycompany.kumaisulibraries.Minecraft;
import com.mycompany.premisesevent.Item.ItemControl;
import com.mycompany.premisesevent.Player.PlayerControl;
import com.mycompany.premisesevent.Player.TopList;
import com.mycompany.premisesevent.config.Config;

/**
 *
 * @author sugichan
 */
public class PremisesEvent extends JavaPlugin implements Listener {

    BukkitTask task = null; //  あとで自分を止めるためのもの
    private Config config;
    private String EventName;
    private final Map<UUID, PlayerControl> pc = new HashMap<>();
    private boolean WarningFlag = true;


    /**
     * 耐久値警告を一定間隔で表示するためのタイマー
     * 掘る毎に出しているとログが埋まってしまうので
     */
    private class Timer extends BukkitRunnable{
        int time;//秒数
        JavaPlugin plugin;//BukkitのAPIにアクセスするためのJavaPlugin

        public Timer( JavaPlugin plugin ,int i ) {
            this.time = i;
            this.plugin = plugin;
        }

        @Override
        public void run() {
            if( time <= 0 ){
                //タイムアップなら
                WarningFlag = true;
                plugin.getServer().getScheduler().cancelTask( task.getTaskId() ); //自分自身を止める
            }
            /*
            else{
                plugin.getServer().broadcastMessage("" + time);//残り秒数を全員に表示
            }
            */
            time--; //  1秒減算
        }
    }

    /**
     * 起動シーケンス
     */
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents( this, this );
        config = new Config( this );
        EventName = config.getEventName();
    }

    /**
     * 終了シーケンス
     * ログインしたままのプレイヤーが存在する場合は、ここで保存処理を行う
     */
    @Override
    public void onDisable(){
        Minecraft.Prt( "[Premises] Disable processing..." );
        pc.entrySet().forEach( ( entry ) -> {
            if ( pc.get( entry.getKey() ).getEntry() != 0 ) {
                pc.get( entry.getKey() ).save();
                Minecraft.Prt( "[Premises] " + ChatColor.AQUA + pc.get( entry.getKey() ).getDisplayName() + " logged out, Saved the Score" );
            }
        } );
    }

    /**
     * プレイヤーのログイン時処理、参加者であれば、スコアをメモリにロードする
     * スコアファイルにプレゼントフラグがある場合は適宜プレゼント処理を行う
     * ※今の所スコアファイルのプレゼントフラグは直接編集でのみ変更可能
     *
     * @param event
     */
    @EventHandler
    public void onPlayerJoin( PlayerJoinEvent event ){
        Player p = event.getPlayer();

        pc.put( p.getUniqueId(), new PlayerControl( p, config, this.getDataFolder().toString() ) );
        pc.get( p.getUniqueId() ).load();

        if ( pc.get( p.getUniqueId() ).getEntry() == 1 ) {
            pc.get( p.getUniqueId() ).ScoreBoardEntry( p );
            Minecraft.Prt( ChatColor.AQUA + p.getDisplayName() + " is participating in the this Event" );

            ItemControl ic = new ItemControl( config );
            if ( pc.get( p.getUniqueId() ).getPresentFlag() ) {
                Minecraft.Prt( p, ChatColor.YELLOW + "イベント装備の再配布", config.isDebugFlag( Utility.consoleMode.normal ) );
                ic.ItemPresent( p );
            }
            if ( pc.get( p.getUniqueId() ).getUpdateFlag() ) {
                Minecraft.Prt( p, ChatColor.YELLOW + "イベントツールの再配布", config.isDebugFlag( Utility.consoleMode.normal ) );
                for( int i = 0; i<config.getTools().size(); i++ ) {
                    ic.ItemUpdate( p, null, config.getEventToolName(), Material.getMaterial( config.getTools().get( i ).toString() ) );
                }
            }
        } else {
            Minecraft.Prt( ChatColor.RED + p.getDisplayName() + " has not joined the this Event" );
        }
    }

    /**
     * プレイヤーのログアウト時処理、参加者であればメモリのスコアをスコアファイルに保存する
     *
     * @param event
     */
    @EventHandler
    public void onPlayerQuit( PlayerQuitEvent event ) {
        Player player = event.getPlayer();
        if ( pc.get( player.getUniqueId() ).getEntry() != 0 ) {
            Minecraft.Prt( "[Premises] " + ChatColor.AQUA + player.getDisplayName() + ChatColor.WHITE + " logged out, Saved the Score" );

            pc.get( player.getUniqueId() ).save();
        } else {
            Minecraft.Prt( "[Premises] " + ChatColor.AQUA + player.getDisplayName() + ChatColor.LIGHT_PURPLE + " logged out, not Save" );
        }
        pc.remove( player.getUniqueId() );
    }

    /**
     * ブロックを設置した時の処理
     * ポイントブロックを置いた場合は、スコアからポイントをマイナスする
     * ポイントブロックで無い場合は何もしない
     * ※自然生成か設置かを判断するための試作をしているが昨日していない
     *
     * @param event
     */
    @EventHandler
    public void onBlockPlace( BlockPlaceEvent event ) {
        Player player = event.getPlayer();
        if ( config.CreativeCount() && player.getGameMode() == GameMode.CREATIVE ) return;
        if ( config.GetField() && !config.CheckArea( event.getBlock().getLocation() ) ) return;
        if ( pc.get( player.getUniqueId() ).getEntry() != 1 ) {
            if ( !config.FreeBreak() )  event.setCancelled( true );
            return;
        }

        //  設置したブロックであるというフラグを設定しているが、機能していない
        Block block = event.getBlock();
        String blockName = Minecraft.getStoneName( block );
        block.setMetadata( "PLACED", new FixedMetadataValue( ( Plugin ) this, true ) );

        if ( config.getStones().contains( blockName ) ) {
            Minecraft.Prt( player.getDisplayName() + " Loss " + blockName + " Point: " + config.getPoint( blockName ), config.isDebugFlag( Utility.consoleMode.max ) );
            pc.get( player.getUniqueId() ).addScore( player, - config.getPoint( blockName ) );
        } else {
            Minecraft.Prt( ChatColor.GREEN + "This [" + ChatColor.GOLD + blockName + ChatColor.GREEN + "] is not a target", config.isDebugFlag( Utility.consoleMode.full ) );
        }

        player.setPlayerListName( ChatColor.WHITE + String.format( "%-12s", player.getDisplayName() ) + " " + ChatColor.YELLOW + String.format( "%8d", pc.get( player.getUniqueId() ).getScore() ) );
    }

    /**
     * ブロックが破壊された時の処理
     *
     * @param event
     */
    @EventHandler
    public void onBlockBreak( BlockBreakEvent event ) {
        Player player = event.getPlayer();

        if ( config.CreativeCount() && player.getGameMode() == GameMode.CREATIVE ) return;
        if ( config.GetField() && !config.CheckArea( event.getBlock().getLocation() ) ) return;

        switch ( pc.get( player.getUniqueId() ).getEntry() ) {
            case 0:
                if ( !config.FreeBreak() ) {
                    Minecraft.Prt( player, ChatColor.RED + "イベントに参加してください", config.isDebugFlag( Utility.consoleMode.normal ) );
                    event.setCancelled( true );
                }
                return;
            case 1:
                // 参加者なので、スルー
                break;
            case 2:
                Minecraft.Prt( player, ChatColor.RED + "イベントエリアの操作はできません", config.isDebugFlag( Utility.consoleMode.normal ) );
                event.setCancelled( true );
                return;
        }

        Block block = event.getBlock();
        Material material = block.getType();
        String blockName = Minecraft.getStoneName( block );
        ItemStack item = player.getInventory().getItemInMainHand();

        if ( config.ToolBreak() && ( item.getType() != Material.TORCH ) ) {
            if (
                    ( item.getType() == Material.AIR ) ||
                    ( !item.getItemMeta().hasDisplayName() ) ||
                    ( !item.getItemMeta().getDisplayName().equalsIgnoreCase( config.getEventToolName() ) )
               ) {
                player.sendMessage( ChatColor.RED + "指定ツールで行ってください" );
                event.setCancelled( true );
                return;
            }
        }

        if ( config.getStones().contains( blockName ) ) {
            Minecraft.Prt(
                player.getDisplayName() + " get " + blockName + " Point: " + config.getPoint( blockName ) +
                ChatColor.YELLOW + " (" + ( block.hasMetadata( "PLACED" ) ? "Placed":"Naturally" ) + ")",
                config.isDebugFlag( Utility.consoleMode.max ) );
            pc.get( player.getUniqueId() ).addStoneCount( blockName );
            pc.get( player.getUniqueId() ).addScore( player, config.getPoint( blockName ) );
            if ( config.getPoint( blockName )<0 ) {
                Minecraft.Prt(
                    ChatColor.RED + "[Premises] Warning " +
                    ChatColor.AQUA + player.getDisplayName() +
                    ChatColor.RED + " broke a " +
                    ChatColor.YELLOW + blockName,
                    config.isDebugFlag( Utility.consoleMode.full )
                );
                if ( config.getSendTitle() ) {
                    player.sendTitle(
                        ChatColor.RED + "破壊不可なブロックを破壊しました",
                        ChatColor.YELLOW + "元に戻してくださいね",
                        0, 100, 0 );
                }
            }

        } else {
            Minecraft.Prt( ChatColor.LIGHT_PURPLE + "This [" + ChatColor.GOLD + block.getType().toString() + ChatColor.LIGHT_PURPLE + "] is not a target", config.isDebugFlag( Utility.consoleMode.full ) );
        }

        player.setPlayerListName( ChatColor.WHITE + String.format( "%-12s", player.getDisplayName() ) + " " + ChatColor.YELLOW + String.format( "%8d", pc.get( player.getUniqueId() ).getScore() ) );

        if ( item.getType() == Material.AIR ) return;

        if ( WarningFlag && item.getItemMeta().hasDisplayName() ) {
            if ( item.getItemMeta().getDisplayName().equalsIgnoreCase( config.getEventToolName() ) ) {
                if ( ( item.getType().getMaxDurability() * config.getRepair() ) <= item.getDurability() ) {
                    Minecraft.Prt( player, ChatColor.RED + "ツールの耐久値がヤバイですよ", config.isDebugFlag( Utility.consoleMode.max ) );
                    WarningFlag = false;
                    task = this.getServer().getScheduler().runTaskTimer( this, new Timer( this ,config.CoolCount() ), 0L, config.CoolTick() );
                    if ( config.getSendTitle() ) {
                        player.sendTitle(
                            ChatColor.RED + "ツールの耐久値がヤバイですよ",
                            ChatColor.YELLOW + "アップデートして成長させましょう",
                            0, 50, 0 );
                    }
                }
            }
        }
    }

    /**
     * 看板ブロックを右クリック
     *
     * @param event
     */
    @EventHandler
    public void onSignClick( PlayerInteractEvent event ) {

        if ( event.getAction() != Action.RIGHT_CLICK_BLOCK ) return;

        Player player = event.getPlayer();
        Block clickedBlock = event.getClickedBlock();
        Material material = clickedBlock.getType();
        if ( material == Material.SIGN_POST || material == Material.WALL_SIGN ) {
            Sign sign = (Sign) clickedBlock.getState();
            switch ( sign.getLine(0) ) {
                case "[P-Get]":
                    pc.get( player.getUniqueId() ).getEventItem( player, sign.getLine( 1 ) );
                    break;
                case "[P-Join]":
                    pc.get( player.getUniqueId() ).JoinPlayer( player );
                    break;
                case "[P-Status]":
                    PlayerStatus( player, "" );
                    break;
                case "[P-Update]":
                    if ( pc.get( player.getUniqueId() ).getEntry() == 1 ) pc.get( player.getUniqueId() ).ToolUpdate( player, false );
                    break;
                case "[P-TOP]":
                    if ( pc.get( player.getUniqueId() ).getEntry() == 1 ) pc.get( player.getUniqueId() ).save();
                    TopList TL = new TopList( config, this.getDataFolder().toString() );
                    TL.Top( player, config.isDebugFlag( Utility.consoleMode.max ) );
                    break;
                default:
            }
        }
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

        if ( cmd.getName().equalsIgnoreCase( "toplist" ) ) {

            if ( player != null && pc.get( player.getUniqueId() ).getEntry() != 0 ) pc.get( player.getUniqueId() ).save();

            Bukkit.getServer().getOnlinePlayers().stream().filter( ( onPlayer ) -> ( pc.get( onPlayer.getUniqueId() ).getEntry() == 1 ) ).forEachOrdered( ( onPlayer ) -> {
                Minecraft.Prt( onPlayer.getDisplayName() + "is Online Event[" + ( ( pc.get( onPlayer.getUniqueId() ).getEntry() == 1 ) ? "true":"false" ) + "]" );
                pc.get( onPlayer.getUniqueId() ).save();
            } );

            TopList TL = new TopList( config, this.getDataFolder().toString() );
            TL.Top( player, config.isDebugFlag( Utility.consoleMode.max ) );
            return true;
        }

        if ( cmd.getName().equalsIgnoreCase( "pstatus" ) && hasPermission ) {
            config.Status( player );
            return true;
        }

        if ( cmd.getName().equalsIgnoreCase( "Premises" ) ) {
            String commandString = "";
            String itemName = "";

            if ( args.length > 0 ) commandString = args[0];
            if ( args.length > 1 ) itemName = args[1];

            if ( hasPermission ) {
                switch ( commandString ) {
                    case "csv":
                        TopList TL = new TopList( config, this.getDataFolder().toString() );
                        try {
                            TL.ToCSV( config.getStones() );
                        } catch ( IOException ex ) {
                            Logger.getLogger( PremisesEvent.class.getName() ).log( Level.SEVERE, null, ex );
                        }
                        return true;
                    case "give":
                        if ( args.length == 4 ) { return GiveScore( player, args[2], args[3] ); }
                        return true;
                    case "Console":
                        config.setDebug( itemName );
                        /*
                            default:
                                Utility.Prt( player, "usage: PremisesEvent Console [max/full/normal/none]", ( player == null ) );
                        */
                        Minecraft.Prt( player,
                            ChatColor.GREEN + "System Debug Mode is [ " +
                            ChatColor.RED + config.getDebug().toString() +
                            ChatColor.GREEN + " ]", ( player == null )
                        );
                        return true;
                    default:
                        break;
                }
            }

            if ( player != null ) {
                if ( commandString.equalsIgnoreCase( "join" ) ) return pc.get( player.getUniqueId() ).JoinPlayer( player );
                switch ( commandString ) {
                    case "get":
                        return pc.get( player.getUniqueId() ).getEventItem( player, itemName );
                    case "update":
                        boolean force = ( itemName.equals( "force" ) );
                        if ( pc.get( player.getUniqueId() ).getEntry() == 1 ) pc.get( player.getUniqueId() ).ToolUpdate( player, force );
                        return true;
                    case "status":
                        return PlayerStatus( player, itemName );
                    case "check":
                        if ( hasPermission ) {
                            ItemControl ic = new ItemControl( config );
                            ic.ShowItemStatus( player );
                            return true;
                        } else return false;
                    case "launch":
                        if ( hasPermission) {
                            Minecraft.launchFireWorks( player.getLocation() );
                            return true;
                        } else return false;
                    default:
                        break;
                }
            } else Minecraft.Prt( player, ChatColor.RED + "コンソールからは操作できないコマンドです", true );

            Minecraft.Prt( player, ChatColor.RED + "[Premises] Unknown Command [" + commandString + "]", config.isDebugFlag( Utility.consoleMode.full ) );
            return false;
        }
        return false;
    }

    /**
     * イベント参加者のステータス表示する処理
     *
     * @param player
     * @param Other
     * @return
     */
    public boolean PlayerStatus( Player player, String Other ) {
        if ( pc.get( player.getUniqueId() ).getEntry() == 1 ) {
            if ( "".equals( Other ) ) {
                pc.get( player.getUniqueId() ).save();
                pc.get( player.getUniqueId() ).getStatus( player );
            } else {
                UUID uuid;

                Minecraft.Prt( player, ChatColor.RED + "Look other Player : " + Other, true );

                OfflinePlayer op = Bukkit.getServer().getOfflinePlayer( Other );
                if ( op.hasPlayedBefore() ) {
                    uuid = op.getUniqueId();
                    pc.put( uuid, new PlayerControl( op, config, this.getDataFolder().toString() ) );
                    pc.get( uuid ).load();
                } else {
                    Minecraft.Prt( player, ChatColor.RED + "This Player is not joined to server.", config.isDebugFlag( Utility.consoleMode.normal ) );
                    return false;
                }
                pc.get( uuid ).getStatus( player );
                if ( player.getUniqueId() != uuid ) pc.remove( uuid );
            }
            return true;
        } else {
            Minecraft.Prt( player, ChatColor.RED + "イベントに参加していません", config.isDebugFlag( Utility.consoleMode.normal ) );
            return false;
        }
    }

    /**
     * 参加者のスコアーを操作する処理
     *
     * @param player
     * @param name
     * @param score
     * @return
     */
    public boolean GiveScore( Player player, String name, String score ) {
        int scoreNum;
        Player scorePlayer;
        boolean createStat = false;
        boolean retStat;

        try {
            scoreNum = Integer.parseInt( score );
        } catch ( NumberFormatException e ) {
            Minecraft.Prt( player, ChatColor.RED + "指定された値が正しくありません", config.isDebugFlag( Utility.consoleMode.full ) );
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
                    pc.put( scorePlayer.getUniqueId(), new PlayerControl( player, config, this.getDataFolder().toString() ) );
                    pc.get( scorePlayer.getUniqueId() ).load();
                    createStat = true;
                } else {
                    Minecraft.Prt( player, ChatColor.RED + "[ " + ChatColor.YELLOW + name + ChatColor.RED + " ] はサーバーに存在しません", config.isDebugFlag( Utility.consoleMode.normal ) );
                    return false;
                }
            }
        }

        if ( pc.get( scorePlayer.getUniqueId() ).getEntry() == 1 ) {
            pc.get( scorePlayer.getUniqueId() ).addScore( null, scoreNum );
            pc.get( scorePlayer.getUniqueId() ).save();
            retStat = true;
        } else {
            Minecraft.Prt( player, ChatColor.RED + "[ " + ChatColor.YELLOW + name + ChatColor.RED + " ] はイベントに参加していません", config.isDebugFlag( Utility.consoleMode.normal ) );
            retStat = false;
        }

        if ( createStat ) pc.remove( scorePlayer.getUniqueId() );
        return retStat;
    }
}
