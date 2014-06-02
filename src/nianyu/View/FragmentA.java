package nianyu.View;

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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

public class FragmentA extends Fragment{
	
	private BluetoothAdapter mAdapter;
	private ArrayAdapter<String> mDeviceArrayAdapter;
	private ListView deivce_lv;
	private BluetoothDevice btDevice;
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
		Button mSearchBtn = null;
		super.onActivityCreated(savedInstanceState);
		
		final LinearLayout mDeviceInfoLl = (LinearLayout)getView().findViewById(R.id.device_info_ll);
		mSearchPb = (ProgressBar)getView().findViewById(R.id.search_pb);
		
		mAdapter = BluetoothAdapter.getDefaultAdapter();
		mDeviceArrayAdapter = new ArrayAdapter<String>(getView().getContext(),R.layout.device_name);		
		deivce_lv = (ListView) getView().findViewById(R.id.device_lv);
		deivce_lv.setAdapter(mDeviceArrayAdapter);
		//deivce_lv.setOnItemClickListener(mDeviceClickListener);
		
		 IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
	     getView().getContext().registerReceiver(mReceiver, filter);

	        // Register for broadcasts when discovery has finished
	     filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
	     getView().getContext().registerReceiver(mReceiver, filter);
	     
	     mSearchBtn = (Button)getView().findViewById(R.id.search_btn);
			mSearchBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					mDeviceInfoLl.setVisibility(View.VISIBLE);
					mSearchPb.setVisibility(View.VISIBLE);
					doDiscovery();
				}
			});
	}
	
	private void doDiscovery(){
		if (mAdapter.isDiscovering()) {
			mAdapter.cancelDiscovery();
        }	
		mAdapter.startDiscovery();
	}
	
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i(TAG, "Receive");
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed already
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                	mDeviceArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                }
            // When discovery is finished, change the Activity title
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                //setProgressBarIndeterminateVisibility(false);
                //setTitle(R.string.select_device);
            	mSearchPb.setVisibility(View.GONE);
                if (mDeviceArrayAdapter.getCount() == 0) {
                    String noDevices = getResources().getText(R.string.none_found_str).toString();
                    mDeviceArrayAdapter.add(noDevices);
                }
            }
        }
    };

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		if (mAdapter != null) {
            mAdapter.cancelDiscovery();
        }
		Log.i(TAG, "onDestroy");
        // Unregister broadcast listeners
        getView().getContext().unregisterReceiver(mReceiver);
	}
    
    
}
