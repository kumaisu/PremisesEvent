/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.kumaisu.premisesevent.Lib;

import java.net.Inet4Address;
import java.net.UnknownHostException;

/**
 *
 * @author sugichan
 */
public class InetCalc {
    /**
     * IPアドレスを整数化する関数
     *
     * @param ipAddr    xxx.xxx.xxx.xxx 形式のアドレス
     * @return          整数化されたアドレス
     */
    public static long ipToInt( Inet4Address ipAddr ) {
        long compacted = 0;
        byte[] bytes = ipAddr.getAddress();
        for ( int i=0 ; i<bytes.length ; i++ ) {
            compacted += ( bytes[i] * Math.pow( 256, 4-i-1 ) );
        }
        return compacted;
    }

    /**
     * 整数化されたアドレスをIPアドレスに変更する関数
     *
     * @param ipAddress 整数化されたアドレス
     * @return          xxx.xxx.xxx.xxx 形式のアドレス文字列
     */
    public static String toInetAddress( long ipAddress ) {
        long ip = ( ipAddress < 0 ) ? (long)Math.pow(2,32)+ipAddress : ipAddress;
        Inet4Address inetAddress = null;
        String addr =  String.valueOf((ip >> 24)+"."+((ip >> 16) & 255)+"."+((ip >> 8) & 255)+"."+(ip & 255));
        return addr;
    }

    /**
     * サブネット(CIDER表記)をIPv4形式に変換する。
     *
     * @param subnet
     * サブネット(CIDR表記) : 0〜32
     * @return 結果文字列(IPv4形式のサブネットマスク)
     */
    public static String convertSubnetCIDR2IPv4(final int subnet) {
        if (subnet < 0 || subnet > 32) {
            throw new IllegalArgumentException( "引数(subnet)は 0〜32 までの数値でなければなりません(" + subnet + ")" );
        }

        // 典型的なサブネット形式は計算せずに導出
        switch ( subnet ) {
            case 8:
                return "255.0.0.0";
            case 16:
                return "255.255.0.0";
            case 24:
                return "255.255.255.0";
            default:
                // 汎用的なサブネット変換
                final StringBuilder subnetBinary = new StringBuilder( Long.toBinaryString( ( long ) ( Math.pow( 2, subnet ) - 1 ) ) );
                for ( int i = subnetBinary.length(); i <= 32; i++ ) {
                    subnetBinary.append( "0" );
                }
                final StringBuilder result = new StringBuilder();
                for ( int i = 8, length = subnetBinary.length(); i <= length; i += 8) {
                    result.append( Integer.parseInt( subnetBinary.substring( i - 8, i ), 2 ) ).append( "." );
                }
                result.setLength(result.length() - 1);
                return result.toString();
        }
    }

    /**
     * サブネット(IPv4形式)をCIDR表記に変換する。
     *
     * @param subnet
     * サブネット(IPv4形式)
     * @return 結果(CIDR表記の値)
     */
    public static int convertSubnetIP2CIDRv4(final String subnet) {
        if (subnet == null || subnet.isEmpty()) {
            throw new IllegalArgumentException( "引数(subnet)は nullまたは空文字です" );
        }
        final String[] split = subnet.split( "\\." );
        if (split.length != 4) {
            throw new IllegalArgumentException( "引数(subnet)は IPv4形式ではありません(" + subnet + ")" );
        }
        final StringBuilder sb = new StringBuilder();
        for (String numStr : split) {
            final int num = Integer.parseInt( numStr );
            if ( num < 0 || num > 255 ) {
                throw new IllegalArgumentException( "引数(subnet)は IPv4形式ではありません(" + subnet + ")" );
            }
            sb.append( Integer.toBinaryString( num ) );
        }
        final String subnetBinary = sb.toString();
        int i = 0;
        while ( subnetBinary.indexOf( "1", i ) != -1 ) {
            i++;
        }
        return i;
    }

    /**
     * IPアドレスとサブネットマスクからネットワークアドレスを算出する
     *
     * @param ip    IPアドレス
     * @param sb    サブネットマスク
     * @return      ネットワークアドレス
     * @throws UnknownHostException
     */
    public static String getNetworkAddress( String ip, String sb ) throws UnknownHostException {
        byte[] bIP = Inet4Address.getByName( ip ).getAddress();
        byte[] bSB = Inet4Address.getByName( sb ).getAddress();
        byte[] bNT = new byte[4];

        for( int i = 0; i<bIP.length; i++ ) {
            bNT[i] = (byte) ( bIP[i] & bSB[i] );
            // System.out.printf( "%02X  ", ( 0x0ff & bNT[i] ) );
        }

        return Inet4Address.getByAddress( bNT ).toString();
    }
}
