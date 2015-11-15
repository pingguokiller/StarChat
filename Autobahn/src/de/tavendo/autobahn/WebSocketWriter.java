/******************************************************************************
 *
 *  Copyright 2011-2012 Tavendo GmbH
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 ******************************************************************************/

package de.tavendo.autobahn;

import java.io.IOException;
import java.net.SocketException;
import java.nio.channels.SocketChannel;
import java.util.Random;

import org.apache.http.NameValuePair;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Base64;
import android.util.Log;

/**
 * WebSocket writer, the sending leg of a WebSockets connection.
 * This is run on it's background thread with it's own message loop.
 * The only method that needs to be called (from foreground thread) is forward(),
 * which is used to forward a WebSockets message to this object (running on
 * background thread) so that it can be formatted and sent out on the
 * underlying TCP socket.
 */
public class WebSocketWriter extends Handler {

   private static final boolean DEBUG = true;
   private static final String TAG = WebSocketWriter.class.getName();

   /// Random number generator for handshake key and frame mask generation.
   private final Random mRng = new Random();

   /// Connection master.
   private final Handler mMaster;

   /// Message looper this object is running on.
   private final Looper mLooper;

   /// The NIO socket channel created on foreground thread.
   private final SocketChannel mSocket;

   /// WebSockets options.
   private final WebSocketOptions mOptions;

   /// The send buffer that holds data to send on socket.
   private final ByteBufferOutputStream mBuffer;


   /**
    * Create new WebSockets background writer.
    *
    * @param looper    The message looper of the background thread on which
    *                  this object is running.
    * @param master    The message handler of master (foreground thread).
    * @param socket    The socket channel created on foreground thread.
    * @param options   WebSockets connection options.
    */
   public WebSocketWriter(Looper looper, Handler master, SocketChannel socket, WebSocketOptions options) {

      super(looper);

      mLooper = looper;
      mMaster = master;
      mSocket = socket;
      mOptions = options;
      mBuffer = new ByteBufferOutputStream(options.getMaxFramePayloadSize() + 14, 4*64*1024);

      if (DEBUG) Log.d(TAG, "created");
   }


   /**
    * Call this from the foreground (UI) thread to make the writer
    * (running on background thread) send a WebSocket message on the
    * underlying TCP.
    *
    * @param message       Message to send to WebSockets writer. An instance of the message
    *                      classes inside WebSocketMessage or another type which then needs
    *                      to be handled within processAppMessage() (in a class derived from
    *                      this class).
    */
   public void forward(Object message) {

      Message msg = obtainMessage();
      msg.obj = message;
      sendMessage(msg);
   }


   /**
    * Notify the master (foreground thread).
    *
    * @param message       Message to send to master.
    */
   private void notify(Object message) {

      Message msg = mMaster.obtainMessage();
      msg.obj = message;
      mMaster.sendMessage(msg);
   }


   /**
    * Create new key for WebSockets handshake.
    *
    * @return WebSockets handshake key (Base64 encoded).
    */
   private String newHandshakeKey() {
      final byte[] ba = new byte[16];
      mRng.nextBytes(ba);
      return Base64.encodeToString(ba, Base64.NO_WRAP);
   }


   /**
    * Create new (random) frame mask.
    *
    * @return Frame mask (4 octets).
    */
   private byte[] newFrameMask() {
      final byte[] ba = new byte[4];
      mRng.nextBytes(ba);
      return ba;
   }


   /**
    * Send WebSocket client handshake.
    */
   private void sendClientHandshake(WebSocketMessage.ClientHandshake message) throws IOException {

      // write HTTP header with handshake
      String path;
      if (message.mQuery != null) {
         path = message.mPath + "?" + message.mQuery;
      } else {
         path = message.mPath;
      }
      mBuffer.write("GET " + path + " HTTP/1.1");
      mBuffer.crlf();
      mBuffer.write("Host: " + message.mHost);
      mBuffer.crlf();
      mBuffer.write("Upgrade: WebSocket");
      mBuffer.crlf();
      mBuffer.write("Connection: Upgrade");
      mBuffer.crlf();

      mBuffer.write("Sec-WebSocket-Key: " + newHandshakeKey());
      mBuffer.crlf();

      if (message.mOrigin != null && !message.mOrigin.equals("")) {
         mBuffer.write("Origin: " + message.mOrigin);
         mBuffer.crlf();
      }

      if (message.mSubprotocols != null && message.mSubprotocols.length > 0) {
         mBuffer.write("Sec-WebSocket-Protocol: ");
         for (int i = 0; i < message.mSubprotocols.length; ++i) {
            mBuffer.write(message.mSubprotocols[i]);
            if (i != message.mSubprotocols.length-1) {
               mBuffer.write(", ");
            }
         }
         mBuffer.crlf();
      }

      mBuffer.write("Sec-WebSocket-Version: 13");
      mBuffer.crlf();

      // Header injection      
      if (message.mHeaderList != null) {
          for (NameValuePair pair : message.mHeaderList) {
        	  mBuffer.write( pair.getName() + ":" + pair.getValue() );
        	  mBuffer.crlf();
          }
      }           
      mBuffer.crlf();
   }


