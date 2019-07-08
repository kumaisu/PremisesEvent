/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.premisesevent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import com.mycompany.kumaisulibraries.Tools;
import com.mycompany.kumaisulibraries.Tools.consoleMode;
import com.mycompany.kumaisulibraries.BukkitTool;
import com.mycompany.premisesevent.Item.ItemControl;
import com.mycompany.premisesevent.Player.PlayerControl;
import com.mycompany.premisesevent.Player.TopList;
import com.mycompany.premisesevent.command.PECommand;
import com.mycompany.premisesevent.config.Config;
import static com.mycompany.premisesevent.config.Config.programCode;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 *
 * @author sugichan
 */
public class PremisesEvent extends JavaPlugin implements Listener {

    private PremisesEvent instance;
    
    public static Config config;
    public static Map<UUID, PlayerControl> pc = new HashMap<>();
    public static int firstLoc_X;
    public static int firstLoc_Y;
    public static int firstLoc_Z;
    public static int secondLoc_X;
    public static int secondLoc_Y;
    public static int secondLoc_Z;

    
    /**
     * 起動シーケンス
     */
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents( this, this );
        config = new Config( this );
        getCommand( "premises" ).setExecutor( new PECommand( this ) );
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
    }

    /**
     * プレイヤーのログイン時処理、参加者であれば、スコアをメモリにロードする
     * スコアファイルにプレゼントフラグがある場合は適宜プレゼント処理を行う
     * ※今の所スコアファイルのプレゼントフラグは直接編集でのみ変更可能
     *
     * @param event
     */
    @EventHandler
    public void onPlayerJoin( PlayerJoinEvent event ) {
        Player p = event.getPlayer();

        pc.put( p.getUniqueId(), new PlayerControl( p, this.getDataFolder().toString() ) );
        pc.get( p.getUniqueId() ).load();

        if ( pc.get( p.getUniqueId() ).getEntry() != 1 ) {
            Tools.Prt( ChatColor.RED + p.getDisplayName() + " has not joined the this Event", programCode );
            return;
        }

        pc.get( p.getUniqueId() ).ScoreBoardEntry( p );
        Tools.Prt( ChatColor.AQUA + p.getDisplayName() + " is participating in the this Event", programCode );

        ItemControl ic = new ItemControl();
        if ( pc.get( p.getUniqueId() ).getPresentFlag() ) {
            Tools.Prt( p, ChatColor.YELLOW + "イベント装備の再配布", Tools.consoleMode.normal, programCode );
            ic.ItemPresent( p );
        }
        if ( pc.get( p.getUniqueId() ).getUpdateFlag() ) {
            Tools.Prt( p, ChatColor.YELLOW + "イベントツールの再配布", Tools.consoleMode.normal, programCode );
            for( int i = 0; i<Config.tools.size(); i++ ) {
                ic.ToolPresent( p, Material.getMaterial( Config.tools.get( i ) ), Config.EventToolName );
            }
        }
    }

    /**
     * プレイヤーのログアウト時処理、参加者であればメモリのスコアをスコアファイルに保存する
     *
     * @param event
     */
    @EventHandler
    public void onPlayerQuit( PlayerQuitEvent event ) {
        Player player = event.getPlayer();
        if ( pc.get( player.getUniqueId() ).getEntry() != 0 ) {
            Tools.Prt( ChatColor.AQUA + player.getDisplayName() + ChatColor.WHITE + " logged out, Saved the Score", programCode );

            pc.get( player.getUniqueId() ).save();
        } else {
            Tools.Prt( ChatColor.AQUA + player.getDisplayName() + ChatColor.LIGHT_PURPLE + " logged out, not Save", programCode );
        }
        pc.remove( player.getUniqueId() );
    }

    /**
     * 座標の取得のメソッド
     *
     * @param event 
     */
    @EventHandler
    public void onClick( PlayerInteractEvent event ) {
        Player player = event.getPlayer();
        Action action = event.getAction();
        Block block = event.getClickedBlock();

        ItemStack item = player.getInventory().getItemInMainHand();
        if ( item.getType() != Material.WOOD_AXE ) { return; }

        switch ( action ) {
            case LEFT_CLICK_BLOCK:
                if ( !( firstLoc_X == block.getLocation().getBlockX() &&
                        firstLoc_Y == block.getLocation().getBlockY() &&
                        firstLoc_Z == block.getLocation().getBlockZ()
                ) ) {
                    firstLoc_X = block.getLocation().getBlockX();
                    firstLoc_Y = block.getLocation().getBlockY();
                    firstLoc_Z = block.getLocation().getBlockZ();
                    Tools.Prt( player,
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
                    Tools.Prt( player,
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

    /**
     * ブロックを設置した時の処理
     * ポイントブロックを置いた場合は、スコアからポイントをマイナスする
     * ※自然生成か設置かを判断するための試作をしているが機能していない
     *
     * @param event
     */
    @EventHandler
    public void onBlockPlace( BlockPlaceEvent event ) {
        Player player = event.getPlayer();
        if ( config.CreativeCount() && player.getGameMode() == GameMode.CREATIVE ) return;
        if ( config.GetField() && !config.CheckArea( event.getBlock().getLocation() ) ) return;
        if ( pc.get( player.getUniqueId() ).getEntry() != 1 ) {
            if ( !Config.placeFree ) event.setCancelled( true );
            return;
        }

        Block block = event.getBlock();
        String blockName = BukkitTool.getStoneName( block );

        //  設置したブロックであるというフラグを設定しているが、機能していない
        block.setMetadata( "PLACED", new FixedMetadataValue( ( Plugin ) this, true ) );

        if ( !Config.stones.contains( blockName ) ) {
            if ( !Config.placeSpecified ) {
                Tools.Prt( player, "このブロックは設置できません", consoleMode.full, programCode );
                event.setCancelled( true );
            }
            Tools.Prt(
                ChatColor.AQUA + player.getDisplayName() +
                ChatColor.GREEN + " [" +
                ChatColor.GOLD + blockName +
                ChatColor.GREEN + "] is not a target",
                consoleMode.full, programCode
            );
            return;
        }

        if ( ( Config.zeroPlace ) || ( config.getPoint( blockName ) == 0 ) || ( pc.get( player.getUniqueId() ).getStoneCount( blockName ) > 0 ) ) {
            Tools.Prt( player.getDisplayName() + " Loss " + blockName + " Point: " + config.getPoint( blockName ), consoleMode.max, programCode );
            if ( config.getPoint( blockName ) != 0 ) {
                pc.get( player.getUniqueId() ).addScore( player, - config.getPoint( blockName ) );
                pc.get( player.getUniqueId() ).subStoneCount( blockName, ( config.getPoint( blockName ) < 0 ) );
                player.setPlayerListName(
                    ChatColor.WHITE + String.format( "%-12s", player.getDisplayName() ) + " " +
                    ChatColor.YELLOW + String.format( "%8d", pc.get( player.getUniqueId() ).getScore() )
                );
            }
        } else {
            if ( !Config.placeFree ) {
                Tools.Prt( player, ChatColor.RED + "これ以上このブロックは設置できません", consoleMode.full, programCode );
                if ( Config.titlePrint ) {
                    player.sendTitle(
                        ChatColor.RED + "ブロックは設置できません",
                        ChatColor.YELLOW + "イベントルールを確認してください",
                        0, 100, 0 );
                }
                event.setCancelled( true );
            }
        }
    }

    /**
     * ブロックが破壊された時の処理
     *
     * @param event
     */
    @EventHandler
    public void onBlockBreak( BlockBreakEvent event ) {
        Player player = event.getPlayer();

        if ( config.CreativeCount() && player.getGameMode() == GameMode.CREATIVE ) return;
        if ( config.GetField() && !config.CheckArea( event.getBlock().getLocation() ) ) return;

        switch ( pc.get( player.getUniqueId() ).getEntry() ) {
            case 0:
                if ( !Config.breakFree ) {
                    Tools.Prt( player, ChatColor.RED + "イベントに参加してください", consoleMode.normal, programCode );
                    event.setCancelled( true );
                }
                return;
            case 1:
                // 参加者なので、スルー
                break;
            case 2:
                Tools.Prt( player, ChatColor.RED + "イベントエリアの操作はできません", consoleMode.normal, programCode );
                event.setCancelled( true );
                return;
        }

        Block block = event.getBlock();
        Material material = block.getType();
        String blockName = BukkitTool.getStoneName( block );
        ItemStack item = player.getInventory().getItemInMainHand();

        if ( Config.breakTool && ( item.getType() != Material.TORCH ) ) {
            if (
                    ( item.getType() == Material.AIR ) ||
                    ( !item.getItemMeta().hasDisplayName() ) ||
                    ( !item.getItemMeta().getDisplayName().equalsIgnoreCase( Config.EventToolName ) )
               ) {
                Tools.Prt( player, ChatColor.RED + "指定ツールで行ってください", consoleMode.full, programCode );
                event.setCancelled( true );
                return;
            }
        }

        if ( Config.stones.contains( blockName ) ) {
            Tools.Prt(
                player.getDisplayName() + " get " + blockName + " Point: " + config.getPoint( blockName ) +
                ChatColor.YELLOW + " (" + ( block.hasMetadata( "PLACED" ) ? "Placed":"Naturally" ) + ")",
                consoleMode.max, programCode
            );

            if ( config.getPoint( blockName )<0 ) {
                Tools.Prt(
                    ChatColor.RED + "Warning " +
                    ChatColor.AQUA + player.getDisplayName() +
                    ChatColor.RED + " broke a " +
                    ChatColor.YELLOW + blockName,
                    consoleMode.full, programCode
                );

                switch( Config.difficulty ) {
                    case Easy:
                        Tools.Prt( player, ChatColor.RED + "そのブロックは破壊できません", consoleMode.full, programCode );
                        event.setCancelled( true );
                        return;
                    case Normal:
                        Tools.Prt( player, ChatColor.RED + "壊してはいけないブロックです", consoleMode.full, programCode );
                        if ( Config.titlePrint ) {
                            player.sendTitle(
                                ChatColor.RED + "破壊不可なブロックです",
                                ChatColor.YELLOW + "元に戻してくださいね",
                                0, 100, 0 );
                        }
                        break;
                    case Hard:
                        break;
                }
            }

            if ( config.getPoint( blockName ) != 0 ){
                pc.get( player.getUniqueId() ).addStoneCount( blockName, ( config.getPoint( blockName ) < 0 ) );
                pc.get( player.getUniqueId() ).addScore( player, config.getPoint( blockName ) );
                player.setPlayerListName(
                    ChatColor.WHITE + String.format( "%-12s", player.getDisplayName() ) + " " +
                    ChatColor.YELLOW + String.format( "%8d", pc.get( player.getUniqueId() ).getScore() )
                );
            }

        } else {
            Tools.Prt(
                ChatColor.AQUA + player.getDisplayName() +
                ChatColor.LIGHT_PURPLE + " [" +
                ChatColor.GOLD + block.getType().toString() +
                ChatColor.LIGHT_PURPLE + "] is not a target",
                consoleMode.full, programCode );
        }

        if ( item.getType() == Material.AIR ) return;

        if ( item.getItemMeta().hasDisplayName() ) {
            if ( item.getItemMeta().getDisplayName().equalsIgnoreCase( Config.EventToolName ) ) {
                if ( ( item.getType().getMaxDurability() * config.getRepair() ) <= item.getDurability() ) {
                    Tools.Prt( player, ChatColor.RED + "ツールの耐久値がヤバイですよ", consoleMode.max, programCode );
                    if ( Config.titlePrint ) {
                        player.sendTitle(
                            ChatColor.RED + "耐久値がヤバイですよ",
                            ChatColor.YELLOW + "アップデートして成長させましょう",
                            0, 50, 0
                        );
                    }
                }
            }
        }
    }

    /**
     * 看板ブロックを右クリック
     *
     * @param event
     */
    @EventHandler
    public void onSignClick( PlayerInteractEvent event ) {

        if ( event.getAction() != Action.RIGHT_CLICK_BLOCK ) return;

        Player player = event.getPlayer();
        Block clickedBlock = event.getClickedBlock();
        Material material = clickedBlock.getType();
        if ( material == Material.SIGN_POST || material == Material.WALL_SIGN ) {
            Sign sign = (Sign) clickedBlock.getState();
            switch ( sign.getLine(0) ) {
                case "[P-Get]":
                    pc.get( player.getUniqueId() ).getEventItem( player, sign.getLine( 1 ) );
                    break;
                case "[P-Join]":
                    pc.get( player.getUniqueId() ).JoinPlayer( player );
                    break;
                case "[P-Status]":
                    PlayerStatus( player, "" );
                    break;
                case "[P-Update]":
                    if ( pc.get( player.getUniqueId() ).getEntry() == 1 ) pc.get( player.getUniqueId() ).ToolUpdate( player, false );
                    break;
                case "[P-TOP]":
                    if ( pc.get( player.getUniqueId() ).getEntry() == 1 ) pc.get( player.getUniqueId() ).save();
                    TopList TL = new TopList( this.getDataFolder().toString() );
                    TL.Top( player, consoleMode.max );
                    break;
                default:
            }
        }
    }

    /**
     * イベント参加者のステータス表示する処理
     *
     * @param player
     * @param Other
     * @return
     */
    public boolean PlayerStatus( Player player, String Other ) {
        if ( pc.get( player.getUniqueId() ).getEntry() == 1 ) {
            if ( "".equals( Other ) ) {
                pc.get( player.getUniqueId() ).save();
                pc.get( player.getUniqueId() ).getStatus( player );
            } else {
                UUID uuid;

                Tools.Prt( player, ChatColor.RED + "Look other Player : " + Other, consoleMode.none, programCode );

                OfflinePlayer op = Bukkit.getServer().getOfflinePlayer( Other );
                if ( op.hasPlayedBefore() ) {
                    uuid = op.getUniqueId();
                    pc.put( uuid, new PlayerControl( op, this.getDataFolder().toString() ) );
                    pc.get( uuid ).load();
                } else {
                    Tools.Prt( player, ChatColor.RED + "This Player is not joined to server.", programCode );
                    return false;
                }
                pc.get( uuid ).getStatus( player );
                if ( player.getUniqueId() != uuid ) pc.remove( uuid );
            }
            return true;
        } else {
            Tools.Prt( player, ChatColor.RED + "イベントに参加していません", programCode );
            return false;
        }
    }

    /**
     * 参加者のスコアーを操作する処理
     *
     * @param player
     * @param name
     * @param score
     * @return
     */
    public boolean GiveScore( Player player, String name, String score ) {
        int scoreNum;
        Player scorePlayer;
        boolean createStat = false;
        boolean retStat;

        try {
            scoreNum = Integer.parseInt( score );
        } catch ( NumberFormatException e ) {
            Tools.Prt( player, ChatColor.RED + "指定された値が正しくありません", programCode );
            return false;
        }

        if ( player.getName().equals( name ) ) {
            scorePlayer = player;
        } else {
            Player checkPlayer = Bukkit.getServer().getPlayer( name );
            if ( checkPlayer != null ) {
                //  online
                scorePlayer = checkPlayer;
            } else {
                //  offline
                OfflinePlayer op = Bukkit.getServer().getOfflinePlayer( name );
                if ( op.hasPlayedBefore() ) {
                    scorePlayer = op.getPlayer();
                    pc.put( scorePlayer.getUniqueId(), new PlayerControl( player, this.getDataFolder().toString() ) );
                    pc.get( scorePlayer.getUniqueId() ).load();
                    createStat = true;
                } else {
                    Tools.Prt( player, ChatColor.RED + "[ " + ChatColor.YELLOW + name + ChatColor.RED + " ] はサーバーに存在しません", programCode );
                    return false;
                }
            }
        }

        if ( pc.get( scorePlayer.getUniqueId() ).getEntry() == 1 ) {
            pc.get( scorePlayer.getUniqueId() ).addScore( null, scoreNum );
            pc.get( scorePlayer.getUniqueId() ).save();
            retStat = true;
        } else {
            Tools.Prt( player, ChatColor.RED + "[ " + ChatColor.YELLOW + name + ChatColor.RED + " ] はイベントに参加していません", programCode );
            retStat = false;
        }

        if ( createStat ) pc.remove( scorePlayer.getUniqueId() );
        return retStat;
    }
}
