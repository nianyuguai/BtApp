package nianyu.Bluetooth;

import java.lang.reflect.Method;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;


public class BluetoothMethod {
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

}
