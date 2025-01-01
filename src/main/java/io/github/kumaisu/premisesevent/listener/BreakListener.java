/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.kumaisu.premisesevent.listener;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import io.github.kumaisu.premisesevent.Lib.Tools;
import io.github.kumaisu.premisesevent.config.Config;
import io.github.kumaisu.premisesevent.config.Messages;
import io.github.kumaisu.premisesevent.database.Database;
import io.github.kumaisu.premisesevent.database.AreaManager;
import io.github.kumaisu.premisesevent.utility.BukkitTool;
import static io.github.kumaisu.premisesevent.PremisesEvent.config;
import static io.github.kumaisu.premisesevent.PremisesEvent.pc;
import static io.github.kumaisu.premisesevent.config.Config.programCode;

/**
 *
 * @author sugichan
 */
public class BreakListener implements Listener {

    public Plugin plugin;

    /**
     *
     * @param plugin
     */
    public BreakListener( Plugin plugin ) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents( this, plugin );
    }

    public void ScoreStand( Location location, int score ) {
        Location loc = location;
        //  loc.add( 0.5, 0.5, 0.5 );
        loc.add( Config.pt_x, Config.pt_y, Config.pt_z );
        ArmorStand stand = location.getWorld().spawn( loc, ArmorStand.class );
        stand.setGravity( true );
        //stand.setMarker( true );
        stand.setSmall( true );
        stand.setBasePlate( false );
        stand.setCustomName( String.format( "§a%d", score ) );
        stand.setCustomNameVisible( true );
        stand.setVisible( false );
        stand.setInvulnerable( true );
        Bukkit.getServer().getScheduler().runTaskTimer( plugin, () -> { stand.remove(); }, Config.pt_delay, Config.pt_delay );
        //  Bukkit.getServer().getScheduler().scheduleAsyncDelayedTask( plugin, () -> { if ( stand.isOnGround() ) { stand.remove(); } }, 60 );
    }

    /**
     * ブロックが破壊された時の処理
     *
     * @param event
     */
    @EventHandler
    public void onBlockBreak( BlockBreakEvent event ) {
        if ( Config.EventName.equals( "none" ) ) return;

        Player player = event.getPlayer();
        Messages.RepPlayer = player.getName();

        if ( !player.getLocation().getWorld().getName().equals( Config.Event_World ) ) return;
        if ( Config.CreativeCount && player.getGameMode() == GameMode.CREATIVE ) return;
        if ( Config.Field && !AreaManager.CheckArea( event.getBlock().getLocation() ) ) return;

        //  イベント参加判定
        switch ( pc.get( player.getUniqueId() ).getEntry() ) {
            case 0:
                if ( !Config.breakFree ) {
                    Tools.Prt( player, Messages.GetString( "RequestJoin" ), Tools.consoleMode.normal, programCode );
                    event.setCancelled( true );
                }
                return;
            case 1:
                // 参加者なので、スルー
                break;
            case 2:
                Tools.Prt( player, Messages.GetString( "OmitPlayer" ), Tools.consoleMode.normal, programCode );
                event.setCancelled( true );
                return;
        }

        Block block = event.getBlock();
        String blockName = BukkitTool.getStoneName( block );
        ItemStack item = player.getInventory().getItemInMainHand();

        //  禁止判定
        Location loc = block.getLocation();
        loc.setY( loc.getY() + 1 );
        Block checkBlock = loc.getBlock();
        if ( player.hasPermission( "Premises.warning" ) && Config.Field ) {
            switch ( Config.UpperBlock ) {
                case Block:
                    if ( AreaManager.WarningCheck( player, checkBlock ) ) {
                        player.addPotionEffect( new PotionEffect( PotionEffectType.SLOWNESS, 30, 2 ) );
                        event.setCancelled( true );
                        return;
                    }
                    break;
                case Warning:
                    if ( AreaManager.WarningCheck( player, checkBlock ) ) {
                        player.addPotionEffect( new PotionEffect( PotionEffectType.SLOWNESS, 200, 2 ) );
                    }
                default:
            }
        }

        //  ツール判定
        if ( Config.breakTool && ( item.getType() != Material.TORCH ) ) {
            if (
                    ( item.getType() == Material.AIR ) ||
                    ( !item.getItemMeta().hasDisplayName() ) ||
                    ( !item.getItemMeta().getDisplayName().equalsIgnoreCase( Config.EventToolName ) )
               ) {
                Tools.Prt( player, Messages.GetString( "NoEventTool" ), Tools.consoleMode.full, programCode );
                event.setCancelled( true );
                return;
            }
        }

        //  エリア関連チェック
        if ( Config.Field && ( Config.PlayerAlarm != Config.UpperMode.None ) ) {
            if ( !AreaManager.GetSQL( AreaManager.PackAreaCode( block.getLocation() ) ) ) {
                if ( ( Config.MAX_REGIST > 0 ) && ( AreaManager.GetRegistCount( player.getName() ) >= Config.MAX_REGIST ) ) {
                    Tools.Prt( player, Messages.GetString( "OverRegist" ), Tools.consoleMode.full, programCode );
                    player.addPotionEffect( new PotionEffect( PotionEffectType.SLOWNESS, 30, 2 ) );
                    event.setCancelled( true );
                    return;
                }
            } else {
                Tools.Prt( "AreaCheck " + Database.Owner + " : " + player.getName(), Tools.consoleMode.max,programCode );
                if ( ( Config.PlayerAlarm == Config.UpperMode.Block ) && ( !Database.Owner.equals( player.getName() ) ) ) {
                    Tools.Prt( player, Messages.GetString( "OtherRegist" ), Tools.consoleMode.full, programCode );
                    player.addPotionEffect( new PotionEffect( PotionEffectType.SLOWNESS, 30, 2 ) );
                    event.setCancelled( true );
                    return;
                }
            }
            AreaManager.AreaCheck( player, block );
        }

        //  機能看板処理
        if ( blockName.contains( "SIGN" ) ) {
            Tools.Prt( "this is Sign Block : " + blockName, Tools.consoleMode.max, programCode );
            Sign sign = ( Sign ) block.getState();
            if (
                ( !Config.SignPlace ) &&
                ( !player.hasPermission( "Premises.admin" ) ) &&
                ( sign.getLine( 3 ).equals( ChatColor.DARK_PURPLE + "Premises" + ChatColor.RED  + " " ) )
            ) {
                event.setCancelled( true );
                return;
            }
        }

        //  ブロック処理
        if ( Config.stones.contains( blockName ) ) {
            Tools.Prt(
                player.getDisplayName() + " get " + blockName + " Point: " + config.getPoint( blockName ),
                Tools.consoleMode.max, programCode
            );

            if ( config.getPoint( blockName )<0 ) {
                Tools.Prt(
                    ChatColor.RED + "Warning " +
                    ChatColor.AQUA + player.getDisplayName() +
                    ChatColor.RED + " broke a " +
                    ChatColor.YELLOW + blockName,
                    Tools.consoleMode.normal, programCode
                );

                switch( Config.difficulty ) {
                    case Easy:
                        Tools.Prt( player, Messages.GetString( "NoBreak" ), Tools.consoleMode.full, programCode );
                        player.addPotionEffect( new PotionEffect( PotionEffectType.SLOWNESS, 30, 2 ) );
                        event.setCancelled( true );
                        return;
                    case Normal:
                        Tools.Prt( player, Messages.GetString( "DontBreak" ), Tools.consoleMode.full, programCode );
                        if ( Config.titlePrint ) {
                            player.sendTitle(
                                Messages.GetString( "NoBreakTitleM" ),
                                Messages.GetString( "NoBreakTitleS" ),
                                0, 100, 0 );
                        }
                        break;
                    case Hard:
                        break;
                }
            }

            if ( config.getPoint( blockName ) != 0 ) {
                if ( Config.PointTip ) {
                    ScoreStand( block.getLocation(), config.getPoint( blockName ) );
                }
                pc.get( player.getUniqueId() ).addStoneCount( blockName, ( config.getPoint( blockName ) < 0 ) );
                pc.get( player.getUniqueId() ).addScore( player, config.getPoint( blockName ) );
                player.setPlayerListName(
                    pc.get( player.getUniqueId() ).getListName() + " " +
                    ChatColor.YELLOW + String.format( "%8d", pc.get( player.getUniqueId() ).getScore() )
                );
            }

        } else {
            Tools.Prt(
                ChatColor.AQUA + player.getDisplayName() +
                ChatColor.LIGHT_PURPLE + " [" +
                ChatColor.GOLD + block.getType().toString() +
                ChatColor.LIGHT_PURPLE + "] is not a target",
                Tools.consoleMode.full, programCode );
        }

        if ( item.getType() == Material.AIR ) return;

        if ( item.getItemMeta().hasDisplayName() ) {
            if ( item.getItemMeta().getDisplayName().equalsIgnoreCase( Config.EventToolName ) ) {
                if ( ( item.getType().getMaxDurability() * Config.Repair ) <= item.getDurability() ) {
                    Tools.Prt( player, Messages.GetString( "ToolWarning" ), Tools.consoleMode.max, programCode );
                    if ( Config.titlePrint ) {
                        player.sendTitle(
                            Messages.GetString( "ToolWarningTitleM" ),
                            Messages.GetString( "ToolWarningTitleS" ),
                            0, 50, 0
                        );
                    }
                }
            }
        }
    }
}
