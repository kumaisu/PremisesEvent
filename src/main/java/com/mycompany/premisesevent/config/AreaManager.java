/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.premisesevent.config;

import java.io.File;
import java.io.IOException;
import org.bukkit.block.Block;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import com.mycompany.premisesevent.utility.DynmapControl;
import com.mycompany.kumaisulibraries.Tools;
import com.mycompany.kumaisulibraries.BukkitTool;
import static com.mycompany.premisesevent.PremisesEvent.config;
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

    /**
     * 保護エリアのチェック
     *
     * @param player
     * @param block
     */
    public static void AreaGet( Player player, Block block ) {
        if ( config.GetField() && !config.CheckArea( block.getLocation() ) ) return;
        int cx = ( int )( block.getLocation().getX() - Config.Event_X1 ) / 16;
        int cz = ( int )( block.getLocation().getZ() - Config.Event_Z1 ) / 16;
        String CheckCode = cx + "-" + cz;
        if ( Config.AreaName.get( CheckCode ) == null ) {
            Tools.Prt( player,
                ChatColor.YELLOW + "[" + CheckCode + "] " +
                ChatColor.GREEN + "誰のエリアでもありません",
                Tools.consoleMode.full, programCode
            );
        } else {
            Tools.Prt( player,
                ChatColor.YELLOW + "[" + CheckCode + "] " +
                ChatColor.AQUA + Config.AreaName.get( CheckCode ) +
                ChatColor.GREEN + "さんの掘削エリアです",
                Tools.consoleMode.normal, programCode
            );
        }
    }

    /**
     * 保護エリアの状態チェック＆新規登録
     *
     * @param player
     * @param block 
     */
    public static void AreaCheck( Player player, Block block ) {
        int cx = ( int )( block.getLocation().getX() - Config.Event_X1 ) / 16;
        int cz = ( int )( block.getLocation().getZ() - Config.Event_Z1 ) / 16;
        String CheckCode = cx + "-" + cz;
        Tools.Prt( 
            "Get Location X:" + block.getLocation().getX() + " Z:" + block.getLocation().getZ() +
            " Area Code [ " + CheckCode + " ]",
            Tools.consoleMode.max, programCode );
        if ( Config.AreaName.get( CheckCode ) == null ) {
            Config.AreaName.put( CheckCode, player.getName() );
            String locKey = ( int ) block.getLocation().getX() + "-" + ( int ) block.getLocation().getY() + "-" + ( int ) block.getLocation().getZ();
            Config.AreaBlock.put( locKey, BukkitTool.getStoneName( block ) );
            if ( Config.OnDynmap ) { DynmapControl.SetDynmapArea( player, cx, cz ); }
            Tools.Prt( player, ChatColor.AQUA + "[" + CheckCode + "] " + Config.AreaName.get( CheckCode ) + "さんのエリアに設定しました", Tools.consoleMode.normal, programCode );
            Tools.Prt( 
                "Break Location X:" + block.getLocation().getX() + " Y:" + block.getLocation().getY() + " Z:" + block.getLocation().getZ() +
                " Area Code [ " + CheckCode + " ] : " + locKey,
                Tools.consoleMode.max, programCode
            );
        } else {
            if ( !Config.AreaName.get( CheckCode ).contains( player.getName() ) || ( player.hasPermission( "Premises.admin" ) && player.isSneaking() ) ) {
                Tools.Prt( player,
                    ChatColor.YELLOW + "[" + CheckCode + "] " +
                    ( Config.AreaName.get( CheckCode ).equals( player.getName() ) ? ChatColor.AQUA : ChatColor.RED ) +
                    Config.AreaName.get( CheckCode ) +
                    ChatColor.YELLOW + "さんの掘削エリアです",
                    Tools.consoleMode.normal, programCode
                );
            }
        }
    }

    /**
     * 保護エリアの解放処理
     *
     * @param player
     * @param block 
     */
    public static void AreaRelease( Player player, Block block ) {
        String locKey = ( int ) block.getLocation().getX() + "-" + ( int ) block.getLocation().getY() + "-" + ( int ) block.getLocation().getZ();
        int cx = ( int )( block.getLocation().getX() - Config.Event_X1 ) / 16;
        int cz = ( int )( block.getLocation().getZ() - Config.Event_Z1 ) / 16;
        String CheckCode = cx + "-" + cz;
        Tools.Prt( 
            "Place Location key : " + locKey +
            " Area Code [ " + CheckCode + " ]",
            Tools.consoleMode.max, programCode
        );

        //  デバッグ表示 Start
        if ( Config.AreaName.get( CheckCode ) != null ) {
            Tools.Prt( "Area : not null", Tools.consoleMode.max, programCode );
            if ( Config.AreaName.get( CheckCode ).contains( player.getName() ) ) {
                Tools.Prt( "Name : " + Config.AreaName.get( CheckCode ), Tools.consoleMode.max, programCode );
            }
        }
        if ( Config.AreaBlock.get( locKey ) != null ) {
            Tools.Prt( "Area Block : " + locKey, Tools.consoleMode.max, programCode);
        }
        //  デバッグ表示 End

        if ( 
           ( Config.AreaName.get( CheckCode ) != null ) && 
           ( Config.AreaName.get( CheckCode ).contains( player.getName() ) ) &&
           ( Config.AreaBlock.get( locKey ) != null )
        ) {
            Tools.Prt( player, ChatColor.RED + "[" + CheckCode + "] " + Config.AreaName.get( CheckCode ) + "さんのエリア解放します", Tools.consoleMode.normal, programCode );
            Config.AreaName.remove( CheckCode );
            Config.AreaBlock.remove( locKey );
            DynmapControl.DelDynmapArea( CheckCode );
        }
    }

    public static boolean WarningCheck( Player player, Block checkBlock ) {
        if ( ( config.getPoint( BukkitTool.getStoneName( checkBlock ) ) > 0 ) && ( !Config.ignoreStone.contains( BukkitTool.getStoneName( checkBlock ) ) ) ) {
            Tools.Prt( player, ChatColor.RED + "違反警告 : " + Config.JoinMessage, Tools.consoleMode.normal, programCode );
            Tools.Prt( ChatColor.RED + player.getDisplayName() + " Upper Block : " + BukkitTool.getStoneName( checkBlock ), Tools.consoleMode.full, programCode );
            if ( Config.titlePrint ) {
                player.sendTitle(
                    ChatColor.RED + "ルール違反の可能性があります",
                    ChatColor.YELLOW + Config.JoinMessage,
                    0, 50, 0
                );
            }
            return true;
        } else return false;
    }
}
