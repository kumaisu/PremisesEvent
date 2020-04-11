/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.premisesevent.config;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import com.mycompany.kumaisulibraries.Tools;
import static com.mycompany.premisesevent.config.Config.programCode;

/**
 *
 * @author sugichan
 */
public class ConfigManager {
    private final Plugin plugin;
    private FileConfiguration config = null;

    private final Map< String, Integer > GetPoint = new HashMap<>();
    
    /**
     * 設定読み込みライブラリ
     *
     * @param plugin
     */
    public ConfigManager( Plugin plugin ) {
        Config.stones = new ArrayList<>();
        this.plugin = plugin;
        Tools.entryDebugFlag( programCode, Tools.consoleMode.print );
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

        Config.ignoreStone = ( List< String > ) config.getList( "IgnoreStone" );
        
        Config.stones = new ArrayList<>();
        List< String > getstr = ( List< String > ) config.getList( "PointStone" );
        for( int i = 0; i<getstr.size(); i++ ) {
            String[] param = getstr.get( i ).split(",");
            GetPoint.put( param[0], Integer.valueOf( param[1] ) );
            Config.stones.add( param[0] );
        }

        List< String > getTool = ( List< String > ) config.getList( "Present" );
        for( int i = 0; i<getTool.size(); i++ ) {
            String[] param = getTool.get( i ).split(",");
            if ( param[1] == null ) { param[1] = "5"; }
            Config.tools.put( param[0], Integer.valueOf( param[1] ) );
        }

        Config.EventName = config.getString( "EventName" );

        //  Field 指定があった場合は予めディレクトリを作成する
        if ( !"none".equals( Config.EventName ) ) {
            File newdir = new File( Config.DataFolder + File.separator + Config.EventName );
            newdir.mkdir();
        }
        Config.databaseName = Config.DataFolder + File.separator + Config.EventName + File.separator + "AreaData.db";

        Config.Field = config.getBoolean( "Field", false );
        Config.ScoreNotice = config.getInt( "ScoreNotice", 0 );
        Config.ScoreBroadcast = config.getInt( "ScoreBroadcast", 0 );
        Config.bc_command = ( List< String > )config.getList( "BroadcastCommand" );
        Config.RePresent = config.getInt( "RePresent", 0 );
        Config.MinDigSpeed = config.getInt( "MinDigSpeed", 5 );
        Config.UpCost = config.getInt( "UpdateCost", 0 );
        Config.Repair = 1 - config.getDouble( "Repair", 0 );
        Config.titlePrint  = config.getBoolean( "sendTitle", true );
        Config.CreativeCount = config.getBoolean( "CreativeCount", true );
        Config.EventToolName = config.getString( "EventToolName" );
        Config.placeFree = config.getBoolean( "FreePlace", false );
        Config.placeSpecified = config.getBoolean( "SpecifiedPlace",true );
        Config.breakFree = config.getBoolean( "FreeBreak", true );
        Config.breakTool = config.getBoolean( "ToolBreak", true );
        Config.zeroPlace = config.getBoolean( "ZeroPlace", false );
        Config.PlayerAlarm = config.getBoolean( "PlayerAlarm", true );
        Config.OnDynmap = config.getBoolean( "OnDynmap", false );
        Config.SignPlace = config.getBoolean( "SignPlace", false );
        Config.MarkReleaseBlock = config.getBoolean( "MarkReleaseBlock", false );
        Config.AreaRegistBroadcast = config.getBoolean( "AreaRegistBroadcast", false );
        Config.AreaReleaseBroadcast = config.getBoolean( "AreaReleaseBroadcast", false );

        Config.PointTip = config.getBoolean( "PointTip.enabled", false );
        Config.pt_x = config.getDouble( "PointTip.x", 0 );
        Config.pt_y = config.getDouble( "PointTip.y", 0 );
        Config.pt_z = config.getDouble( "PointTip.z", 0 );
        Config.pt_delay = config.getInt( "PointTip.delay", 0 );

        Config.Event_World = config.getString( "World" );

        String pos = config.getString( "AreaPos1" );
        String[] pos_param = pos.split( "," );
        Config.Event_X1 = Integer.parseInt( pos_param[0] );
        Config.Event_Y1 = Integer.parseInt( pos_param[1] );
        Config.Event_Z1 = Integer.parseInt( pos_param[2] );

        pos = config.getString( "AreaPos2" );
        String[] pos_param2 = pos.split( "," );
        Config.Event_X2 = Integer.parseInt( pos_param2[0] );
        Config.Event_Y2 = Integer.parseInt( pos_param2[1] );
        Config.Event_Z2 = Integer.parseInt( pos_param2[2] );

        if ( Config.Event_X1>Config.Event_X2 ) {
            int temp = Config.Event_X1;
            Config.Event_X1 = Config.Event_X2;
            Config.Event_X2 = temp;
        }

        if ( Config.Event_Y1>Config.Event_Y2 ) {
            int temp = Config.Event_Y1;
            Config.Event_Y1 = Config.Event_Y2;
            Config.Event_Y2 = temp;
        }

        if ( Config.Event_Z1>Config.Event_Z2 ) {
            int temp = Config.Event_Z1;
            Config.Event_Z1 = Config.Event_Z2;
            Config.Event_Z2 = temp;
        }

        Config.MAX_REGIST = config.getInt( "MaxRegist", 0 );

        Config.JoinMessage = config.getString( "JOIN_MESSAGE" );

        try {
            Config.difficulty = Config.EventMode.valueOf( config.getString( "Difficulty" ) );
        } catch ( IllegalArgumentException e ) {
            Tools.Prt( Messages.GetString( "FraudEvent" ), Tools.consoleMode.print, programCode );
            Config.difficulty = Config.EventMode.Normal;
        }

        try {
            Config.UpperBlock = Config.UpperMode.valueOf( config.getString( "UpperBlock" ) );
        } catch ( IllegalArgumentException e ) {
            Tools.Prt( Messages.GetString( "FraudUpper" ), Tools.consoleMode.print, programCode );
            Config.UpperBlock = Config.UpperMode.None;
        }

        if ( !Tools.setDebug( config.getString( "Debug" ), programCode ) ) {
            Tools.entryDebugFlag( programCode, Tools.consoleMode.normal );
            Tools.Prt( ChatColor.RED + "Config Debugモードの指定値が不正なので、normal設定にしました", programCode );
        }
    }

