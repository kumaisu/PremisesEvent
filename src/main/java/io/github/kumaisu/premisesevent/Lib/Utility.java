/*
 *  Copyright (c) 2019 sugichan. All rights reserved.
 */
package io.github.kumaisu.premisesevent.Lib;

import java.util.Date;

/**
 * 各プラグイン共通の関数群.....にするつもりのもの
 *
 * @author sugichan
 */
public final class Utility {

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

    /**
     * 文字列を左から指定文字数取り出す
     *
     * @param data
     * @param length
     * @return
     */
    public static String leftString( String data, int length ) {
        if ( data.length()>length ) {
            return data.substring( 0, length );
        }
        return data;
    }

    /**
     * 文字列を右から指定文字数取り出す
     *
     * @param data
     * @param length
     * @return
     */
    public static String rightString( String data, int length ) {
        if ( data.length()>length ) {
            return data.substring( data.length() - length, data.length() );
        }
        return data;
    }

    /**
     * 長い文字を途中省略して、文字列成形する
     *
     * @param data
     * @param length
     * @param separator
     * @return
     */
    public static String CutMiddleString( String data, int length, String separator ) {
        if ( length <= ( separator.length() + 6 ) ) { return data; }

        if ( data.length() > length ) {
            return StringBuild(
                    leftString( data, length - ( separator.length() + 5 ) ),
                    separator,
                    rightString( data, 5 ) );
        }

        return data;
    }

    /**
     * 区切り文字のディフォルト設定
     *
     * @param data
     * @param length
     * @return
     */
    public static String CutMiddleString( String data, int length ) { return CutMiddleString( data, length, " ... " ); }

    /**
     * 日数の差分を計算するメソッド
     *
     * @param dateFrom
     * @param dateTo
     * @return
     */
    public static int dateDiff( Date dateFrom, Date dateTo ) {
        // 差分の日数を計算する
        long dateTimeTo = dateTo.getTime();
        long dateTimeFrom = dateFrom.getTime();
        long dayDiff = ( dateTimeTo - dateTimeFrom  ) / ( 1000 * 60 * 60 * 24 );

        return (int) dayDiff;
    }
}
