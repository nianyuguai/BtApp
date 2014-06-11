package nianyu.Bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.os.ParcelUuid;


public class BluetoothMethod {
	public static final int TYPE_RFCOMM = 1;  
	public static final int TYPE_SCO = 2;  
	public static final int TYPE_L2CAP = 3;  
	
	public static final int CONTROL_CHANNEL = 0x11;
	public static final int DATA_CHANNEL = 0x13;
	
	private static BluetoothSocket controlSocket;
	private static BluetoothSocket dataSocket;
	
	/** 
     * 与设备配对 参考源码：platform/packages/apps/Settings.git 
     * /Settings/src/com/android/settings/bluetooth/CachedBluetoothDevice.java 
     */  
    static public boolean createBond(Class btClass,BluetoothDevice btDevice) throws Exception {  
        Method createBondMethod = btClass.getMethod("createBond");  
        Boolean returnValue = (Boolean) createBondMethod.invoke(btDevice);  
        return returnValue.booleanValue();  
    }  
    
    /** 
     * 与设备解除配对 参考源码：platform/packages/apps/Settings.git 
     * /Settings/src/com/android/settings/bluetooth/CachedBluetoothDevice.java 
     */  
    static public boolean removeBond(Class btClass,BluetoothDevice btDevice) throws Exception {  
        Method removeBondMethod = btClass.getMethod("removeBond");  
        Boolean returnValue = (Boolean) removeBondMethod.invoke(btDevice);  
        return returnValue.booleanValue();  
    } 
    
    static public boolean setPin(Class btClass, BluetoothDevice btDevice,
			String str) throws Exception
	{
		try
		{
			Method removeBondMethod = btClass.getDeclaredMethod("setPin",new Class[]{byte[].class});
			Boolean returnValue = (Boolean) removeBondMethod.invoke(btDevice,
					new Object[]
					{str.getBytes()});
		}
		catch (SecurityException e)
		{
			// throw new RuntimeException(e.getMessage());
			e.printStackTrace();
		}
		catch (IllegalArgumentException e)
		{
			// throw new RuntimeException(e.getMessage());
			e.printStackTrace();
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;

	}

	// 取消用户输入
	static public boolean cancelPairingUserInput(Class btClass,
			BluetoothDevice device)

	throws Exception
	{
		Method createBondMethod = btClass.getMethod("cancelPairingUserInput");
	    //cancelBondProcess();
		Boolean returnValue = (Boolean) createBondMethod.invoke(device);
		return returnValue.booleanValue();
	}

	// 取消配对
	static public boolean cancelBondProcess(Class btClass,
			BluetoothDevice device)

	throws Exception
	{
		Method createBondMethod = btClass.getMethod("cancelBondProcess");
		Boolean returnValue = (Boolean) createBondMethod.invoke(device);
		return returnValue.booleanValue();
	}
	
	//A2DP 与  HeadSet
	static public boolean connect(Class btClass,BluetoothProfile proxy,BluetoothDevice btDevice) throws Exception {  
        Method connectMethod = btClass.getDeclaredMethod("connect", BluetoothDevice.class);       
        connectMethod.setAccessible(true);
        Boolean returnValue = (Boolean) connectMethod.invoke(proxy,btDevice);  
        return returnValue.booleanValue();  
    }
	
	static public boolean disconnect(Class btClass,BluetoothProfile proxy,BluetoothDevice btDevice) throws Exception {  
        Method disconnectMethod = btClass.getDeclaredMethod("disconnect", BluetoothDevice.class);     
        disconnectMethod.setAccessible(true);
        Boolean returnValue = (Boolean) disconnectMethod.invoke(proxy,btDevice);  
        return returnValue.booleanValue();  
    }
	
	
	static public BluetoothSocket createL2CAPBluetoothSocket(BluetoothDevice device,int psm){
		return createBluetoothSocket(TYPE_L2CAP,-1,false,false,device,psm);
	}
	
	private static BluetoothSocket createBluetoothSocket(int type,int fd,boolean auth,boolean encrypt,BluetoothDevice device,int port)
	  {
	    try
	    {
            Constructor<BluetoothSocket> constructor = BluetoothSocket.class.getDeclaredConstructor(int.class,
                    int.class, boolean.class, boolean.class, BluetoothDevice.class, int.class, ParcelUuid.class);
            constructor.setAccessible(true);
            BluetoothSocket clientSocket = (BluetoothSocket) constructor.newInstance(type, fd, auth, encrypt, device,
            		port, null);
            return clientSocket;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
	    }
	  }
	
	static public void connect(BluetoothDevice device) {
	    try {
	        controlSocket = createL2CAPBluetoothSocket(device, CONTROL_CHANNEL);        
	        controlSocket.connect();
	        //os = controlSocket.getOutputStream();

	        dataSocket = createL2CAPBluetoothSocket(device, DATA_CHANNEL);
	        dataSocket.connect();
	        //is = dataSocket.getInputStream();      

	        // open transmit & receive threads for input and output streams appropriately

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	 // address must use upper case  
	/*
    public static final BluetoothSocket createBluetoothSocket(int type, int fd,  boolean auth, boolean encrypt, String address, int port)  
            throws IOException  
    {  
        BluetoothSocket socket = null;  
        try  
        {  
            Constructor<BluetoothSocket> cs = BluetoothSocket.class  
                    .getDeclaredConstructor(int.class, int.class,  
                            boolean.class, boolean.class, String.class,  
                            int.class);  
            if (!cs.isAccessible())  
            {  
                cs.setAccessible(true);  
            }  
            socket = cs.newInstance(type, fd, auth, encrypt, address, port);  
        }  
        catch (SecurityException e)  
        {  
        }  
        catch (NoSuchMethodException e)  
        {  
        }  
        catch (IllegalArgumentException e)  
        {  
        }  
        catch (InstantiationException e)  
        {  
        }  
        catch (IllegalAccessException e)  
        {  
        }  
        catch (InvocationTargetException e)  
        {  
        }  
        return socket;  
    }  
    */
	

}
