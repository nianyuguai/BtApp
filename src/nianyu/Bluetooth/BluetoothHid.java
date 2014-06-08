package nianyu.Bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.ParcelUuid;
import android.util.Log;

public class BluetoothHid {
			private static String TAG = "BluetoothHid";
	
			public static final int TYPE_RFCOMM = 1;  
			public static final int TYPE_SCO = 2;  
			public static final int TYPE_L2CAP = 3;  
			
			public static final int CONTROL_CHANNEL = 17;
			public static final int DATA_CHANNEL = 19;
			
			//private BluetoothSocket controlSocket;
			//private BluetoothSocket dataSocket;
			
			private static ConnectedHidThread mHidThread;

			static public BluetoothSocket createL2CAPBluetoothSocket(BluetoothDevice device, final int channel) {
		        int type = TYPE_L2CAP; // L2CAP protocol
		        int fd = -1; // Create a new socket
		        boolean auth = false; // No authentication
		        boolean encrypt = false; // Not encrypted

		        try {
		            Constructor<BluetoothSocket> constructor = BluetoothSocket.class.getDeclaredConstructor(int.class,
		                    int.class, boolean.class, boolean.class, BluetoothDevice.class, int.class, ParcelUuid.class);
		            constructor.setAccessible(true);
		            BluetoothSocket clientSocket = (BluetoothSocket) constructor.newInstance(type, fd, auth, encrypt, device,
		                    channel, null);
		            return clientSocket;
		        } catch (Exception e) {
		            e.printStackTrace();
		            return null;
		        }
		    }
			
			
			static public void connectHid(BluetoothDevice device,int channel){
				BluetoothSocket socket = createL2CAPBluetoothSocket(device, channel);
				Log.i(TAG,"hid socket");
				try {
					socket.connect();
					Log.i(TAG,"hid socket connect");
					
					BluetoothHid bt_hid = new BluetoothHid();
					mHidThread = bt_hid.new ConnectedHidThread(socket);
					Log.i(TAG,"new thread");
					bt_hid.mHidThread.start();
					Log.i(TAG,"thread start");
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					if(socket!=null)
						try {
							socket.close();
							Log.i(TAG,"hid socket close");
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							Log.i(TAG,"hid socket fail");
							e1.printStackTrace();
						}
					e.printStackTrace();
				}
			}
			
			
			private class ConnectedHidThread extends Thread{
				//private OutputStream os;
				private InputStream is;
				private BluetoothSocket mSocket;
				
				public ConnectedHidThread(BluetoothSocket socket){
					Log.i(TAG, "create ConnectedHidThread");
					InputStream tmpIn = null;
					mSocket = socket;
					
					try {
						is = socket.getInputStream();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Log.e(TAG, "sockets not created", e);
					}
					
					//is = tmpIn;
				}

				@Override
				public void run() {
					// TODO Auto-generated method stub
					Log.i(TAG, "BEGIN HID ConnectedThread");
					byte[] buffer = new byte[1024];
					int bytes;
					
					while(true){
						try {
							bytes = is.read(buffer);
							Log.i(TAG, "Msg: "+bytes);
							
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							 Log.e(TAG, "hid disconnected", e);
							 try {
								mSocket.close();
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}
					}
				}
				
				
			}
/*	   
	    public void connect(BluetoothDevice device) {
	        try {
	            controlSocket = createL2CAPBluetoothSocket(device, CONTROL_CHANNEL);        
	            controlSocket.connect();
	            os = controlSocket.getOutputStream();

	            dataSocket = createL2CAPBluetoothSocket(device, DATA_CHANNEL);
	            dataSocket.connect();
	            is = dataSocket.getInputStream();      

	            // open transmit & receive threads for input and output streams appropriately

	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
*/
	  
	
}
