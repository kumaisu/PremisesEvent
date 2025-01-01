/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.kumaisu.premisesevent.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.*;
import org.bukkit.ChatColor;
import io.github.kumaisu.premisesevent.Lib.Tools;
import io.github.kumaisu.premisesevent.config.Config;
import static io.github.kumaisu.premisesevent.config.Config.programCode;

/**
 *
 * @author sugichan
 */
public class SQLControl {
    /**
     * Database Open(接続) 処理
     */
    public static void connect() {
        if ( Database.dataSource != null ) {
            try {
                if ( Database.dataSource.isClosed() ) {
                    Tools.Prt( ChatColor.RED + "database closed.", Tools.consoleMode.full, programCode );
                    disconnect();
                } else {
                    Tools.Prt( ChatColor.AQUA + "dataSource is not null", Tools.consoleMode.max, programCode );
                    return;
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        // 接続
        Database.DB_URL = "jdbc:sqlite:" + Config.databaseName;
    }

    /**
     * Database Close 処理
     */
    public static void disconnect() {
        if ( Database.dataSource != null ) {
            try {
                Database.dataSource.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Database Table Initialize
     */
    public static void TableUpdate() {
        try ( Connection con = DriverManager.getConnection( Database.DB_URL ) ) {
            //  イベントゾーンテーブル
            //      AreaCode : varchar(7)   AreaCode
            //      Owner : varchar(20)     PlayerName
            //      World : varchar(20)     world
            //      x : int
            //      y : int
            //      z ; int
            //      Block : varchar(10)     BlockName
            //      Date : DATETIME         Stamp
            //  存在すれば、無視される
            String sql = "CREATE TABLE IF NOT EXISTS area( AreaCode varchar(7), Owner varchar(20), World varchar(20), x int, y int, z int, Block varchar(10), Date DATETIME );";
            Tools.Prt( "SQL : " + sql, Tools.consoleMode.max , programCode );
            PreparedStatement preparedStatement = con.prepareStatement( sql );
            preparedStatement.executeUpdate();
            Tools.Prt( ChatColor.AQUA + "dataSource Open Success.", Tools.consoleMode.full, programCode );
            con.close();
        } catch( SQLException e ) {
            Tools.Prt( ChatColor.RED + "Connection Error : " + e.getMessage(), programCode);
        }
    }
}
