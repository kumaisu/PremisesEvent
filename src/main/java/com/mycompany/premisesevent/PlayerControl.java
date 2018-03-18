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
import java.util.Map;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

/**
 *
 * @author sugichan
 */
public class PlayerControl {

    private final Plugin plugin;
    
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
    private boolean EntryFlag = false;
    private String FirstDate;
    private int PlayerScore;
    private final Map<String,Integer> BlockCount = new HashMap<>();

    public PlayerControl( Plugin plugin ) {
        this.plugin = plugin;
        this.PlayerScore = 0;
    }
    
    public void ScoreBoardEntry( Player p ) {
        obj.setDisplayName( "Mining Count" );
        obj.setDisplaySlot( DisplaySlot.SIDEBAR );
        p.setScoreboard( board );
        score = obj.getScore( ChatColor.YELLOW + "Score:" );
    }
    
    /*
     * 設定をロードします
     */
    public void load( Player player ) {
        // 設定ファイルを保存
        File UKfile = new File( plugin.getDataFolder(), player.getUniqueId() + ".yml" );
        FileConfiguration UKData = YamlConfiguration.loadConfiguration( UKfile );

        if( !UKfile.exists() ) { return; }

        FirstDate = UKData.getString( "Joined" );
        PlayerScore = UKData.getInt( "Score" );
        
        if ( UKData.contains( "Counter" ) ) {
            UKData.getConfigurationSection( "Counter" ).getKeys( false ).forEach( ( key ) -> {
                BlockCount.put( key, UKData.getInt( "Counter." + key ) );
            } );
        }
        EntryFlag = true;
        ScoreBoardEntry( player );
    }
    
    public void save( Player player ) {
        File UKfile = new File( plugin.getDataFolder(), player.getUniqueId() + ".yml" );
        FileConfiguration UKData = YamlConfiguration.loadConfiguration( UKfile );

        UKData.set( "Joined", FirstDate );
        UKData.set( "Score", PlayerScore );

        BlockCount.entrySet().forEach( ( entry ) -> {
            UKData.set( "Counter." + entry.getKey(), entry.getValue() );
        } );
        
        try {
            UKData.save( UKfile );
            EntryFlag = true;
        }
        catch (IOException e) {
            plugin.getServer().getLogger().log( Level.WARNING, "{0}Could not save UnknownIP File.", ChatColor.RED );
        }
        // player.sendMessage( ChatColor.AQUA + "Data Saved" );
    }
    
    public void JoinPlayer( Player player ) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        FirstDate = sdf.format( new Date() );
        save( player );
        player.sendMessage( ChatColor.AQUA + "Joined Date was " + ChatColor.WHITE + FirstDate );
    }

    public boolean getEntry() {
        return EntryFlag;
    }
    
    public String getJoinDate() {
        return FirstDate;
    }
    
    public int getScore() {
        return PlayerScore;
    }
    
    public void addScore( int amount ) {
        PlayerScore += amount;

        // プレイヤーのスコアーを更新し反映します
        score.setScore( PlayerScore );
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
        // プレイヤーの掘削数を更新し反映します
        mines.put( StoneName, obj.getScore( ChatColor.GREEN + StoneName + ":" ) );
        mines.get( StoneName ).setScore( CD );
    }
}
