/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.premisesevent.config;

import java.io.File;
import java.io.IOException;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import com.mycompany.kumaisulibraries.Tools;
import static com.mycompany.premisesevent.config.Config.programCode;

/**
 *
 * @author sugichan
 */
public class AreaManager {

    /**
     * エリア情報ロードします
     *
     * @param DataFolder
     * @return
     */
    public static boolean load( String DataFolder ) {
        //  this.getDataFolder().toString()
        Tools.Prt( "Loading Area Data.", Tools.consoleMode.full, programCode );
        // 設定ファイルを保存
        File dataFolder = new File( DataFolder + File.separator + Config.EventName );
        File UKfile = new File( dataFolder, "AreaData.yml" );
        FileConfiguration UKData = YamlConfiguration.loadConfiguration( UKfile );

        if( !UKfile.exists() ) { return false; }

        try {
            UKData.getConfigurationSection( "Area" ).getKeys( false ).forEach(
                    ( key ) -> {
                        String DataStr = UKData.getString( "Area." + key );
                        Config.AreaName.put( key, DataStr );
                        Tools.Prt( "Area Get [" + key + "] " + DataStr, Tools.consoleMode.max, programCode );
                    }
            );
            UKData.getConfigurationSection( "Blocks" ).getKeys( false ).forEach(
                    ( key ) -> {
                        String DataStr = UKData.getString( "Blocks." + key );
                        Config.AreaBlock.put( key, DataStr );
                        Tools.Prt( "Loc Get [" + key + "] " + DataStr, Tools.consoleMode.max, programCode );
                    }
            );
        } catch ( Exception e ) {
            Tools.Prt( ChatColor.RED + "Could not load AreaData File.", programCode );
            return false;
        }

        return true;
    }

    /**
     * エリア情報を保存します
     *
     * @param DataFolder
     * @return 
     */
    public static boolean save( String DataFolder ) {
        Tools.Prt( "Saving Area Data.", Tools.consoleMode.full, programCode );
        File dataFolder = new File( DataFolder + File.separator + Config.EventName );
        if( !dataFolder.exists() ) { dataFolder.mkdir(); }

        File UKfile = new File( dataFolder, "AreaData.yml" );
        FileConfiguration UKData = YamlConfiguration.loadConfiguration( UKfile );

        Config.AreaName.entrySet().forEach( ( entry ) -> {
            UKData.set( "Area." + entry.getKey(), entry.getValue() );
            Tools.Prt( "Area Put [" + entry.getKey() + "] " + entry.getValue(), Tools.consoleMode.max, programCode );
        } );

        Config.AreaBlock.entrySet().forEach( ( entry ) -> {
            UKData.set( "Blocks." + entry.getKey(), entry.getValue() );
            Tools.Prt( "Loc Put [" + entry.getKey() + "] " + entry.getValue(), Tools.consoleMode.max, programCode );
        } );

        try {
            UKData.save( UKfile );
        }
        catch ( IOException e ) {
            Tools.Prt( ChatColor.RED + "Could not save AreaData File.", programCode );
            return false;
        }
        
        return true;
    }

    /**
     * エリア確保者リスト
     *
     * @param player 
     */
    public static void List( Player player ) {
        Config.AreaName.entrySet().forEach( ( entry ) -> {
            Tools.Prt( player,
                ChatColor.YELLOW + "Area [" +
                ChatColor.WHITE + entry.getKey() +
                ChatColor.YELLOW + "] Owner : " +
                ChatColor.AQUA + entry.getValue()
                , programCode
            );
        } );
    }

    /**
     * 解放キーブロックロケーション一覧
     *
     * @param player
     */
    public static void LocList( Player player ) {
        Config.AreaBlock.entrySet().forEach( ( entry ) -> {
            Tools.Prt( player,
                ChatColor.YELLOW + "Location [" +
                ChatColor.WHITE + entry.getKey() +
                ChatColor.YELLOW + "] Block : " +
                ChatColor.GREEN + entry.getValue()
                , programCode );
        } );
    }
}
