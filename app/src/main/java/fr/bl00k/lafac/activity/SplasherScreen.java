package fr.bl00k.lafac.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import fr.bl00k.lafac.R;

public class SplasherScreen extends AppCompatActivity {
    ImageView imageView;
    Animation animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        imageView = findViewById(R.id.logo);
        animation = AnimationUtils.loadAnimation(this, R.anim.animation);
        imageView.setAnimation(animation);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplasherScreen.this, SignupActivity.class);
                startActivity(intent);
                finish();
            }
        },3000);
    }
}