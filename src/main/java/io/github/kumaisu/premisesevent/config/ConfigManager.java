package io.github.kumaisu.premisesevent.config;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.plugin.Plugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import io.github.kumaisu.premisesevent.Lib.Tools;
import io.github.kumaisu.premisesevent.config.Config;
import io.github.kumaisu.premisesevent.config.Messages;
import static io.github.kumaisu.premisesevent.config.Config.programCode;

public class ConfigManager {
    private final Plugin plugin;
    private FileConfiguration config = null;
    private final Map<String, Integer> pointMap = new HashMap<>();

    private static final String IGNORE_STONE_KEY = "IgnoreStone";
    private static final String POINT_STONE_KEY = "PointStone";
    private static final String PRESENT_KEY = "Present";
    private static final String EVENT_NAME_KEY = "EventName";
    private static final String AREA_POS1_KEY = "AreaPos1";
    private static final String AREA_POS2_KEY = "AreaPos2";

    public ConfigManager(Plugin plugin) {
        Config.stones = new ArrayList<>();
        this.plugin = plugin;
        Tools.entryDebugFlag(programCode, Tools.consoleMode.print);
        Tools.Prt("Config Loading now...", programCode);
        load();
    }

    public void load() {
        plugin.saveDefaultConfig();
        if (config != null) {
            Tools.Prt("Config Reloading now...", programCode);
            plugin.reloadConfig();
        }

        config = plugin.getConfig();
        Config.ignoreStone = config.getStringList(IGNORE_STONE_KEY);
        Config.stones = new ArrayList<>();

        loadItemsWithPoints(POINT_STONE_KEY, pointMap, Config.stones);
        loadItemsWithPoints(PRESENT_KEY, Config.tools, null);

        Config.EventName = config.getString(EVENT_NAME_KEY);
        prepareDirectory(Config.EventName);

        Config.databaseName = Config.DataFolder + File.separator + Config.EventName + File.separator + "AreaData.db";

        loadEventAreaBounds();
        Config.MAX_REGIST = config.getInt("MaxRegist", 0);
        Config.JoinMessage = config.getString("JOIN_MESSAGE");

        setConfigEnums();
        Tools.setDebug( config.getString( "Debug"), programCode );
    }

    private void loadItemsWithPoints(String key, Map<String, Integer> targetMap, List<String> optionalList) {
        List<String> entries = config.getStringList(key);
        for (String entry : entries) {
            String[] params = entry.split(",");
            String itemName = params[0];
            int value = params.length > 1 ? Integer.parseInt(params[1]) : 5;
            targetMap.put(itemName, value);
            if (optionalList != null) {
                optionalList.add(itemName);
            }
        }
    }

    private void prepareDirectory(String eventName) {
        if (!"none".equals(eventName)) {
            File eventDir = new File(Config.DataFolder + File.separator + eventName);
            eventDir.mkdir();
        }
    }

    private void loadEventAreaBounds() {
        String[] pos1 = config.getString(AREA_POS1_KEY).split(",");
        String[] pos2 = config.getString(AREA_POS2_KEY).split(",");
        Config.Event_X1 = Math.min(Integer.parseInt(pos1[0]), Integer.parseInt(pos2[0]));
        Config.Event_Y1 = Math.min(Integer.parseInt(pos1[1]), Integer.parseInt(pos2[1]));
        Config.Event_Z1 = Math.min(Integer.parseInt(pos1[2]), Integer.parseInt(pos2[2]));
        Config.Event_X2 = Math.max(Integer.parseInt(pos1[0]), Integer.parseInt(pos2[0]));
        Config.Event_Y2 = Math.max(Integer.parseInt(pos1[1]), Integer.parseInt(pos2[1]));
        Config.Event_Z2 = Math.max(Integer.parseInt(pos1[2]), Integer.parseInt(pos2[2]));
    }

