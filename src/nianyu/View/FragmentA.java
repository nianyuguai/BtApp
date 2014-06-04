package nianyu.View;

import nianyu.Bluetooth.BluetoothChatService;
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
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class FragmentA extends Fragment{
	
	private BluetoothAdapter mAdapter;
	private ArrayAdapter<String> mDeviceArrayAdapter;
	private ArrayAdapter<String> mConversationArrayAdapter;
	private ListView mConversationView;
	private ListView device_lv;
	private BluetoothDevice btDevice;
	private BluetoothReceiver receiver;
	private BluetoothChatService mChatService = null;
	
	
	private Context mContext;
	
	private boolean mConnectFlag = false;
	private boolean receiver_flag  = false;
	private Button mSearchBtn = null;
	private TextView mStatusTv = null;
	private EditText mOutEditText;
    private Button mSendButton;
	private ProgressBar mSearchPb;
	private LinearLayout mMessagell;
	
	private StringBuffer mOutStringBuffer;
	private String mConnectedDeviceName;
	private String TAG = "FragmentA";
	private static final int MENU_CANCEL_PAIRED = 1;
	
	// Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    
    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.frag_spp_view, container,false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		
		super.onActivityCreated(savedInstanceState);
		
		mMessagell = (LinearLayout)getView().findViewById(R.id.message_ll);
		mSearchPb = (ProgressBar)getView().findViewById(R.id.search_pb);
		
		mStatusTv = (TextView)getView().findViewById(R.id.bt_status);
		
		//设备listview
		mAdapter = BluetoothAdapter.getDefaultAdapter();
		mDeviceArrayAdapter = new ArrayAdapter<String>(getView().getContext(),R.layout.device_name);		
		device_lv = (ListView) getView().findViewById(R.id.device_lv);
		device_lv.setAdapter(mDeviceArrayAdapter);
		device_lv.setOnItemClickListener(mDeviceClickListener);
		device_lv.setOnCreateContextMenuListener(mCCMListener);
		
		//聊天listview
		
		//注册广播
		IntentFilter filter = new IntentFilter();
		filter.addAction(BluetoothDevice.ACTION_FOUND);
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		filter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
		filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
		
		mContext = getView().getContext();
		receiver = new BluetoothReceiver();
		mContext.registerReceiver(receiver,filter);
	        
			
	     mSearchBtn = (Button)getView().findViewById(R.id.search_btn);
		 mSearchBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					//mDeviceInfoLl.setVisibility(View.VISIBLE);
					device_lv.setVisibility(View.VISIBLE);
					mSearchPb.setVisibility(View.VISIBLE);
					mMessagell.setVisibility(View.GONE);
					SearchDao s_dao = new SearchDao(mContext);
					s_dao.deleteAll();
					
					mStatusTv.setText("正在搜索");
					mDeviceArrayAdapter.clear();
					mDeviceArrayAdapter.notifyDataSetChanged();
					mSearchBtn.setClickable(false);
					
					doDiscovery();
				}
			});
		 
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onStart() {
		super.onStart();
		Log.e(TAG, "++FragmentA ON START ++");
		
		if(!mAdapter.isEnabled()){
			mSearchBtn.setClickable(false);
			mStatusTv.setText("蓝牙未开启");
		 }else{
			 	 
		 }
		
	}
	
	@Override
	public void onDestroy() {
		if(!receiver_flag){
			receiver_flag = true;
			mContext.unregisterReceiver(receiver);
		}
		if (mChatService != null) mChatService.stop();
		super.onDestroy();
	}

	private void doDiscovery(){
		if (mAdapter.isDiscovering()) {
			mAdapter.cancelDiscovery();
        }	
		mAdapter.startDiscovery();
	}
	
	private void doPair(String addr){
		  BluetoothDevice device = mAdapter.getRemoteDevice(addr);
		  mStatusTv.setText(device.getName()+"正在配对");
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
				setupChat(device);
			}
	}
	
	
	
	private void unPair(String addr){
		BluetoothDevice device = mAdapter.getRemoteDevice(addr);
		if(device.getBondState()== BluetoothDevice.BOND_BONDED){
			try {
				BluetoothMethod.removeBond(device.getClass(), device);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
     * Sends a message.
     * @param message  A string of text to send.
     */
    private void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(mContext,"设备未连接", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mChatService.write(send);

            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);
            mOutEditText.setText(mOutStringBuffer);
        }
    }
	
	private void setupChat(BluetoothDevice device) {
        Log.d(TAG, "setupChat()");
        device_lv.setVisibility(View.GONE);
        mStatusTv.setText(device.getName()+"正在连接");
        btDevice = device;
        // Initialize the array adapter for the conversation thread
        mConversationArrayAdapter = new ArrayAdapter<String>(mContext, R.layout.message);
        mConversationView = (ListView) getView().findViewById(R.id.in);
        mConversationView.setAdapter(mConversationArrayAdapter);

        // Initialize the compose field with a listener for the return key
        mOutEditText = (EditText) getView().findViewById(R.id.edit_text_out);
        mOutEditText.setOnEditorActionListener(mWriteListener);

        // Initialize the send button with a listener that for click events
        mSendButton = (Button) getView().findViewById(R.id.button_send);
        mSendButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Send a message using content of the edit text widget
                TextView view = (TextView) getView().findViewById(R.id.edit_text_out);
                String message = view.getText().toString();
                sendMessage(message);
            }
        });
        mMessagell.setVisibility(View.VISIBLE);
        /*
        sp = this.getSharedPreferences("ButtonInfo", MODE_PRIVATE);
        arrayButton[0] = (Button)findViewById(R.id.play);
        arrayButton[1] = (Button)findViewById(R.id.previous);
        for(int i=0;i < 6;i++){
            arrayButton[i].setText(sp.getString(nameString[i], ""));
            arrayButton[i].setOnClickListener(this);
            arrayButton[i].setOnLongClickListener(this);
        }
	   */
        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothChatService(mContext, mHandler);
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
                // Start the Bluetooth chat services
                mChatService.start();
            }
        }
        mChatService.connect(device);
        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
    }
	// The action listener for the EditText widget, to listen for the return key
    private TextView.OnEditorActionListener mWriteListener =new TextView.OnEditorActionListener() {
				@Override
				public boolean onEditorAction(TextView v, int actionId,
						KeyEvent event) {
					if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {
                        String message = v.getText().toString();
                        sendMessage(message);
                    }
                   Log.i(TAG, "END onEditorAction");
                    return true;
				}
            };

	private OnItemClickListener mDeviceClickListener = new OnItemClickListener(){

		@Override
		public void onItemClick(AdapterView<?> arg0, View v, int arg2,
				long arg3) {
			mAdapter.cancelDiscovery();
			mSearchPb.setVisibility(View.VISIBLE);
			
			String info = ((TextView) v).getText().toString();

	        String address = info.substring(info.length() - 17);
	        doPair(address);
			
		}
		
	};
	
	private OnCreateContextMenuListener mCCMListener = new OnCreateContextMenuListener(){

		@Override
		public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {
	        //doPair(address);
			menu.setHeaderTitle("执行操作");
			menu.add(0,MENU_CANCEL_PAIRED,0,"取消配对");
		}
	};
	
	public boolean onContextItemSelected(MenuItem item){
		
		AdapterView.AdapterContextMenuInfo contextMenuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		Log.i(TAG,"select item");
		//int position = contextMenuInfo.position;
		switch(item.getItemId()){
        case MENU_CANCEL_PAIRED:

         	    String info = ((TextView) contextMenuInfo.targetView).getText().toString();
         	    String address = info.substring(info.length() - 17);
                BluetoothDevice device = mAdapter.getRemoteDevice(address);
         	    unPair(address);
         	    
         	    Toast.makeText(getView().getContext(), device.getName()+" 配对取消", Toast.LENGTH_SHORT) .show();
         	break;
        default:
            break;
        }
		return super.onContextItemSelected(item);
	}
	
    private class BluetoothReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
            
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed already
                
                //if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
             
                	SearchDao s_dao = new SearchDao(context);
                	String addr = device.getAddress();
				    String name = device.getName();
				    if(!s_dao.find(addr)){
				    	 mDeviceArrayAdapter.add(device.getName() + "\n" + device.getAddress());
		                 Log.i(TAG, "Receive "+device.getName() + "  " + device.getAddress());
				    	 s_dao.add(name, addr, 0, 0, 0);
				    }
                	
            // When discovery is finished, change the Activity title
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                //setProgressBarIndeterminateVisibility(false);
                //setTitle(R.string.select_device);
            	
            	mSearchBtn.setClickable(true);
                if (mDeviceArrayAdapter.getCount() == 0) {
                    //String noDevices = getResources().getText(R.string.none_found_str).toString();
                    //mDeviceArrayAdapter.add(noDevices);
                	mStatusTv.setText("附近未找到蓝牙设备");
                	mSearchPb.setVisibility(View.GONE);
                }else{
                	
                }
            }else if(BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)){
				switch(mAdapter.getState()){
				case BluetoothAdapter.STATE_ON:
					mSearchBtn.setClickable(true);
					mStatusTv.setText("蓝牙已开启");
					break;
				case BluetoothAdapter.STATE_OFF:
					mSearchBtn.setClickable(false);
					mStatusTv.setText("蓝牙未开启");
					mSearchPb.setVisibility(View.GONE);
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
					e.printStackTrace();
				}
            }else if(BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)){
            	Log.i(TAG, "ACTION_BOND_STATE_CHANGED");
            	BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            	if(device.getBondState()==BluetoothDevice.BOND_BONDED){
            		
            		setupChat(device);
            		
            	}
            }
        }
    }
  
    // The Handler that gets information back from the BluetoothChatService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                           
                            mConversationArrayAdapter.clear();
                            mSearchPb.setVisibility(View.GONE);
                            //mConnect.setText(R.string.btn_disconnect);
                            //设置连接标志
                            //mConnectFlag = true;
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            //mTitle.setText(R.string.title_connecting);
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:
                            //mTitle.setText(R.string.title_not_connected);
                            //mConnect.setText(R.string.btn_connect);
                          //设置连接标志
                            //mConnectFlag = false;
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    mConversationArrayAdapter.add("我:  " + writeMessage);
                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    mConversationArrayAdapter.add(mConnectedDeviceName+":  " + readMessage);
                    break;
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    //Toast.makeText(getApplicationContext(), "Connected to "
                    //        + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                	mStatusTv.setText(mConnectedDeviceName+"已连接");
                    break;
                case MESSAGE_TOAST:
                    //Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                    //        Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
   

}
