/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.kumaisu.premisesevent.Player;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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
import io.github.kumaisu.premisesevent.Lib.Tools;
import io.github.kumaisu.premisesevent.config.Config;
import io.github.kumaisu.premisesevent.config.Messages;
import static io.github.kumaisu.premisesevent.config.Config.programCode;

/**
 *
 * @author sugichan
 */
public class TopList {
    private final String DataFolder;

    /**
     * スコアーランキングリスト
     *
     * @param DF
     */
    public TopList( String DF ) {
        this.DataFolder = DF;
    }

    /**
     * ファイルネームからUUIDを取り出す
     *
     * @param   fileName
     * @return  UUID
     */
    public static String getPreffix( String fileName ) {

        if ( fileName == null ) return null;

        int point = fileName.lastIndexOf( "." );
        if ( point != -1 ) {
            return fileName.substring( 0, point );
        }
        return fileName;
    }

    /**
     * ファイル内のトータルスコアを取り出す
     *
     * @param   filename
     * @return  Total Score
     */
    public int getScore( String filename ) {
        File getFile = new File( DataFolder + File.separator + Config.EventName + File.separator + "users" + File.separator + filename );
        FileConfiguration UKData = YamlConfiguration.loadConfiguration( getFile );

        if( !getFile.exists() ) { return 0; }

        return UKData.getInt( "Score" );
    }

    /**
     * 掘削ブロック別のカウント取得
     *
     * @param   filename
     * @param   StoneName
     * @return  Count
     */
    public int getCount( String filename, String StoneName ) {
        File getFile = new File( DataFolder + File.separator + Config.EventName + File.separator + "users" + File.separator + filename );
        FileConfiguration UKData = YamlConfiguration.loadConfiguration( getFile );

        if( !getFile.exists() ) { return 0; }

        return UKData.getInt( "Counter." + StoneName );
    }

    /**
     * ランキング表示本体
     *
     * @param player
     * @param key
     */
    public void Top( Player player, Tools.consoleMode key ) {
        Messages.RepPlayer = ( ( player == null ) ? "null":player.getName() );
        Tools.Prt( player, ChatColor.GREEN + Messages.GetString( "EventList" ), programCode );
        Tools.Prt( player, ChatColor.GREEN + "============================", programCode );

        Map<String, Integer> rank = new HashMap<>();
        File folder;
        folder = new File( DataFolder + File.separator + Config.EventName + File.separator + "users" + File.separator );
        File files[] = folder.listFiles();

        // 1.File からスコアの取り出しし、マッピングする
        for ( File file : files ) {
            rank.put( Bukkit.getOfflinePlayer( UUID.fromString( getPreffix( file.getName() ) ) ).getName(), getScore( file.getName() ) );
        }

        // 2.Map.Entryのリストを作成する
        List<Entry<String, Integer>> list_entries = new ArrayList<>( rank.entrySet() );

        // 3. 比較関数Comparatorを使用してMap.Entryの値を比較する（降順）
        Collections.sort( list_entries, ( Entry<String, Integer> obj1, Entry<String, Integer> obj2 ) -> obj2.getValue().compareTo( obj1.getValue() ) );

        // 4. ループで要素順に値を取得する
        int i = 0;
        boolean lineflag = true;
        for( Entry<String, Integer> entry : list_entries ) {
            i++;
            if ( entry.getKey().equals( Messages.RepPlayer ) ) lineflag = false;
            if ( ( i<11 ) || entry.getKey().equals( Messages.RepPlayer ) || ( player == null ) )
                Tools.Prt( player, 
                    ChatColor.WHITE + String.format( "%2d", i ) + " : " +
                    ( entry.getKey().equals( Messages.RepPlayer ) ? ChatColor.AQUA:ChatColor.GRAY ) +
                    String.format( "%-15s", entry.getKey() ) + ChatColor.YELLOW +
                    String.format( "%8d", entry.getValue() ),
                    programCode
                );
            if ( ( i == 10 ) && lineflag ) Tools.Prt( player, ChatColor.GREEN + "============================", programCode );
        }

        Tools.Prt( player, ChatColor.GREEN + "============================", programCode );
    }

    /**
     * CSV形式でファイルに出力する
     *
     * @param stone
     * @throws IOException
     */
    public void ToCSV( List<String>stone ) throws IOException {
        try {
            File cvsFile = new File( DataFolder + File.separator + Config.EventName + File.separator + "data.csv" );
            //  cvs Header を作成
            try (PrintWriter pw = new PrintWriter( new BufferedWriter( new FileWriter( cvsFile ) ) )) {
                //  cvs Header を作成
                String Header = "UserName,Score";
                Header = stone.stream().map( ( s ) -> "," + s ).reduce( Header, String::concat );
                pw.println( Header );

                File folder;
                folder = new File( DataFolder + File.separator + Config.EventName + File.separator + "users" + File.separator );
                File files[] = folder.listFiles();

                for( File file : files ) {
                    String DataStr = Bukkit.getOfflinePlayer( UUID.fromString( getPreffix( file.getName() ) ) ).getName() + "," + getScore( file.getName() );
                    DataStr = stone.stream().map( ( s ) -> "," + getCount( file.getName(), s )).reduce( DataStr, String::concat );
                    pw.println( DataStr );
                }
            }
        } catch ( IOException e ) {
            Bukkit.getServer().getConsoleSender().sendMessage( ChatColor.RED + "Failed to output file !!" );
        }
    }
}
