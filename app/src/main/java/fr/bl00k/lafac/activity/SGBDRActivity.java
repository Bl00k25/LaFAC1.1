package fr.bl00k.lafac.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import fr.bl00k.lafac.R;

public class SGBDRActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sgbdr_layout);
    }

    public void setfosikasgbd(View view) {
        startActivity(new Intent(getApplicationContext(),SGBDR.class));

    }
}