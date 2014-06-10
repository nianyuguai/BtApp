package nianyu.View;

import java.io.IOException;
import java.io.InputStream;

import nianyu.Bluetooth.BluetoothChatService;
import nianyu.Bluetooth.BluetoothHid;
import nianyu.Bluetooth.BluetoothMethod;
import nianyu.Data.DeviceDao;
import nianyu.Data.SearchDao;
import nianyu.btapp.MainActivity;
import nianyu.btapp.R;
import nianyu.btapp.Setting;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

public class FragmentA extends Fragment implements OnClickListener,OnLongClickListener{
	
	private BluetoothAdapter mAdapter;
	private ArrayAdapter<String> mDeviceArrayAdapter;
	private ArrayAdapter<String> mConversationArrayAdapter;
	private ListView mConversationView;
	private ListView device_lv;
	private BluetoothDevice btDevice;
	private BluetoothReceiver receiver;
	private BluetoothChatService mChatService = null;
	private BluetoothSocket mBtSocket;
	private InputStream istream;
	
	private Context mContext;
	
	private boolean mConnectFlag = false;
	private boolean receiver_flag  = false;
	private boolean search_flag = false;
	
	public boolean edit_pc	= false;
	public boolean btn_pc	= false;
	
	private Button mSearchBtn = null;
	private TextView mStatusTv = null;
	private EditText mOutEditText;
    private Button mSendButton;
	private ProgressBar mSearchPb;
	private LinearLayout mMessagell;
	private LinearLayout mMessagell2;
	private LinearLayout mMessageBtn;
	private TabWidget mTab;

	private Button []arrayButton = new Button[2];
	private Integer[] Button_id = { R.id.one_btn, R.id.two_btn};
	private String nameString[]={"Btn1","Btn2",};
	private String valueString[]={"Btn1value","Btn2value",};
	
    private AlertDialog.Builder malertDialog;
    private String mButton;
    private String mButtonValue;
    private SharedPreferences sp;
    private EditText ename;
    private EditText emsg;
	
	private StringBuffer mOutStringBuffer;
	private String mConnectedDeviceName;
	private String TAG = "FragmentA";
	private static final int MENU_CANCEL_PAIRED = 1;
	
	private BluetoothHid btHid = null;
	
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
		setHasOptionsMenu(true);
		mMessagell = (LinearLayout)getView().findViewById(R.id.message_ll);
		mMessagell2 = (LinearLayout)getView().findViewById(R.id.message_ll2);
		mMessageBtn = (LinearLayout)getView().findViewById(R.id.msg_btn_ll);
		
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
	        
