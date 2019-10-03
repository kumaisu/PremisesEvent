/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.premisesevent.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.bukkit.ChatColor;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.mycompany.kumaisulibraries.Tools;
import com.mycompany.premisesevent.config.Config;
import static com.mycompany.premisesevent.config.Config.programCode;

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
            if ( Database.dataSource.isClosed() ) {
                Tools.Prt( ChatColor.RED + "database closed.", Tools.consoleMode.full, programCode );
                disconnect();
            } else {
                Tools.Prt( ChatColor.AQUA + "dataSource is not null", Tools.consoleMode.max, programCode );
                return;
            }
        }

        // HikariCPの初期化
        HikariConfig config = new HikariConfig();
        
        // データのファイルパス
        String dbPath = Config.databaseName;

        // SQLite用ドライバを設定
        config.setDriverClassName( "org.sqlite.JDBC" );
    
        // 「jdbc:sqlite:/data/sqlite.db」の様に指定する。
        config.setJdbcUrl( "jdbc:sqlite:" + dbPath );

        // 最小接続数まで接続を確保できない時に例外を投げる
        config.setInitializationFailFast( true );
        // 接続をテストするためのクエリ
        config.setConnectionInitSql( "SELECT 1" );

        // 接続
        Database.dataSource = new HikariDataSource( config );
    }

    /**
     * Database Close 処理
     */
    public static void disconnect() {
        if ( Database.dataSource != null ) {
            Database.dataSource.close();
        }
    }

    /**
     * Database Table Initialize
     */
    public static void TableUpdate() {
        try ( Connection con = Database.dataSource.getConnection() ) {
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
