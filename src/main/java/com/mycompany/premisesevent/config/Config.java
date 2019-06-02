/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.premisesevent.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import com.mycompany.kumaisulibraries.Utility;
import com.mycompany.kumaisulibraries.Minecraft;

/*
 *
 * @author sugichan
 */
public class Config {

    private final Plugin plugin;
    private FileConfiguration config = null;

    private final Map< String, Integer > GetPoint = new HashMap<>();
    private List< String > stones;
    private List< String > tools;
    private List< String > bc_command;
    private boolean OPMode;
    private int ScoreNotice;
    private int ScoreBroadcast;
    private int RePresent;
    private int UpCost;
    private double Repair;
    private boolean Field;
    private String EventToolName;
    private boolean FreeBreak;
    private boolean ToolBreak;
    private String EventName;
    private String Event_World;
    private int Event_X1;
    private int Event_X2;
    private int Event_Y1;
    private int Event_Y2;
    private int Event_Z1;
    private int Event_Z2;
    private String JoinMessage;
    private boolean titlePrint;
    private static Utility.consoleMode DebugFlag;

    /**
     * 設定読み込みライブラリ
     *
     * @param plugin
     */
    public Config( Plugin plugin ) {
        this.stones = new ArrayList<>();
        this.plugin = plugin;
        plugin.getLogger().info( "Config Loading now..." );
        load();
    }

    /**
     * 設定をロードします
     *
     */
    public void load() {
        // 設定ファイルを保存
        plugin.saveDefaultConfig();
        if (config != null) { // configが非null == リロードで呼び出された
            plugin.getLogger().info( "Config Reloading now..." );
            plugin.reloadConfig();
        }
        config = plugin.getConfig();

        stones = new ArrayList<>();
        List< String > getstr = ( List< String > ) config.getList( "PointStone" );
        for( int i = 0; i<getstr.size(); i++ ) {
            String[] param = getstr.get( i ).split(",");
            GetPoint.put( param[0], Integer.valueOf( param[1] ) );
            stones.add( param[0] );
        }

        tools = ( List< String > )config.getList( "Present" );

        EventName = config.getString( "EventName" );
        Field = config.getBoolean( "Field" );
        ScoreNotice = config.getInt( "ScoreNotice" );
        ScoreBroadcast = config.getInt( "ScoreBroadcast" );
        bc_command = ( List< String > )config.getList( "BroadcastCommand" );
        RePresent = config.getInt( "RePresent" );
        UpCost = config.getInt( "UpdateCost" );
        Repair = 1 - config.getDouble( "Repair" );
        titlePrint  = config.getBoolean( "sendTitle" );
        OPMode = config.getBoolean( "CreativeCount" );
        EventToolName = config.getString( "EventToolName" );
        FreeBreak = config.getBoolean( "FreeBreak" );
        ToolBreak = config.getBoolean( "ToolBreak" );

        Event_World = config.getString( "World" );

        String pos = config.getString( "AreaPos1" );
        String[] pos_param = pos.split( "," );
        Event_X1 = Integer.parseInt( pos_param[0] );
        Event_Y1 = Integer.parseInt( pos_param[1] );
        Event_Z1 = Integer.parseInt( pos_param[2] );

        pos = config.getString( "AreaPos2" );
        String[] pos_param2 = pos.split( "," );
        Event_X2 = Integer.parseInt( pos_param2[0] );
        Event_Y2 = Integer.parseInt( pos_param2[1] );
        Event_Z2 = Integer.parseInt( pos_param2[2] );

        if ( Event_X1>Event_X2 ) {
            int temp = Event_X1;
            Event_X1 = Event_X2;
            Event_X2 = temp;
        }

        if ( Event_Y1>Event_Y2 ) {
            int temp = Event_Y1;
            Event_Y1 = Event_Y2;
            Event_Y2 = temp;
        }

        if ( Event_Z1>Event_Z2 ) {
            int temp = Event_Z1;
            Event_Z1 = Event_Z2;
            Event_Z2 = temp;
        }

        JoinMessage = config.getString( "JOIN_MESSAGE" );

        try {
            DebugFlag = Utility.consoleMode.valueOf( config.getString( "Debug" ) );
        } catch( IllegalArgumentException e ) {
            Minecraft.Prt( null, ChatColor.RED + "Config Debugモードの指定値が不正なので、normal設定にしました", true );
            DebugFlag = Utility.consoleMode.normal;
        }
    }

