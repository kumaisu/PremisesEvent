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
import com.mycompany.kumaisulibraries.Tools;
import com.mycompany.kumaisulibraries.Tools.consoleMode;

/*
 *
 * @author sugichan
 */
public class Config {

    public static String programCode = "PE";

    private final Plugin plugin;
    private FileConfiguration config = null;

    /**
     * イベント参加モード用のenum
     *
     * Easy : NG行為を事前にブロックする
     * Normal : NG行為を警告し、リカバリー可能
     * Hard : NG行為を警告しない
     */
    public static enum EventMode { Easy, Normal, Hard };
    public static enum UpperMode { None, Warning, Block };

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
    public static UpperMode UpperBlock;
    public static boolean placeFree;
    public static boolean placeSpecified;
    public static boolean breakFree;
    public static boolean breakTool;
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

    /**
     * 設定読み込みライブラリ
     *
     * @param plugin
     */
    public Config( Plugin plugin ) {
        stones = new ArrayList<>();
        this.plugin = plugin;
        Tools.entryDebugFlag( programCode, consoleMode.none );
        Tools.Prt( "Config Loading now...", programCode );
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
            Tools.Prt( "Config Reloading now...", programCode );
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
        Field = config.getBoolean( "Field", false );
        ScoreNotice = config.getInt( "ScoreNotice", 0 );
        ScoreBroadcast = config.getInt( "ScoreBroadcast", 0 );
        bc_command = ( List< String > )config.getList( "BroadcastCommand" );
        RePresent = config.getInt( "RePresent", 0 );
        UpCost = config.getInt( "UpdateCost", 0 );
        Repair = 1 - config.getDouble( "Repair", 0 );
        titlePrint  = config.getBoolean( "sendTitle", true );
        OPMode = config.getBoolean( "CreativeCount", true );
        EventToolName = config.getString( "EventToolName" );
        placeFree = config.getBoolean( "FreePlace", false );
        placeSpecified = config.getBoolean( "SpecifiedPlace",true );
        breakFree = config.getBoolean( "FreeBreak", true );
        breakTool = config.getBoolean( "ToolBreak", true );
        zeroPlace = config.getBoolean( "ZeroPlace", false );

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

        consoleMode DebugFlag;
        try {
            DebugFlag = consoleMode.valueOf( config.getString( "Debug" ) );
        } catch( IllegalArgumentException e ) {
            Tools.Prt( ChatColor.RED + "Config Debugモードの指定値が不正なので、normal設定にしました", programCode );
            DebugFlag = consoleMode.normal;
        }
        Tools.entryDebugFlag( programCode, DebugFlag );

        try {
            difficulty = EventMode.valueOf( config.getString( "Difficulty" ) );
        } catch ( IllegalArgumentException e ) {
            Tools.Prt( ChatColor.RED + "Config Eventが不正なので、Normal 設定にしました", consoleMode.none, programCode );
            difficulty = EventMode.Normal;
        }

        try {
            UpperBlock = UpperMode.valueOf( config.getString( "UpperBlock" ) );
        } catch ( IllegalArgumentException e ) {
            Tools.Prt( ChatColor.RED + "Config UpperBlockが不正なので、None 設定にしました", consoleMode.none, programCode );
            UpperBlock = UpperMode.None;
        }
    }

    /**
     * ポイントブロックの一覧表示
     *
     * @param player 
     */
    public void getStoneList( Player player ) {
        Tools.Prt( player, ChatColor.GREEN + "=== Premises Stone List ===", consoleMode.full, programCode );
        GetPoint.keySet().forEach( ( key ) -> {
            Tools.Prt( player, ChatColor.GREEN + key + " : " + ChatColor.WHITE + GetPoint.get( key ), consoleMode.full, programCode );
        } );
    }

