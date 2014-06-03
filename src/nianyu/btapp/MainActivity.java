package nianyu.btapp;


import java.util.Timer;
import java.util.TimerTask;

import nianyu.View.TabMenuView;
import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.widget.TabHost;
import android.widget.Toast;
import android.widget.TabHost.OnTabChangeListener;

public class MainActivity extends FragmentActivity {
	private String TAG = "BtMain";
	private boolean isQuit = false;
	private boolean D = true;
	//蓝牙相关
	BluetoothAdapter mBluetoothAdapter = null;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		if(D)Log.d(TAG,"-- onCreate --");
		
		//获取蓝牙适配器
		if(D)Log.d(TAG,"获取蓝牙适配器");
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();		
		if (mBluetoothAdapter == null) {
		     Toast.makeText(this, "设备不支持蓝牙功能!", Toast.LENGTH_LONG).show();
		     finish();
		     return;
		}
		if(D)Log.d(TAG,"获取适配器成功");
		
		
		 TabHost tabHost = (TabHost)findViewById(R.id.tabhost);
		 tabHost.setup();
		 
		 final TabMenuView mv1 = (TabMenuView)LayoutInflater.from(this).inflate(R.layout.tab_view, null);
		 mv1.setText(R.string.spp_str);
		 mv1.setImage(R.drawable.home);
		 
		 final TabMenuView mv2 = (TabMenuView)LayoutInflater.from(this).inflate(R.layout.tab_view, null);
		 mv2.setText(R.string.other_str);
		 mv2.setImage(R.drawable.music);
		 
		 tabHost.setOnTabChangedListener(new OnTabChangeListener() {
			
			@Override
			public void onTabChanged(String tabId) {
				mv1.unSelected();
				mv2.unSelected();
				if(tabId.equals("taba")){
					mv1.selected();
				}else if(tabId.equals("tabb")){
					mv2.selected();
				}
			}
		});
		 
		tabHost.addTab(tabHost.newTabSpec("taba").setIndicator(mv1)
				.setContent(R.id.tab1));
		tabHost.addTab(tabHost.newTabSpec("tabb").setIndicator(mv2)
				.setContent(R.id.tab2));
		
	}
	
	
	
	
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		if(D) Log.d(TAG, "+++ ON START +++");
	
		if(!mBluetoothAdapter.isEnabled()){
			//Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			//startActivityForResult(enableIntent,REQUEST_ENABLE_BT);
			mBluetoothAdapter.enable();//enable()方式不弹出窗口
		}
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if(D) Log.e(TAG, "-- ON DESTROY --");
		super.onDestroy();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	Timer timer = new Timer();
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode == KeyEvent.KEYCODE_BACK){
			if(isQuit == false){
				isQuit = true;
				Toast.makeText(getBaseContext(), "再按一次退出程序", Toast.LENGTH_LONG).show();
				TimerTask m_task = null;
				m_task = new TimerTask(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						isQuit = false;
					}
					
				};
				timer.schedule(m_task, 2000);
			}else{
				MainActivity.this.finish();
			}
			return false;
		}
		return super.onKeyDown(keyCode, event);
	} 

}
