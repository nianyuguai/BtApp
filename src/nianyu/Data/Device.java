/**  
 * All rights Reserved, Designed By nianyuguai   
 * @Title:  Device.java   
 * @Package nianyu.app   
 * @Description:    TODO(描述该文件做什么)   
 * @author: nianyuguai     
 * @date:   2014-1-16 上午12:24:47   
 * @version V1.0     
 */ 
package nianyu.Data;

/** 
 * @ClassName:	Device 
 * @Description:TODO(一句话描述这个类的作用) 
 * @author:	nianyuguai
 * @date:	2014-1-16 上午12:24:47  
 */
public class Device {
	private int id;
	private String name;
	private String address;
	private int pair;
	private int mA2dp;
	private int mHeadset;
	
	
	public Device() {
	}

	/** 
	 * @Title:	Device 
	 * @Description:	TODO(这里用一句话描述这个方法的作用) 
	 * @param:	@param id
	 * @param:	@param name
	 * @param:	@param address
	 * @param:	@param pair
	 * @param:	@param mA2dp
	 * @param:	@param mHeadset
	 * @throws 
	 */
	public Device(int id, String name, String address, int pair, int mA2dp,
			int mHeadset) {
		this.id = id;
		this.name = name;
		this.address = address;
		this.pair = pair;
		this.mA2dp = mA2dp;
		this.mHeadset = mHeadset;
	}
	/**
	 * @Title:	getId <BR>
	 * @Description: please write your description <BR>
	 * @return:	int <BR>
	 */
	public int getId() {
		return id;
	}
	/**
	 * @Title:	setId <BR>
	 * @Description: please write your description <BR>
	 * @return:	int <BR>
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * @Title:	getName <BR>
	 * @Description: please write your description <BR>
	 * @return:	String <BR>
	 */
	public String getName() {
		return name;
	}
	/**
	 * @Title:	setName <BR>
	 * @Description: please write your description <BR>
	 * @return:	String <BR>
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @Title:	getAddress <BR>
	 * @Description: please write your description <BR>
	 * @return:	String <BR>
	 */
	public String getAddress() {
		return address;
	}
	/**
	 * @Title:	setAddress <BR>
	 * @Description: please write your description <BR>
	 * @return:	String <BR>
	 * @param address the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}
	/**
	 * @Title:	getPair <BR>
	 * @Description: please write your description <BR>
	 * @return:	int <BR>
	 */
	public int getPair() {
		return pair;
	}
	/**
	 * @Title:	setPair <BR>
	 * @Description: please write your description <BR>
	 * @return:	int <BR>
	 * @param pair the pair to set
	 */
	public void setPair(int pair) {
		this.pair = pair;
	}
	/**
	 * @Title:	getA2dp <BR>
	 * @Description: please write your description <BR>
	 * @return:	int <BR>
	 */
	public int getmA2dp() {
		return mA2dp;
	}
	/**
	 * @Title:	setA2dp <BR>
	 * @Description: please write your description <BR>
	 * @return:	int <BR>
	 * @param a2dp the a2dp to set
	 */
	public void setmA2dp(int ma2dp) {
		mA2dp = ma2dp;
	}
	/**
	 * @Title:	getHeadset <BR>
	 * @Description: please write your description <BR>
	 * @return:	int <BR>
	 */
	public int getmHeadset() {
		return mHeadset;
	}
	/**
	 * @Title:	setHeadset <BR>
	 * @Description: please write your description <BR>
	 * @return:	int <BR>
	 * @param headset the headset to set
	 */
	public void setmHeadset(int mheadset) {
		mHeadset = mheadset;
	}
}
