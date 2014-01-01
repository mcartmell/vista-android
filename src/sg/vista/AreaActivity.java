package sg.vista;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class AreaActivity extends FragmentActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.area_main);
        Bundle b = getIntent().getExtras();
        
    }
}
