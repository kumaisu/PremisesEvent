/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.premisesevent;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

/**
 *
 * @author sugichan
 */
public class PremisesEvent extends JavaPlugin implements Listener {

    private Config config;
    private PlayerControl pc;
    private final String OBJECTIVE_NAME = "Premises";
    private Scoreboard board;
    private Objective obj;
    private Score score;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents( this, this );
        config = new Config( this );
        pc = new PlayerControl( ( Plugin ) this );

        board = Bukkit.getServer().getScoreboardManager().getNewScoreboard();
        obj = board.registerNewObjective( OBJECTIVE_NAME, "dummy" );
        obj.setDisplayName( "Mining Count" );
        obj.setDisplaySlot( DisplaySlot.SIDEBAR );
     }

    @EventHandler
    public void onPlayerJoin( PlayerJoinEvent e){
        Player p = e.getPlayer();
        
        p.setScoreboard( board );
        
        score = obj.getScore( ChatColor.GREEN + "Score:" );
    }
    
    @EventHandler
    public void onBlockPlace( BlockPlaceEvent event ) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        String blockName = getStoneName( block );
        block.setMetadata( "PLACED", new FixedMetadataValue( ( Plugin ) this, true ) );
        player.sendMessage( ChatColor.YELLOW + "Set Custom MetaData" );
        player.sendMessage( "You Loss " + blockName + " Point: " + config.getPoint( blockName ) );
    }
    
    @EventHandler
    public void onBlockBreak( BlockBreakEvent event ) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Material material = block.getType();
        String blockName = getStoneName( block );

        player.sendMessage( "You get " + blockName + " Point: " + config.getPoint( blockName ) + ChatColor.YELLOW + " (" + ( block.hasMetadata( "PLACED" ) ? "Placed":"Naturally" ) + ")" );

        pc.load( player );
        pc.addScore( config.getPoint( blockName ) );
        pc.addStoneCount( blockName );
        pc.save( player );

        // 全プレイヤーの現在の体力を反映します
        score.setScore( pc.getScore() );
    }
    
    @Override
    public boolean onCommand(CommandSender sender,Command cmd, String commandLabel, String[] args) {
        if ( !( sender instanceof Player ) ) {
            Bukkit.getServer().getConsoleSender().sendMessage( ChatColor.RED + "コマンドはコンソールから操作できません" );
            return false;
        }
        Player p = (Player)sender;
        if ( cmd.getName().equalsIgnoreCase( "Premises" ) ) {
            if ( args.length > 0 ) {
                /*
                /<command> join PlayerName
                /<command> status [PlayerName]
                */
                switch ( args[0] ) {
                    case "join":
                        pc.JoinPlayer( sender );
                        return true;
                    case "status":
                        if ( args.length > 1 ) {
                            Bukkit.getServer().getConsoleSender().sendMessage( ChatColor.RED + "Look other Player Status: " + args[1] );
                            // pc.load( 設定したプレイヤー構造体を取得する方法探す );
                        } else {
                            Bukkit.getServer().getConsoleSender().sendMessage( ChatColor.RED + "Look other Player Status: This Player" );
                            pc.load( (Player)sender );
                        }

                        sender.sendMessage( ChatColor.GREEN + "--------------------------------------------------" );
                        sender.sendMessage( ChatColor.AQUA + "Ores mined by: " + p.getDisplayName() );
                        sender.sendMessage( ChatColor.GOLD + "SCORE: " + ChatColor.WHITE + pc.getScore() );

                        List<String> SL = config.getStones();
                        for( int i = 0; i<SL.size(); i++ ) {
                            int sc = pc.getStoneCount( SL.get( i ) );
                            if ( sc>0 ) {
                                sender.sendMessage( ChatColor.GREEN + SL.get( i ) + ": " + ChatColor.YELLOW + sc );
                            }
                        }

                        /*
                        sender.sendMessage( ChatColor.AQUA + "Stone: " + p.getStatistic(Statistic.MINE_BLOCK, Material.STONE));
                        */
                        sender.sendMessage( ChatColor.GREEN + "--------------------------------------------------" );
                        return true;
                    default:
                        sender.sendMessage( ChatColor.RED + "[Premises] Unknown Command" );
                        return false;
                }
            }
        }
        return false;
    }

    public String getStoneName( Block b ) {
        String retStr = b.getType().toString();
        if ( b.getType().equals( Material.STONE ) ) {
            switch ( b.getData() ) {
                case 1:
                    retStr = "GRANITE";
                    break;
                case 3:
                    retStr = "DIORITE";
                    break;
                case 5:
                    retStr = "ANDESITE";
                    break;
            }
        }
        return retStr;
    }

}
