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
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
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
    private final Config config;
    
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
    private boolean PresentFlag;
    private boolean UpdateFlag;
    private final Map<String,Integer> BlockCount = new HashMap<>();

    public PlayerControl( Plugin plugin, Config config ) {
        this.plugin = plugin;
        this.PlayerScore = 0;
        this.config = config;
    }

    public void setUUID( UUID setuuid ) {
        uuid = setuuid;
    }
    
    public void setDisplayName( String name ) {
        DisplayName = name;
    }
    
    public String getDisplayName() {
        return DisplayName;
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
        File dataFolder = new File( plugin.getDataFolder() + File.separator + config.getEventName() + File.separator + "users" );
        File UKfile = new File( dataFolder, uuid + ".yml" );
        FileConfiguration UKData = YamlConfiguration.loadConfiguration( UKfile );

        if( !UKfile.exists() ) { return false; }

        EntryFlag = UKData.getBoolean( "Entry", true );
        FirstDate = UKData.getString( "Joined" );
        PlayerScore = UKData.getInt( "Score" );
        PresentFlag = UKData.getBoolean( "Present" );
        UpdateFlag = UKData.getBoolean( "Update" );
        
        if ( UKData.contains( "Counter" ) ) {
            UKData.getConfigurationSection( "Counter" ).getKeys( false ).forEach( ( key ) -> {
                BlockCount.put( key, UKData.getInt( "Counter." + key ) );
            } );
        }

        return true;
    }
    
    public void save() {
        File dataFolder = new File( plugin.getDataFolder() + File.separator + config.getEventName() + File.separator + "users" );
        if( !dataFolder.exists() ) { dataFolder.mkdir(); }

        File UKfile = new File( dataFolder, uuid + ".yml" );
        FileConfiguration UKData = YamlConfiguration.loadConfiguration( UKfile );

        UKData.set( "Name", Bukkit.getOfflinePlayer( uuid ).getName() );
        UKData.set( "Entry", EntryFlag );
        UKData.set( "Joined", FirstDate );
        UKData.set( "Score", PlayerScore );
        UKData.set( "Present", false );
        UKData.set( "Update", false );

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
        
        if ( EntryFlag ) {
            Bukkit.getServer().getConsoleSender().sendMessage( ChatColor.RED + "Double registration failure." );
            p.sendMessage( ChatColor.RED + "既にイベントへ参加しています" );
            return false;
        }

        if ( !Arrays.asList( p.getInventory().getStorageContents() ).contains( null ) ) {
            p.sendMessage( ChatColor.RED + "参加アイテム配布用のためインベントリに空きが必要です" );
            return false;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        FirstDate = sdf.format( new Date() );
        EntryFlag = true;
        save();
        ScoreBoardEntry( p );
        p.sendMessage( ChatColor.AQUA + "Joined Date was " + ChatColor.WHITE + FirstDate );

        ItemControl ic = new ItemControl( plugin );
        ic.ItemPresent( p );
        for( int i = 0; i<config.getTools().size(); i++ ) {
            //  Bukkit.getServer().getConsoleSender().sendMessage( ChatColor.GREEN + "Config Tool Name : " + config.getTools().get( i ) );
            ic.ItemUpdate( p, null, config.getEventToolName(), Material.getMaterial( config.getTools().get( i ).toString() ) );
        }

        Bukkit.broadcastMessage( "<Premises> " + ChatColor.WHITE + p.getDisplayName() + ChatColor.GREEN + "さんが、イベントに参加しました" );
        return true;
    }
    
    public boolean itemget( Player player, Material Tool ) {
        int Rep = config.getRePresent();
        if ( getScore() > Rep ) {
            ItemControl ic = new ItemControl( plugin );
            ic.ItemUpdate( player, null, config.getEventToolName(), Tool );
            addScore( -Rep );
            Bukkit.getServer().getConsoleSender().sendMessage( ChatColor.GOLD + player.getDisplayName() + " Redistributing " + Tool.name() + " update tools !!" );
            return true;
        } else {
            player.sendMessage( ChatColor.RED + "Scoreが足りないので配布できません" );
            return false;
        }
    }

    public void ToolUpdate( Player player, boolean Force ) {

        if ( player.getInventory().getItemInMainHand().getType() == Material.AIR ) {
            player.sendMessage( ChatColor.RED + "アップデートするアイテムを持ってください" );
            return;
        }

        int Rep = config.getUpCost();
        if ( getScore() > Rep ) {
            ItemStack item = player.getInventory().getItemInMainHand();

            if ( item.getItemMeta().hasDisplayName() ) {
                if ( item.getItemMeta().getDisplayName().equalsIgnoreCase( config.getEventToolName() ) ) {
                    double CheckDurability = ( item.getType().getMaxDurability() * 0.9 );
                    if ( ( CheckDurability <= item.getDurability() ) || Force ) {
                        ItemControl ic = new ItemControl( plugin );
                        player.getInventory().setItemInMainHand( null );
                        ic.ItemUpdate( player, item, config.getEventToolName(), null );
                        addScore( -Rep );
                    } else {
                        player.sendMessage(
                            ChatColor.YELLOW + "ツール耐久値は " +
                            ChatColor.WHITE + ( item.getType().getMaxDurability() - item.getDurability() ) +
                            ChatColor.YELLOW + " なので " +
                            ChatColor.WHITE + ( (int) ( item.getType().getMaxDurability() - CheckDurability ) ) +
                            ChatColor.YELLOW + " 以下にしてね"
                        );
                    }
                } else player.sendMessage( ChatColor.YELLOW + "ツール名が違います" );
            } else player.sendMessage( ChatColor.YELLOW + "イベント用のツールではありません" );
        } else player.sendMessage( ChatColor.RED + "Scoreが足りないのでアップデートできません" );
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
            Bukkit.getServer().getConsoleSender().sendMessage( ChatColor.RED + "No Data for [" + StoneName + "]" );
            CD = 0;
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
    
    public boolean getPresentFlag() {
        return PresentFlag;
    }
    
    public boolean getUpdateFlag() {
        return UpdateFlag;
    }
}