    /**
     * 設定内容を表示する
     *
     * @param p
     */
    public void Status( Player p ) {
        Tools.Prt( p, ChatColor.GREEN + "=== Premises Status ===", programCode );
        Tools.Prt( p, ChatColor.WHITE + "Degub Mode : " + ChatColor.YELLOW + Tools.consoleFlag.get( programCode ).toString(), programCode );
        Tools.Prt( p, ChatColor.WHITE + "イベント名       : " + ChatColor.YELLOW + Config.EventName, programCode );
        Tools.Prt( p, ChatColor.WHITE + "難易度           : " + ChatColor.YELLOW + Config.difficulty.toString(), programCode );
        Tools.Prt( p, ChatColor.WHITE + "下層掘削制限     : " + ChatColor.YELLOW + Config.UpperBlock.toString(), programCode );
        Tools.Prt( p, ChatColor.WHITE + "ツール再取得Cost : " + ChatColor.YELLOW + Config.RePresent, programCode );
        Tools.Prt( p, ChatColor.WHITE + "最小効率強化     : " + ChatColor.YELLOW + Config.MinDigSpeed, programCode );
        Tools.Prt( p, ChatColor.WHITE + "ツール更新Cost   : " + ChatColor.YELLOW + Config.UpCost, programCode );
        Tools.Prt( p, ChatColor.WHITE + "耐久度警告値     : " + ChatColor.YELLOW + Config.Repair, programCode );
        Tools.Prt( p, ChatColor.WHITE + "通知 Console     : " + ChatColor.YELLOW + Config.ScoreNotice, programCode );
        Tools.Prt( p, ChatColor.WHITE + "通知 Broadcast   : " + ChatColor.YELLOW + Config.ScoreBroadcast, programCode );
        Tools.Prt( p, ChatColor.WHITE + "Area確保Broadcast: " + ChatColor.YELLOW + ( Config.AreaRegistBroadcast ? "あり":"なし" ), programCode );
        Tools.Prt( p, ChatColor.WHITE + "Area解放Broadcast: " + ChatColor.YELLOW + ( Config.AreaReleaseBroadcast ? "あり":"なし" ), programCode );
        Tools.Prt( p, ChatColor.WHITE + "参加者以外の掘削 : " + ChatColor.YELLOW + ( Config.breakFree ? "許可":"不可" ), programCode );
        Tools.Prt( p, ChatColor.WHITE + "一般Toolでの掘削 : " + ChatColor.YELLOW + ( Config.breakTool ? "不可":"許可" ), programCode );
        Tools.Prt( p, ChatColor.WHITE + "専用看板設置破壊 : " + ChatColor.YELLOW + ( Config.SignPlace ? "許可":"不可" ), programCode );
        Tools.Prt( p, ChatColor.WHITE + "ブロック無限設置 : " + ChatColor.YELLOW + ( Config.zeroPlace ? "許可":"不可" ), programCode );
        Tools.Prt( p, ChatColor.WHITE + "CreativeでCount  : " + ChatColor.YELLOW + ( Config.CreativeCount ? "しない":"する" ), programCode );
        Tools.Prt( p, ChatColor.WHITE + "参加者以外の設置 : " + ChatColor.YELLOW + ( Config.placeFree ? "許可":"不可" ), programCode );
        Tools.Prt( p, ChatColor.WHITE + "指定以外の設置   : " + ChatColor.YELLOW + ( Config.placeSpecified ? "許可":"不可" ), programCode );
        Tools.Prt( p, ChatColor.WHITE + "タイトル表示     : " + ChatColor.YELLOW + ( Config.titlePrint ? "する":"しない" ), programCode );
        Tools.Prt( p, ChatColor.WHITE + "イベントツール名 : " + Config.EventToolName, programCode );

        Config.tools.keySet().forEach ( ( gn ) -> {
            Tools.Prt( p, ChatColor.WHITE + "Tools : " + ChatColor.YELLOW + gn + "(" + Config.tools.get( gn ) + ")", programCode );
        } );

        Tools.Prt( p, ChatColor.WHITE + "掘削範囲指定 : " + ChatColor.YELLOW + ( Config.Field ? "あり":"なし" ), programCode );
        if ( Config.Field ) {
            Tools.Prt( p, ChatColor.WHITE + "他者エリア警告   : " + ChatColor.YELLOW + ( Config.PlayerAlarm ? "あり":"なし" ), programCode );
            Tools.Prt( p, ChatColor.WHITE + "Dynmap Area 表示 : " + ChatColor.YELLOW + ( Config.OnDynmap ? "あり":"なし" ), programCode );
            Tools.Prt( p, ChatColor.WHITE + "Check World: " + ChatColor.YELLOW + Config.Event_World, programCode );
            Tools.Prt( p,
                ChatColor.WHITE + "Area1 X=" + ChatColor.YELLOW + String.format( "%-7d", Config.Event_X1 ) +
                ChatColor.WHITE + ",Y=" + ChatColor.YELLOW + String.format( "%-3d", Config.Event_Y1 ) +
                ChatColor.WHITE + ",Z=" + ChatColor.YELLOW + Config.Event_Z1,
                programCode );
            Tools.Prt( p,
                ChatColor.WHITE + "Area2 X=" + ChatColor.YELLOW + String.format( "%-7d", Config.Event_X2 ) +
                ChatColor.WHITE + ",Y=" + ChatColor.YELLOW + String.format( "%-3d", Config.Event_Y2 ) +
                ChatColor.WHITE + ",Z=" + ChatColor.YELLOW + Config.Event_Z2,
                programCode );
        }

        Tools.Prt( p, ChatColor.WHITE + "PointTip表示 : " + ChatColor.YELLOW + ( Config.PointTip ? "あり":"なし" ), programCode );
        if ( Config.PointTip ) {
            Tools.Prt( p,
                ChatColor.WHITE + "Offset : X:" + ChatColor.YELLOW + Config.pt_x +
                ChatColor.WHITE + " Y:" + ChatColor.YELLOW + Config.pt_y +
                ChatColor.WHITE + " Z:" + ChatColor.YELLOW + Config.pt_z,
                programCode );
            Tools.Prt( p, ChatColor.WHITE + "Delay : " + ChatColor.YELLOW + Config.pt_delay, programCode );
        }

        Tools.Prt( p, ChatColor.WHITE + "参加時メッセージ : " + Config.JoinMessage, programCode );
        Tools.Prt( p, ChatColor.WHITE + "Broadcast Command:", programCode );
        for( int i = 0; i<Config.bc_command.size(); i++ ) {
            Tools.Prt( p, ChatColor.WHITE + String.valueOf( i ) + ") : " + ChatColor.YELLOW + Config.bc_command.get( i ), programCode );
        }

        Tools.Prt( p, ChatColor.GREEN + "=======================", programCode );
    }

    /**
     * ポイントブロックの一覧表示
     *
     * @param player
     */
    public void getStoneList( Player player ) {
        Tools.Prt( player, ChatColor.GREEN + "=== Premises Stone List ===", Tools.consoleMode.full, programCode );
        GetPoint.keySet().forEach( ( key ) -> {
            Tools.Prt( player, ChatColor.GREEN + key + " : " + ChatColor.WHITE + GetPoint.get( key ), Tools.consoleMode.full, programCode );
        } );
    }

    /**
     * 石に設定されているポイントを取得
     *
     * @param sd    判定する石の名称（コードではない）
     * @return
     */
    public int getPoint( String sd ) {
        if ( Config.stones.contains( sd ) ) {
            return GetPoint.get( sd );
        }
        return 0;
    }
}
