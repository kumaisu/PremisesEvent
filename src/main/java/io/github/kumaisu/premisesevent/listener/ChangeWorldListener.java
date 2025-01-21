/*
 *  Copyright (c) 2019 sugichan. All rights reserved.
 */
package io.github.kumaisu.premisesevent.listener;

import org.bukkit.plugin.Plugin;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import static io.github.kumaisu.premisesevent.PremisesEvent.pc;

/**
 *
 * @author sugichan
 */
public class ChangeWorldListener implements Listener {

    /**
     *
     * @param plugin
     */
    public ChangeWorldListener( Plugin plugin ) {
        plugin.getServer().getPluginManager().registerEvents( this, plugin );
    }

    /**
     * プレイヤーがWorldを移動した時にTAB情報を書き換える
     *
     * @param event
     */
    @EventHandler( priority = EventPriority.HIGHEST )
    public void onChangeWorld( PlayerChangedWorldEvent event ) {
        Player player = event.getPlayer();
        if ( pc.containsKey( player.getUniqueId() ) ) {
            pc.get( player.getUniqueId() ).setListName( player.getPlayerListName() );
        }
    }
}
