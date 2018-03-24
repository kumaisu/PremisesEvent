/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.premisesevent;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
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

    public void Prt( Player p ,String Msg ) {
        if ( p == null ) {
            Bukkit.getServer().getConsoleSender().sendMessage( Msg );
        } else {
            p.sendMessage( Msg );
        }
    }
    
    public void Top( Player player ) {
        String PlayerName = ( ( player == null ) ? "null":player.getDisplayName() );
        Prt( player, ChatColor.GREEN + "イベントプレイヤーランキング" );

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
        int i = 0;
        for( Entry<String, Integer> entry : list_entries ) {
            i++;
            if ( ( i<11 ) || entry.getKey().equals( PlayerName ) )
                Prt( player, 
                    ChatColor.WHITE + String.format( "%2d", i ) + " : " +
                    ( entry.getKey().equals( PlayerName ) ? ChatColor.AQUA:ChatColor.GRAY ) +
                    String.format( "%-15s", entry.getKey() ) + ChatColor.YELLOW +
                    String.format( "%8d", entry.getValue() )
                );
            if ( i == 10 ) Prt( player, ChatColor.GREEN + "============================" );
        }
    }
}
