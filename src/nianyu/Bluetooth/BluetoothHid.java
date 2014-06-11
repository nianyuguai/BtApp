package nianyu.Bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import android.bluetooth.BluetoothServerSocket;
import android.content.Context;
import android.os.ParcelUuid;
import android.util.Log;

public class BluetoothHid {
			private static String TAG = "BluetoothHid";
			private boolean D = true;
			
			public static final int TYPE_RFCOMM = 1;  
			public static final int TYPE_SCO = 2;  
			public static final int TYPE_L2CAP = 3;  
			
			public static final int CONTROL_CHANNEL = 17;
			public static final int DATA_CHANNEL = 19;
			
			//private BluetoothSocket controlSocket;
			//private BluetoothSocket dataSocket;
			
			private ConnectedHidThread mConnectedThread;
			private ConnectHidThread mConnectThread;
			private BluetoothAdapter mAdapter; 
/*
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
	*/
			
			public BluetoothHid(Context context){
				mAdapter = BluetoothAdapter.getDefaultAdapter();
				if(D)Log.i(TAG,"construtor");
			}
			
			
			public synchronized void stop(){
				if(D)Log.d(TAG,"BluetoothHid stop()");
				if(mConnectedThread!=null){
					mConnectedThread.cancel();
					mConnectedThread = null;
				}
				if(mConnectThread!=null){
					mConnectThread.cancel();
					mConnectThread = null;
				}
				
			}
			
			public synchronized void connectedHid(BluetoothSocket socket,BluetoothDevice device){
				if(mConnectedThread!=null){
					mConnectedThread.cancel();
					mConnectedThread = null;
				}
				if(mConnectThread!=null){
					mConnectThread.cancel();
					mConnectThread = null;
				}
				mConnectedThread = new ConnectedHidThread(socket);
				mConnectedThread.start();
				
			}
			
			
			/*
			public static BluetoothServerSocket createBluetoothServerSocket(String address,int psm){
				return createBluetoothServerSocket(TYPE_L2CAP,-1,false,false,address,psm);
			}
			
			private static BluetoothServerSocket createBluetoothServerSocket(int type,int fd,boolean auth,boolean encrypt,String address,int port){
				try {
		            Constructor<BluetoothServerSocket> constructor = BluetoothServerSocket.class.getDeclaredConstructor(
		                    int.class, int.class,boolean.class,boolean.class,String.class, int.class);
		            constructor.setAccessible(true);
		            BluetoothServerSocket clientSocket = (BluetoothServerSocket) 
		                constructor.newInstance(type,fd,auth,encrypt,address,port);
		            
		            
		            
		            return clientSocket;
		        }catch (Exception e) { return null; }
			}
			
			
			private static void bindListen(BluetoothServerSocket serverSocket)
					throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException{
				BluetoothSocket localBluetoothSocket = getSocket(serverSocket);
			    Method localMethod = BluetoothSocket.class.getDeclaredMethod("bindListen", new Class[0]);
			    localMethod.setAccessible(true);
			    int i = ((Integer)localMethod.invoke(localBluetoothSocket, new Object[0])).intValue();
			    if (i != 0);
			    try
			    {
			    	serverSocket.close();
			    	throwErrnoNative(localBluetoothSocket, i);
			    	return;
			    }
			    catch (IOException localIOException)
			    {
			    }
			}
			
			private static BluetoothSocket getSocket(BluetoothServerSocket serverSocket)
				    throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException
				  {
				    Field localField = BluetoothServerSocket.class.getDeclaredField("mSocket");
				    localField.setAccessible(true);
				    return ((BluetoothSocket)localField.get(serverSocket));
				  }
			
			private static void throwErrnoNative(BluetoothSocket paramBluetoothSocket, int paramInt)
				    throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException
				  {
				    Class[] arrayOfClass = new Class[1];
				    arrayOfClass[0] = Integer.TYPE;
				    Method localMethod = BluetoothSocket.class.getDeclaredMethod("throwErrnoNative", arrayOfClass);
				    localMethod.setAccessible(true);
				    Object[] arrayOfObject = new Object[1];
				    arrayOfObject[0] = Integer.valueOf(paramInt);
				    localMethod.invoke(paramBluetoothSocket, arrayOfObject);
				  }
			*/
			/*
			public static BluetoothSocket createL2CAPBluetoothSocket(String address,int psm){
				return createBluetoothSocket(TYPE_L2CAP,-1,false,false,address,psm);
			}
			
			 private static BluetoothSocket createBluetoothSocket(int type,int fd,boolean auth,boolean encrypt,String address,int port)
			  {
			    try
			    {
			    	Constructor<BluetoothSocket> constructor = BluetoothSocket.class.getDeclaredConstructor(
		                    int.class, int.class,boolean.class,boolean.class,String.class, int.class);
		            constructor.setAccessible(true);
		            BluetoothSocket clientSocket = (BluetoothSocket) 
		                constructor.newInstance(type,fd,auth,encrypt,address,port);
		            return clientSocket;
			    }
			    catch (Exception localException)
			    {
			    }
			    return null;
			  }
			*/
			/*
			private static BluetoothSocket createBluetoothSocket(int type,int fd,boolean auth,boolean encrypt,String address,int port){
				try {
		            Constructor<BluetoothSocket> constructor = BluetoothSocket.class.getDeclaredConstructor(
		                    int.class, int.class,boolean.class,boolean.class,String.class, int.class);
		            constructor.setAccessible(true);
		            BluetoothSocket clientSocket = (BluetoothSocket) 
		                constructor.newInstance(type,fd,auth,encrypt,address,port);
		            return clientSocket;
		        }catch (Exception e) { return null; }
			}
			*/
			public synchronized void connectHid(BluetoothDevice device,int channel){
				if(mConnectedThread!=null){
					mConnectedThread.cancel();
					mConnectedThread = null;
				}
				if(mConnectThread!=null){
					mConnectThread.cancel();
					mConnectThread = null;
				}
				
				mConnectThread = new ConnectHidThread(device,channel);
				mConnectThread.start();
				if(D)Log.i(TAG,"connectHid()");
				
			}
			
			
			private class ConnectHidThread extends Thread{
				private BluetoothSocket mSocket;
				private BluetoothDevice mDevice;
				private BluetoothSocket controlSocket;
				private BluetoothSocket dataSocket;
				
