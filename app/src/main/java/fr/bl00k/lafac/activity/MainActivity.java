package fr.bl00k.lafac.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import fr.bl00k.lafac.R;
import fr.bl00k.lafac.fragment.HomeFragment;
import fr.bl00k.lafac.util.DateAndTime;
import fr.bl00k.lafac.util.PrefUtils;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference ref;
    public static String UPDATE_STATUS = "status";
    private DateAndTime dt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_main);

        dt = new DateAndTime();
        ref = FirebaseDatabase.getInstance().getReference("users/" + PrefUtils.getUserId());
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_main,
                new HomeFragment()).commit();
    }

    @Override
    protected void onPause() {
        super.onPause();
        setStatus("Last seen at " + dt.getTime());
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (getIntent().getStringExtra(UPDATE_STATUS).equals("NO_CHANGE")) {
                UPDATE_STATUS = "changed";
                Log.e("CHANGED!!", "ON RESUME");
                return;
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        setStatus("Online");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setStatus("Last seen at " + dt.getTime());
    }

    private void setStatus(String status){
        Log.e("STATUS CHANGED = ", status );
        Map<String, Object> mp = new HashMap<>();
        mp.put("isOnline", status);
        ref.updateChildren(mp);
    }

}