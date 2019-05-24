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
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Builder;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Firework;
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
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import com.mycompany.kumaisulibraries.Utility;

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
        Utility.Prt( null, "[Premises] Disable processing...", true );
        pc.entrySet().forEach( ( entry ) -> {
            if ( pc.get( entry.getKey() ).getEntry() != 0 ) {
                pc.get( entry.getKey() ).save();
                Utility.Prt( null, "[Premises] " + ChatColor.AQUA + pc.get( entry.getKey() ).getDisplayName() + " logged out, Saved the Score", true );
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

        pc.put( p.getUniqueId(), new PlayerControl( ( Plugin ) this, config ) );
        pc.get( p.getUniqueId() ).setDisplayName( p.getDisplayName() );
        pc.get( p.getUniqueId() ).setUUID( p.getUniqueId() );
        pc.get( p.getUniqueId() ).load();

        if ( pc.get( p.getUniqueId() ).getEntry() == 1 ) {
            pc.get( p.getUniqueId() ).ScoreBoardEntry( p );
            Utility.Prt( null, ChatColor.AQUA + p.getDisplayName() + " is participating in the this Event", true );

            ItemControl ic = new ItemControl( this );
            if ( pc.get( p.getUniqueId() ).getPresentFlag() ) {
                Utility.Prt( p, ChatColor.YELLOW + "イベント装備の再配布", config.isDebugFlag( Utility.consoleMode.normal ) );
                ic.ItemPresent( p );
            }
            if ( pc.get( p.getUniqueId() ).getUpdateFlag() ) {
                Utility.Prt( p, ChatColor.YELLOW + "イベントツールの再配布", config.isDebugFlag( Utility.consoleMode.normal ) );
                for( int i = 0; i<config.getTools().size(); i++ ) {
                    ic.ItemUpdate( p, null, config.getEventToolName(), Material.getMaterial( config.getTools().get( i ).toString() ) );
                }
            }
        } else {
            Utility.Prt( null, ChatColor.RED + p.getDisplayName() + " has not joined the this Event", true );
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
            Utility.Prt( null, "[Premises] " + ChatColor.AQUA + player.getDisplayName() + ChatColor.WHITE + " logged out, Saved the Score", true );

            pc.get( player.getUniqueId() ).save();
        } else {
            Utility.Prt( null, "[Premises] " + ChatColor.AQUA + player.getDisplayName() + ChatColor.LIGHT_PURPLE + " logged out, not Save", true );
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
        String blockName = getStoneName( block );
        block.setMetadata( "PLACED", new FixedMetadataValue( ( Plugin ) this, true ) );

        if ( config.getStones().contains( blockName ) ) {
            Utility.Prt( null, player.getDisplayName() + " Loss " + blockName + " Point: " + config.getPoint( blockName ), config.isDebugFlag( Utility.consoleMode.max ) );
            pc.get( player.getUniqueId() ).addScore( - config.getPoint( blockName ) );
        } else {
            Utility.Prt( null, ChatColor.LIGHT_PURPLE + "This block is not a target", config.isDebugFlag( Utility.consoleMode.full ) );
        }

        player.setPlayerListName( ChatColor.WHITE + String.format( "%-12s", player.getDisplayName() ) + " " + ChatColor.YELLOW + String.format( "%8d", pc.get( player.getUniqueId() ).getScore() ) );
    }

    /**
     *  指定された場所に花火を打ち上げる関数
     *
     * @param loc
     */
    public static void launchFireWorks( Location loc ) {
        /*
            static private FireworkEffect.Type[] types = { FireworkEffect.Type.BALL,
                FireworkEffect.Type.BALL_LARGE, FireworkEffect.Type.BURST,
                FireworkEffect.Type.CREEPER, FireworkEffect.Type.STAR, };
        */
        // 花火を作る
        Firework firework = loc.getWorld().spawn( loc, Firework.class );

        // 花火の設定情報オブジェクトを取り出す
        FireworkMeta meta = firework.getFireworkMeta();
        Builder effect = FireworkEffect.builder();

        // 形状を星型にする
        effect.with( FireworkEffect.Type.STAR );

        // 基本の色を単色～5色以内でランダムに決める
        effect.withColor( Color.AQUA );

        // 余韻の色を単色～3色以内でランダムに決める
        effect.withFade( Color.YELLOW );

        // 爆発後に点滅するかをランダムに決める
        effect.flicker( true );

        // 爆発後に尾を引くかをランダムに決める
        effect.trail( true );

        // 打ち上げ高さを1以上4以内でランダムに決める
        meta.setPower( 1 );

        // 花火の設定情報を花火に設定
        meta.addEffect( effect.build() );
        firework.setFireworkMeta( meta );
    }

    /**
     * Config.ymlで指定されたコンソールコマンドを実行する
     *
     * @param player
     * @param Message
     */
    public void ExecOtherCommand( Player player, String Message ) {
        for( int i = 0; i<config.getBC_Command().size(); i++ ) {
            String ExecCommand = config.getBC_Command().get( i ).toString();
            ExecCommand = ExecCommand.replace( "%message%", Message );
            ExecCommand = ExecCommand.replace( "%player%", player.getDisplayName() );
            Utility.Prt( null, ChatColor.WHITE + String.valueOf( i ) + ") : " + ChatColor.YELLOW + ExecCommand, config.isDebugFlag( Utility.consoleMode.normal ) );
            Bukkit.getServer().dispatchCommand( Bukkit.getConsoleSender(), ExecCommand );
        }
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
                    Utility.Prt( player, ChatColor.RED + "イベントに参加してください", config.isDebugFlag( Utility.consoleMode.normal ) );
                    event.setCancelled( true );
                }
                return;
            case 1:
                // 参加者なので、スルー
                break;
            case 2:
                Utility.Prt( player, ChatColor.RED + "イベントエリアの操作はできません", config.isDebugFlag( Utility.consoleMode.normal ) );
                event.setCancelled( true );
                return;
        }

        Block block = event.getBlock();
        Material material = block.getType();
        String blockName = getStoneName( block );
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
            Utility.Prt( null, player.getDisplayName() + " get " + blockName + " Point: " + config.getPoint( blockName ) + ChatColor.YELLOW + " (" + ( block.hasMetadata( "PLACED" ) ? "Placed":"Naturally" ) + ")", config.isDebugFlag( Utility.consoleMode.max ) );
            pc.get( player.getUniqueId() ).addStoneCount( blockName );
            pc.get( player.getUniqueId() ).addScore( config.getPoint( blockName ) );

            //  デバッグ（または保守）用、一定数到達記録をコンソールログに残す
            //  不具合や他責によるスコアの未記録時の対応ログとして表示
            if ( config.getScoreNotice() > 0 ) {
                if ( ( pc.get( player.getUniqueId() ).getScore() % config.getScoreNotice() ) == 0 ) {
                    Utility.Prt( null, "[Premises] " + player.getDisplayName() + " reached " + pc.get( player.getUniqueId() ).getScore() + " points.", config.isDebugFlag( Utility.consoleMode.normal ) );
                }
            }

            //  ブロードキャスト、一定スコア達成をオンラインプレイヤーに知らせる
            if ( ( player.hasPermission( "Premises.broadcast" ) ) && ( config.getScoreBroadcast() > 0 ) ) {
                if ( ( pc.get( player.getUniqueId() ).getScore() % config.getScoreBroadcast() ) == 0 ) {
                    String SendMessage = "<イベント> " + ChatColor.AQUA + player.getDisplayName() + ChatColor.WHITE + " さんが " + ChatColor.YELLOW + pc.get( player.getUniqueId() ).getScore() + ChatColor.WHITE + " 点に到達しました";
                    Bukkit.broadcastMessage( SendMessage );
                    launchFireWorks( player.getLocation() );
                    ExecOtherCommand( player, SendMessage );
                }
            }

        } else {
            if ( player.isOp() ) Utility.Prt( null, ChatColor.LIGHT_PURPLE + "This [" + ChatColor.GOLD + block.getType().toString() + ChatColor.LIGHT_PURPLE + "] is not a target", config.isDebugFlag( Utility.consoleMode.normal ) );
        }

        player.setPlayerListName( ChatColor.WHITE + String.format( "%-12s", player.getDisplayName() ) + " " + ChatColor.YELLOW + String.format( "%8d", pc.get( player.getUniqueId() ).getScore() ) );

        if ( item.getType() == Material.AIR ) return;

        if ( WarningFlag && item.getItemMeta().hasDisplayName() ) {
            if ( item.getItemMeta().getDisplayName().equalsIgnoreCase( config.getEventToolName() ) ) {
                if ( ( item.getType().getMaxDurability() * config.getRepair() ) <= item.getDurability() ) {
                    Utility.Prt( player, ChatColor.RED + "ツールの耐久値がヤバイですよ", config.isDebugFlag( Utility.consoleMode.max ) );
                    WarningFlag = false;
                    task = this.getServer().getScheduler().runTaskTimer( this, new Timer( this ,config.CoolCount() ), 0L, config.CoolTick() );
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
                    GetEventItem( player, sign.getLine( 1 ) );
                    break;
                case "[P-Join]":
                    PlayerJoin( player );
                    break;
                case "[P-Status]":
                    PlayerStatus( player, "" );
                    break;
                case "[P-Update]":
                    if ( pc.get( player.getUniqueId() ).getEntry() == 1 ) pc.get( player.getUniqueId() ).ToolUpdate( player, false );
                    break;
                case "[P-TOP]":
                    if ( pc.get( player.getUniqueId() ).getEntry() == 1 ) pc.get( player.getUniqueId() ).save();
                    TopList TL = new TopList( this, EventName );
                    TL.Top( player, config.isDebugFlag( Utility.consoleMode.full ) );
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
                Utility.Prt( null, onPlayer.getDisplayName() + "is Online Event[" + ( ( pc.get( onPlayer.getUniqueId() ).getEntry() == 1 ) ? "true":"false" ) + "]", true );
                pc.get( onPlayer.getUniqueId() ).save();
            } );

            TopList TL = new TopList( this, EventName );
            TL.Top( player, config.isDebugFlag( Utility.consoleMode.full ) );
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
                        TopList TL = new TopList( this, EventName );
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
                        Utility.Prt( player,
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
                if ( commandString.equalsIgnoreCase( "join" ) ) return PlayerJoin( player );
                switch ( commandString ) {
                    case "get":
                        return GetEventItem( player, itemName );
                    case "update":
                        boolean force = ( itemName.equals( "force" ) );
                        if ( pc.get( player.getUniqueId() ).getEntry() == 1 ) pc.get( player.getUniqueId() ).ToolUpdate( player, force );
                        return true;
                    case "status":
                        return PlayerStatus( player, itemName );
                    case "check":
                        if ( hasPermission ) {
                            ItemControl ic = new ItemControl( this );
                            ic.ShowItemStatus( player );
                            return true;
                        } else return false;
                    case "launch":
                        if ( hasPermission) {
                            launchFireWorks( player.getLocation() );
                            return true;
                        } else return false;
                    default:
                        break;
                }
            } else Utility.Prt( player, ChatColor.RED + "コンソールからは操作できないコマンドです", true );

            Utility.Prt( player, ChatColor.RED + "[Premises] Unknown Command [" + commandString + "]", config.isDebugFlag( Utility.consoleMode.full ) );
            return false;
        }
        return false;
    }

    /**
     * 特殊な名前のブロックに対する対処
     * GRANITE：花崗岩
     * DIORITE：閃緑岩
     * ANDESITE：安山岩
     *
     * @param b
     * @return
     */
    public String getStoneName( Block b ) {
        String retStr = b.getType().toString();
        if ( b.getType().equals( Material.STONE ) ) {
            switch ( b.getData() ) {
                case 1:
                    retStr = "GRANITE";
                    break;
                case 3:
                    retStr = "DIORITE";
                    break;
                case 5:
                    retStr = "ANDESITE";
                    break;
            }
        }
        return retStr;
    }

    /**
     * プレイヤーがイベントに参加した時の処理
     *
     * @param player
     * @return
     */
    public boolean PlayerJoin( Player player ) {
        switch ( pc.get( player.getUniqueId() ).getEntry() ) {
            case 1: //  Double registration failure.
                Utility.Prt( player, ChatColor.RED + "既にイベントへ参加しています", config.isDebugFlag( Utility.consoleMode.normal ) );
                ExecOtherCommand( player, player.getDisplayName() + " さんは、既にイベントに参加しています" );
                return false;
            case 2: //  Kick registration.
                Utility.Prt( player, ChatColor.RED + "イベントへの参加は拒否されています", config.isDebugFlag( Utility.consoleMode.normal ) );
                //  ExecOtherCommand( player, player.getDisplayName() + " さんは、イベントに参加できませんでした" );
                return false;
            default: // Registration success.
                Bukkit.getServer().getConsoleSender().sendMessage( ChatColor.AQUA + "Registration success." );
                Utility.Prt( player, config.GetJoinMessage(), config.isDebugFlag( Utility.consoleMode.normal ) );
                ExecOtherCommand( player, player.getDisplayName() + " さんが、イベントに参加しました" );
                break;
        }
        return pc.get( player.getUniqueId() ).JoinPlayer( player );
    }

    /**
     * イベント参加者がイベントアイテムを受け取る時の処理
     *
     * @param player
     * @param Item
     * @return
     */
    public boolean GetEventItem( Player player, String Item ) {
        if ( pc.get( player.getUniqueId() ).getEntry() == 1 ) {
            if ( config.getTools().contains( Item ) ) {
                pc.get( player.getUniqueId() ).itemget( player, Material.getMaterial( Item ) );
            } else Utility.Prt( player, ChatColor.RED + "再配布対象のツールではありません", config.isDebugFlag( Utility.consoleMode.normal ) );
        } else Utility.Prt( player, ChatColor.RED + "イベント参加者のみです", config.isDebugFlag( Utility.consoleMode.normal ) );
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

                Utility.Prt( player, ChatColor.RED + "Look other Player : " + Other, true );

                OfflinePlayer op = Bukkit.getServer().getOfflinePlayer( Other );
                if ( op.hasPlayedBefore() ) {
                    uuid = op.getUniqueId();
                    pc.put( uuid, new PlayerControl( ( Plugin ) this, config ) );
                    pc.get( uuid ).setDisplayName( op.getName() );
                    pc.get( uuid ).setUUID( op.getUniqueId() );
                    pc.get( uuid ).load();
                } else {
                    Utility.Prt( player, ChatColor.RED + "This Player is not joined to server.", config.isDebugFlag( Utility.consoleMode.normal ) );
                    return false;
                }
                pc.get( uuid ).getStatus( player );
                if ( player.getUniqueId() != uuid ) pc.remove( uuid );
            }
            return true;
        } else {
            Utility.Prt( player, ChatColor.RED + "イベントに参加していません", config.isDebugFlag( Utility.consoleMode.normal ) );
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
            Utility.Prt( player, ChatColor.RED + "指定された値が正しくありません", config.isDebugFlag( Utility.consoleMode.full ) );
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
                    pc.put( scorePlayer.getUniqueId(), new PlayerControl( ( Plugin ) this, config ) );
                    pc.get( scorePlayer.getUniqueId() ).setDisplayName( op.getName() );
                    pc.get( scorePlayer.getUniqueId() ).setUUID( op.getUniqueId() );
                    pc.get( scorePlayer.getUniqueId() ).load();
                    createStat = true;
                } else {
                    Utility.Prt( player, ChatColor.RED + "[ " + ChatColor.YELLOW + name + ChatColor.RED + " ] はサーバーに存在しません", config.isDebugFlag( Utility.consoleMode.normal ) );
                    return false;
                }
            }
        }

        if ( pc.get( scorePlayer.getUniqueId() ).getEntry() == 1 ) {
            pc.get( scorePlayer.getUniqueId() ).addScore( scoreNum );
            pc.get( scorePlayer.getUniqueId() ).save();
            retStat = true;
        } else {
            Utility.Prt( player, ChatColor.RED + "[ " + ChatColor.YELLOW + name + ChatColor.RED + " ] はイベントに参加していません", config.isDebugFlag( Utility.consoleMode.normal ) );
            retStat = false;
        }

        if ( createStat ) pc.remove( scorePlayer.getUniqueId() );
        return retStat;
    }
}
