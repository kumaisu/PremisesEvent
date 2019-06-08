/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.premisesevent.Player;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import com.mycompany.kumaisulibraries.Utility;
import com.mycompany.premisesevent.Item.ItemControl;
import com.mycompany.premisesevent.config.Config;
import com.mycompany.premisesevent.tool.Tools;

/**
 *
 * @author sugichan
 */
public class PlayerControl {
    private final String DataFolder;
    
    /*
    スコアボードコントロール
    */
    private final String OBJECTIVE_NAME = "Premises";
    private final Scoreboard board = Bukkit.getServer().getScoreboardManager().getNewScoreboard();
    private final Objective obj = board.registerNewObjective( OBJECTIVE_NAME, "dummy" );
    private Score score;
    private final Map<String, Score> mines = new HashMap<>();

    /*
    プレイヤーデータ
    */
    private UUID uuid;
    private String DisplayName;
    private int EntryFlag = 0;
    private String FirstDate;
    private int PlayerScore;
    private boolean PresentFlag;
    private boolean UpdateFlag;
    private final Map<String,Integer> BlockCount = new HashMap<>();
    private int scoreNotice;
    private int scoreBroadcast;

    /**
     * プレイヤーコントロールライブラリ
     *
     * @param player
     * @param DF
     */
    public PlayerControl( OfflinePlayer player, String DF ) {
        this.DisplayName = player.getName();
        this.uuid = player.getUniqueId();
        this.PlayerScore = 0;
        this.DataFolder = DF;
    }

    /**
     * UUID セット
     *
     * @param setuuid
     */
    public void setUUID( UUID setuuid ) {
        uuid = setuuid;
    }

    /**
     * プレイヤー名セット
     *
     * @param name
     */
    public void setDisplayName( String name ) {
        DisplayName = name;
    }

    /**
     * プレイヤー名取得
     *
     * @return
     */
    public String getDisplayName() {
        return DisplayName;
    }

    /**
     * スコアボードへの表示登録
     *
     * @param player
     */
    public void ScoreBoardEntry( Player player ) {
        obj.setDisplayName( "Mining Count" );
        obj.setDisplaySlot( DisplaySlot.SIDEBAR );
        player.setScoreboard( board );
        score = obj.getScore( Utility.StringBuild( ChatColor.YELLOW.toString(), "Score:" ) );
    }

    /**
     * 参加プレイヤーの情報をロードします
     *
     * @return
     */
    public boolean load() {
        // 設定ファイルを保存
        File dataFolder = new File( DataFolder + File.separator + Config.EventName + File.separator + "users" );
        File UKfile = new File( dataFolder, uuid + ".yml" );
        FileConfiguration UKData = YamlConfiguration.loadConfiguration( UKfile );

        if( !UKfile.exists() ) { return false; }

        EntryFlag = UKData.getInt( "Entry", 1 );
        FirstDate = UKData.getString( "Joined" );
        PlayerScore = UKData.getInt( "Score" );
        PresentFlag = UKData.getBoolean( "Present" );
        UpdateFlag = UKData.getBoolean( "Update" );
        scoreNotice = UKData.getInt( "Notice", 0 );
        scoreBroadcast = UKData.getInt( "Broadcast", 0 );

        if ( UKData.contains( "Counter" ) ) {
            UKData.getConfigurationSection( "Counter" ).getKeys( false ).forEach( ( key ) -> {
                BlockCount.put( key, UKData.getInt( "Counter." + key ) );
            } );
        }

        return true;
    }

