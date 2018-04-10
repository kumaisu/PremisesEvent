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
    private boolean OPMode;
    private int RePresent;
    private int UpCost;
    private double Repair;
    private boolean Field;
    private String Event_World;
    private int Event_X1;
    private int Event_X2;
    private int Event_Y1;
    private int Event_Y2;
    private int Event_Z1;
    private int Event_Z2;

    public Config(Plugin plugin) {
        this.stones = new ArrayList<>();
        this.plugin = plugin;
        plugin.getLogger().info( "Config Loading now..." );
        load();
    }
    
    /*
     * 設定をロードします
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

        Field = config.getBoolean( "Field" );
        RePresent = config.getInt( "RePresent" );
        UpCost = config.getInt( "UpdateCost" );
        Repair = config.getDouble( "Repair" );
        OPMode = config.getBoolean( "CreativeCount" );

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
    }

    public void Status() {
        Bukkit.getServer().getConsoleSender().sendMessage( ChatColor.GREEN + "=== Premises Status ===" );
        Bukkit.getServer().getConsoleSender().sendMessage( ChatColor.WHITE + "Field     : " + ( Field ? "TRUE":"FALSE" ) );
        Bukkit.getServer().getConsoleSender().sendMessage( ChatColor.WHITE + "RePresent : " + RePresent );
        Bukkit.getServer().getConsoleSender().sendMessage( ChatColor.WHITE + "Repair    : " + Repair );
        if ( Field ) {
            Bukkit.getServer().getConsoleSender().sendMessage( ChatColor.WHITE + "Check World: " + Event_World );
            Bukkit.getServer().getConsoleSender().sendMessage( ChatColor.WHITE + "Area1 X=" + Event_X1 + ",Y=" + Event_Y1 + ",Z=" + Event_Z1 );
            Bukkit.getServer().getConsoleSender().sendMessage( ChatColor.WHITE + "Area2 X=" + Event_X2 + ",Y=" + Event_Y2 + ",Z=" + Event_Z2 );
        }
        Bukkit.getServer().getConsoleSender().sendMessage( ChatColor.GREEN + "=======================" );
    }
    
    public String getEventName() {
        return config.getString( "EventName" );
    }

    public List getStones() {
        return stones;
    }
    
    public int getPoint( String sd ) {
        if ( stones.contains( sd ) ) {
            return GetPoint.get( sd );
        }
        return 0;
    }
    
    public int getRePresent() {
        return RePresent;
    }
    
    public int getUpCost() {
        return UpCost;
    }
    
    public boolean GetField() {
        return Field;
    }
    
    public boolean CreativeCount() {
        return OPMode;
    }

    public boolean CheckArea( Location loc ) {
        if ( !loc.getWorld().getName().equals( Event_World ) ) return false;
        if ( loc.getBlockX()<Event_X1 || loc.getBlockX()>Event_X2 ) return false;
        if ( loc.getBlockY()<Event_Y1 || loc.getBlockY()>Event_Y2 ) return false;
        if ( loc.getBlockZ()<Event_Z1 || loc.getBlockZ()>Event_Z2 ) return false;

        /*
        Bukkit.getServer().getConsoleSender().sendMessage(
                "Loc = " + loc.getWorld().getName() +
                "X(" + loc.getBlockX() +
                ")Y(" + loc.getBlockY() +
                ")Z(" + loc.getBlockZ() + ")"
        );
        */

        return true;
    }

}
