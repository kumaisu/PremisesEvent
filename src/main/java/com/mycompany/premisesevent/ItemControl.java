/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.premisesevent;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

/**
 * アイテムコントロールライブラリ
 *
 * @author sugichan
 */
public class ItemControl {

    private final Plugin plugin;

    /**
     * アイテムコントロール初期化
     *
     * @param plugin
     */
    public ItemControl( Plugin plugin ) {
        this.plugin = plugin;
    }

    /**
     * アイテムの生成とプレゼント処理
     *
     * @param player
     */
    public void ItemPresent( Player player ) {

        ItemStack is = new ItemStack( Material.CHAINMAIL_BOOTS, 1 );    // ChainMail Boots
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
        is.setItemMeta( im );                       //元のItemStackに、変更したItemMetaを設定

        player.getInventory().addItem( is );
        player.sendMessage( ChatColor.GREEN + "イベント用装備をプレゼントしました" );
        Bukkit.getServer().getConsoleSender().sendMessage( ChatColor.GREEN + "Successfully gifted present tool." );
    }

    /**
     * イベントツールアップデート処理
     *
     * @param player
     * @param itemstack
     * @param ToolName
     * @param tool
     */
    public void ItemUpdate( Player player, ItemStack itemstack, String ToolName, Material tool ) {
        List<String> lores = new ArrayList();

        if ( itemstack == null ) {
            //  ItemStack is = new ItemStack( Material.IRON_PICKAXE, 1);
            ItemStack is = new ItemStack( tool, 1 );
            if ( player.isOp() ) {
                is.addUnsafeEnchantment( Enchantment.DIG_SPEED, 10 );           // Efficiency 
                is.addUnsafeEnchantment( Enchantment.LURE, 10 );                // Lure
                is.addUnsafeEnchantment( Enchantment.DURABILITY, 10 );          // Unbreaking
                is.addUnsafeEnchantment( Enchantment.ARROW_INFINITE, 10 );      // Infinity
                lores.add( "§7効率強化 Ⅹ" );
                lores.add( "§7耐久力 Ⅹ" );
            } else {
                is.addUnsafeEnchantment( Enchantment.DIG_SPEED, 6 );            // Efficiency 
                is.addUnsafeEnchantment( Enchantment.LURE, 6 );                 // Lure
                is.addUnsafeEnchantment( Enchantment.DURABILITY, 0 );           // Unbreaking
                is.addUnsafeEnchantment( Enchantment.ARROW_INFINITE, 0 );       // Infinity
                lores.add( "§7効率強化 Ⅵ" );
            }

            lores.add( "§d整地イベント参加賞" );

            ItemMeta im = is.getItemMeta();             //  ItemStackから、ItemMetaを取得
            im.setDisplayName( ToolName );              //  Item名を設定
            im.setLore( lores );                        //  loreを設定します。
            im.addItemFlags( ItemFlag.HIDE_ENCHANTS );  //  本来のエンチャント情報を隠す
            is.setItemMeta( im );                       //  元のItemStackに、変更したItemMetaを設定

            player.getInventory().addItem( is );
            player.sendMessage( ChatColor.GREEN + "イベント用ツールをプレゼントしました" );
            Bukkit.getServer().getConsoleSender().sendMessage( ChatColor.GREEN + "Successfully gifted update tool." );
        } else {
            String[] stringArray = { "", "Ⅰ", "Ⅱ", "Ⅲ", "Ⅳ", "Ⅴ", "Ⅵ", "Ⅶ", "Ⅷ", "Ⅸ", "Ⅹ", "ⅩⅠ" };
            String UpdateMessage;

            int ench = itemstack.getItemMeta().getEnchantLevel( Enchantment.ARROW_INFINITE );
            int digs = itemstack.getItemMeta().getEnchantLevel( Enchantment.LURE );

            if ( ench == 10 ) {
                if ( digs < 10 ) {
                    digs++;
                    ench = 0;
                } else {
                    ench = 10;
                }
            } else {
                ench++;
            }

            itemstack.addUnsafeEnchantment( Enchantment.DIG_SPEED, digs );
            itemstack.addUnsafeEnchantment( Enchantment.LURE, digs );
            itemstack.addUnsafeEnchantment( Enchantment.DURABILITY, ench );
            itemstack.addUnsafeEnchantment( Enchantment.ARROW_INFINITE, ench );
            lores.add( "§7効率強化 " + stringArray[digs] );
            UpdateMessage = ChatColor.WHITE + "効率強化" + stringArray[digs];
            if ( ench > 0 ) {
                lores.add( "§7耐久力 " + stringArray[ench] );
                UpdateMessage += ",耐久力" + stringArray[ench];
            }
            lores.add( "§d整地イベント参加賞" );

            ItemMeta im = itemstack.getItemMeta();      //  ItemStackから、ItemMetaを取得
            im.setLore( lores );                        //  loreを設定します。
            itemstack.setItemMeta(im);                  //  元のItemStackに、変更したItemMetaを設定

            itemstack.setDurability( (short) 0 );

            player.getInventory().addItem( itemstack );
            player.sendMessage( ChatColor.AQUA + "イベント用ツールを[" + UpdateMessage + ChatColor.AQUA + "]にアップデートしました" );
            Bukkit.getServer().getConsoleSender().sendMessage( ChatColor.AQUA + player.getDisplayName() + ChatColor.GOLD + " Tool Update !! [効率強化" + digs + " 耐久力" + ench + "]"  );
        }
    }

    /**
     * 所持アイテムのステータス表示
     *
     * @param player
     */
    public void ShowItemStatus( Player player ) {
            ItemStack PlayerItem = player.getInventory().getItemInMainHand();
            player.sendMessage( ChatColor.GREEN + "Detect Enchant..." );
            player.sendMessage( "効率強化 : " + PlayerItem.getItemMeta().getEnchantLevel( Enchantment.DIG_SPEED ) );
            player.sendMessage( "耐久力   : " + PlayerItem.getItemMeta().getEnchantLevel( Enchantment.DURABILITY ) );
            player.sendMessage( "入れ食い : " + PlayerItem.getItemMeta().getEnchantLevel( Enchantment.LURE ) );
            player.sendMessage( "無限     : " + PlayerItem.getItemMeta().getEnchantLevel( Enchantment.ARROW_INFINITE ) );
    }
}
