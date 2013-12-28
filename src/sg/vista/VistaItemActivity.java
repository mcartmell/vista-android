package sg.vista;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import sg.vista.Vista.VistaResponse;

import com.loopj.android.http.RequestParams;

import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class VistaItemActivity extends VistaActivity {

	boolean resuming_from_activity = false;
	String mCurrentPhotoPath;
	VistaItem mCurrentVista;
	ImageView iv;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vista_item_detail);
		iv = (ImageView) findViewById(R.id.photo_preview); 
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
		findViewById(R.id.upload_photo_bar).setVisibility(View.INVISIBLE);
	}
	
	public void displayVistaDetails(String vista_id) {
		final View rootView = getWindow().getDecorView().getRootView();
		Vista.get(c(), "/vistas/" + vista_id, new RequestParams(), new VistaResponse() {

			@Override
			public void onResponse(JSONObject json) throws JSONException {
				VistaItem vi = VistaItem.fromJSON(json);
				mCurrentVista = vi;
				TextView tName = (TextView) rootView.findViewById(R.id.name);
				tName.setText(vi.name);
			}
		});
	}
	
    public void takePhoto(View v){
        Log.i("EOH","hi...");
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
        Vista.post(c(), "/vistas/" + mCurrentVista.vista_id + "/photo", rp, new Vista.VistaResponse() {
			
			@Override
			public void onResponse(JSONObject json) throws JSONException {
				// Uploaded!
				Log.i("", "GOT SOMETHING");
			}
		});
    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == RESULT_OK)
        {
        	Log.i("","GOT PHOTO!" + mCurrentPhotoPath);
        	File file = new File(mCurrentPhotoPath);
        	iv.setImageURI(Uri.fromFile(file));
            View ll = findViewById(R.id.upload_photo_bar);
    		findViewById(R.id.upload_photo_bar).setVisibility(View.VISIBLE);
            resuming_from_activity = true;
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

}
