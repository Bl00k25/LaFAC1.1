package fr.bl00k.lafac.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import fr.bl00k.lafac.R;

public class libraryActivity extends AppCompatActivity {

    private Button Matlab;
    private Button SGBDR;
    private Button Statistiques;
    private Button Programmation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.library_activity);

        Matlab=findViewById(R.id.matlab);
        Statistiques=findViewById(R.id.statistiques);
        SGBDR = findViewById(R.id.sgbdr);
        Programmation = findViewById(R.id.reseaux);

        Matlab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MatlabActivity.class));

            }
        });

        Statistiques.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),StatsActivity.class));

            }
        });

        SGBDR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),SGBDRActivity.class));

            }
        });

        Programmation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),programmationActivity.class));
            }
        });
    }
}