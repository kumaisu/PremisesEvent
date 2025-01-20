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

    private static final String PREMISES_COMMAND = "premises";
    private static final String AREA_COMMAND = "area";

    public static ConfigManager configManager;
    public static MessagesManager messagesManager;
    public static Map<UUID, PlayerControl> playerControlMap = new HashMap<>();

    /**
     * 起動シーケンス
     */
    @Override
    public void onEnable() {
        initializeConfig();
        initializeListeners();
        initializeCommands();
    }

    private void initializeConfig() {
        Config.DataFolder = this.getDataFolder().toString();
        this.configManager = new ConfigManager(this); // Ensure ConfigManager is correctly defined and has a matching constructor
        this.messagesManager = new MessagesManager(this);

        if (Config.Field) {
            Tools.Prt("Open SQLite Database : " + Config.databaseName, Tools.consoleMode.max, Config.programCode);
            SQLControl.connect();
            SQLControl.TableUpdate();
        }
    }

    private void initializeListeners() {
        getServer().getPluginManager().registerEvents(this, this);
        new PlayerListener(this);
        new ClickListener(this);
        new PlaceListener(this);
        new BreakListener(this);
        new ChangeWorldListener(this);
    }

    private void initializeCommands() {
        getCommand( PREMISES_COMMAND ).setExecutor(new PECommand(this));
        getCommand( AREA_COMMAND ).setExecutor(new AreaCommand(this));
    }

    /**
     * 終了シーケンス
     * ログインしたままのプレイヤーが存在する場合は、ここで保存処理を行う
     */
    @Override
    public void onDisable(){
        Tools.Prt( "Disable processing...", programCode );
        playerControlMap.entrySet().forEach( ( entry ) -> {
            if ( playerControlMap.get( entry.getKey() ).getEntry() != 0 ) {
                playerControlMap.get( entry.getKey() ).save();
                Tools.Prt( ChatColor.AQUA + playerControlMap.get( entry.getKey() ).getDisplayName() + " logged out, Saved the Score", programCode );
            }
        } );
        // SQLControl.disconnect();
    }
}
