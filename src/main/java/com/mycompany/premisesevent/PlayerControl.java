/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.premisesevent;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author sugichan
 */
public class PlayerControl {

    private final Plugin plugin;
    
    /*
    プレイヤーデータ
    */
    private String FirstDate;
    private int PlayerScore;
    private final Map<String,Integer> BlockCount = new HashMap<>();

    public PlayerControl( Plugin plugin ) {
        this.plugin = plugin;
        this.PlayerScore = 0;
    }
    
    /*
     * 設定をロードします
     */
    public void load( Player player ) {
        // 設定ファイルを保存
        File UKfile = new File( plugin.getDataFolder(), player.getUniqueId() + ".yml" );
        FileConfiguration UKData = YamlConfiguration.loadConfiguration( UKfile );
        
        FirstDate = UKData.getString( "Joined" );
        PlayerScore = UKData.getInt( "Score" );

        for (String key : UKData.getConfigurationSection( "Counter" ).getKeys( false ) ) {
            BlockCount.put( key, UKData.getInt( "Counter." + key ) );
        }
        
        // player.sendMessage( ChatColor.AQUA + "Data Loaded" );
    }
    
    public void save( Player player ) {
        File UKfile = new File( plugin.getDataFolder(), player.getUniqueId() + ".yml" );
        FileConfiguration UKData = YamlConfiguration.loadConfiguration( UKfile );


        UKData.set( "Joined", FirstDate );
        UKData.set( "Score", PlayerScore );

        for(Iterator<Map.Entry<String, Integer>> iterator = BlockCount.entrySet().iterator() ; iterator.hasNext() ;){
            Map.Entry<String, Integer> entry = iterator.next();
            UKData.set( "Counter." + entry.getKey(), entry.getValue() );
        }
        
        try {
            UKData.save( UKfile );
        }
        catch (IOException e) {
            plugin.getServer().getLogger().log( Level.WARNING, "{0}Could not save UnknownIP File.", ChatColor.RED );
        }

        // player.sendMessage( ChatColor.AQUA + "Data Saved" );
    }
    
    public void JoinPlayer( CommandSender sender ) {
        Player player = (Player)sender;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        FirstDate = sdf.format( new Date() );
        save( player );
        player.sendMessage( ChatColor.AQUA + "Joined Date was " + ChatColor.WHITE + FirstDate );
    }

    public String getJoinDate() {
        return FirstDate;
    }
    
    public int getScore() {
        return PlayerScore;
    }
    
    public void addScore( int amount ) {
        PlayerScore += amount;
    }
    
    public int getStoneCount( String StoneName ) {
        int CD;
        try {
            CD = BlockCount.get( StoneName );
        } catch( Exception e ) {
            // Bukkit.getServer().getConsoleSender().sendMessage( ChatColor.RED + "No Data for " + StoneName );
            return 0;
        }
        return CD;
    }
    
    public void addStoneCount( String StoneName ) {
        int CD = getStoneCount( StoneName );
        CD++;
        BlockCount.put( StoneName, CD );
    }
}
