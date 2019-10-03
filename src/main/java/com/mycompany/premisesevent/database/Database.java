/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.premisesevent.database;

import org.bukkit.Location;
import com.zaxxer.hikari.HikariDataSource;

/**
 *
 * @author sugichan
 */
public class Database {
    public static HikariDataSource dataSource = null;

    public static String AreaCode = null;
    public static String Owner = null;
    public static Location Location = null;
    public static String Block = null;
    public static String GetDate = null;
}
