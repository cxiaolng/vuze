/*
 * Created on Dec 10, 2004
 * Created by Alon Rohter
 * Copyright (C) 2004 Aelitis, All Rights Reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 * AELITIS, SARL au capital de 30,000 euros
 * 8 Allee Lenotre, La Grille Royale, 78600 Le Mesnil le Roi, France.
 *
 */

package com.aelitis.azureus.core.versioncheck;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import org.gudy.azureus2.core3.util.SystemTime;

import com.aelitis.azureus.core.networkmanager.TCPTransport;



/**
 * Encodes and writes a message into a socket channel or transport byte stream.
 * The stream format looks like:
 * 1) one byte for the handshake key length
 * 2) handshake key bytes
 * 3) four bytes (int) for the payload length
 * 4) payload bytes
 */
public class StreamEncoder {
  private final ByteBuffer payload;
  private long first_encode_time = 0;
  
  /**
   * Create a new encoder using the given handshake key and message.
   * @param handshake_key message key
   * @param message message payload
   */
  public StreamEncoder( String handshake_key, ByteBuffer message ) {
    byte[] handshake = handshake_key.getBytes();
    payload = ByteBuffer.allocate( 1 + handshake.length + 4 + message.limit() );
    payload.put( (byte)handshake_key.length() );
    payload.put( handshake );
    payload.putInt( message.limit() );
    payload.put( message );
    payload.flip();
  }
  
  
  /**
   * Perform a write operation on the given channel,
   * i.e. encode the message into a stream.
   * NOTE: If given channel is in blocking mode, then internal timeout check will not function.
   * @param channel connection to write to
   * @return true if message writing is complete, false if more writing is required for encoding
   * @throws IOException on channel write exception or stream encode error
   */
  public boolean encode( SocketChannel channel ) throws IOException {
    if( first_encode_time == 0 ) {
      first_encode_time = SystemTime.getCurrentTime();
    }
    
    channel.write( payload );
    
    if( !payload.hasRemaining() ) {
      return true;
    }

    if( SystemTime.getCurrentTime() - first_encode_time > 30*1000 ) {
      throw new IOException( "encode timeout after 30sec" );
    }
      
    try{  Thread.sleep( 10 );  }catch( Throwable t ) {  t.printStackTrace(); }
      
    return false;    
  }

  
  /**
   * Perform a write operation on the given transport,
   * i.e. encode the message into a stream.
   * @param transport connection to write to
   * @return true if message writing is complete, false if more writing is required for encoding
   * @throws IOException on channel write exception or stream encode error
   */
  public boolean encode( TCPTransport transport ) throws IOException {
    return encode( transport.getSocketChannel() );
  }
  
  
}