   /**
    * Send WebSockets close.
    */
   private void sendClose(WebSocketMessage.Close message) throws IOException, WebSocketException {

      if (message.mCode > 0) {

         byte[] payload = null;

         if (message.mReason != null && !message.mReason.equals("")) {
            byte[] pReason = message.mReason.getBytes("UTF-8");
            payload = new byte[2 + pReason.length];
            for (int i = 0; i < pReason.length; ++i) {
               payload[i + 2] = pReason[i];
            }
         } else {
            payload = new byte[2];
         }

         if (payload != null && payload.length > 125) {
            throw new WebSocketException("close payload exceeds 125 octets");
         }

         payload[0] = (byte)((message.mCode >> 8) & 0xff);
         payload[1] = (byte)(message.mCode & 0xff);

         sendFrame(8, true, payload);

      } else {

         sendFrame(8, true, null);
      }
   }


   /**
    * Send WebSockets ping.
    */
   private void sendPing(WebSocketMessage.Ping message) throws IOException, WebSocketException {
      if (message.mPayload != null && message.mPayload.length > 125) {
         throw new WebSocketException("ping payload exceeds 125 octets");
      }
      sendFrame(9, true, message.mPayload);
   }


   /**
    * Send WebSockets pong. Normally, unsolicited Pongs are not used,
    * but Pongs are only send in response to a Ping from the peer.
    */
   private void sendPong(WebSocketMessage.Pong message) throws IOException, WebSocketException {
      if (message.mPayload != null && message.mPayload.length > 125) {
         throw new WebSocketException("pong payload exceeds 125 octets");
      }
      sendFrame(10, true, message.mPayload);
   }


   /**
    * Send WebSockets binary message.
    */
   private void sendBinaryMessage(WebSocketMessage.BinaryMessage message) throws IOException, WebSocketException {
      if (message.mPayload.length > mOptions.getMaxMessagePayloadSize()) {
         throw new WebSocketException("message payload exceeds payload limit");
      }
      sendFrame(2, true, message.mPayload);
   }


   /**
    * Send WebSockets text message.
    */
   private void sendTextMessage(WebSocketMessage.TextMessage message) throws IOException, WebSocketException {
      byte[] payload = message.mPayload.getBytes("UTF-8");
      if (payload.length > mOptions.getMaxMessagePayloadSize()) {
         throw new WebSocketException("message payload exceeds payload limit");
      }
      sendFrame(1, true, payload);
   }


   /**
    * Send WebSockets binary message.
    */
   private void sendRawTextMessage(WebSocketMessage.RawTextMessage message) throws IOException, WebSocketException {
      if (message.mPayload.length > mOptions.getMaxMessagePayloadSize()) {
         throw new WebSocketException("message payload exceeds payload limit");
      }
      sendFrame(1, true, message.mPayload);
   }


   /**
    * Sends a WebSockets frame. Only need to use this method in derived classes which implement
    * more message types in processAppMessage(). You need to know what you are doing!
    *
    * @param opcode     The WebSocket frame opcode.
    * @param fin        FIN flag for WebSocket frame.
    * @param payload    Frame payload or null.
    */
   protected void sendFrame(int opcode, boolean fin, byte[] payload) throws IOException {
      if (payload != null) {
         sendFrame(opcode, fin, payload, 0, payload.length);
      } else {
         sendFrame(opcode, fin, null, 0, 0);
      }
   }


