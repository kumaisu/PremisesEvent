/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.premisesevent.Item;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import com.mycompany.kumaisulibraries.Items;
import com.mycompany.kumaisulibraries.Tools;
import com.mycompany.kumaisulibraries.Tools.consoleMode;
import static com.mycompany.premisesevent.config.Config.programCode;

/**
 * アイテムコントロールライブラリ
 *
 * @author sugichan
 */
public class ItemControl {

    /**
     * アイテムコントロール初期化
     *
     */
    public ItemControl() {
    }

    /**
     * アイテムの生成とプレゼント処理
     *
     * @param player
     */
    public void ItemPresent( Player player ) {
        player.getInventory().addItem( Items.PresentArmor() );
        Tools.Prt( player, ChatColor.GREEN + "イベント用装備をプレゼントしました", consoleMode.full, programCode );
    }

    /**
     * イベントツールプレゼント処理
     *
     * @param player        操作プレイヤー情報
     * @param ToolName      イベントツール判定用文字列
     * @param Digs          効率強化の値
     * @param tool          新規時のアイテム基本情報
     */
    public void ToolPresent( Player player, Material tool, int Digs, String ToolName ) {
        player.getInventory().addItem( Items.EventTool( ToolName, tool, Digs, player.isOp() ) );
        Tools.Prt( player, ChatColor.GREEN + "イベント用ツール[" + tool + "]をプレゼントしました", consoleMode.full, programCode );
    }

    /**
     * イベントツールアップデート処理
     *
     * @param player        操作プレイヤー情報
     * @param itemstack     持っているアイテムの情報(nullの場合、新規となる)
     */
    public void ItemUpdate( Player player, ItemStack itemstack ) {
        List<String> lores = new ArrayList();

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
        itemstack.addUnsafeEnchantment( Enchantment.SILK_TOUCH, 1);
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
        Tools.Prt( player, 
                ChatColor.AQUA + "イベント用ツールを[" + 
                ChatColor.GREEN + UpdateMessage +
                ChatColor.AQUA + "]にアップデートしました",
                consoleMode.full, programCode );
    }

    /**
     * 所持アイテムのステータス表示
     *
     * @param player
     */
    public void ShowItemStatus( Player player ) {
            ItemStack PlayerItem = player.getInventory().getItemInMainHand();
            Tools.Prt( player, ChatColor.GREEN + "Detect Enchant...", consoleMode.max, programCode );
            Tools.Prt( player, "効率強化 : " + PlayerItem.getItemMeta().getEnchantLevel( Enchantment.DIG_SPEED ), consoleMode.max, programCode );
            Tools.Prt( player, "耐久力   : " + PlayerItem.getItemMeta().getEnchantLevel( Enchantment.DURABILITY ), consoleMode.max, programCode );
            Tools.Prt( player, "シルク   : " + PlayerItem.getItemMeta().getEnchantLevel( Enchantment.SILK_TOUCH ), consoleMode.max, programCode );
            Tools.Prt( player, "入れ食い : " + PlayerItem.getItemMeta().getEnchantLevel( Enchantment.LURE ), consoleMode.max, programCode );
            Tools.Prt( player, "無限     : " + PlayerItem.getItemMeta().getEnchantLevel( Enchantment.ARROW_INFINITE ), consoleMode.max, programCode );
    }
}