    /**
     * 設定内容を表示する
     *
     * @param p
     */
    public void Status( Player p ) {
        boolean consolePrintFlag = ( p == null );
        Minecraft.Prt( p, ChatColor.GREEN + "=== Premises Status ===", consolePrintFlag );
        Minecraft.Prt( p, ChatColor.WHITE + "Degub Mode : " + ChatColor.YELLOW + DebugFlag.toString(), consolePrintFlag );
        Minecraft.Prt( p, ChatColor.WHITE + "イベント名       : " + ChatColor.YELLOW + EventName, consolePrintFlag );
        Minecraft.Prt( p, ChatColor.WHITE + "ツール再取得Cost : " + ChatColor.YELLOW + RePresent, consolePrintFlag );
        Minecraft.Prt( p, ChatColor.WHITE + "ツール更新Cost   : " + ChatColor.YELLOW + UpCost, consolePrintFlag );
        Minecraft.Prt( p, ChatColor.WHITE + "耐久度警告値     : " + ChatColor.YELLOW + Repair, consolePrintFlag );
        Minecraft.Prt( p, ChatColor.WHITE + "参加者以外の掘削 : " + ChatColor.YELLOW + ( FreeBreak ? "許可":"不可" ), consolePrintFlag );
        Minecraft.Prt( p, ChatColor.WHITE + "一般Toolでの掘削 : " + ChatColor.YELLOW + ( ToolBreak ? "不可":"許可" ), consolePrintFlag );
        Minecraft.Prt( p, ChatColor.WHITE + "CreativeでCount  : " + ChatColor.YELLOW + ( OPMode ? "しない":"する" ), consolePrintFlag );
        Minecraft.Prt( p, ChatColor.WHITE + "タイトル表示     : " + ChatColor.YELLOW + ( titlePrint ? "する":"しない" ), consolePrintFlag );
        Minecraft.Prt( p, ChatColor.WHITE + "イベントツール名 : " + EventToolName, consolePrintFlag );

        for( int i = 0; i<tools.size(); i++ ) {
            Minecraft.Prt( p, ChatColor.WHITE + "Tools (" + i + ") : " + ChatColor.YELLOW + tools.get( i ), consolePrintFlag );
        }

        Minecraft.Prt( p, ChatColor.WHITE + "掘削範囲指定 : " + ChatColor.YELLOW + ( Field ? "あり":"なし" ), consolePrintFlag );
        if ( Field ) {
            Minecraft.Prt( p, ChatColor.WHITE + "Check World: " + ChatColor.YELLOW + Event_World, consolePrintFlag );
            Minecraft.Prt( p,
                ChatColor.WHITE + "Area1 X=" + ChatColor.YELLOW + String.format( "%-7d", Event_X1 ) +
                ChatColor.WHITE + ",Y=" + ChatColor.YELLOW + String.format( "%-3d",Event_Y1 ) +
                ChatColor.WHITE + ",Z=" + ChatColor.YELLOW + Event_Z1,
                consolePrintFlag );
            Minecraft.Prt( p,
                ChatColor.WHITE + "Area2 X=" + ChatColor.YELLOW + String.format( "%-7d", Event_X2 ) +
                ChatColor.WHITE + ",Y=" + ChatColor.YELLOW + String.format( "%-3d", Event_Y2 ) +
                ChatColor.WHITE + ",Z=" + ChatColor.YELLOW + Event_Z2,
                consolePrintFlag );
        }
        Minecraft.Prt( p, ChatColor.WHITE + "Broadcast Command:", consolePrintFlag );
        for( int i = 0; i<bc_command.size(); i++ ) {
            Minecraft.Prt( p, ChatColor.WHITE + String.valueOf( i ) + ") : " + ChatColor.YELLOW + bc_command.get( i ), consolePrintFlag );
        }

        Minecraft.Prt( p, ChatColor.GREEN + "=======================", consolePrintFlag );
    }

    /**
     * 実行されているイベント名取得
     *
     * @return
     */
    public String getEventName() {
        return EventName;
    }

    /**
     * カウントされている石情報取得
     *
     * @return
     */
    public List getStones() {
        return stones;
    }

