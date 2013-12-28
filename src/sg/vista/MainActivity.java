package sg.vista;

import java.util.ArrayList;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.loopj.android.http.RequestParams;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
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

public class MainActivity extends FragmentActivity implements ActionBar.TabListener,GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener {

	LocationClient mLocationClient;
	
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
        mLocationClient = new LocationClient(this, this, this);

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
        mLocationClient.connect();
        
    }
    
    /*
     * Called when the Activity is no longer visible.
     */
    @Override
    protected void onStop() {
        // Disconnecting the client invalidates it.
        mLocationClient.disconnect();
        super.onStop();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
        if (tab.getPosition() == 0) {
        	try {
				getLocation();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
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
        	switch (position) {
        		case 0:
        			frag = new ShowAreaFragment();
        		return frag;
        	}
        	
        	if (frag == null) {
        		frag = new DummySectionFragment();
        	}
        	frag.setArguments(args);
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
                case 0:
                    return "Current area";
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }
    
    public void test(View view) throws JSONException {
    	getLocation();
    }
    public void getLocation() throws JSONException {
    	if (!mLocationClient.isConnected()) {
    		return;
    	}
    	RequestParams rp = latLongParams();
    	Log.d("Geo", "trying to get location...");
    	Vista.get(getApplicationContext(), "/geo/whereami", rp, new Vista.VistaResponse() {
    		public void onResponse(JSONObject j) throws JSONException {
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
    
    public RequestParams latLongParams() {
    	RequestParams rp = new RequestParams();
    	Location loc = mLocationClient.getLastLocation();
    	String lat = Double.toString(loc.getLatitude());
    	String lon = Double.toString(loc.getLongitude());
    	rp.put("lat", lat);
    	rp.put("lon", lon);    	
    	return rp;
    }
    
    public void getNearbyVistas() throws JSONException {
    	RequestParams rp = latLongParams();
    	Vista.get(getApplicationContext(), "/geo/vistas", rp, new Vista.VistaResponse() {
    	Context ctx = getApplicationContext();	
			@Override
			public void onResponse(JSONObject json) throws JSONException {
	            ArrayList<VistaItem> vistas = new ArrayList<VistaItem>();
				JSONArray vistas_j = json.getJSONArray("vistas");
	            for (int i = 0; i < vistas_j.length(); i++) {
	                JSONObject jo = vistas_j.getJSONObject(i);
	                VistaItem vista = VistaItem.fromJSON(jo);
	                vistas.add(vista);
	            }
	            ListView lv = (ListView) findViewById(R.id.area_vistas_list);
	            final VistaAdapter adapter = new VistaAdapter(vistas, ctx);
	            lv.setAdapter(adapter);
	            lv.setClickable(true);
	            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int position, long arg3) {
						VistaItem vi = (VistaItem) adapter.getItem(position);
			            Intent i = new Intent(getApplicationContext(), VistaItemActivity.class);
			            Bundle b = new Bundle();
			            b.putString("vista_id", vi.vista_id);
			            i.putExtras(b);
			            startActivity(i);
					}
				});
			}
    	});
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
            TextView dummyTextView = (TextView) rootView.findViewById(R.id.section_label);
            dummyTextView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }
    
    public static class ShowAreaFragment extends Fragment {
    	public ShowAreaFragment() {
    	}
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_show_area, container, false);
            // rootView.setTag(getArguments().getInt("section_number"));
            return rootView;
        }
        
        @Override
        public void onHiddenChanged(boolean hidden) {
        	Log.i("Test", "RESUMED!!!");
        }
        
        
    }
    
	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnected(Bundle arg0) {
		try {
			getLocation();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}

}
