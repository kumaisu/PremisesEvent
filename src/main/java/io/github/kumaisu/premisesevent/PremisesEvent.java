/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.kumaisu.premisesevent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import io.github.kumaisu.premisesevent.Lib.Tools;
import io.github.kumaisu.premisesevent.Player.PlayerControl;
import io.github.kumaisu.premisesevent.listener.PlayerListener;
import io.github.kumaisu.premisesevent.listener.ClickListener;
import io.github.kumaisu.premisesevent.listener.PlaceListener;
import io.github.kumaisu.premisesevent.listener.BreakListener;
import io.github.kumaisu.premisesevent.listener.ChangeWorldListener;
import io.github.kumaisu.premisesevent.command.AreaCommand;
import io.github.kumaisu.premisesevent.command.PECommand;
import io.github.kumaisu.premisesevent.config.Config;
import io.github.kumaisu.premisesevent.config.ConfigManager;
import io.github.kumaisu.premisesevent.config.MessagesManager;
import io.github.kumaisu.premisesevent.database.SQLControl;
import static io.github.kumaisu.premisesevent.config.Config.programCode;

/**
 *
 * @author sugichan
 */
public class PremisesEvent extends JavaPlugin implements Listener {

    public static ConfigManager config;
    public static MessagesManager messe;
    public static Map<UUID, PlayerControl> pc = new HashMap<>();

    /**
     * 起動シーケンス
     */
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents( this, this );
        Config.DataFolder = this.getDataFolder().toString();
        config = new ConfigManager( this );
        messe = new MessagesManager( this );
        if ( Config.Field ) {
            Tools.Prt( "Open SQLite Database : " + Config.databaseName, Tools.consoleMode.max, programCode );
            SQLControl.connect();
            SQLControl.TableUpdate();
        }
        new PlayerListener( this );
        new ClickListener( this );
        new PlaceListener( this );
        new BreakListener( this );
        new ChangeWorldListener( this );
        getCommand( "premises" ).setExecutor( new PECommand( this ) );
        getCommand( "area" ).setExecutor( new AreaCommand( this ) );
    }

    /**
     * 終了シーケンス
     * ログインしたままのプレイヤーが存在する場合は、ここで保存処理を行う
     */
    @Override
    public void onDisable(){
        Tools.Prt( "Disable processing...", programCode );
        pc.entrySet().forEach( ( entry ) -> {
            if ( pc.get( entry.getKey() ).getEntry() != 0 ) {
                pc.get( entry.getKey() ).save();
                Tools.Prt( ChatColor.AQUA + pc.get( entry.getKey() ).getDisplayName() + " logged out, Saved the Score", programCode );
            }
        } );
        // SQLControl.disconnect();
    }
}
