/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.premisesevent.listener;

import org.bukkit.plugin.Plugin;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import com.mycompany.kumaisulibraries.Tools;
import com.mycompany.premisesevent.Player.PlayerStatus;
import com.mycompany.premisesevent.Player.TopList;
import com.mycompany.premisesevent.config.Config;
import com.mycompany.premisesevent.database.AreaManager;
import static com.mycompany.premisesevent.PremisesEvent.pc;
import static com.mycompany.premisesevent.config.Config.programCode;

/**
 *
 * @author sugichan
 */
public class ClickListener implements Listener {

    private String DataFolder = "";
    public static int firstLoc_X;
    public static int firstLoc_Y;
    public static int firstLoc_Z;
    public static int secondLoc_X;
    public static int secondLoc_Y;
    public static int secondLoc_Z;
    public static boolean ClickFlag = true;
    
    /**
     *
     * @param plugin
     */
    public ClickListener( Plugin plugin ) {
        plugin.getServer().getPluginManager().registerEvents( this, plugin );
        DataFolder = plugin.getDataFolder().toString();
    }

    /**
     * 座標の取得のメソッド
     *
     * @param event
     */
    @EventHandler
    public void onClick( PlayerInteractEvent event ) {
        if ( Config.EventName.equals( "none" ) ) return;

        if ( event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_AIR ) { return; }

        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        ItemStack item = player.getInventory().getItemInMainHand();
        Material material = block.getType();
        Tools.Prt( "HandTool = " + item.getType().toString() + ", Material = " + material.name(), Tools.consoleMode.max, programCode );

        if (
            ( "WOOD_AXE".equals( item.getType().toString() ) )
            ||
            ( "WOODEN_AXE".equals( item.getType().toString() ) )
        ) {
            switch ( event.getAction() ) {
                case LEFT_CLICK_BLOCK:
                    if ( !( firstLoc_X == block.getLocation().getBlockX() &&
                            firstLoc_Y == block.getLocation().getBlockY() &&
                            firstLoc_Z == block.getLocation().getBlockZ()
                    ) ) {
                        firstLoc_X = block.getLocation().getBlockX();
                        firstLoc_Y = block.getLocation().getBlockY();
                        firstLoc_Z = block.getLocation().getBlockZ();
                        Tools.Prt(
                            ChatColor.AQUA +
                            "First Target Location = X:" + firstLoc_X +
                            " Y:" + firstLoc_Y +
                            " Z:" + firstLoc_Z,
                            Tools.consoleMode.full, programCode
                        );
                    }
                    break;
                case RIGHT_CLICK_BLOCK:
                    if ( !( secondLoc_X == block.getLocation().getBlockX() &&
                        secondLoc_Y == block.getLocation().getBlockY() &&
                        secondLoc_Z == block.getLocation().getBlockZ()
                    ) ) {
                        secondLoc_X = block.getLocation().getBlockX();
                        secondLoc_Y = block.getLocation().getBlockY();
                        secondLoc_Z = block.getLocation().getBlockZ();
                        Tools.Prt(
                            ChatColor.AQUA +
                            "Second Target Location = X:" + secondLoc_X +
                            " Y:" + secondLoc_Y +
                            " Z:" + secondLoc_Z,
                            Tools.consoleMode.full, programCode
                        );
                    }
                    break;
                default:
                    break;
            }
        }

        try {
            Sign sign = ( Sign ) block.getState();
            if ( sign.getLine( 3 ).equals( ChatColor.DARK_PURPLE + "Premises" + ChatColor.RED  + " " ) ) {
                switch ( sign.getLine( 0 ) ) {
                    case "[P-Get]":
                        pc.get( player.getUniqueId() ).getEventItem( player, sign.getLine( 1 ) );
                        break;
                    case "[P-Join]":
                        pc.get( player.getUniqueId() ).JoinPlayer( player );
                        break;
                    case "[P-Status]":
                        PlayerStatus.Print( player, "" );
                        break;
                    case "[P-Update]":
                        if ( pc.get( player.getUniqueId() ).getEntry() == 1 ) pc.get( player.getUniqueId() ).ToolUpdate( player, false );
                        break;
                    case "[P-TOP]":
                        if ( pc.get( player.getUniqueId() ).getEntry() == 1 ) pc.get( player.getUniqueId() ).save();
                        TopList TL = new TopList( DataFolder );
                        TL.Top( player, Tools.consoleMode.max );
                        break;
                    default:
                }
            }
        } catch ( ClassCastException e ) {}

        if ( player.isSneaking() ) {
            if ( item.hasItemMeta() && item.getItemMeta().hasDisplayName() ) {
                if ( ( item.getItemMeta().getDisplayName().equals( Config.EventToolName ) ) &&
                    ( event.getAction() == Action.RIGHT_CLICK_BLOCK )
                ) {
                    if ( Config.Field && ClickFlag ) {
                        AreaManager.AreaGet( player, block );
                    }
                    ClickFlag = !ClickFlag;
                }
            }
        }
    }
}