    /**
     * 参加プレイヤーの情報を保存します
     *
     */
    public void save() {
        File dataFolder = new File( DataFolder + File.separator + Config.EventName + File.separator + "users" );
        if( !dataFolder.exists() ) { dataFolder.mkdir(); }

        File UKfile = new File( dataFolder, uuid + ".yml" );
        FileConfiguration UKData = YamlConfiguration.loadConfiguration( UKfile );

        UKData.set( "Name", Bukkit.getOfflinePlayer( uuid ).getName() );
        UKData.set( "Entry", EntryFlag );
        UKData.set( "Joined", FirstDate );
        UKData.set( "Score", PlayerScore );
        UKData.set( "Notice", scoreNotice );
        UKData.set( "Broadcast", scoreBroadcast );
        UKData.set( "Present", false );
        UKData.set( "Update", false );

        BlockCount.entrySet().forEach( ( entry ) -> {
            UKData.set( "Counter." + entry.getKey(), entry.getValue() );
        } );

        try {
            UKData.save( UKfile );
            EntryFlag = 1;
        }
        catch ( IOException e ) {
            Tools.Prt( ChatColor.RED + "Could not save UnknownIP File." );
        }
    }

    /**
     * プレイヤーの参加処理をします
     *
     * @param p
     * @return
     */
    public boolean JoinPlayer( Player p ) {
        if ( !Arrays.asList( p.getInventory().getStorageContents() ).contains( null ) ) {
            Tools.Prt( p, ChatColor.RED + "参加アイテム配布用のためインベントリに空きが必要です", Utility.consoleMode.normal );
            return false;
        }

        switch ( getEntry() ) {
            case 1: //  Double registration failure.
                Tools.Prt( p, ChatColor.RED + "既にイベントへ参加しています", Utility.consoleMode.normal );
                Tools.ExecOtherCommand( p, p.getDisplayName() + " さんは、既にイベントに参加しています" );
                return false;
            case 2: //  Kick registration.
                Tools.Prt( p, ChatColor.RED + "イベントへの参加は拒否されています", Utility.consoleMode.normal );
                //  ExecOtherCommand( player, player.getDisplayName() + " さんは、イベントに参加できませんでした" );
                return false;
            default: // Registration success.
                Bukkit.getServer().getConsoleSender().sendMessage( ChatColor.AQUA + "Registration success." );
                Tools.Prt( p, Config.JoinMessage, Utility.consoleMode.normal );
                Tools.ExecOtherCommand( p, p.getDisplayName() + " さんが、イベントに参加しました" );
                break;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        FirstDate = sdf.format( new Date() );
        EntryFlag = 1;
        save();
        ScoreBoardEntry( p );
        Tools.Prt( p, ChatColor.AQUA + "Joined Date was " + ChatColor.WHITE + FirstDate, Utility.consoleMode.normal );

        ItemControl ic = new ItemControl();
        ic.ItemPresent( p );
        for( int i = 0; i<Config.tools.size(); i++ ) {
            Tools.Prt( ChatColor.GREEN + "Config Tool Name : " + Config.tools.get( i ) );
            ic.ItemUpdate( p, null, Config.EventToolName, Material.getMaterial( Config.tools.get( i ) ) );
        }

        Bukkit.broadcastMessage( "<Premises> " + ChatColor.WHITE + p.getDisplayName() + ChatColor.GREEN + "さんが、イベントに参加しました" );
        return true;
    }

    /**
     * イベントツールの再取得処理
     *
     * @param player
     * @param Item
     * @return
     */
    public boolean getEventItem( Player player, String Item ) {
        if ( getEntry() != 1 ) {
            Tools.Prt( player, ChatColor.RED + "イベント参加者のみです", Utility.consoleMode.normal );
            return false;
        }
    
        if ( !Arrays.asList( player.getInventory().getStorageContents() ).contains( null ) ) {
            Tools.Prt( player, ChatColor.RED + "アイテム配布用のためインベントリに空きが必要です", Utility.consoleMode.normal );
            return false;
        }

        if ( !Config.tools.contains( Item ) ) {
            Tools.Prt( player, ChatColor.RED + "再配布対象のツールではありません", Utility.consoleMode.normal );
            return false;
        }

        int Rep = Config.RePresent;
        if ( getScore() > Rep ) {
            ItemControl ic = new ItemControl();
            ic.ItemUpdate( player, null, Config.EventToolName, Material.getMaterial( Item ) );
            addScore( null, - Rep );
            Tools.Prt( ChatColor.GOLD + player.getDisplayName() + " Redistributing " + Material.getMaterial( Item ).name() + " tools !!", Utility.consoleMode.normal );
            return true;
        } else {
            Tools.Prt( player, ChatColor.RED + "Scoreが足りないので配布できません", Utility.consoleMode.normal );
            return false;
        }
    }

    /**
     * イベントツールのアップデート処理
     *
     * @param player
     * @param Force
     */
    public void ToolUpdate( Player player, boolean Force ) {

        if ( player.getInventory().getItemInMainHand().getType() == Material.AIR ) {
            Tools.Prt( player, ChatColor.RED + "アップデートするアイテムを持ってください", Utility.consoleMode.full );
            return;
        }

        int Rep = Config.UpCost;
        if ( getScore() > Rep ) {
            ItemStack item = player.getInventory().getItemInMainHand();

            if ( item.getItemMeta().hasDisplayName() ) {
                if ( item.getItemMeta().getDisplayName().equalsIgnoreCase( Config.EventToolName ) ) {
                    double CheckDurability = ( item.getType().getMaxDurability() * 0.9 );
                    if ( ( CheckDurability <= item.getDurability() ) || Force ) {
                        ItemControl ic = new ItemControl();
                        player.getInventory().setItemInMainHand( null );
                        ic.ItemUpdate( player, item, Config.EventToolName, null );
                        addScore( null, -Rep );
                    } else {
                        Tools.Prt( player,
                            ChatColor.YELLOW + "ツール耐久値は " +
                            ChatColor.WHITE + ( item.getType().getMaxDurability() - item.getDurability() ) +
                            ChatColor.YELLOW + " なので " +
                            ChatColor.WHITE + ( (int) ( item.getType().getMaxDurability() - CheckDurability ) ) +
                            ChatColor.YELLOW + " 以下にしてね",
                            Utility.consoleMode.full
                        );
                    }
                } else Tools.Prt( player, ChatColor.YELLOW + "ツール名が違います", Utility.consoleMode.full );
            } else Tools.Prt( player, ChatColor.YELLOW + "イベント用のツールではありません", Utility.consoleMode.full );
        } else Tools.Prt( player, ChatColor.RED + "Scoreが足りないのでアップデートできません", Utility.consoleMode.full );
    }

    /**
     * プレイヤーの参加状態を取得します
     *
     * @return
     */
    public int getEntry() {
        return EntryFlag;
    }

    /**
     * プレイヤーの参加した日を取得します
     *
     * @return
     */
    public String getJoinDate() {
        return FirstDate;
    }

    /**
     * プレイヤーのトータルスコアを取得します
     *
     * @return
     */
    public int getScore() {
        return PlayerScore;
    }

    /**
     * プレイヤーに特定スコアを付与または剥奪します
     *
     * @param player
     * @param amount
     */
    public void addScore( Player player, int amount ) {
        PlayerScore += amount;

        // プレイヤーのスコアーを更新し反映します
        score.setScore( (int)PlayerScore );

        if ( player != null ) {
            //  デバッグ（または保守）用、一定数到達記録をコンソールログに残す
            //  不具合や他責によるスコアの未記録時の対応ログとして表示
            if ( Config.ScoreNotice > 0 ) {
                if ( PlayerScore >= scoreNotice ) {
                    Tools.Prt( "[Premises] " + DisplayName + " reached " + PlayerScore + " points.", Utility.consoleMode.normal );
                    scoreNotice = Config.ScoreNotice * ( ( int ) Math.floor( PlayerScore / Config.ScoreNotice ) + 1 );
                }
            }

            //  ブロードキャスト、一定スコア達成をオンラインプレイヤーに知らせる
            if ( ( player.hasPermission( "Premises.broadcast" ) ) && ( Config.ScoreBroadcast > 0 ) ) {
                if ( PlayerScore >= scoreBroadcast ) {
                    String SendMessage = "<イベント> " + ChatColor.AQUA + DisplayName + ChatColor.WHITE + " さんが " + ChatColor.YELLOW + PlayerScore + ChatColor.WHITE + " 点に到達しました";
                    Bukkit.broadcastMessage( SendMessage );
                    Tools.launchFireWorks( player.getLocation() );
                    Tools.ExecOtherCommand( player, SendMessage );
                    scoreBroadcast = Config.ScoreBroadcast * ( ( int ) Math.floor( PlayerScore / Config.ScoreBroadcast ) + 1 );
                }
            }
        }
    }

    /**
     * 指定ブロックの掘削数を取得します
     *
     * @param StoneName
     * @return
     */
    public int getStoneCount( String StoneName ) {
        int CD;
        try {
            CD = BlockCount.get( StoneName );
        } catch( Exception e ) {
            Bukkit.getServer().getConsoleSender().sendMessage( Utility.StringBuild( ChatColor.RED.toString(), "No Data for [", StoneName, "]" ) );
            CD = 0;
        }
        return CD;
    }

    /**
     * 指定ブロックの掘削数を加算します
     *
     * @param StoneName
     * @param toScore
     */
    public void addStoneCount( String StoneName, boolean toScore ) {
        int CD = getStoneCount( StoneName );
        CD++;
        BlockCount.put( StoneName, CD );
        if ( toScore ) {
            // プレイヤーの掘削数を更新し反映します
            mines.put( StoneName, obj.getScore( Utility.StringBuild( ChatColor.RED.toString(), StoneName + ":" ) ) );
            mines.get( StoneName ).setScore( CD );
        }
    }

    /**
     * 指定ブロックの掘削数を減算します
     *
     * @param StoneName
     * @param toScore
     */
    public void subStoneCount( String StoneName, boolean toScore ) {
        int CD = getStoneCount( StoneName );
        CD--;
        BlockCount.put( StoneName, CD );
        if ( toScore ) {
            // プレイヤーの掘削数を更新し反映します
            mines.put( StoneName, obj.getScore( Utility.StringBuild( ChatColor.RED.toString(), StoneName + ":" ) ) );
            mines.get( StoneName ).setScore( CD );
        }
    }

    /**
     * 参加プレイヤーのスコア状況を表示します
     *
     * @param p
     */
    public void getStatus( Player p ) {
        Tools.Prt( null, ChatColor.RED + "Look Status: " + DisplayName, Utility.consoleMode.normal );
        Tools.Prt( p, ChatColor.GREEN + "--------------------------------------------------", Utility.consoleMode.max );
        Tools.Prt( p, ChatColor.AQUA + "Block mined by: " + DisplayName, Utility.consoleMode.max );
        Tools.Prt( p, ChatColor.GOLD + "SCORE: " + ChatColor.WHITE + getScore(), Utility.consoleMode.max );

        if ( p.isOp() ) {
            Tools.Prt( p, ChatColor.GOLD + "Notice   : " + ChatColor.WHITE + scoreNotice, Utility.consoleMode.max );
            Tools.Prt( p, ChatColor.GOLD + "Broadcast: " + ChatColor.WHITE + scoreBroadcast, Utility.consoleMode.max );
        }
        
        BlockCount.entrySet().forEach( ( entry ) -> {
            Tools.Prt( p, ChatColor.GREEN + entry.getKey() + ": " + ChatColor.YELLOW + entry.getValue(), Utility.consoleMode.max );
        } );

        Tools.Prt( p, ChatColor.GREEN + "--------------------------------------------------", Utility.consoleMode.max );
    }

    /**
     * イベント装備の再プレゼントフラグを取得します
     *
     * @return
     */
    public boolean getPresentFlag() {
        return PresentFlag;
    }

    /**
     * イベントツールの再プレゼントフラグを取得します
     *
     * @return
     */
    public boolean getUpdateFlag() {
        return UpdateFlag;
    }
}
