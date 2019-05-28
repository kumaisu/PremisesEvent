/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.premisesevent;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import com.mycompany.kumaisulibraries.Utility;
import com.mycompany.premisesevent.config.Config;

/**
 *
 * @author sugichan
 */
public class PlayerControl {

    private final Plugin plugin;
    private final Config config;

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

    /**
     * プレイヤーコントロールライブラリ
     *
     * @param plugin
     * @param config
     */
    public PlayerControl( Plugin plugin, Config config ) {
        this.plugin = plugin;
        this.PlayerScore = 0;
        this.config = config;
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
        File dataFolder = new File( plugin.getDataFolder() + File.separator + config.getEventName() + File.separator + "users" );
        File UKfile = new File( dataFolder, uuid + ".yml" );
        FileConfiguration UKData = YamlConfiguration.loadConfiguration( UKfile );

        if( !UKfile.exists() ) { return false; }

        EntryFlag = UKData.getInt( "Entry", 1 );
        FirstDate = UKData.getString( "Joined" );
        PlayerScore = UKData.getInt( "Score" );
        PresentFlag = UKData.getBoolean( "Present" );
        UpdateFlag = UKData.getBoolean( "Update" );

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
        File dataFolder = new File( plugin.getDataFolder() + File.separator + config.getEventName() + File.separator + "users" );
        if( !dataFolder.exists() ) { dataFolder.mkdir(); }

        File UKfile = new File( dataFolder, uuid + ".yml" );
        FileConfiguration UKData = YamlConfiguration.loadConfiguration( UKfile );

        UKData.set( "Name", Bukkit.getOfflinePlayer( uuid ).getName() );
        UKData.set( "Entry", EntryFlag );
        UKData.set( "Joined", FirstDate );
        UKData.set( "Score", PlayerScore );
        UKData.set( "Present", false );
        UKData.set( "Update", false );

        BlockCount.entrySet().forEach( ( entry ) -> {
            UKData.set( "Counter." + entry.getKey(), entry.getValue() );
        } );

        try {
            UKData.save( UKfile );
            EntryFlag = 1;
        }
        catch (IOException e) {
            plugin.getServer().getLogger().log( Level.WARNING, "{0}Could not save UnknownIP File.", ChatColor.RED );
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
            Utility.Prt( p, ChatColor.RED + "参加アイテム配布用のためインベントリに空きが必要です", config.isDebugFlag( Utility.consoleMode.normal ) );
            return false;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        FirstDate = sdf.format( new Date() );
        EntryFlag = 1;
        save();
        ScoreBoardEntry( p );
        Utility.Prt( p, ChatColor.AQUA + "Joined Date was " + ChatColor.WHITE + FirstDate, config.isDebugFlag( Utility.consoleMode.normal ) );

        ItemControl ic = new ItemControl( plugin, config );
        ic.ItemPresent( p );
        for( int i = 0; i<config.getTools().size(); i++ ) {
            //  Bukkit.getServer().getConsoleSender().sendMessage( ChatColor.GREEN + "Config Tool Name : " + config.getTools().get( i ) );
            ic.ItemUpdate( p, null, config.getEventToolName(), Material.getMaterial( config.getTools().get( i ).toString() ) );
        }

        Bukkit.broadcastMessage( "<Premises> " + ChatColor.WHITE + p.getDisplayName() + ChatColor.GREEN + "さんが、イベントに参加しました" );
        return true;
    }

    /**
     * イベントツールの再取得処理
     *
     * @param player
     * @param Tool
     * @return
     */
    public boolean itemget( Player player, Material Tool ) {
        int Rep = config.getRePresent();
        if ( getScore() > Rep ) {
            ItemControl ic = new ItemControl( plugin, config );
            ic.ItemUpdate( player, null, config.getEventToolName(), Tool );
            addScore( -Rep );
            Utility.Prt( null, ChatColor.GOLD + player.getDisplayName() + " Redistributing " + Tool.name() + " update tools !!", config.isDebugFlag( Utility.consoleMode.normal ) );
            return true;
        } else {
            Utility.Prt( player, ChatColor.RED + "Scoreが足りないので配布できません", config.isDebugFlag( Utility.consoleMode.normal ) );
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
            Utility.Prt( player, ChatColor.RED + "アップデートするアイテムを持ってください", config.isDebugFlag( Utility.consoleMode.full ) );
            return;
        }

        int Rep = config.getUpCost();
        if ( getScore() > Rep ) {
            ItemStack item = player.getInventory().getItemInMainHand();

            if ( item.getItemMeta().hasDisplayName() ) {
                if ( item.getItemMeta().getDisplayName().equalsIgnoreCase( config.getEventToolName() ) ) {
                    double CheckDurability = ( item.getType().getMaxDurability() * 0.9 );
                    if ( ( CheckDurability <= item.getDurability() ) || Force ) {
                        ItemControl ic = new ItemControl( plugin, config );
                        player.getInventory().setItemInMainHand( null );
                        ic.ItemUpdate( player, item, config.getEventToolName(), null );
                        addScore( -Rep );
                    } else {
                        Utility.Prt( player,
                            ChatColor.YELLOW + "ツール耐久値は " +
                            ChatColor.WHITE + ( item.getType().getMaxDurability() - item.getDurability() ) +
                            ChatColor.YELLOW + " なので " +
                            ChatColor.WHITE + ( (int) ( item.getType().getMaxDurability() - CheckDurability ) ) +
                            ChatColor.YELLOW + " 以下にしてね",
                            config.isDebugFlag( Utility.consoleMode.full )
                        );
                    }
                } else Utility.Prt( player, ChatColor.YELLOW + "ツール名が違います", config.isDebugFlag( Utility.consoleMode.full ) );
            } else Utility.Prt( player, ChatColor.YELLOW + "イベント用のツールではありません", config.isDebugFlag( Utility.consoleMode.full ) );
        } else Utility.Prt( player, ChatColor.RED + "Scoreが足りないのでアップデートできません", config.isDebugFlag( Utility.consoleMode.full ) );
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
     * @param amount
     */
    public void addScore( int amount ) {
        PlayerScore += amount;

        // プレイヤーのスコアーを更新し反映します
        score.setScore( PlayerScore );
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
     */
    public void addStoneCount( String StoneName ) {
        int CD = getStoneCount( StoneName );
        CD++;
        BlockCount.put( StoneName, CD );
        // プレイヤーの掘削数を更新し反映します
        mines.put( StoneName, obj.getScore( Utility.StringBuild( ChatColor.GREEN.toString(), StoneName + ":" ) ) );
        mines.get( StoneName ).setScore( CD );
    }

    /**
     * 参加プレイヤーのスコア状況を表示します
     *
     * @param p
     */
    public void getStatus( Player p ) {
        Utility.Prt( null, ChatColor.RED + "Look Status: " + DisplayName, config.isDebugFlag( Utility.consoleMode.normal ) );
        Utility.Prt( p, ChatColor.GREEN + "--------------------------------------------------", config.isDebugFlag( Utility.consoleMode.max ) );
        Utility.Prt( p, ChatColor.AQUA + "Block mined by: " + DisplayName, config.isDebugFlag( Utility.consoleMode.max ) );
        Utility.Prt( p, ChatColor.GOLD + "SCORE: " + ChatColor.WHITE + getScore(), config.isDebugFlag( Utility.consoleMode.max ) );

        BlockCount.entrySet().forEach( ( entry ) -> {
            Utility.Prt( p, ChatColor.GREEN + entry.getKey() + ": " + ChatColor.YELLOW + entry.getValue(), config.isDebugFlag( Utility.consoleMode.max ) );
        } );

        Utility.Prt( p, ChatColor.GREEN + "--------------------------------------------------", config.isDebugFlag( Utility.consoleMode.max ) );
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
