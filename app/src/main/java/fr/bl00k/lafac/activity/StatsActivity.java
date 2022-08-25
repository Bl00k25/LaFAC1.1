package fr.bl00k.lafac.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import fr.bl00k.lafac.R;

public class StatsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statistiques_layout);
    }

    public void setfosikastat(View view) {
        startActivity(new Intent(getApplicationContext(),Statistiques.class));
    }
}