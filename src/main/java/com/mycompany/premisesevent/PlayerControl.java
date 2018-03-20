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
    private UUID uuid;
    private String DisplayName;
    private boolean EntryFlag = false;
    private String FirstDate;
    private int PlayerScore;
    private final Map<String,Integer> BlockCount = new HashMap<>();

    public PlayerControl( Plugin plugin ) {
        this.plugin = plugin;
        this.PlayerScore = 0;
    }

    public void setUUID( UUID setuuid ) {
        uuid = setuuid;
    }
    
    public void setDisplayName( String name ) {
        DisplayName = name;
    }
    
    public void ScoreBoardEntry( Player player ) {
        obj.setDisplayName( "Mining Count" );
        obj.setDisplaySlot( DisplaySlot.SIDEBAR );
        player.setScoreboard( board );
        score = obj.getScore( ChatColor.YELLOW + "Score:" );
    }
    
    /*
     * 設定をロードします
     */
    public boolean load() {
        // 設定ファイルを保存
        File dataFolder = new File( plugin.getDataFolder() + File.separator + "users" );
        File UKfile = new File( dataFolder, uuid + ".yml" );
        FileConfiguration UKData = YamlConfiguration.loadConfiguration( UKfile );

        if( !UKfile.exists() ) { return false; }

        FirstDate = UKData.getString( "Joined" );
        PlayerScore = UKData.getInt( "Score" );
        
        if ( UKData.contains( "Counter" ) ) {
            UKData.getConfigurationSection( "Counter" ).getKeys( false ).forEach( ( key ) -> {
                BlockCount.put( key, UKData.getInt( "Counter." + key ) );
            } );
        }
        EntryFlag = true;
        return true;
    }
    
    public void save() {
        File dataFolder = new File( plugin.getDataFolder() + File.separator + "users" );
        if( !dataFolder.exists() ) { dataFolder.mkdir(); }

        File UKfile = new File( dataFolder, uuid + ".yml" );
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
    
    public boolean JoinPlayer( Player p ) {
        
        if ( !Arrays.asList( p.getInventory().getStorageContents() ).contains( null ) ) {
            p.sendMessage( ChatColor.RED + "参加アイテム配布用のためインベントリに空きが必要です" );
            return false;
        }

        if ( !getEntry() ) {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            FirstDate = sdf.format( new Date() );
            save();
            ScoreBoardEntry( p );
            EntryFlag = true;

            p.sendMessage( ChatColor.AQUA + "Joined Date was " + ChatColor.WHITE + FirstDate );

            ItemControl ic = new ItemControl( plugin );
            ic.ItemPresent( p );
            ic.ItemUpdate( p, null );
        }

        return true;
    }
    
    public boolean itemget( Player player ) {
        if ( getScore() > 2000 ) {
            ItemControl ic = new ItemControl( plugin );
            ic.ItemUpdate( player, null );
            addScore( -2000 );
            return true;
        } else {
            player.sendMessage( ChatColor.RED + "Scoreが足りないので配布できません" );
            return false;
        }
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
    
    public void getStatus( Player p ) {
        Bukkit.getServer().getConsoleSender().sendMessage( ChatColor.RED + "Look Status: " + DisplayName );
        p.sendMessage( ChatColor.GREEN + "--------------------------------------------------" );
        p.sendMessage( ChatColor.AQUA + "Block mined by: " + DisplayName );
        p.sendMessage( ChatColor.GOLD + "SCORE: " + ChatColor.WHITE + getScore() );

        BlockCount.entrySet().forEach( ( entry ) -> {
            p.sendMessage( ChatColor.GREEN + entry.getKey() + ": " + ChatColor.YELLOW + entry.getValue() );
        } );

        p.sendMessage( ChatColor.GREEN + "--------------------------------------------------" );
    }
}
