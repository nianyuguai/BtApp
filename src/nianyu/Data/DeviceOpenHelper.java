/**  
 * All rights Reserved, Designed By nianyuguai   
 * @Title:  DeviceOpenHelper.java   
 * @Package nianyu.app   
 * @Description:    TODO(描述该文件做什么)   
 * @author: nianyuguai     
 * @date:   2014-1-15 下午11:08:07   
 * @version V1.0     
 */ 
package nianyu.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/** 
 * @ClassName:	DeviceOpenHelper 
 * @Description:TODO(一句话描述这个类的作用) 
 * @author:	nianyuguai
 * @date:	2014-1-15 下午11:08:07  
 */
public class DeviceOpenHelper extends SQLiteOpenHelper{
	
	public DeviceOpenHelper(Context context){
		super(context,"device.db",null,1);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// 初始化数据库
		db.execSQL("create table list(id integer primary key autoincrement, name varchar(20),address varchar(20),pair int,mA2dp int,mHeadset int)");
		db.execSQL("create table search(id integer primary key autoincrement, name varchar(20),address varchar(20),pair int,mA2dp int,mHeadset int)");
	}

	/** 
	 * Title: onUpgrade
	 * Description: 
	 * @param arg0
	 * @param arg1
	 * @param arg2 
	 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int) 
	 */
	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

}
