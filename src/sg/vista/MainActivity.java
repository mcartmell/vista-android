package sg.vista;

import java.util.ArrayList;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.loopj.android.http.RequestParams;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends VistaFragmentActivity implements ActionBar.TabListener,GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener,LocationListener {

	public static final int TWITTER_INTENT = 0;
	public static final int SETTINGS_INTENT = 1;
	
	public static final int PAGE_CURRENTAREA = 0;
	public static final int PAGE_PROFILE = 1;
	public static final int PAGE_EXPLORE = 2;
	JSONObject mLocation;
	Fragment[] mFragments = new Fragment[3];
	
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
     * will keep every loaded fragment in memory. If this becomes too memory
     * intensive, it may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the app.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // Create location client
        Vista.mLocationClient = new LocationClient(this, this, this);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
        	
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
    }
    
    /*
     * Called when the Activity becomes visible.
     */
    @Override
    protected void onStart() {
        super.onStart();
        // Connect the client.
        Vista.mLocationClient.connect();
        setTwitterToken();
        
    }
    
    /*
     * Called when the Activity is no longer visible.
     */
    @Override
    protected void onStop() {
        // Disconnecting the client invalidates it.
        Vista.stopLocationUpdates((LocationListener) this);
        Vista.mLocationClient.disconnect();

        super.onStop();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
       
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
 
        case R.id.action_settings:
            Intent i = new Intent(this, SettingsActivity.class);
            startActivityForResult(i, SETTINGS_INTENT);
            break;
 
        }
 
        return true;
    }    
    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
        switch(tab.getPosition()) {
        case PAGE_CURRENTAREA:
        	requestLocation();
        	break;
        case PAGE_PROFILE:
        	if (mFragments[PAGE_PROFILE] != null) {
        	  ((ProfileFragment) mFragments[PAGE_PROFILE]).refresh();
        	}
        	break;
        case PAGE_EXPLORE:
        	if (mFragments[PAGE_EXPLORE] != null) {
          	  ((ExploreFragment) mFragments[PAGE_EXPLORE]).refresh();
          	}
        	break;
        default: ;
        }
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    	int i = tab.getPosition();
    	((ProgressFragment) mFragments[i]).setContentShown(false);
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a DummySectionFragment (defined as a static inner class
            // below) with the page number as its lone argument.
            Bundle args = new Bundle();
            args.putInt("section_number", position + 1);
            Fragment frag = null;
            Log.i("", "CREATING FRAGMENT " + Integer.toString(position));
        	switch (position) {
        		case 0:
        			frag = new ShowAreaFragment();
        			break;
        		case 1:
        			frag = new ProfileFragment();
        			break;
        		case 2:
        			frag = new ExploreFragment();
        			break;
        		default:
        			frag = new DummySectionFragment();
        			break;
        	}
        	frag.setArguments(args);
        	mFragments[position] = frag;
            return frag;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case PAGE_CURRENTAREA:
                    return "Current area";
                case PAGE_PROFILE:
                    return "My profile";
                case PAGE_EXPLORE:
                    return "Explore";
            }
            return null;
        }
    }
	
    // Delayed location update
	public void requestLocation() {
		// show loader until location becomes available
		if (Vista.requestLocationUpdates((LocationListener) this)) {
			if (mFragments[PAGE_CURRENTAREA] != null) {
				ProgressFragment frag = (ProgressFragment) mFragments[PAGE_CURRENTAREA];
				if (frag.getContentView() != null) {
					frag.setContentShown(false);
				}
			}
		}
	}
	
	// Get location using last known location
    public void getLocation() throws JSONException {
    	if (mViewPager.getCurrentItem() != PAGE_CURRENTAREA) return;
    	if (!Vista.mLocationClient.isConnected()) {
    		return;
    	}
    	RequestParams rp = Vista.latLongParams();
    	Log.d("Geo", "trying to get location...");
    	Vista.get(getApplicationContext(), "/geo/whereami", rp, new Vista.VistaResponse() {
    		public void onResponse(JSONObject j) throws JSONException {
    			mLocation = j;
    			TextView cur_area = (TextView) findViewById(R.id.current_area);
    			if (cur_area != null) {
    				cur_area.setText(j.getJSONObject("area").getString("name"));
    			}
    			TextView cur_sz = (TextView) findViewById(R.id.current_subzone);
    			if (cur_sz != null) {
    				cur_sz.setText(j.getJSONObject("subzone").getString("name"));
    			}
    			getNearbyVistas();
    		}
    	});
    }
    
    public void getNearbyVistas() throws JSONException {
    	String area_name = mLocation.getJSONObject("area").getString("name");
    	((ShowAreaFragment) mFragments[0]).getVistas(area_name);
    }

    /**
     * A dummy fragment representing a section of the app, but that simply
     * displays dummy text.
     */
    public static class DummySectionFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        public static final String ARG_SECTION_NUMBER = "section_number";

        public DummySectionFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main_dummy, container, false);
            return rootView;
        }
    }
    
	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if (Vista.mLocationClient.isConnected()) {
			try {
				getLocation();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onConnected(Bundle arg0) {
		Log.i("","requesting GPS location");
		requestLocation();

	}


	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub		
	}
   
    public void connectTwitter(View v) {
        Intent intent = new Intent(this, TwitterAuth.class);
        startActivityForResult(intent, TWITTER_INTENT);
    }

    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TWITTER_INTENT && resultCode == RESULT_OK) {
        	saveTwitterToken(data);
        	((ProfileFragment) mFragments[1]).refresh();
        }
        if (requestCode == SETTINGS_INTENT) {

        }
    }

	@Override
	// When we get a location update, show the location and stop gps updates
	public void onLocationChanged(Location location) {
		Log.i("", "Got GPS location");
		try {
			getLocation();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Vista.stopLocationUpdates((LocationListener) this);
		// TODO Auto-generated method stub
		
	}
}
