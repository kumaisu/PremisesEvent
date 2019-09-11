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
        Messages.AreaCode = cx + "-" + cz;
        if ( Config.AreaName.get( Messages.AreaCode ) == null ) {
            Tools.Prt( player, Messages.ReplaceString( "NoOwnerArea" ), Tools.consoleMode.full, programCode );
        } else {
            Messages.RepPlayer = Config.AreaName.get( Messages.AreaCode );
            Tools.Prt( player, Messages.ReplaceString( "OwnerArea" ), Tools.consoleMode.normal, programCode );
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
        Messages.AreaCode = cx + "-" + cz;
        Tools.Prt( 
            "Get Location X:" + block.getLocation().getX() + " Z:" + block.getLocation().getZ() +
            " Area Code [ " + Messages.AreaCode + " ]",
            Tools.consoleMode.max, programCode );
        if ( Config.AreaName.get( Messages.AreaCode ) == null ) {
            Config.AreaName.put( Messages.AreaCode, player.getName() );
            String locKey = ( int ) block.getLocation().getX() + "-" + ( int ) block.getLocation().getY() + "-" + ( int ) block.getLocation().getZ();
            Config.AreaBlock.put( locKey, BukkitTool.getStoneName( block ) );
            if ( Config.OnDynmap ) { DynmapControl.SetDynmapArea( player, cx, cz, block ); }
            String getMessage = Messages.ReplaceString( "GetAreaM" );
            Messages.RepPlayer = Config.AreaName.get( Messages.AreaCode );
            String getSubMessage = Messages.ReplaceString( "GetAreaS" );
            Tools.Prt( player, getMessage + getSubMessage, Tools.consoleMode.normal, programCode );
            if ( Config.titlePrint ) { player.sendTitle( getMessage + Messages.ReplaceString( "GetAreaM2" ), getSubMessage, 0, 50, 0 ); }
            Tools.Prt( 
                "Break Location X:" + block.getLocation().getX() + " Y:" + block.getLocation().getY() + " Z:" + block.getLocation().getZ() +
                " Area Code [ " + Messages.AreaCode + " ] : " + locKey,
                Tools.consoleMode.max, programCode
            );
        } else {
            if ( !Config.AreaName.get( Messages.AreaCode ).contains( player.getName() ) || ( player.hasPermission( "Premises.admin" ) && player.isSneaking() ) ) {
                Messages.RepPlayer = ( Config.AreaName.get( Messages.AreaCode ).equals( player.getName() ) ? ChatColor.AQUA : ChatColor.RED ) + Config.AreaName.get( Messages.AreaCode );
                Tools.Prt( player, Messages.ReplaceString( "OwnerArea" ), Tools.consoleMode.normal, programCode );
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
        Messages.AreaCode = cx + "-" + cz;
        Tools.Prt( 
            "Place Location key : " + locKey +
            " Area Code [ " + Messages.AreaCode + " ]",
            Tools.consoleMode.max, programCode
        );

        //  デバッグ表示 Start
        if ( Config.AreaName.get( Messages.AreaCode ) != null ) {
            Tools.Prt( "Area : not null", Tools.consoleMode.max, programCode );
            if ( Config.AreaName.get( Messages.AreaCode ).contains( player.getName() ) ) {
                Tools.Prt( "Name : " + Config.AreaName.get( Messages.AreaCode ), Tools.consoleMode.max, programCode );
            }
        }
        if ( Config.AreaBlock.get( locKey ) != null ) {
            Tools.Prt( "Area Block : " + locKey, Tools.consoleMode.max, programCode);
        }
        //  デバッグ表示 End

        if ( 
           ( Config.AreaName.get( Messages.AreaCode ) != null ) && 
           ( Config.AreaName.get( Messages.AreaCode ).contains( player.getName() ) ) &&
           ( Config.AreaBlock.get( locKey ) != null )
        ) {
            Messages.RepPlayer = Config.AreaName.get( Messages.AreaCode );
            Tools.Prt( player, Messages.ReplaceString( "FreeArea" ), Tools.consoleMode.normal, programCode );
            Config.AreaName.remove( Messages.AreaCode );
            Config.AreaBlock.remove( locKey );
            DynmapControl.DelDynmapArea( Messages.AreaCode );
        }
    }

    public static boolean WarningCheck( Player player, Block checkBlock ) {
        if ( ( config.getPoint( BukkitTool.getStoneName( checkBlock ) ) > 0 ) && ( !Config.ignoreStone.contains( BukkitTool.getStoneName( checkBlock ) ) ) ) {
            Messages.RepMessage = Config.JoinMessage;
            Tools.Prt( player, Messages.ReplaceString( "WarningMsg" ), Tools.consoleMode.normal, programCode );
            Tools.Prt( ChatColor.RED + player.getDisplayName() + " Upper Block : " + BukkitTool.getStoneName( checkBlock ), Tools.consoleMode.full, programCode );
            if ( Config.titlePrint ) {
                player.sendTitle(
                    Messages.ReplaceString( "WarnTitleM" ),
                    Messages.ReplaceString( "WarnTitleS" ),
                    0, 50, 0
                );
            }
            return true;
        } else return false;
    }
}
