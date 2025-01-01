/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.kumaisu.premisesevent.database;

import org.bukkit.Location;
import java.sql.Connection;

/**
 *
 * @author sugichan
 */
public class Database {
    public static Connection dataSource = null;

    public static String AreaCode = null;
    public static String Owner = null;
    public static Location Location = null;
    public static String Block = null;
    public static String GetDate = null;
    public static String DB_URL = "";
}
