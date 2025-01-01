/*
 *  Copyright (c) 2019 sugichan. All rights reserved.
 */
package io.github.kumaisu.premisesevent.Lib;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.enchantments.Enchantment;

/**
 * 各プラグイン共通の関数群.....にするつもりのもの
 *
 * @author 九尾乃狐
 */

public class Items {

    /**
     * サーバー独自アイテム「ExpCube」生成
     *
     * @return
     */
    public static ItemStack ExpCube() {
        ItemStack item = new ItemStack( Material.QUARTZ_BLOCK, 1 );

        List<String> lores = new ArrayList();

        lores.add( "§eスニーク§fしながら" );
        lores.add( "§b右クリック§fで貯める" );
        lores.add( "§b左クリック§fで取り出す" );

        ItemMeta im = item.getItemMeta();   //ItemStackから、ItemMetaを取得します。
        im.setDisplayName( "§aExpCube" );  //Item名を設定
        im.setLore( lores );                //loreを設定します。
        im.addItemFlags( ItemFlag.HIDE_ENCHANTS );
        item.setItemMeta(im);               //元のItemStackに、変更したItemMetaを設定します。

        item.addUnsafeEnchantment( Enchantment.PROTECTION, 0 );

        return item;
    }

    /**
     * サーバー独自アイテム「くまそうる」生成
     *
     * @return
     */
    public static ItemStack KumaSoul() {
        ItemStack item = new ItemStack( Material.IRON_NUGGET, 1 );

        List<String> lores = new ArrayList();

        lores.add( "§dクマイス§eの§4魂§eが集まったもの" );
        lores.add( "§1デイリー§eで貰えるよ" );
        lores.add( "§e９つ集まるとオーブと交換できる" );

        ItemMeta im = item.getItemMeta();   //ItemStackから、ItemMetaを取得します。
        im.setDisplayName( "§aくまそうる" );  //Item名を設定
        im.setLore( lores );                //loreを設定します。
        im.addItemFlags( ItemFlag.HIDE_ENCHANTS );
        item.setItemMeta(im);               //元のItemStackに、変更したItemMetaを設定します。

        item.addUnsafeEnchantment( Enchantment.PROTECTION, 0 );

        return item;
    }

    /**
     * サーバー独自アイテム「くまおーぶ」生成
     *
     * @return
     */
    public static ItemStack KumaOrb() {
        ItemStack item = new ItemStack( Material.EMERALD, 1 );

        List<String> lores = new ArrayList();

        lores.add( "§dクマイス§eから§4投票§eへの" );
        lores.add( "§e感謝のお礼として貰えるアイテム" );
        lores.add( "§eガチャがひけるよ！§d ❤" );

        ItemMeta im = item.getItemMeta();   //ItemStackから、ItemMetaを取得します。
        im.setDisplayName( "§a§lくまおーぶ" );  //Item名を設定
        im.setLore( lores );                //loreを設定します。
        im.addItemFlags( ItemFlag.HIDE_ENCHANTS );
        item.setItemMeta(im);               //元のItemStackに、変更したItemMetaを設定します。

        item.addUnsafeEnchantment( Enchantment.PROTECTION, 0 );

        return item;
    }

    public static ItemStack PresentArmor() {
        ItemStack is = new ItemStack( Material.CHAINMAIL_BOOTS, 1 );    // ChainMail Boots

        List<String> lores = new ArrayList();

        lores.add( "§7落下耐性 Ⅴ" );
        lores.add( "§7火炎耐性 Ⅴ" );
        lores.add( "§7耐久力 Ⅲ" );
        lores.add( "§d整地イベント参加賞" );

        ItemMeta im = is.getItemMeta();             //ItemStackから、ItemMetaを取得
        im.setDisplayName( "§bイベントブーツ" );    //Item名を設定
        im.setLore( lores );                        //loreを設定します。
        im.addItemFlags( ItemFlag.HIDE_ENCHANTS );  //本来のエンチャント情報を隠す
        is.setItemMeta( im );                       //元のItemStackに、変更したItemMetaを設定

        is.addUnsafeEnchantment( Enchantment.FEATHER_FALLING, 5 );      // Featherfall
        is.addUnsafeEnchantment( Enchantment.FIRE_PROTECTION, 5 );      // ProtectionFire
        is.addUnsafeEnchantment( Enchantment.UNBREAKING, 3 );           // Unbreaking
        is.addUnsafeEnchantment( Enchantment.INFINITY, 0 );             // Infinity

        return is;
    }

    public static ItemStack EventTool( String ToolName, Material tool, int DigSpeed, boolean full ) {
        ItemStack is = new ItemStack( tool, 1 );

        String[] stringArray = { "", "Ⅰ", "Ⅱ", "Ⅲ", "Ⅳ", "Ⅴ", "Ⅵ", "Ⅶ", "Ⅷ", "Ⅸ", "Ⅹ", "ⅩⅠ" };
        List<String> lores = new ArrayList();

        if ( full ) {
            is.addUnsafeEnchantment( Enchantment.EFFICIENCY, 10 );           // Efficiency
            is.addUnsafeEnchantment( Enchantment.LURE, 10 );                // Lure
            is.addUnsafeEnchantment( Enchantment.UNBREAKING, 10 );          // Unbreaking
            is.addUnsafeEnchantment( Enchantment.INFINITY, 10 );      // Infinity
            lores.add( "§7効率強化 Ⅹ" );
            lores.add( "§7耐久力 Ⅹ" );
        } else {
            is.addUnsafeEnchantment( Enchantment.EFFICIENCY, DigSpeed );     // Efficiency
            is.addUnsafeEnchantment( Enchantment.LURE, DigSpeed );          // Lure
            is.addUnsafeEnchantment( Enchantment.UNBREAKING, 0 );           // Unbreaking
            is.addUnsafeEnchantment( Enchantment.INFINITY, 0 );       // Infinity
            lores.add( "§7効率強化 " + stringArray[DigSpeed] );
        }

        is.addUnsafeEnchantment( Enchantment.SILK_TOUCH, 1);                // SILK_TOUCH
        lores.add( "§d整地イベント参加賞" );

        ItemMeta im = is.getItemMeta();             //  ItemStackから、ItemMetaを取得
        im.setDisplayName( ToolName );              //  Item名を設定
        im.setLore( lores );                        //  loreを設定します。
        im.addItemFlags( ItemFlag.HIDE_ENCHANTS );  //  本来のエンチャント情報を隠す
        is.setItemMeta( im );                       //  元のItemStackに、変更したItemMetaを設定

        return is;
    }
}
