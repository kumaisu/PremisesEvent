/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.premisesevent.config;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/*
 *
 * @author sugichan
 */
public class Config {

    public static String programCode = "PE";

    public static Map< String, Integer > tools = new TreeMap< String, Integer >();

    /**
     * イベント参加モード用のenum
     *
     * Easy : NG行為を事前にブロックする
     * Normal : NG行為を警告し、リカバリー可能
     * Hard : NG行為を警告しない
     */
    public static enum EventMode { Easy, Normal, Hard };
    /**
     * 下層ブロックのみの掘削制限用のenum
     *
     * None :      制限しない
     * Warning :   注意喚起チャットメッセージのみ
     * Block :     破壊不能
     */
    public static enum UpperMode { None, Warning, Block };

    public static String DataFolder;
    public static boolean CreativeCount;
    public static double Repair;
    public static boolean Field;
    public static int Event_X1;
    public static int Event_Y1;
    public static int Event_Z1;
    public static int Event_X2;
    public static int Event_Z2;
    public static int Event_Y2;
    public static String Event_World;

    public static EventMode difficulty;
    public static UpperMode UpperBlock;
    public static boolean PlayerAlarm;
    public static boolean placeFree;
    public static boolean placeSpecified;
    public static boolean breakFree;
    public static boolean breakTool;
    public static boolean zeroPlace;
    public static boolean titlePrint;
    public static String databaseName;
    public static String EventName;
    public static String JoinMessage;
    public static String EventToolName;
    public static int RePresent;
    public static int MinDigSpeed;
    public static int UpCost;
    public static int ScoreNotice;
    public static int ScoreBroadcast;
    public static List< String > bc_command;
    public static List< String > stones;
    public static List< String > ignoreStone;
    public static boolean OnDynmap;
    public static boolean MarkReleaseBlock;
    public static boolean SignPlace;
}
