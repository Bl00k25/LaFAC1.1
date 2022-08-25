package fr.bl00k.lafac.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import fr.bl00k.lafac.R;

public class programmationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.programmation_layout);
    }

    public void setfosikaprog(View view) {
        startActivity(new Intent(getApplicationContext(),Programmation.class));

    }
}