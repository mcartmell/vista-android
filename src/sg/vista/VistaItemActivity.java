package sg.vista;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import sg.vista.Vista.VistaResponse;
import sg.vista.Twitter;

import com.loopj.android.http.RequestParams;

import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class VistaItemActivity extends VistaActivity {

	boolean resuming_from_activity = false;
	String mCurrentPhotoPath;
	VistaItem mCurrentVista;
	LinearLayout mPhotoBar;
	LinearLayout mUserPhotos;
	FrameLayout mUserPhotoFrame;
	
	ImageView mMainPhoto;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vista_item_detail);
        mUserPhotos = (LinearLayout) findViewById(R.id.user_images_ll);
        mUserPhotoFrame = (FrameLayout) findViewById(R.id.frame_user_photos);
        mMainPhoto = (ImageView) findViewById(R.id.vista_main_photo);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.vista_item, menu);
		return true;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if (resuming_from_activity) {
			resuming_from_activity = false;
			return;
		}
		mCurrentPhotoPath = null;
		mCurrentVista = null;
		Intent i = getIntent();
		Bundle b = i.getExtras();
		String vista_id = b.getString("vista_id");
		
		displayVistaDetails(vista_id);
		Log.i("","starting");
		loadUserPhotos(vista_id);
	}
	
	public void loadUserPhotos(String vista_id) {
		final Context ctx = this;
		Vista.get(c(), "/vistas/" + vista_id + "/vista_photos/list_user", new RequestParams(), new VistaResponse() {
			
			@Override
			public void onResponse(JSONObject json) throws JSONException {
				// Clear images
				mUserPhotos.removeAllViews();
				LinearLayout.LayoutParams llp=new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
				llp.setMargins(10,0,0,0);
				JSONArray a = json.getJSONArray("photos");
				// For each photo
				mUserPhotoFrame.setVisibility(View.GONE);
				for (int i = 0; i < a.length(); i++) {
					JSONObject photo = (JSONObject) a.get(i);
					String thumbUrl = photo.getString("thumb");
					// Create image view
					int myImage = getResources().getIdentifier("user_photo_" + Integer.toString(i), "drawable", getPackageName());
					ImageView newIV = (ImageView) getLayoutInflater().inflate(R.layout.horizontal_thumbnail_image, null);
					newIV.setId(myImage);

					mUserPhotos.addView(newIV, llp);
					
					// Start download task
					DownloadImageTask.dl(thumbUrl, newIV);
				}
				if (a.length() > 0) {
					mUserPhotoFrame.setVisibility(View.VISIBLE);
				}
			}			
		});
	}
	
	public void displayVistaDetails(String vista_id) {
		final View rootView = getWindow().getDecorView().getRootView();
		RequestParams rp = Vista.latLongParams();
		Vista.get(c(), "/vistas/" + vista_id, rp, new VistaResponse() {

			@Override
			public void onResponse(JSONObject json) throws JSONException {
				VistaItem vi = VistaItem.fromJSON(json);
				mCurrentVista = vi;
				TextView tName = (TextView) rootView.findViewById(R.id.name);
				tName.setText(vi.name);
				TextView tDesc = (TextView) rootView.findViewById(R.id.vista_description);
				TextView tDirecs = (TextView) rootView.findViewById(R.id.vista_directions);
				String description = vi.description;
				if (description.isEmpty()) {
					description = "There is no description for this place yet.";
				}
			    String htmlInfo = "<h3>Description</h3>" + "<p>" + description + "</p>";
			    tDesc.setText(Html.fromHtml(htmlInfo));
			    if (vi.directions.isEmpty()) {
			    	tDirecs.setVisibility(View.GONE);
			    }
			    else {
			    	tDirecs.setVisibility(View.VISIBLE);
			    	String htmlDirections = "<h3>Directions</h3><p>" + vi.directions + "</p>";
			    	tDirecs.setText(Html.fromHtml(htmlDirections));
			    }
			    setVisited(vi.visited);
			    
			    // Load image thumbnail
			    DownloadImageTask.dl(json.getString("photo_thumb"), mMainPhoto);
			    
			}
		});
	}
	
	public void setVisited(boolean visited) {
		ImageView vi = (ImageView) findViewById(R.id.visited_icon);
		if (visited) {
			vi.setImageResource(android.R.drawable.btn_star_big_on);
		}
		else {
			vi.setImageResource(android.R.drawable.btn_star_big_off);
		}
	}
	
    public void takePhoto(View v){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, 1);
            }
        }
    }
    
    public void uploadPhoto(View v) throws IOException {
    	RequestParams rp = new RequestParams();
    	ByteArrayOutputStream bos = new ByteArrayOutputStream();
    	Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
        ExifInterface oldExif = new ExifInterface(mCurrentPhotoPath);
        String exifOrientation = oldExif.getAttribute(ExifInterface.TAG_ORIENTATION);
        
        FileOutputStream fos = new FileOutputStream(mCurrentPhotoPath);
    	
    	bitmap.compress(CompressFormat.JPEG, 85, fos);
        
    	ExifInterface newExif = new ExifInterface(mCurrentPhotoPath);
    	if (exifOrientation != null) {
    	  newExif.setAttribute(ExifInterface.TAG_ORIENTATION, exifOrientation);
    	}

    	newExif.saveAttributes();
        rp.put("file", new File(mCurrentPhotoPath));
        Log.i("","uploading photo...");
        Vista.post(c(), "/vistas/" + mCurrentVista.vista_id + "/vista_photos", rp, new Vista.VistaResponse() {
			
			@Override
			public void onResponse(JSONObject json) throws JSONException {
				// Uploaded!
				Log.d("VistaItem", "Got response from photo upload");
				// Refresh photos
				loadUserPhotos(mCurrentVista.vista_id);
				// Set the visited badge
				setVisited(true);
				// Send a tweet
				sendTweet();
			}
		});
    }
    
    public void sendTweet() {
    	if (!(prefs().getBoolean("twitter_enabled", false))) return;
    	String tweet = "I'm at " + mCurrentVista + "! @sgVista";
    	Twitter.getInstance().postTweet(tweet);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        resuming_from_activity = true;

        if (resultCode == RESULT_OK)
        {
        	Log.i("","GOT PHOTO!" + mCurrentPhotoPath);
        	File file = new File(mCurrentPhotoPath);
            // upload the photo immediately
            try {
    			uploadPhoto(null);
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
        }

    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",         /* suffix */
            storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
    
    public void getDirections(View v) {
    	double from_lat = Vista.mLastLocation.getLatitude();
    	double from_lon = Vista.mLastLocation.getLongitude();
    	double to_lat = mCurrentVista.lat;
    	double to_lon = mCurrentVista.lon;
    	String maps_url = "http://maps.google.com/maps?saddr=" + Double.toString(from_lat) + "," + Double.toString(from_lon) + "&daddr=" + Double.toString(to_lat) + "," + Double.toString(to_lon);
    	Intent i = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(maps_url));
    	startActivity(i);
    }

}