		mTab = (TabWidget)getActivity().findViewById(android.R.id.tabs);
	    mSearchBtn = (Button)getView().findViewById(R.id.search_btn);
		mSearchBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(!mConnectFlag){
						if(!search_flag){
						//mDeviceInfoLl.setVisibility(View.VISIBLE);
						device_lv.setVisibility(View.VISIBLE);
						mSearchPb.setVisibility(View.VISIBLE);
						mMessagell.setVisibility(View.GONE);
						mMessagell2.setVisibility(View.GONE);
						mMessageBtn.setVisibility(View.GONE);
						SearchDao s_dao = new SearchDao(mContext);
						s_dao.deleteAll();
						
						mStatusTv.setText("正在搜索");
						mDeviceArrayAdapter.clear();
						mDeviceArrayAdapter.notifyDataSetChanged();
						
						//标志位
						search_flag = true;
						
						doDiscovery();
						}
					}else{
						if (mChatService != null) mChatService.stop();
						mSearchBtn.setText("搜索");
						
						mSearchBtn.setBackgroundResource(R.drawable.btn_selector);
						mConversationArrayAdapter.clear();
						mTab.setVisibility(View.VISIBLE);
						mSearchPb.setVisibility(View.GONE);
						mMessagell.setVisibility(View.GONE);
						mMessagell2.setVisibility(View.GONE);
						mMessageBtn.setVisibility(View.GONE);
						mStatusTv.setText("未连接");
					}
				}
			});
		
			PreferenceManager.setDefaultValues(mContext, R.xml.preferences, false);
			loadPref();
		 
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
				//setupChat(device);
				hidConnect(device);
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
	
	private void hidConnect(BluetoothDevice device){
		Log.i(TAG,"hid connect");
		if (mAdapter.isDiscovering()) {
			mAdapter.cancelDiscovery();
        }
		if(btHid!=null){
			btHid.stop();
		}
		Log.i(TAG,"hid start");
		btHid.connectHid(device, BluetoothHid.DATA_CHANNEL);
		Log.i(TAG,"hid start");
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
        
        sp = mContext.getSharedPreferences("ButtonInfo", mContext.MODE_PRIVATE);
        arrayButton[0] = (Button)getView().findViewById(Button_id[0]);
        arrayButton[1] = (Button)getView().findViewById(Button_id[1]);

        for(int i=0;i < 2;i++){
        	if(sp.getString(nameString[i], "").isEmpty()){
        		arrayButton[i].setTextColor(getResources().getColor(R.color.btncolor));
        		arrayButton[i].setText(R.string.btn_edit_long);
        		Log.i(TAG,"empty");
        	}else{
        		arrayButton[i].setTextColor(getResources().getColor(android.R.color.white));
        		arrayButton[i].setText(sp.getString(nameString[i], ""));
        		Log.i(TAG,"not empty");
        	}
            //arrayButton[i].setText(sp.getString(nameString[i], ""));
            arrayButton[i].setOnClickListener(this);
            arrayButton[i].setOnLongClickListener(this);
        }
        
        loadPref();
        
        mMessagell.setVisibility(View.VISIBLE);
        if(btn_pc)mMessageBtn.setVisibility(View.VISIBLE);
        if(edit_pc)mMessagell2.setVisibility(View.VISIBLE);
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
	
    public void onClick(View v) {
        // TODO Auto-generated method stub
        for(int i=0;i<2;i++){
            if(v.getId() == Button_id[i]){
                sendMessage(sp.getString(valueString[i], ""));
                break;
            }
        }
    }

    public boolean onLongClick(View v) {
        // TODO Auto-generated method stub
        //int tag = (Integer)v.getTag();
        for(int i=0;i<2;i++){
            if(v.getId() == Button_id[i]){
                buttonSetting(i);
            }
        }
        return true;
    }
    
    public void buttonSetting(final int tag){
    	LayoutInflater buttonset = LayoutInflater.from(mContext);
        View view = buttonset.inflate(R.layout.button_set, null);
        malertDialog = new AlertDialog.Builder(mContext);
        malertDialog.setTitle(R.string.btn_edit);
        //LinearLayout buttonset = (LinearLayout)getLayoutInflater().inflate(R.layout.button_set,null);
        
        malertDialog.setView(view);


        ename = (EditText)view.findViewById(R.id.editName);
        emsg = (EditText)view.findViewById(R.id.editMsg);

        ename.setText(sp.getString(nameString[tag], ""));
        emsg.setText(sp.getString(valueString[tag], ""));
        //ename.setText(sp.getString(key, defValue))
        malertDialog.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener(){

            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
            	
                mButton = ename.getText().toString();
                mButtonValue = emsg.getText().toString();
                Editor Edit = sp.edit();
                Edit.putString(nameString[tag],mButton);
                Edit.putString(valueString[tag],mButtonValue);
                Edit.commit();
                if(mButton.isEmpty()){
                	arrayButton[tag].setTextColor(getResources().getColor(R.color.btncolor));
                	arrayButton[tag].setText(R.string.btn_edit_long);
                }else{
                	arrayButton[tag].setTextColor(getResources().getColor(android.R.color.white));
                	arrayButton[tag].setText(mButton);
                }
            }
        });
        malertDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub

            }
        });
        malertDialog.create().show();
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
			search_flag = false;
			
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
	
	@Override
	public void onCreateOptionsMenu(Menu menu,MenuInflater inflater) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getActivity().getMenuInflater().inflate(R.menu.main, menu);
		
	}
	
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
    	switch(item.getItemId()){
    	case R.id.action_settings:
    		Log.d(TAG,"item");
        	Intent intent = new Intent();
    		intent.setClass(mContext, Setting.class);
    		startActivityForResult(intent, 0); 
    		return true;
    	case R.id.msg_clr:
    		if(mConnectFlag)mConversationArrayAdapter.clear();
    		return true;
    	}
    	return false;
	}

    @Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
    	//super.onActivityResult(requestCode, resultCode, data);
    	loadPref();
	}
    
	private void loadPref(){
		SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		edit_pc = mySharedPreferences.getBoolean("checkbox_edit_pc", false);
		if(edit_pc&&mConnectFlag){
			mMessagell2.setVisibility(View.VISIBLE);
		}else{
			mMessagell2.setVisibility(View.GONE);
		}
		/*if(edit_pc){
			Log.i(TAG,"哈哈哈哈");
		}else{
			Log.i(TAG,"呵呵");
		}*/
		btn_pc = mySharedPreferences.getBoolean("checkbox_btn_pc", false);
		if(btn_pc&&mConnectFlag){
			mMessageBtn.setVisibility(View.VISIBLE);
		}else{
			mMessageBtn.setVisibility(View.GONE);
		}
		/*if(btn_pc){
			Log.i(TAG,"啦啦啦啦");
		}else{
			Log.i(TAG,"傻傻");
		}*/
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
                Bundle mBundle = intent.getExtras();
                int classValue = hexStringToAlgorism(String.valueOf(mBundle.get("android.bluetooth.device.extra.CLASS")));
                
                
                //if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
             
                	SearchDao s_dao = new SearchDao(context);
                	String addr = device.getAddress();
				    String name = device.getName();
				    if(!s_dao.find(addr)){
				    	 mDeviceArrayAdapter.add(device.getName()  +"(信号:"+String.valueOf(mBundle.get("android.bluetooth.device.extra.RSSI"))+ "dB"+")"+"\n" + device.getAddress());
		                 Log.i(TAG, "Receive "+device.getName() + "  " + device.getAddress());
		                 Log.i(TAG,"设备类型: "+ device.getName() + "  "+classValue);
				    	 s_dao.add(name, addr, 0, 0, 0);
				    }
                	
            // When discovery is finished, change the Activity title
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                //setProgressBarIndeterminateVisibility(false);
                //setTitle(R.string.select_device);
            	
            	mSearchBtn.setClickable(true);
            	search_flag = false;
            	
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
            		
            		//setupChat(device);
            		hidConnect(device);
            		
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
                            mSearchBtn.setText("断开");
                            //mSearchBtn.setBackground(getResources().getDrawable(R.drawable.btn_out_selector));
                            mSearchBtn.setBackgroundResource(R.drawable.btn_out_selector);
                            mConnectFlag = true;
                            
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
                            mConnectFlag = false;
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
                	mTab.setVisibility(View.GONE);
                    break;
                case MESSAGE_TOAST:
                    //Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                    //        Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    
    /**
     * 十六进制字符串装十进制
     * 
     * @param hex
     *            十六进制字符串
     * @return 十进制数值
     */
    public static int hexStringToAlgorism(String hex) {
        hex = hex.toUpperCase();
        int max = hex.length();
        int result = 0;
        for (int i = max; i > 0; i--) {
            char c = hex.charAt(i - 1);
            int algorism = 0;
            if (c >= '0' && c <= '9') {
                algorism = c - '0';
            } else {
                algorism = c - 55;
            }
            result += Math.pow(16, max - i) * algorism;
        }
        return result;
    }
   

}
