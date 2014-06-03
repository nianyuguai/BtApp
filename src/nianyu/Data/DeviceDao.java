/**  
 * All rights Reserved, Designed By nianyuguai   
 * @Title:  DeviceDao.java   
 * @Package nianyu.app   
 * @Description:    TODO(描述该文件做什么)   
 * @author: nianyuguai     
 * @date:   2014-1-16 上午12:06:02   
 * @version V1.0     
 */ 
package nianyu.Data;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/** 
 * @ClassName:	DeviceDao 
 * @Description: 
 * @author:	nianyuguai
 * @date:	2014-1-16 上午12:06:02  
 */
public class DeviceDao {
	private static final String TAG = "DeviceDao";
    private static final boolean D = true;
	private DeviceOpenHelper helper;
	public DeviceDao(Context context){
		helper = new DeviceOpenHelper(context);
	}
	//添加一条记录
	public void add(String name,String address,int pair,int mA2dp,int mHeadset){
		SQLiteDatabase db = helper.getWritableDatabase();
		db.execSQL("insert into list (name,address,pair,mA2dp,mHeadset) values (?,?,?,?,?)", new Object[]{name,address,pair,mA2dp,mHeadset});
		db.close();
	}
	//查询是否存在记录
	public boolean find(String address){
		if (D) Log.d(TAG, "device sql find");
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor  = db.rawQuery("select * from list where address = ?", new String[]{address});
		boolean result = cursor.moveToNext();
		cursor.close();
		db.close();
		return result;
	}
	//修改一条记录
	public void update(String name,String address,int pair,int mA2dp,int mHeadset){
		SQLiteDatabase db = helper.getWritableDatabase();
		db.execSQL("update list set name = ?,pair = ?,mA2dp = ?,mHeadset = ? where address = ?", new Object[]{name,pair,mA2dp,mHeadset,address});
		db.close();
	}
	//删除一条记录
	public void delete(String address){
		SQLiteDatabase db = helper.getWritableDatabase();
		db.execSQL("delete from list where address = ?", new String[]{address});
		db.close();
	}
	//查找全部的记录，并返回一个List集合
	public List<Device>findAll(){
		SQLiteDatabase db = helper.getReadableDatabase();
		List<Device> devices = new ArrayList<Device>();
		Cursor cursor = db.rawQuery("select * from list", null);
		while(cursor.moveToNext()){
			int id =  cursor.getInt(cursor.getColumnIndex("id"));
			String name = cursor.getString(cursor.getColumnIndex("name"));
			String address = cursor.getString(cursor.getColumnIndex("address"));
			int pair = cursor.getInt(cursor.getColumnIndex("pair"));
			int mA2dp = cursor.getInt(cursor.getColumnIndex("mA2dp"));
			int mHeadset = cursor.getInt(cursor.getColumnIndex("mHeadset"));
			Device d = new Device(id,name,address,pair,mA2dp,mHeadset);
			devices.add(d);
		}
		cursor.close();
		db.close();
		return devices;
	}
	//删除所有的记录
	public void deleteAll(){
		SQLiteDatabase db = helper.getWritableDatabase();
		db.execSQL("delete from list");
		db.execSQL("update sqlite_sequence SET seq = 0 where name = 'list'");
		db.close();
	}
}
