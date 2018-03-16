/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.premisesevent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

/*
 *
 * @author sugichan
 */
public class Config {

    private final Plugin plugin;
    private FileConfiguration config = null;

    private final Map< String, Integer > map = new HashMap<>();
    private List< String > stones;
    
    public Config(Plugin plugin) {
        this.stones = new ArrayList<>();
        this.plugin = plugin;
        plugin.getLogger().info( "Config Loading now..." );
        load();
    }
    
    /*
     * 設定をロードします
     */
    public void load() {
        // 設定ファイルを保存
        plugin.saveDefaultConfig();
        if (config != null) { // configが非null == リロードで呼び出された
            plugin.getLogger().info( "Config Reloading now..." );
            plugin.reloadConfig();
        }
        config = plugin.getConfig();
        
        stones = new ArrayList<>();
        List< String > getstr = ( List< String > ) config.getList( "Count" );
        for( int i = 0; i<getstr.size(); i++ ) {
            String[] param = getstr.get(i).split(",");
            map.put( param[0], Integer.valueOf( param[1] ) );
            stones.add( param[0] );
        }
    }

    public List getStones() {
        return stones;
    }
    
    public int getPoint( String sd ) {
        return map.get( sd );
    }
    
}
