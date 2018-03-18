/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.premisesevent;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author sugichan
 */
public class ItemControl {

    private final Plugin plugin;

    public ItemControl( Plugin plugin ) {
        this.plugin = plugin;
    }

    public void ItemPresent( Player player ) {

        ItemStack is = new ItemStack(Material.DIAMOND_BOOTS, 1);
        is.addUnsafeEnchantment( Enchantment.PROTECTION_FALL, 5 );      // Featherfall
        is.addUnsafeEnchantment( Enchantment.PROTECTION_FIRE, 5 );      // ProtectionFire
        is.addUnsafeEnchantment( Enchantment.DURABILITY, 3 );           // Unbreaking
        is.addUnsafeEnchantment( Enchantment.ARROW_INFINITE, 0 );       // Infinity

        List<String> lores = new ArrayList();
        
        lores.add( "§7落下耐性 Ⅴ" );
        lores.add( "§7火炎耐性 Ⅴ" );
        lores.add( "§7耐久力 Ⅲ" );
        lores.add( "§d整地イベント参加賞" );

        ItemMeta im = is.getItemMeta();             //ItemStackから、ItemMetaを取得
        im.setDisplayName( "§bイベントブーツ" );    //Item名を設定
        im.setLore( lores );                        //loreを設定します。
        im.addItemFlags( ItemFlag.HIDE_ENCHANTS );  //本来のエンチャント情報を隠す
        is.setItemMeta(im);                         //元のItemStackに、変更したItemMetaを設定

        player.getInventory().addItem( is );
        player.sendMessage( ChatColor.GREEN + "イベント用装備をプレゼントしました" );
    }
    
    public void ItemUpdate( Player player, ItemStack itemstack ) {
        List<String> lores = new ArrayList();

        if ( itemstack == null ) {
            ItemStack is = new ItemStack(Material.DIAMOND_PICKAXE, 1);
            is.addUnsafeEnchantment( Enchantment.DIG_SPEED, 5 );            // Efficiency 
            is.addUnsafeEnchantment( Enchantment.DURABILITY, 0 );           // Unbreaking
            is.addUnsafeEnchantment( Enchantment.ARROW_INFINITE, 0 );       // Infinity
        
            lores.add( "§7効率強化 Ⅴ" );
            lores.add( "§d整地イベント参加賞" );

            ItemMeta im = is.getItemMeta();             //ItemStackから、ItemMetaを取得
            im.setDisplayName( "§bイベントつるはし" );  //Item名を設定
            im.setLore( lores );                        //loreを設定します。
            im.addItemFlags( ItemFlag.HIDE_ENCHANTS );  //本来のエンチャント情報を隠す
            is.setItemMeta(im);                         //元のItemStackに、変更したItemMetaを設定

            player.getInventory().addItem( is );
            player.sendMessage( ChatColor.GREEN + "イベント用ツールをプレゼントしました" );
        } else {
            String[] stringArray = { "", "Ⅰ", "Ⅱ", "Ⅲ", "Ⅳ", "Ⅴ", "Ⅵ", "Ⅶ", "Ⅷ", "Ⅸ", "Ⅹ" };

            int ench = itemstack.getItemMeta().getEnchantLevel( Enchantment.ARROW_INFINITE );
            int digs = itemstack.getItemMeta().getEnchantLevel( Enchantment.DIG_SPEED );

            if ( ench == 10 ) {
                digs++;
                itemstack.addUnsafeEnchantment( Enchantment.DIG_SPEED, digs );
                ench = 0;
            } else {
                ench++;
            }
            
            itemstack.addUnsafeEnchantment( Enchantment.DURABILITY, ench );
            itemstack.addUnsafeEnchantment( Enchantment.ARROW_INFINITE, ench );
            lores.add( "§7効率強化 " + stringArray[digs] );
            if ( ench > 0 ) {
                lores.add( "§7耐久力 " + stringArray[ench] );
            }
            lores.add( "§d整地イベント参加賞" );

            ItemMeta im = itemstack.getItemMeta();      //ItemStackから、ItemMetaを取得
            im.setLore( lores );                        //loreを設定します。
            itemstack.setItemMeta(im);                  //元のItemStackに、変更したItemMetaを設定

            itemstack.setDurability( (short) 0 );

            player.getInventory().addItem( itemstack );
            player.sendMessage( ChatColor.AQUA + "イベント用ツールをアップデートしました" );
        }
    }
}
