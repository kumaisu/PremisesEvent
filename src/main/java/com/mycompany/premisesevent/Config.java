/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.premisesevent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

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

    /**
     * 
     *
     * @param plugin 
     */
    public Config(Plugin plugin) {
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
    }

    /**
     * 
     */
    public void Status() {
        Bukkit.getServer().getConsoleSender().sendMessage( ChatColor.GREEN + "=== Premises Status ===" );
        Bukkit.getServer().getConsoleSender().sendMessage( ChatColor.WHITE + "EventName : " + ChatColor.YELLOW + EventName );
        Bukkit.getServer().getConsoleSender().sendMessage( ChatColor.WHITE + "RePresent : " + ChatColor.YELLOW + RePresent );
        Bukkit.getServer().getConsoleSender().sendMessage( ChatColor.WHITE + "UpdateCost: " + ChatColor.YELLOW + UpCost );
        Bukkit.getServer().getConsoleSender().sendMessage( ChatColor.WHITE + "Repair    : " + ChatColor.YELLOW + Repair );
        Bukkit.getServer().getConsoleSender().sendMessage( ChatColor.WHITE + "FreeBreak : " + ChatColor.YELLOW + ( FreeBreak ? "TRUE":"FALSE" ) );
        Bukkit.getServer().getConsoleSender().sendMessage( ChatColor.WHITE + "ToolBreak : " + ChatColor.YELLOW + ( ToolBreak ? "TRUE":"FALSE" ) );
        Bukkit.getServer().getConsoleSender().sendMessage( ChatColor.WHITE + "Creative  : " + ChatColor.YELLOW + ( OPMode ? "TRUE":"FALSE" ) );
        Bukkit.getServer().getConsoleSender().sendMessage( ChatColor.WHITE + "ToolName  : " + EventToolName );
        
        for( int i = 0; i<tools.size(); i++ ) {
            Bukkit.getServer().getConsoleSender().sendMessage( ChatColor.WHITE + "Tools (" + i + ") : " + ChatColor.YELLOW + tools.get( i ) );
        }

        Bukkit.getServer().getConsoleSender().sendMessage( ChatColor.WHITE + "Field     : " + ChatColor.YELLOW + ( Field ? "TRUE":"FALSE" ) );
        if ( Field ) {
            Bukkit.getServer().getConsoleSender().sendMessage( ChatColor.WHITE + "Check World: " + ChatColor.YELLOW + Event_World );
            Bukkit.getServer().getConsoleSender().sendMessage( ChatColor.WHITE + "Area1 X=" + ChatColor.YELLOW + Event_X1 + ChatColor.WHITE + ",Y=" + ChatColor.YELLOW + Event_Y1 + ChatColor.WHITE + ",Z=" + ChatColor.YELLOW + Event_Z1 );
            Bukkit.getServer().getConsoleSender().sendMessage( ChatColor.WHITE + "Area2 X=" + ChatColor.YELLOW + Event_X2 + ChatColor.WHITE + ",Y=" + ChatColor.YELLOW + Event_Y2 + ChatColor.WHITE + ",Z=" + ChatColor.YELLOW + Event_Z2 );
        }
        Bukkit.getServer().getConsoleSender().sendMessage( ChatColor.WHITE + "Broadcast Command:" );
        for( int i = 0; i<bc_command.size(); i++ ) {
            Bukkit.getServer().getConsoleSender().sendMessage( ChatColor.WHITE + String.valueOf( i ) + ") : " + ChatColor.YELLOW + bc_command.get( i ) );
        }

        Bukkit.getServer().getConsoleSender().sendMessage( ChatColor.GREEN + "=======================" );
    }

    /**
     * 
     *
     * @return 
     */
    public String getEventName() {
        return EventName;
    }

    /**
     * 
     *
     * @return 
     */
    public List getStones() {
        return stones;
    }

    /**
     * 
     *
     * @param sd
     * @return 
     */
    public int getPoint( String sd ) {
        if ( stones.contains( sd ) ) {
            return GetPoint.get( sd );
        }
        return 0;
    }

    /**
     * 
     *
     * @return 
     */
    public String getEventToolName() {
        return EventToolName;
    }

    /**
     * 
     *
     * @return 
     */
    public int getRePresent() {
        return RePresent;
    }

    /**
     * 
     *
     * @return 
     */
    public int getUpCost() {
        return UpCost;
    }

    /**
     * 
     *
     * @return 
     */
    public double getRepair() {
        return Repair;
    }

    /**
     * 
     *
     * @return 
     */
    public boolean GetField() {
        return Field;
    }

    /**
     * 
     *
     * @return 
     */
    public boolean CreativeCount() {
        return OPMode;
    }

    /**
     * 
     *
     * @param loc
     * @return 
     */
    public boolean CheckArea( Location loc ) {
        if ( !loc.getWorld().getName().equals( Event_World ) ) return false;
        return !( ( loc.getBlockX()<Event_X1 || loc.getBlockX()>Event_X2 ) || ( loc.getBlockY()<Event_Y1 || loc.getBlockY()>Event_Y2 ) || ( loc.getBlockZ()<Event_Z1 || loc.getBlockZ()>Event_Z2 ) );
    }

    /**
     * 
     *
     * @return 
     */
    public boolean FreeBreak() {
        return FreeBreak;
    }

    /**
     * 
     *
     * @return 
     */
    public boolean ToolBreak() {
        return ToolBreak;
    }

    /**
     * 
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
}
