package nianyu.View;

import nianyu.btapp.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TabMenuView extends LinearLayout{

	private TextView mTextView = null;
	private ImageView mImageView = null;
	private Context mContext = null;
	private Animation mAnim_txt;
	private Animation mAnim_img;
	private boolean intenFlag;
	
	public TabMenuView(Context context) {
		super(context);
		mContext = context;
	}
	
	public TabMenuView(Context context,AttributeSet attrs){
		 super(context, attrs);
		 
		 LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		 layoutInflater.inflate(R.layout.tab_menu, this);
		 //find id
		 mTextView = (TextView)findViewById(R.id.tabmenu_tv);
		 mImageView = (ImageView)findViewById(R.id.tabmenu_iv);
		 
		 mContext = context;
		 mAnim_txt = AnimationUtils.loadAnimation(mContext, R.anim.menu_txt_anim);
		 
		 mAnim_txt.setAnimationListener(new AnimationListener(){

			@Override
			public void onAnimationEnd(Animation arg0) {
				
			}

			@Override
			public void onAnimationRepeat(Animation arg0) {
				
			}

			@Override
			public void onAnimationStart(Animation arg0) {
				if(!intenFlag){
					mTextView.setVisibility(View.GONE);
				}
			}
			 
		 });
		 
		 mAnim_img = AnimationUtils.loadAnimation(mContext, R.id.)
		 

	}
	
	 public void setText(String title){
		 mTextView.setText(title);
	 }
	
}
