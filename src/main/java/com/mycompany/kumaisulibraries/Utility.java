/*
 *  Copyright (c) 2019 sugichan. All rights reserved.
 */
package com.mycompany.kumaisulibraries;

/**
 * 各プラグイン共通の関数群.....にするつもりのもの
 *
 * @author sugichan
 */
public final class Utility {

    public static enum consoleMode { none, normal, full, max, stop }

    /**
     * 複数の文字列を連結するる
     * 通常の＋による連結よりも若干速くなるので速度重視の場所に利用
     * @param StrItem   文字列の集合
     * @return          完成した一つの文章
     */
    public static String StringBuild( String ... StrItem ) {
        StringBuilder buf = new StringBuilder();

        for ( String StrItem1 : StrItem ) buf.append( StrItem1 );

        return buf.toString();
    }

    /**
     * カラーコードを書き換える
     * @param data  書き換え元の文章
     * @return      書き換え後の文章
     */
    public static String ReplaceString( String data ) {
        return data.replace( "%$", "§" );
    }

    /**
     * Configで記録された%player%をプレイヤー名に差し替える
     * @param data      差し替え元の文章
     * @param Names     差し替えるプレイヤー名
     * @return          差し替え後の文章
     */
    public static String ReplaceString( String data, String Names ) {
        return ReplaceString( data.replace( "%player%", Names ) );
    }
}