    private void setConfigEnums() {
        try {
            Config.difficulty = Config.EventMode.valueOf(config.getString("Difficulty"));
        } catch (IllegalArgumentException e) {
            Tools.Prt(Messages.GetString("FraudEvent"), Tools.consoleMode.print, programCode);
            Config.difficulty = Config.EventMode.Normal;
        }
        try {
            Config.UpperBlock = Config.UpperMode.valueOf(config.getString("UpperBlock"));
        } catch (IllegalArgumentException e) {
            Tools.Prt(Messages.GetString("FraudEnum"), Tools.consoleMode.print, programCode);
            Config.UpperBlock = Config.UpperMode.None;
        }
        try {
            Config.PlayerAlarm = Config.UpperMode.valueOf(config.getString("PlayerAlarm"));
        } catch (IllegalArgumentException e) {
            Tools.Prt(Messages.GetString("FraudEnum"), Tools.consoleMode.print, programCode);
            Config.PlayerAlarm = Config.UpperMode.None;
        }
    }

    /**
     * 設定内容を表示する
     *
     * @param p
     */
    public void Status( Player p ) {
        Tools.Prt( p, ChatColor.GREEN + "=== Premises Status ===", programCode );
        Tools.Prt( p, ChatColor.WHITE + "Degub Mode : " + ChatColor.YELLOW + Tools.consoleFlag.get( programCode ).toString(), programCode );
        Tools.Prt( p, ChatColor.WHITE + "イベント名       : " + ChatColor.YELLOW + Config.EventName, programCode );
        Tools.Prt( p, ChatColor.WHITE + "難易度           : " + ChatColor.YELLOW + Config.difficulty.toString(), programCode );
        Tools.Prt( p, ChatColor.WHITE + "下層掘削制限     : " + ChatColor.YELLOW + Config.UpperBlock.toString(), programCode );
        Tools.Prt( p, ChatColor.WHITE + "CreativeでCount  : " + ChatColor.YELLOW + ( Config.CreativeCount ? "しない":"する" ), programCode );

        Tools.Prt( p,
                ChatColor.WHITE + "ツールCost : 再取得(" + ChatColor.YELLOW + Config.RePresent +
                        ChatColor.WHITE + ") 更新(" + ChatColor.YELLOW + Config.UpCost +
                        ChatColor.WHITE + ")",
                programCode
        );

        Tools.Prt( p,
                ChatColor.WHITE + "通知 : Console(" + ChatColor.YELLOW + Config.ScoreNotice +
                        ChatColor.WHITE + ") Broadcast(" + ChatColor.YELLOW + Config.ScoreBroadcast +
                        ChatColor.WHITE + ")",
                programCode
        );

        Tools.Prt( p,
                ChatColor.WHITE + "AreaBroadcast: 確保[" + ChatColor.YELLOW + ( Config.AreaRegistBroadcast ? "あり":"なし" ) +
                        ChatColor.WHITE + "] 解放[" + ChatColor.YELLOW + ( Config.AreaReleaseBroadcast ? "あり":"なし" ) +
                        ChatColor.WHITE + "]",
                programCode
        );

        Tools.Prt( p,
                ChatColor.WHITE + "掘削 : 参加者以外[" + ChatColor.YELLOW + ( Config.breakFree ? "許可":"不可" ) +
                        ChatColor.WHITE + "] 一般Tool[" + ChatColor.YELLOW + ( Config.breakTool ? "不可":"許可" ) +
                        ChatColor.WHITE + "]",
                programCode
        );

        Tools.Prt( p, ChatColor.WHITE + "専用看板設置破壊 : " + ChatColor.YELLOW + ( Config.SignPlace ? "許可":"不可" ), programCode );

        Tools.Prt( p,
                ChatColor.WHITE + "設置 : ブロック無限[" + ChatColor.YELLOW + ( Config.zeroPlace ? "許可":"不可" ) +
                        ChatColor.WHITE + "] 参加者以外[" + ChatColor.YELLOW + ( Config.placeFree ? "許可":"不可" ) +
                        ChatColor.WHITE + "] 指定以外[" + ChatColor.YELLOW + ( Config.placeSpecified ? "許可":"不可" ) +
                        ChatColor.WHITE + "]",
                programCode
        );

        Tools.Prt( p, ChatColor.WHITE + "タイトル表示     : " + ChatColor.YELLOW + ( Config.titlePrint ? "する":"しない" ), programCode );

        Tools.Prt( p, ChatColor.WHITE + "イベントツール名 : " + Config.EventToolName, programCode );
        Tools.Prt( p, ChatColor.WHITE + "最小効率強化     : " + ChatColor.YELLOW + Config.MinDigSpeed, programCode );
        Tools.Prt( p, ChatColor.WHITE + "耐久度警告値     : " + ChatColor.YELLOW + Config.Repair, programCode );

        String ToolName = ChatColor.WHITE + "Tools : " + ChatColor.YELLOW;
        for ( Map.Entry< String, Integer > entry : Config.tools.entrySet() ) {
            ToolName += String.format( "%s (%d) , ", entry.getKey(), entry.getValue() );
        }
        Tools.Prt( p, ToolName, programCode );

        Tools.Prt( p,
                ChatColor.WHITE + "掘削範囲指定     : " +
                        ChatColor.YELLOW + ( Config.Field ?
                        Config.Event_X1 + "," + Config.Event_Y1 + "," + Config.Event_Z1 + " - " +
                                Config.Event_X2 + "," + Config.Event_Y2 + "," + Config.Event_Z2 + " [" +
                                Config.Event_World + "]"
                        :
                        "なし" ),
                programCode
        );

        if ( Config.Field ) {
            Tools.Prt( p, ChatColor.WHITE + "他者エリア警告   : " + ChatColor.YELLOW + Config.PlayerAlarm.toString(), programCode );
            Tools.Prt( p, ChatColor.WHITE + "Dynmap Area 表示 : " + ChatColor.YELLOW + ( Config.OnDynmap ? "あり":"なし" ), programCode );
        }

        Tools.Prt( p,
                ChatColor.WHITE + "PointTip表示 : " +
                        ( Config.PointTip ?
                                "Offset : X:" + ChatColor.YELLOW + Config.pt_x +
                                        ChatColor.WHITE + " Y:" + ChatColor.YELLOW + Config.pt_y +
                                        ChatColor.WHITE + " Z:" + ChatColor.YELLOW + Config.pt_z +
                                        ChatColor.WHITE + " Delay : " + ChatColor.YELLOW + Config.pt_delay
                                :
                                ChatColor.YELLOW + "なし"
                        ),
                programCode
        );

        Tools.Prt( p, ChatColor.WHITE + "参加時メッセージ : " + Config.JoinMessage, programCode );
        Tools.Prt( p, ChatColor.WHITE + "Broadcast Command:", programCode );
        for( int i = 0; i<Config.bc_command.size(); i++ ) {
            Tools.Prt( p, ChatColor.WHITE + String.valueOf( i ) + ") : " + ChatColor.YELLOW + Config.bc_command.get( i ), programCode );
        }

        Tools.Prt( p, ChatColor.GREEN + "=======================", programCode );
    }

    /**
     * ポイントブロックの一覧表示
     *
     * @param player
     */
    public void getStoneList( Player player ) {
        Tools.Prt( player, ChatColor.GREEN + "=== Premises Stone List ===", Tools.consoleMode.full, programCode );
        pointMap.keySet().forEach( ( key ) -> {
            Tools.Prt( player, ChatColor.GREEN + key + " : " + ChatColor.WHITE + pointMap.get( key ), Tools.consoleMode.full, programCode );
        } );
    }

    /**
     * 石に設定されているポイントを取得
     *
     * @param sd    判定する石の名称（コードではない）
     * @return
     */
    public int getPoint( String sd ) {
        if ( Config.stones.contains( sd ) ) {
            return pointMap.get( sd );
        }
        return 0;
    }
}