				public ConnectHidThread(BluetoothDevice device,int channel){
					mDevice = device;
					
					BluetoothSocket tmp	= null;
					
					//tmp = BluetoothMethod.createL2CAPBluetoothSocket(device, channel);
					controlSocket = BluetoothMethod.createL2CAPBluetoothSocket(device, BluetoothMethod.CONTROL_CHANNEL);
					dataSocket = BluetoothMethod.createL2CAPBluetoothSocket(device, BluetoothMethod.DATA_CHANNEL);
					mSocket = dataSocket;
					if(D)Log.i(TAG,"ConnnectThread: createL2CPBluetoothSocket");
					//mSocket = tmp;
				}

				@Override
				public void run() {
					// TODO Auto-generated method stub
					setName("ConnectHidThread");
					try {
						//mSocket.connect();
						controlSocket.connect();
						dataSocket.connect();
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						try {
							//mSocket.close();
							controlSocket.close();
							dataSocket.close();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
							if(D)Log.e(TAG,"ConnnectThread: unable to close() socket during connection failure",e1);
						}
						
						BluetoothHid.this.stop();
						return;
					}
					
					synchronized (BluetoothHid.this){
						mConnectThread = null;
					}
					
					connectedHid(mSocket,mDevice);
				}
				
				public void cancel(){
					try {
						mSocket.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Log.e(TAG, "ConnnectThread: close() of connect socket failed", e);
					}
				}
				
				
			}
			
			
			private class ConnectedHidThread extends Thread{
				//private OutputStream os;
				private InputStream is;
				private BluetoothSocket mSocket;
				
				public ConnectedHidThread(BluetoothSocket dataSocket){
					Log.i(TAG, "create ConnectedHidThread");
					InputStream tmpIn = null;
		
					mSocket = dataSocket;
					
					try {
						is = dataSocket.getInputStream();
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
							Log.i(TAG, "Msg: "+String.valueOf(bytes));
							
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
							BluetoothHid.this.stop();
							return;
						}
					}
				}
				
				public void cancel(){
					try {
						mSocket.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
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
