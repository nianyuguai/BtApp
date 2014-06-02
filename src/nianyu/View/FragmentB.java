package nianyu.View;

import nianyu.btapp.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class FragmentB extends Fragment{
	private String TAG = "FragmentB";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Log.d(TAG,"FB start");
		return inflater.inflate(R.layout.frag_b_view, container,false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		Log.d(TAG,"FB Ac Create");
		//LinearLayout mLl = (LinearLayout)getView().findViewById(R.id.frag_b);
		//mLl.setBackgroundColor(Color.GRAY);
		
	}
/*	
	@SuppressLint("NewApi")
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		
		
		super.onCreateOptionsMenu(menu, inflater);

		menu.clear();
	}
*/
}