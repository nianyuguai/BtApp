package nianyu.btapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;

public class Setting extends Activity{

	
		@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getFragmentManager().beginTransaction().replace(android.R.id.content,
                new PrefsFragment()).commit();
	
		getActionBar().setDisplayHomeAsUpEnabled(true);
		}

		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			// TODO Auto-generated method stub
			switch(item.getItemId()){
			case android.R.id.home:
				//Intent upIntent = new Intent(this, AppClient.class);
			    finish();
			    return true;
			}
			return super.onOptionsItemSelected(item);
		}

		

}
