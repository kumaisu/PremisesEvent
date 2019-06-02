/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.kumaisulibraries;

import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

/**
 *
 * @author sugichan
 */
public final class Minecraft {

    /**
     * メッセージ表示
     * @param player    表示するプレイヤー
     * @param msg       表示内容
     * @param console   システムコンソールに表示するか？
     */
    public static void Prt( Player player, String msg, boolean console ) {
        if ( console ) Bukkit.getServer().getConsoleSender().sendMessage( msg );
        if ( player != null ) player.sendMessage( msg );
    }

    public static void Prt( String msg )                { Prt( ( Player ) null, msg, true ); }
    public static void Prt( String msg, boolean Flag )  { Prt( ( Player ) null, msg, Flag ); }
    public static void Prt( Player player, String msg ) { Prt( player, msg, ( player == null ) ); }

    /**
     * Config.ymlで指定されたコンソールコマンドを実行する
     *
     * @param player
     * @param Message
     * @param command
     */
    public static void ExecOtherCommand( Player player, String Message, List< String > command ) {
        for( int i = 0; i<command.size(); i++ ) {
            String ExecCommand = command.get( i );
            ExecCommand = ExecCommand.replace( "%message%", Message );
            ExecCommand = ExecCommand.replace( "%player%", player.getDisplayName() );
            Prt( ChatColor.WHITE + String.valueOf( i ) + ") : " + ChatColor.YELLOW + ExecCommand );
            Bukkit.getServer().dispatchCommand( Bukkit.getConsoleSender(), ExecCommand );
        }
    }

    /**
     * 特殊な名前のブロックに対する対処
     * GRANITE：花崗岩
     * DIORITE：閃緑岩
     * ANDESITE：安山岩
     *
     * @param b
     * @return
     */
    public static String getStoneName( Block b ) {
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

    /**
     *  指定された場所に花火を打ち上げる関数
     *
     * @param loc
     */
    public static void launchFireWorks( Location loc ) {
        /*
            static private FireworkEffect.Type[] types = { FireworkEffect.Type.BALL,
                FireworkEffect.Type.BALL_LARGE, FireworkEffect.Type.BURST,
                FireworkEffect.Type.CREEPER, FireworkEffect.Type.STAR, };
        */
        // 花火を作る
        Firework firework = loc.getWorld().spawn( loc, Firework.class );

        // 花火の設定情報オブジェクトを取り出す
        FireworkMeta meta = firework.getFireworkMeta();
        FireworkEffect.Builder effect = FireworkEffect.builder();

        // 形状を星型にする
        effect.with( FireworkEffect.Type.STAR );

        // 基本の色を単色～5色以内でランダムに決める
        effect.withColor( Color.AQUA );

        // 余韻の色を単色～3色以内でランダムに決める
        effect.withFade( Color.YELLOW );

        // 爆発後に点滅するかをランダムに決める
        effect.flicker( true );

        // 爆発後に尾を引くかをランダムに決める
        effect.trail( true );

        // 打ち上げ高さを1以上4以内でランダムに決める
        meta.setPower( 1 );

        // 花火の設定情報を花火に設定
        meta.addEffect( effect.build() );
        firework.setFireworkMeta( meta );
    }
}