   /**
    * Sends a WebSockets frame. Only need to use this method in derived classes which implement
    * more message types in processAppMessage(). You need to know what you are doing!
    *
    * @param opcode     The WebSocket frame opcode.
    * @param fin        FIN flag for WebSocket frame.
    * @param payload    Frame payload or null.
    * @param offset     Offset within payload of the chunk to send.
    * @param length     Length of the chunk within payload to send.
    */
   protected void sendFrame(int opcode, boolean fin, byte[] payload, int offset, int length) throws IOException {

      // first octet
      byte b0 = 0;
      if (fin) {
         b0 |= (byte) (1 << 7);
      }
      b0 |= (byte) opcode;
      mBuffer.write(b0);

      // second octet
      byte b1 = 0;
      if (mOptions.getMaskClientFrames()) {
         b1 = (byte) (1 << 7);
      }

      long len = length;

      // extended payload length
      if (len <= 125) {
         b1 |= (byte) len;
         mBuffer.write(b1);
      } else if (len <= 0xffff) {
         b1 |= (byte) (126 & 0xff);
         mBuffer.write(b1);
         mBuffer.write(new byte[] {(byte)((len >> 8) & 0xff),
                                   (byte)(len & 0xff)});
      } else {
         b1 |= (byte) (127 & 0xff);
         mBuffer.write(b1);
         mBuffer.write(new byte[] {(byte)((len >> 56) & 0xff),
                                   (byte)((len >> 48) & 0xff),
                                   (byte)((len >> 40) & 0xff),
                                   (byte)((len >> 32) & 0xff),
                                   (byte)((len >> 24) & 0xff),
                                   (byte)((len >> 16) & 0xff),
                                   (byte)((len >> 8)  & 0xff),
                                   (byte)(len         & 0xff)});
      }

      byte mask[] = null;
      if (mOptions.getMaskClientFrames()) {
         // a mask is always needed, even without payload
         mask = newFrameMask();
         mBuffer.write(mask[0]);
         mBuffer.write(mask[1]);
         mBuffer.write(mask[2]);
         mBuffer.write(mask[3]);
      }

      if (len > 0) {
         if (mOptions.getMaskClientFrames()) {
            /// \todo optimize masking
            /// \todo masking within buffer of output stream
            for (int i = 0; i < len; ++i) {
               payload[i + offset] ^= mask[i % 4];
            }
         }
         mBuffer.write(payload, offset, length);
      }
   }


   /**
    * Process message received from foreground thread. This is called from
    * the message looper set up for the background thread running this writer.
    *
    * @param msg     Message from thread message queue.
    */
   @Override
   public void handleMessage(Message msg) {

      try {

         // clear send buffer
         mBuffer.clear();

         // process message from master
         processMessage(msg.obj);

         // send out buffered data
         mBuffer.flip();
         while (mBuffer.remaining() > 0) {
            // this can block on socket write
            @SuppressWarnings("unused")
            int written = mSocket.write(mBuffer.getBuffer());
         }

      } catch (SocketException e) {
    	  
    	  if (DEBUG) Log.d(TAG, "run() : SocketException (" + e.toString() + ")");
    	  
    	  // wrap the exception and notify master
    	  notify(new WebSocketMessage.ConnectionLost());
      } catch (Exception e) {

         if (DEBUG) e.printStackTrace();

         // wrap the exception and notify master
         notify(new WebSocketMessage.Error(e));
      }
   }


   /**
    * Process WebSockets or control message from master. Normally,
    * there should be no reason to override this. If you do, you
    * need to know what you are doing.
    *
    * @param msg     An instance of the message types within WebSocketMessage
    *                or a message that is handled in processAppMessage().
    */
   protected void processMessage(Object msg) throws IOException, WebSocketException {

      if (msg instanceof WebSocketMessage.TextMessage) {

         sendTextMessage((WebSocketMessage.TextMessage) msg);

      } else if (msg instanceof WebSocketMessage.RawTextMessage) {

         sendRawTextMessage((WebSocketMessage.RawTextMessage) msg);

      } else if (msg instanceof WebSocketMessage.BinaryMessage) {

         sendBinaryMessage((WebSocketMessage.BinaryMessage) msg);

      } else if (msg instanceof WebSocketMessage.Ping) {

         sendPing((WebSocketMessage.Ping) msg);

      } else if (msg instanceof WebSocketMessage.Pong) {

         sendPong((WebSocketMessage.Pong) msg);

      } else if (msg instanceof WebSocketMessage.Close) {

         sendClose((WebSocketMessage.Close) msg);

      } else if (msg instanceof WebSocketMessage.ClientHandshake) {

         sendClientHandshake((WebSocketMessage.ClientHandshake) msg);

      } else if (msg instanceof WebSocketMessage.Quit) {

         mLooper.quit();

         if  (DEBUG) Log.d(TAG, "ended");

         return;

      } else {

         // call hook which may be overridden in derived class to process
         // messages we don't understand in this class
         processAppMessage(msg);
      }
   }


   /**
    * Process message other than plain WebSockets or control message.
    * This is intended to be overridden in derived classes.
    *
    * @param msg      Message from foreground thread to process.
    */
   protected void processAppMessage(Object msg) throws WebSocketException, IOException {

      throw new WebSocketException("unknown message received by WebSocketWriter");
   }
}
