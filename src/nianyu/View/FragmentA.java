package nianyu.View;

import nianyu.Bluetooth.BluetoothMethod;
import nianyu.Data.DeviceDao;
import nianyu.Data.SearchDao;
import nianyu.btapp.R;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class FragmentA extends Fragment{
	
	private BluetoothAdapter mAdapter;
	private ArrayAdapter<String> mDeviceArrayAdapter;
	private ListView deivce_lv;
	private BluetoothDevice btDevice;
	private BluetoothReceiver receiver;
	private Context mContext;
	
	
	private boolean receiver_flag  = false;
	private Button mSearchBtn = null;
	private TextView mStatusTv = null;
	
	private ProgressBar mSearchPb;
	
	private String TAG = "FragmentA";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.frag_spp_view, container,false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		super.onActivityCreated(savedInstanceState);
		
		final LinearLayout mDeviceInfoLl = (LinearLayout)getView().findViewById(R.id.device_info_ll);
		mSearchPb = (ProgressBar)getView().findViewById(R.id.search_pb);
		
		mStatusTv = (TextView)getView().findViewById(R.id.bt_status);
		
		//Éè±¸listview
		mAdapter = BluetoothAdapter.getDefaultAdapter();
		mDeviceArrayAdapter = new ArrayAdapter<String>(getView().getContext(),R.layout.device_name);		
		deivce_lv = (ListView) getView().findViewById(R.id.device_lv);
		deivce_lv.setAdapter(mDeviceArrayAdapter);
		deivce_lv.setOnItemClickListener(mDeviceClickListener);
		//×¢²á¹ã²¥
		IntentFilter filter = new IntentFilter();
		filter.addAction(BluetoothDevice.ACTION_FOUND);
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		filter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
		
		mContext = getView().getContext();
		receiver = new BluetoothReceiver();
		mContext.registerReceiver(receiver,filter);
	        
			
	     mSearchBtn = (Button)getView().findViewById(R.id.search_btn);
		 mSearchBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					mDeviceInfoLl.setVisibility(View.VISIBLE);
					mSearchPb.setVisibility(View.VISIBLE);
					mStatusTv.setText("ÕýÔÚËÑË÷");
					mDeviceArrayAdapter.clear();
					mDeviceArrayAdapter.notifyDataSetChanged();
					mSearchBtn.setClickable(false);
					doDiscovery();
				}
			});
		 if(!mAdapter.isEnabled()){
			mSearchBtn.setClickable(false);
			mStatusTv.setText("À¶ÑÀÎ´¿ªÆô");
		 }
		 
	}
	


	private void doDiscovery(){
		if (mAdapter.isDiscovering()) {
			mAdapter.cancelDiscovery();
        }	
		mAdapter.startDiscovery();
	}
	
	private void doPair(String addr){
		  BluetoothDevice device = mAdapter.getRemoteDevice(addr);
		  btDevice = device;
		  if(device.getBondState()!= BluetoothDevice.BOND_BONDED){
				try {
					Log.i(TAG, "setPin 0000");
					//if(BluetoothMethod.setPin(device.getClass(), device,"1234"))
						Log.i(TAG, "setPin true");	
				    Log.i(TAG, "createBond");
					BluetoothMethod.createBond(device.getClass(), device);
					//BluetoothMethod.cancelPairingUserInput(device.getClass(), device);
				} catch (Exception e) {
					//connectfailed();
					Log.i(TAG, "doPari fail");
					e.printStackTrace();
				}
			}else{
				
			}
	}
	
	private OnItemClickListener mDeviceClickListener = new OnItemClickListener(){

		@Override
		public void onItemClick(AdapterView<?> arg0, View v, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			mAdapter.cancelDiscovery();
			
			String info = ((TextView) v).getText().toString();

	        String address = info.substring(info.length() - 17);
	        doPair(address);
			
		}
		
	};
	
	
	
    private class BluetoothReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
            
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed already
                
                //if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                	
                	DeviceDao dao = new DeviceDao(context);
                	SearchDao s_dao = new SearchDao(context);
                	String addr = device.getAddress();
				    String name = device.getName();
				    //if(!dao.find(addr)){
				    	 dao.add(name, addr, 0, 0, 0);
				    	 s_dao.add(name, addr, 1, 1, 1);
				    //}else{
				    //	 dao.update(name, addr, 0, 0, 0);
				    //}
                	
                	mDeviceArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                	Log.i(TAG, "Receive "+device.getName() + "  " + device.getAddress());
                //}
            // When discovery is finished, change the Activity title
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                //setProgressBarIndeterminateVisibility(false);
                //setTitle(R.string.select_device);
            	mSearchPb.setVisibility(View.GONE);
            	mSearchBtn.setClickable(true);
                if (mDeviceArrayAdapter.getCount() == 0) {
                    //String noDevices = getResources().getText(R.string.none_found_str).toString();
                    //mDeviceArrayAdapter.add(noDevices);
                	mStatusTv.setText("¸½½üÎ´ÕÒµ½À¶ÑÀÉè±¸");
                }
            }else if(BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)){
				switch(mAdapter.getState()){
				case BluetoothAdapter.STATE_ON:
					mSearchBtn.setClickable(true);
					mStatusTv.setText("À¶ÑÀÒÑ¿ªÆô");
					break;
				case BluetoothAdapter.STATE_OFF:
					mSearchBtn.setClickable(false);
					mStatusTv.setText("À¶ÑÀÎ´¿ªÆô");
					break;
				default:
					break;
				}
            }else if(BluetoothDevice.ACTION_PAIRING_REQUEST.equals(action)){
            	Log.i(TAG, "ACTION_PAIRING_REQUEST");
            	BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            	try {
					BluetoothMethod.setPin(device.getClass(), device,"0000");
					//BluetoothMethod.cancelPairingUserInput(device.getClass(), device);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        }
    }
  

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		if(!receiver_flag){
			receiver_flag = true;
			mContext.unregisterReceiver(receiver);
		}
	
		super.onDestroy();
	}
    

}