    /**
     * 石に設定されているポイントを取得
     *
     * @param sd    判定する石の名称（コードではない）
     * @return
     */
    public int getPoint( String sd ) {
        if ( stones.contains( sd ) ) {
            return GetPoint.get( sd );
        }
        return 0;
    }

    /**
     * イベントで配布されているツール名称取得
     *
     * @return
     */
    public String getEventToolName() {
        return EventToolName;
    }

    /**
     * ツール再取得の時に必要なスコア
     *
     * @return
     */
    public int getRePresent() {
        return RePresent;
    }

    /**
     * ツールのアップデートに必要なスコア
     *
     * @return
     */
    public int getUpCost() {
        return UpCost;
    }

    /**
     * ツールの耐久度警告を行うタイミング
     * 0.1 = 耐久度残り10%
     *
     * @return
     */
    public double getRepair() {
        return Repair;
    }

    /**
     * 掘削エリア指定
     * True:あり False:なし
     *
     * @return
     */
    public boolean GetField() {
        return Field;
    }

    /**
     * CreativeMode時のスコアカウント
     * True:する False:しない
     *
     * @return
     */
    public boolean CreativeCount() {
        return OPMode;
    }

    /**
     * 指定範囲内かの判定
     *
     * @param loc   現在位置
     * @return
     */
    public boolean CheckArea( Location loc ) {
        if ( !loc.getWorld().getName().equals( Event_World ) ) return false;
        return !( ( loc.getBlockX()<Event_X1 || loc.getBlockX()>Event_X2 ) || ( loc.getBlockY()<Event_Y1 || loc.getBlockY()>Event_Y2 ) || ( loc.getBlockZ()<Event_Z1 || loc.getBlockZ()>Event_Z2 ) );
    }

    /**
     * 一般掘削の許可フラグ
     * True:全プレイヤー False:参加者のみ
     *
     * @return
     */
    public boolean FreeBreak() {
        return FreeBreak;
    }

    /**
     * 指定ツールでの掘削可否
     * True:指定ツールのみ False:すべてOK
     *
     * @return
     */
    public boolean ToolBreak() {
        return ToolBreak;
    }

    /**
     * イベントツールの種類
     *
     * @return
     */
    public List getTools() {
        return tools;
    }

    /**
     * プレイヤーに対してのスコアーアナウンスの点数
     *
     * @return
     */
    public int getScoreNotice() {
        return ScoreNotice;
    }

    /**
     * スコアーアナウンスを何点で行うか
     *
     * @return
     */
    public int getScoreBroadcast() {
        return ScoreBroadcast;
    }

    /**
     * ブロードキャスト用コマンド
     * Discordなどに独自でメッセージを送信するときに利用
     *
     * @return
     */
    public List getBC_Command() {
        return bc_command;
    }

    /**
     * 5tick(0.25秒)ごとにTimerクラスのrunメソッドを実行してね
     * Timer 5tick×4回 = 1秒です
     *
     * @return
     */
    public long CoolTick() {
        return config.getLong( "CoolTick" );
    }

    /**
     * クールタイムの利用回数
     *
     * @return
     */
    public int CoolCount() {
        return config.getInt( "CoolCount" );
    }

    /**
     * 参加時に表示されるメッセージ
     *
     * @return
     */
    public String GetJoinMessage() {
        return JoinMessage;
    }

    /**
     * 警告メッセージをタイトル表示するかの可否
     *
     * @return 
     */
    public boolean getSendTitle() {
        return titlePrint;
    }

    /**
     * DebugMode を数値で受け取る
     *
     * @return 
     */
    public Utility.consoleMode getDebug() {
        return DebugFlag;
    }

    /**
     * 一時的にDebugModeを設定しなおす
     * ただし、Config.ymlには反映しない
     *
     * @param key 
     */
    public void setDebug( String key ) {
        try {
            DebugFlag = Utility.consoleMode.valueOf( key );
        } catch( IllegalArgumentException e ) {
            DebugFlag = Utility.consoleMode.none;
        }
    }

    /**
     * keyに対して、設定されているDebugMode下での可否判定を返す
     *
     * @param key
     * @return 
     */
    public boolean isDebugFlag( Utility.consoleMode key ) {
        return ( DebugFlag.ordinal() >= key.ordinal() );
    }
}
