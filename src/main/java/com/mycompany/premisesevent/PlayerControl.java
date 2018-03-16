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
    private int Score;

    public PlayerControl( Plugin plugin ) {
        this.Score = 0;
        this.plugin = plugin;
    }
    
    /*
     * 設定をロードします
     */
    public void load( Player player ) {
        // 設定ファイルを保存
        File UKfile = new File( plugin.getDataFolder(), player.getUniqueId() + ".yml" );
        FileConfiguration UKData = YamlConfiguration.loadConfiguration( UKfile );
        
        FirstDate = UKData.getString( "Joined" );
        Score = UKData.getInt( "Score" );
        
        player.sendMessage( ChatColor.AQUA + "Joined Date was " + ChatColor.WHITE + FirstDate + " Score:" + Score );
    }
    
    public void save( Player player ) {
        File UKfile = new File( plugin.getDataFolder(), player.getUniqueId() + ".yml" );
        FileConfiguration UKData = YamlConfiguration.loadConfiguration( UKfile );

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        UKData.set( "Joined", sdf.format( new Date() ) );
        UKData.set( "Score", Score );

        try {
            UKData.save( UKfile );
        }
        catch (IOException e) {
            plugin.getServer().getLogger().log( Level.WARNING, "{0}Could not save UnknownIP File.", ChatColor.RED );
        }
    }
    
    public void JoinPlayer( CommandSender sender ) {
        
    }

    public String getJoinDate() {
        return FirstDate;
    }
    
    public int getScore() {
        return Score;
    }
}