    /**
     * 設定内容を表示する
     *
     * @param p
     */
    public void Status( Player p ) {
        Tools.Prt( p, ChatColor.GREEN + "=== Premises Status ===", programCode );
        Tools.Prt( p, ChatColor.WHITE + "Degub Mode : " + ChatColor.YELLOW + Tools.consoleFlag.get( programCode ).toString(), programCode );
        Tools.Prt( p, ChatColor.WHITE + "イベント名       : " + ChatColor.YELLOW + EventName, programCode );
        Tools.Prt( p, ChatColor.WHITE + "難易度           : " + ChatColor.YELLOW + difficulty.toString(), programCode );
        Tools.Prt( p, ChatColor.WHITE + "下層掘削制限     : " + ChatColor.YELLOW + UpperBlock.toString(), programCode );
        Tools.Prt( p, ChatColor.WHITE + "ツール再取得Cost : " + ChatColor.YELLOW + RePresent, programCode );
        Tools.Prt( p, ChatColor.WHITE + "ツール更新Cost   : " + ChatColor.YELLOW + UpCost, programCode );
        Tools.Prt( p, ChatColor.WHITE + "耐久度警告値     : " + ChatColor.YELLOW + Repair, programCode );
        Tools.Prt( p, ChatColor.WHITE + "通知 Console     : " + ChatColor.YELLOW + ScoreNotice, programCode );
        Tools.Prt( p, ChatColor.WHITE + "通知 Broadcast   : " + ChatColor.YELLOW + ScoreBroadcast, programCode );
        Tools.Prt( p, ChatColor.WHITE + "参加者以外の掘削 : " + ChatColor.YELLOW + ( breakFree ? "許可":"不可" ), programCode );
        Tools.Prt( p, ChatColor.WHITE + "一般Toolでの掘削 : " + ChatColor.YELLOW + ( breakTool ? "不可":"許可" ), programCode );
        Tools.Prt( p, ChatColor.WHITE + "ブロック無限設置 : " + ChatColor.YELLOW + ( zeroPlace ? "許可":"不可" ), programCode );
        Tools.Prt( p, ChatColor.WHITE + "CreativeでCount  : " + ChatColor.YELLOW + ( OPMode ? "しない":"する" ), programCode );
        Tools.Prt( p, ChatColor.WHITE + "参加者以外の設置 : " + ChatColor.YELLOW + ( placeFree ? "許可":"不可" ), programCode );
        Tools.Prt( p, ChatColor.WHITE + "指定以外の設置   : " + ChatColor.YELLOW + ( placeSpecified ? "許可":"不可" ), programCode );
        Tools.Prt( p, ChatColor.WHITE + "タイトル表示     : " + ChatColor.YELLOW + ( titlePrint ? "する":"しない" ), programCode );
        Tools.Prt( p, ChatColor.WHITE + "イベントツール名 : " + EventToolName, programCode );

        for( int i = 0; i<tools.size(); i++ ) {
            Tools.Prt( p, ChatColor.WHITE + "Tools (" + i + ") : " + ChatColor.YELLOW + tools.get( i ), programCode );
        }

        Tools.Prt( p, ChatColor.WHITE + "掘削範囲指定 : " + ChatColor.YELLOW + ( Field ? "あり":"なし" ), programCode );
        if ( Field ) {
            Tools.Prt( p, ChatColor.WHITE + "Check World: " + ChatColor.YELLOW + Event_World, programCode );
            Tools.Prt( p,
                ChatColor.WHITE + "Area1 X=" + ChatColor.YELLOW + String.format( "%-7d", Event_X1 ) +
                ChatColor.WHITE + ",Y=" + ChatColor.YELLOW + String.format( "%-3d",Event_Y1 ) +
                ChatColor.WHITE + ",Z=" + ChatColor.YELLOW + Event_Z1,
                programCode );
            Tools.Prt( p,
                ChatColor.WHITE + "Area2 X=" + ChatColor.YELLOW + String.format( "%-7d", Event_X2 ) +
                ChatColor.WHITE + ",Y=" + ChatColor.YELLOW + String.format( "%-3d", Event_Y2 ) +
                ChatColor.WHITE + ",Z=" + ChatColor.YELLOW + Event_Z2,
                programCode );
        }

        Tools.Prt( p, ChatColor.WHITE + "参加時メッセージ : " + JoinMessage, programCode );

        Tools.Prt( p, ChatColor.WHITE + "Broadcast Command:", programCode );
        for( int i = 0; i<bc_command.size(); i++ ) {
            Tools.Prt( p, ChatColor.WHITE + String.valueOf( i ) + ") : " + ChatColor.YELLOW + bc_command.get( i ), programCode );
        }

        Tools.Prt( p, ChatColor.GREEN + "=======================", programCode );
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
        return !(
            ( loc.getBlockX()<Event_X1 || loc.getBlockX()>Event_X2 ) ||
            ( loc.getBlockY()<Event_Y1 || loc.getBlockY()>Event_Y2 ) ||
            ( loc.getBlockZ()<Event_Z1 || loc.getBlockZ()>Event_Z2 )
        );
    }
}
