/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.premisesevent;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author sugichan
 */
public class TopList {

    private final Plugin plugin;

    public TopList( Plugin plugin ) {
        this.plugin = plugin;
    }

    public static String getPreffix( String fileName ) {

        if ( fileName == null ) return null;

        int point = fileName.lastIndexOf( "." );
        if ( point != -1 ) {
            return fileName.substring( 0, point );
        } 
        return fileName;
    }

    public int getScore( String filename ) {
        File getFile = new File( plugin.getDataFolder() + File.separator + "users" + File.separator + filename );
        FileConfiguration UKData = YamlConfiguration.loadConfiguration( getFile );

        if( !getFile.exists() ) { return 0; }

        return UKData.getInt( "Score" );
    }

    public void Top( Player player ) {
        Map<String, Integer> rank = new HashMap<>();
        File folder;
        folder = new File( plugin.getDataFolder() + File.separator + "users" + File.separator );
        File files[] = folder.listFiles();

        // 1.File からスコアの取り出しし、マッピングする
        for ( File file : files ) { rank.put( Bukkit.getOfflinePlayer( UUID.fromString( getPreffix( file.getName() ) ) ).getName(), getScore( file.getName() ) ); }

        // 2.Map.Entryのリストを作成する
        List<Entry<String, Integer>> list_entries = new ArrayList<>( rank.entrySet() );

        // 3. 比較関数Comparatorを使用してMap.Entryの値を比較する（降順）
        Collections.sort( list_entries, ( Entry<String, Integer> obj1, Entry<String, Integer> obj2 ) -> obj2.getValue().compareTo( obj1.getValue() ) );

        // 4. ループで要素順に値を取得する
        player.sendMessage( ChatColor.GREEN + "イベントプレイヤーランキング" );
        boolean OutRange = true;
        int i = 0;
        for( Entry<String, Integer> entry : list_entries ) {
            i++;
            String SM = ChatColor.WHITE + String.format( "%2d", i ) + " : ";
            if ( entry.getKey().equals( player.getDisplayName() ) ) {
                OutRange = false;
                SM += ChatColor.AQUA;
            } else {
                SM += ChatColor.GRAY;
            }
            SM += ChatColor.AQUA + String.format( "%-15s", entry.getKey() ) + ChatColor.YELLOW + String.format( "%8d", entry.getValue() );
            player.sendMessage( SM );
            if ( i>9 ) break;
        }
        
        if ( OutRange ) {
            player.sendMessage( "-- : " + ChatColor.AQUA + String.format( "%-15s", player.getDisplayName() ) + ChatColor.YELLOW + String.format( "%8d", rank.get( player.getDisplayName() ) ) );
        }
    }
}
