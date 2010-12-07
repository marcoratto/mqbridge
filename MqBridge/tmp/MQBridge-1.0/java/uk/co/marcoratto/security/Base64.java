/*
 * Copyright (C) 2010 Marco Ratto
 *
 * This file is part of the project MqBridge.
 *
 * MqBridge is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * any later version.
 *
 * MqBridge is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MqBridge; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package uk.co.marcoratto.security;

public class Base64 {

	public Base64() {
   }

	public static String encode (String s) {
	  if (s == null) return null;
	  return encode(s.getBytes());
	}

	public static String encode (byte[] octetString ) {
	 int bits24;
	 int bits6;

	 char [] out = new char [ ( ( octetString.length - 1 ) / 3 + 1 ) * 4 ];

	 int outIndex = 0;
	 int i = 0;

	 while ( ( i + 3 ) <= octetString.length ) {
	   // store the octets
	   bits24 = (octetString [ i++ ] & 0xFF ) << 16;
	   bits24 |= (octetString [ i++ ] & 0xFF ) << 8;
	   bits24 |= (octetString [ i++ ] & 0xFF ) << 0;

	   bits6 = ( bits24 & 0x00FC0000 ) >> 18;
	   out [ outIndex++ ] = alphabet [ bits6 ];
	   bits6 = ( bits24 & 0x0003F000 ) >> 12;
	   out [ outIndex++ ] = alphabet [ bits6 ];
	   bits6 = ( bits24 & 0x00000FC0 ) >> 6;
	   out [ outIndex++ ] = alphabet [ bits6 ];
	   bits6 = ( bits24 & 0x0000003F );
	   out [ outIndex++ ] = alphabet [ bits6 ];
	 }

	 if ( octetString.length - i == 2 ) {
	   // store the octets
	   bits24 = ( octetString [ i ] & 0xFF ) << 16;
	   bits24 |= ( octetString [ i + 1 ] & 0xFF ) << 8;

	   bits6 = ( bits24 & 0x00FC0000 ) >> 18;
	   out [ outIndex++ ] = alphabet [ bits6 ];
	   bits6 = ( bits24 & 0x0003F000 ) >> 12;
	   out [ outIndex++ ] = alphabet [ bits6 ];
	   bits6 = ( bits24 & 0x00000FC0 ) >> 6;
	   out [ outIndex++ ] = alphabet [ bits6 ];

	   // padding
	   out [ outIndex++ ] = '=';
	 } else if ( octetString.length - i == 1 ) {
	   // store the octets
	   bits24 = ( octetString [ i ] & 0xFF ) << 16;

	   bits6 = ( bits24 & 0x00FC0000 ) >> 18;
	   out [ outIndex++ ] = alphabet [ bits6 ];
	   bits6 = ( bits24 & 0x0003F000 ) >> 12;
	   out [ outIndex++ ] = alphabet [ bits6 ];

	   // padding
	   out [ outIndex++ ] = '=';
	   out [ outIndex++ ] = '=';
	 }

	  return new String (out);
	 }

 	public static String decode(String s) {
	  if (s == null) return null;

	  return decode (s.getBytes());
	}

	public static String decode(byte[] data) {
		int len = ((data.length + 3) / 4) * 3;

		if (data.length>0 && data[data.length-2] == '=') len--;
		if (data.length>0 && data[data.length-1] == '=') len--;
		byte[] out = new byte[len];


		int shift = 0; // # di bit in eccesso in accum
		int accum = 0; // Bits in eccesso
		int index = 0;

		for (int ix=0; ix<data.length; ix++) {
			int value = codes[ data[ix] & 0xFF ];
			if ( value >= 0 ) {
				accum <<= 6;
				shift += 6;
				accum |= value;
				if ( shift >= 8 ) {
					shift -= 8;
					out[index++] = (byte) ((accum >> shift) & 0xff);
				}
			}
		}
		if (index != out.length) throw new Error("Base64.decode: Lunghezza stringa errata!");

   	return new String(out);
	}

 	static final char [ ] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=".toCharArray();
	static private byte[] codes = new byte[256];
	static {
		for (int i=0; i<256; i++) codes[i] = -1;
		for (int i = 'A'; i <= 'Z'; i++) codes[i] = (byte)( i - 'A');
		for (int i = 'a'; i <= 'z'; i++) codes[i] = (byte)(26 + i - 'a');
		for (int i = '0'; i <= '9'; i++) codes[i] = (byte)(52 + i - '0');
		codes['+'] = 62;
		codes['/'] = 63;
	}
}