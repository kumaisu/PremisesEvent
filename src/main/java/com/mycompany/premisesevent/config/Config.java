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
import com.mycompany.premisesevent.tool.Tools;

/*
 *
 * @author sugichan
 */
public class Config {

    private final Plugin plugin;
    private FileConfiguration config = null;

    public static enum EventMode { Easy, Normal, Hard }

    private final Map< String, Integer > GetPoint = new HashMap<>();
    private boolean OPMode;
    private double Repair;
    private boolean Field;
    private String Event_World;
    private int Event_X1;
    private int Event_X2;
    private int Event_Y1;
    private int Event_Y2;
    private int Event_Z1;
    private int Event_Z2;

    public static EventMode difficulty;
    public static boolean FreePlace;
    public static boolean EventPlace;
    public static boolean FreeBreak;
    public static boolean ToolBreak;
    public static boolean zeroPlace;
    public static boolean titlePrint;
    public static String EventName;
    public static String JoinMessage;
    public static String EventToolName;
    public static int RePresent;
    public static int UpCost;
    public static int ScoreNotice;
    public static int ScoreBroadcast;
    public static List< String > bc_command;
    public static List< String > stones;
    public static List< String > tools;
    public static Utility.consoleMode DebugFlag;

    /**
     * 設定読み込みライブラリ
     *
     * @param plugin
     */
    public Config( Plugin plugin ) {
        stones = new ArrayList<>();
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
        FreePlace = config.getBoolean( "FreePlace" );
        EventPlace = config.getBoolean( "EventPlace" );
        FreeBreak = config.getBoolean( "FreeBreak" );
        ToolBreak = config.getBoolean( "ToolBreak" );
        zeroPlace = config.getBoolean( "ZeroPlace" );

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
        } catch ( IllegalArgumentException e ) {
            Tools.Prt( ChatColor.RED + "Config Debugモードの指定値が不正なので、normal設定にしました", Utility.consoleMode.none );
            DebugFlag = Utility.consoleMode.normal;
        }
        try {
            difficulty = EventMode.valueOf( config.getString( "Difficulty" ) );
        } catch ( IllegalArgumentException e ) {
            Tools.Prt( ChatColor.RED + "Config Event 難易度が不正なので、Normal 設定にしました", Utility.consoleMode.none );
            difficulty = EventMode.Normal;
        }
    }

    /**
     * 設定内容を表示する
     *
     * @param p
     */
    public void Status( Player p ) {
        Utility.consoleMode consolePrintFlag = ( ( p == null ) ? Utility.consoleMode.none:Utility.consoleMode.max );
        Tools.Prt( p, ChatColor.GREEN + "=== Premises Status ===", consolePrintFlag );
        Tools.Prt( p, ChatColor.WHITE + "Degub Mode : " + ChatColor.YELLOW + DebugFlag.toString(), consolePrintFlag );
        Tools.Prt( p, ChatColor.WHITE + "イベント名       : " + ChatColor.YELLOW + EventName, consolePrintFlag );
        Tools.Prt( p, ChatColor.WHITE + "難易度           : " + ChatColor.YELLOW + difficulty.toString(), consolePrintFlag );
        Tools.Prt( p, ChatColor.WHITE + "ツール再取得Cost : " + ChatColor.YELLOW + RePresent, consolePrintFlag );
        Tools.Prt( p, ChatColor.WHITE + "ツール更新Cost   : " + ChatColor.YELLOW + UpCost, consolePrintFlag );
        Tools.Prt( p, ChatColor.WHITE + "耐久度警告値     : " + ChatColor.YELLOW + Repair, consolePrintFlag );
        Tools.Prt( p, ChatColor.WHITE + "参加者以外の掘削 : " + ChatColor.YELLOW + ( FreeBreak ? "許可":"不可" ), consolePrintFlag );
        Tools.Prt( p, ChatColor.WHITE + "一般Toolでの掘削 : " + ChatColor.YELLOW + ( ToolBreak ? "不可":"許可" ), consolePrintFlag );
        Tools.Prt( p, ChatColor.WHITE + "ブロック無限設置 : " + ChatColor.YELLOW + ( zeroPlace ? "許可":"不可" ), consolePrintFlag );
        Tools.Prt( p, ChatColor.WHITE + "CreativeでCount  : " + ChatColor.YELLOW + ( OPMode ? "しない":"する" ), consolePrintFlag );
        Tools.Prt( p, ChatColor.WHITE + "参加者以外の設置 : " + ChatColor.YELLOW + ( FreePlace ? "許可":"不可" ), consolePrintFlag );
        Tools.Prt( p, ChatColor.WHITE + "指定以外の設置   : " + ChatColor.YELLOW + ( EventPlace ? "許可":"不可" ), consolePrintFlag );
        Tools.Prt( p, ChatColor.WHITE + "タイトル表示     : " + ChatColor.YELLOW + ( titlePrint ? "する":"しない" ), consolePrintFlag );
        Tools.Prt( p, ChatColor.WHITE + "イベントツール名 : " + EventToolName, consolePrintFlag );

        for( int i = 0; i<tools.size(); i++ ) {
            Tools.Prt( p, ChatColor.WHITE + "Tools (" + i + ") : " + ChatColor.YELLOW + tools.get( i ), consolePrintFlag );
        }

        Tools.Prt( p, ChatColor.WHITE + "掘削範囲指定 : " + ChatColor.YELLOW + ( Field ? "あり":"なし" ), consolePrintFlag );
        if ( Field ) {
            Tools.Prt( p, ChatColor.WHITE + "Check World: " + ChatColor.YELLOW + Event_World, consolePrintFlag );
            Tools.Prt( p,
                ChatColor.WHITE + "Area1 X=" + ChatColor.YELLOW + String.format( "%-7d", Event_X1 ) +
                ChatColor.WHITE + ",Y=" + ChatColor.YELLOW + String.format( "%-3d",Event_Y1 ) +
                ChatColor.WHITE + ",Z=" + ChatColor.YELLOW + Event_Z1,
                consolePrintFlag );
            Tools.Prt( p,
                ChatColor.WHITE + "Area2 X=" + ChatColor.YELLOW + String.format( "%-7d", Event_X2 ) +
                ChatColor.WHITE + ",Y=" + ChatColor.YELLOW + String.format( "%-3d", Event_Y2 ) +
                ChatColor.WHITE + ",Z=" + ChatColor.YELLOW + Event_Z2,
                consolePrintFlag );
        }

        Tools.Prt( p, ChatColor.WHITE + "参加時メッセージ : " + JoinMessage, consolePrintFlag );

        Tools.Prt( p, ChatColor.WHITE + "Broadcast Command:", consolePrintFlag );
        for( int i = 0; i<bc_command.size(); i++ ) {
            Tools.Prt( p, ChatColor.WHITE + String.valueOf( i ) + ") : " + ChatColor.YELLOW + bc_command.get( i ), consolePrintFlag );
        }

        Tools.Prt( p, ChatColor.GREEN + "=======================", consolePrintFlag );
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
}
