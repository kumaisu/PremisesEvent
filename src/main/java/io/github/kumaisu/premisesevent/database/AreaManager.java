/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.kumaisu.premisesevent.database;

import java.sql.*;
import java.util.Date;
import java.text.SimpleDateFormat;

import io.github.kumaisu.premisesevent.Lib.Discord;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import io.github.kumaisu.premisesevent.Lib.Tools;
import io.github.kumaisu.premisesevent.utility.BukkitTool;
import io.github.kumaisu.premisesevent.utility.DynmapControl;
import io.github.kumaisu.premisesevent.config.Config;
import io.github.kumaisu.premisesevent.config.Messages;
import static io.github.kumaisu.premisesevent.PremisesEvent.config;
import static io.github.kumaisu.premisesevent.config.Config.programCode;

/**
 * イベントゾーンテーブル
 *      AreaCode : varchar(7)   AreaCode
 *      Owner : varchar(20)     PlayerName
 *      World : varchar(20)     world
 *      x : int
 *      y : int
 *      z ; int
 *      Block : varchar(10)     BlockName
 *      Date : DATETIME         Stamp
 *
 * @author sugichan
 */
public class AreaManager {
    private static final SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );

    /**
     * プレイヤー情報を新規追加する
     *
     * @param Owner
     * @param Loc
     * @param block
     * @param AreaCode
     */
    public static void AddSQL( String Owner, Location Loc, String block, String AreaCode ) {
        try ( Connection con = DriverManager.getConnection( Database.DB_URL ) ) {
            String sql = "INSERT INTO area (AreaCode, Owner, World, x, y, z, Block, Date) VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
            Tools.Prt( "SQL : " + sql, Tools.consoleMode.max , programCode );
            PreparedStatement preparedStatement = con.prepareStatement( sql );
            preparedStatement.setString( 1, AreaCode );
            preparedStatement.setString( 2, Owner );
            preparedStatement.setString( 3, Loc.getWorld().getName() );
            preparedStatement.setInt( 4, Loc.getBlockX() );
            preparedStatement.setInt( 5, Loc.getBlockY() );
            preparedStatement.setInt( 6, Loc.getBlockZ() );
            preparedStatement.setString( 7, block );
            preparedStatement.setString( 8, sdf.format( new Date() ) );

            preparedStatement.executeUpdate();
            con.close();

            Database.AreaCode = AreaCode;
            Database.Owner = Owner;
            Database.Location = Loc;
            Database.Block = block;
            Database.GetDate = sdf.format( new Date() );

            Tools.Prt( "Add Data to SQL Success.", Tools.consoleMode.full, programCode );
        } catch ( SQLException e ) {
            Tools.Prt( ChatColor.RED + "Error AddToSQL" + e.getMessage(), programCode );
        }
    }

    /**
     * プレイヤー情報を削除する
     *
     * @param AREA
     * @return
     */
    public static boolean DelSQL( String AREA ) {
        try ( Connection con = DriverManager.getConnection( Database.DB_URL ) ) {
            String sql = "DELETE FROM area WHERE AreaCode = '" + AREA + "';";
            Tools.Prt( "SQL : " + sql, Tools.consoleMode.max , programCode );
            PreparedStatement preparedStatement = con.prepareStatement( sql );
            preparedStatement.executeUpdate();
            Tools.Prt( "Delete Data from SQL Success.", Tools.consoleMode.full , programCode );
            con.close();
            return true;
        } catch ( SQLException e ) {
            Tools.Prt( ChatColor.RED + "Error DelFromSQL" + e.getMessage(), programCode );
            return false;
        }
    }

    /**
     * UUIDからプレイヤー情報を取得する
     *
     * @param AREA
     * @return
     */
    public static boolean GetSQL( String AREA ) {
        try ( Connection con = DriverManager.getConnection( Database.DB_URL ) ) {
            boolean retStat = false;
            Statement stmt = con.createStatement();
            String sql = "SELECT * FROM area WHERE AreaCode = '" + AREA + "';";
            Tools.Prt( "SQL : " + sql, Tools.consoleMode.max , programCode );
            ResultSet rs = stmt.executeQuery( sql );
            if ( rs.next() ) {
                Database.AreaCode   = rs.getString( "AreaCode" );
                Database.Owner      = rs.getString( "Owner" );
                Database.Location   = new Location(
                                        Bukkit.getWorld( rs.getString( "world" ) ),
                                        rs.getInt( "x" ),
                                        rs.getInt( "y" ),
                                        rs.getInt( "z" ) );
                Database.Block      = rs.getString( "Block" );
                Database.GetDate    = rs.getString( "Date" );
                Tools.Prt( "Get Data from SQL Success.", Tools.consoleMode.max , programCode );
                retStat = true;
            }
            con.close();
            return retStat;
        } catch ( SQLException e ) {
            Tools.Prt( ChatColor.RED + "Error GetSQL : " + e.getMessage(), Tools.consoleMode.full, programCode );
            return false;
        }
    }

    //  ツール類
    
    public static boolean CheckLoc( Location ORG, Location TGT ) {
        return (
                ( ORG.getX() == TGT.getX() ) &&
                ( ORG.getY() == TGT.getY() ) &&
                ( ORG.getZ() == TGT.getZ() )
                );
    }

    public static String PackAreaCode( Location Loc ) {
        int cx = ( int )( Loc.getX() - Config.Event_X1 ) / 16;
        int cz = ( int )( Loc.getZ() - Config.Event_Z1 ) / 16;
        //  Messages.AreaCode = cx + "-" + cz;
        return cx + "-" + cz;
    }

    /**
     * 保護エリアのチェック
     *
     * @param player
     * @param block
     */
    public static void AreaGet( Player player, Block block ) {
        if ( Config.Field && !CheckArea( block.getLocation() ) ) return;
        Messages.AreaCode = PackAreaCode( block.getLocation() );
        if ( GetSQL( Messages.AreaCode ) ) {
            Messages.RepPlayer = Database.Owner;
            Tools.Prt( player, Messages.GetString( "OwnerArea" ), Tools.consoleMode.full, programCode );
        } else {
            Tools.Prt( player, Messages.GetString( "NoOwnerArea" ), Tools.consoleMode.full, programCode );
        }
    }

    /**
     * 指定範囲内かの判定
     *
     * @param loc   現在位置
     * @return
     */
    public static boolean CheckArea( Location loc ) {
        return 
            loc.getWorld().getName().equals( Config.Event_World ) &&
            ( loc.getBlockX()>=Config.Event_X1 && loc.getBlockX()<=Config.Event_X2 ) &&
            ( loc.getBlockY()>=Config.Event_Y1 && loc.getBlockY()<=Config.Event_Y2 ) &&
            ( loc.getBlockZ()>=Config.Event_Z1 && loc.getBlockZ()<=Config.Event_Z2 );
    }

    //  コマンド関数

    /**
     * エリア保護者リスト
     *
     * @param player 
     * @param Key 
     */
    public static void AreaList( Player player, String Key ) {
        Tools.Prt( player, ChatColor.GREEN + "List for AreaCode...", programCode );

        try ( Connection con = DriverManager.getConnection( Database.DB_URL ) ) {
            Statement stmt = con.createStatement();
            String sql = "SELECT * FROM area";
            if ( !"".equals( Key ) ) { sql += " WHERE Owner = '" + Key + "'"; }
            sql += " ORDER BY AreaCode ASC;";
            Tools.Prt( "SQL : " + sql, Tools.consoleMode.max , programCode );
            ResultSet rs = stmt.executeQuery( sql );
            while( rs.next() ) {
                Tools.Prt( player,
                    rs.getString( "AreaCode" ) + " " +
                    rs.getString( "Date" ) + " : " +
                    rs.getString( "Owner" ) + " -> " +
                    rs.getString( "World" ) + ":" +
                    rs.getInt( "x" ) + "," +
                    rs.getInt( "y" ) + "," +
                    rs.getInt( "z" ) + " [" +
                    rs.getString( "Block" ) + "]"
                    , programCode
                );
            }
            con.close();
            Tools.Prt( player, ChatColor.GREEN + "List [EOF]", programCode );
        } catch ( SQLException e ) {
            Tools.Prt( ChatColor.RED + "Error AreaList : " + e.getMessage(), programCode );
        }
    }

    /**
     * 手動削除
     *
     * @param player
     * @param AreaCode
     */
    public static void DelRegister( Player player, String AreaCode ) {
        DelSQL( AreaCode );
        DynmapControl.DelDynmapArea( AreaCode );
        Tools.Prt( player, "Manual UnRegist Area Code [ " + AreaCode + " ]", Tools.consoleMode.full, programCode );
    }

    /**
     * 手動登録
     *
     * @param player
     * @param Owner
     */
    public static void ManualAddRegist( Player player, String Owner ) {
        String[] param = Messages.AreaCode.split( "-" );
        int bx = ( Integer.valueOf( param[0] ) * 16 ) + Config.Event_X1;
        int by = 1;
        int bz = ( Integer.valueOf( param[1] ) * 16 ) + Config.Event_Z1;
        Tools.Prt( "Location(X) : [" + bx + "]", Tools.consoleMode.max, programCode );
        Tools.Prt( "Location(Y) : [" + by + "]", Tools.consoleMode.max, programCode );
        Tools.Prt( "Location(Z) : [" + bz + "]", Tools.consoleMode.max, programCode );
        Location Loc = new Location( player.getLocation().getWorld(), bx, by, bz );
        if ( !GetSQL( Messages.AreaCode ) ) {
            AddSQL( Owner, Loc, "STONE", Messages.AreaCode );
            if ( Config.OnDynmap ) {
                DynmapControl.SetDynmapArea( Owner, Messages.AreaCode, Loc );
            }
            Tools.Prt( player, "Manual Regist X:" + bx + " Y:" + by + " Z:" + bz + " Area Code [ " + Messages.AreaCode + " ]", Tools.consoleMode.full, programCode );
        } else {
            Tools.Prt( player, "既に所有者が居ます", Tools.consoleMode.full, programCode );
        }
    }

    /**
     * 保護エリアの状態チェック＆新規登録
     *
     * @param player
     * @param block 
     */
    public static void AreaCheck( Player player, Block block ) {
        Messages.AreaCode = PackAreaCode( block.getLocation() );
        Tools.Prt( 
            "Get Location X:" + block.getLocation().getX() + " Z:" + block.getLocation().getZ() +
            " Area Code [ " + Messages.AreaCode + " ]",
            Tools.consoleMode.max, programCode );
        if ( !GetSQL( Messages.AreaCode ) ) {
            AddSQL( player.getName(), block.getLocation(), block.getType().name(), Messages.AreaCode );
            DynmapControl.SetDynmapArea( player.getName(), Messages.AreaCode, block.getLocation() );

            Messages.RepPlayer = player.getName();
            String getMessage = Messages.GetString( "GetAreaM" );
            Messages.RepPlayer = Database.Owner;
            String getSubMessage = Messages.GetString( "GetAreaS" );
            Tools.Prt( player, Messages.GetString( "GetNewBlock" )
                + " X:" + block.getLocation().getX()
                + " Y:" + block.getLocation().getY()
                + " Z:" + block.getLocation().getZ()
                + " Name[" + Database.Block + "]",
                Tools.consoleMode.normal, programCode
            );
            if ( Config.AreaRegistBroadcast ) {
                Bukkit.broadcastMessage( getMessage + getSubMessage );
                // Discord.sendMessage( Config.WebHookURL, "Event", getMessage + getSubMessage );
            } else {
                Tools.Prt( player, getMessage + getSubMessage, Tools.consoleMode.normal, programCode );
            }
            if ( Config.titlePrint ) { player.sendTitle( getMessage + Messages.GetString( "GetAreaM2" ), getSubMessage, 0, 50, 0 ); }
            Tools.Prt( "Area Code [ " + Messages.AreaCode + " ] : " + block.getLocation().toString(), Tools.consoleMode.max, programCode );
        } else {
            if ( !Database.Owner.contains( player.getName() ) || ( player.hasPermission( "Premises.admin" ) && player.isSneaking() ) ) {
                Messages.RepPlayer = ( Database.Owner.equals( player.getName() ) ? ChatColor.AQUA : ChatColor.RED ) + Database.Owner;
                Tools.Prt( player, Messages.GetString( "OwnerArea" ), Tools.consoleMode.full, programCode );
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
        Messages.AreaCode = PackAreaCode( block.getLocation() );

        Tools.Prt( 
            "Place Location key : " + block.getLocation().toString() +
            " Area Code [ " + Messages.AreaCode + " ]",
            Tools.consoleMode.max, programCode
        );

        if ( GetSQL( Messages.AreaCode ) ) {
            //  デバッグ表示 Start
            Tools.Prt( "Area : not null", Tools.consoleMode.max, programCode );
            if ( Database.Owner.contains( player.getName() ) ) {
                Tools.Prt( "Name : " + Database.Owner, Tools.consoleMode.max, programCode );
            }
            Tools.Prt( "Area Block : " + Database.Block, Tools.consoleMode.max, programCode);
            //  デバッグ表示 End
            if ( Database.Owner.contains( player.getName() ) ) {
                if ( CheckLoc( block.getLocation(), Database.Location ) ) {
                    Messages.RepPlayer = player.getName();
                    if ( Config.AreaReleaseBroadcast ) {
                        Bukkit.broadcastMessage( Messages.GetString( "FreeArea" ) );
                        // Discord.sendMessage(Config.WebHookURL, "Event", Messages.GetString( "FreeArea" ) );
                    } else {
                        Tools.Prt( player, Messages.GetString( "FreeArea" ), Tools.consoleMode.normal, programCode );
                    }
                    Messages.RepPlayer = Database.Owner;
                    DelSQL( Messages.AreaCode );
                    if ( Config.OnDynmap ) {
                        DynmapControl.DelDynmapArea( Messages.AreaCode );
                    }
                }
            }
        }
    }

    public static boolean WarningCheck( Player player, Block checkBlock ) {
        if ( ( config.getPoint( BukkitTool.getStoneName( checkBlock ) ) > 0 ) && ( !Config.ignoreStone.contains( BukkitTool.getStoneName( checkBlock ) ) ) ) {
            Messages.RepMessage = Config.JoinMessage;
            Messages.RepPlayer = player.getName();
            Tools.Prt( player, Messages.GetString( "WarningMsg" ), Tools.consoleMode.normal, programCode );
            Tools.Prt(
                ChatColor.RED + player.getDisplayName() +
                " Upper Block : " +
                BukkitTool.getStoneName( checkBlock ) +
                " (" + checkBlock.getLocation().toString() + ")",
                Tools.consoleMode.full, programCode
            );
            if ( Config.titlePrint ) {
                player.sendTitle(
                    Messages.GetString( "WarnTitleM" ),
                    Messages.GetString( "WarnTitleS" ),
                    0, 50, 0
                );
            }
            return true;
        } else return false;
    }

    public static void AllClear( Player player ) {
        Tools.Prt( player, ChatColor.GREEN + "Clear All Area Markings", programCode );

        try ( Connection con = DriverManager.getConnection( Database.DB_URL ) ) {
            Statement stmt = con.createStatement();
            String sql = "SELECT * FROM area ORDER BY AreaCode DESC;";
            Tools.Prt( "SQL : " + sql, Tools.consoleMode.max , programCode );
            ResultSet rs = stmt.executeQuery( sql );
            while( rs.next() ) {
                DynmapControl.DelDynmapArea( rs.getString( "AreaCode" ) );
            }
            con.close();
        } catch ( SQLException e ) {
            Tools.Prt( ChatColor.RED + "Error Area Marking Delete : " + e.getMessage(), programCode );
        }

        Tools.Prt( player, ChatColor.YELLOW + "Clearing from KeyLocation...", programCode );

        try ( Connection con = DriverManager.getConnection( Database.DB_URL ) ) {
            String sql = "DELETE FROM area;";
            Tools.Prt( "SQL : " + sql, Tools.consoleMode.max , programCode );
            PreparedStatement preparedStatement = con.prepareStatement( sql );
            preparedStatement.executeUpdate();
            con.close();
        } catch ( SQLException e ) {
            Tools.Prt( ChatColor.RED + "Error All Delete : " + e.getMessage(), programCode );
        }
    }

    public static int AreaCount( String AreaCode ) {
        int NGBlock = 0;
        String[] param = AreaCode.split( "-" );
        int bx = Integer.valueOf( param[0] );
        int bz = Integer.valueOf( param[1] );
        Tools.Prt( "Location(X) : [" + bx + "]", Tools.consoleMode.max, programCode );
        Tools.Prt( "Location(Z) : [" + bz + "]", Tools.consoleMode.max, programCode );
        int lx = ( bx * 16 ) + Config.Event_X1;
        int lz = ( bz * 16 ) + Config.Event_Z1;
        int hx = ( ( bx + 1 ) * 16 ) + Config.Event_X1 - 1;
        int hz = ( ( bz + 1 ) * 16 ) + Config.Event_Z1 - 1;

        Tools.Prt( "Area " + lx + "," + lz + " - " + hx + "," + hz, Tools.consoleMode.max, programCode );
        for ( int x = lx; x <= hx; ++x ) {
            for ( int y = 1; y <= 256; ++y ) {
                for ( int z = lz; z <= hz; ++z ) {
                    Location loc = new Location( Bukkit.getWorld( Config.Event_World ), x, y, z, 0, 0 );
                    if ( config.getPoint( loc.getBlock().getType().toString() ) > 0 ) {
                        NGBlock += config.getPoint( loc.getBlock().getType().toString() );
                        Tools.Prt( loc.getBlock().getType().toString() + "[" + x + "," + y + "," + z +"]", Tools.consoleMode.max, programCode );
                    }
                }
            }
        }
        Tools.Prt( "Area " + lx + "," + lz + " - " + hx + "," + hz + " : " + NGBlock, Tools.consoleMode.full, programCode );
        return NGBlock;
    }

    public static int GetRegist( String Owner, boolean Output, Player player ) {
        try ( Connection con = DriverManager.getConnection( Database.DB_URL ) ) {
            Statement stmt = con.createStatement();
            String sql = "SELECT * FROM area WHERE Owner = '" + Owner + "' ORDER BY AreaCode ASC;";
            Tools.Prt( "SQL : " + sql, Tools.consoleMode.max , programCode );
            ResultSet rs = stmt.executeQuery( sql );
            int AC = 0;
            if ( Output ) Tools.Prt( player, "あなたの掘削未完リスト", programCode );
            while( rs.next() ) {
                if ( AreaCount( rs.getString( "AreaCode" ) ) > 0 ) {
                    AC++;
                    if ( Output ) Tools.Prt( player, rs.getString( "AreaCode" ), programCode );
                }
            }
            con.close();
            Tools.Prt( "Area Regist Count : " + AC, Tools.consoleMode.full, programCode );
            return AC;
        } catch ( SQLException e ) {
            Tools.Prt( ChatColor.RED + "Error GetRegist : " + e.getMessage(), programCode );
            return 0;
        }
    }

    public static int GetRegistCount( String Owner ) {
        return GetRegist( Owner, false, null );
    }
}

