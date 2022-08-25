package fr.bl00k.lafac.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import fr.bl00k.lafac.R;


public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText mEmail,mPassword;
    private TextView mConnecting;
    private float x1,y1,x2,y2;

    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        mEmail= findViewById(R.id.email);
        mPassword=findViewById(R.id.password);
        mConnecting=findViewById(R.id.btn_login);

        fAuth = FirebaseAuth.getInstance();

        mConnecting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email= mEmail.getText().toString().trim();
                String password= mPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email))
                {
                    mEmail.setError("Ce champ est requis");
                    return;
                }
                if (TextUtils.isEmpty(password))
                {
                    mPassword.setError("Ce champ est requis");
                    return;
                }
                if (password.length()<4)
                {
                    mPassword.setError("Le mot de passe est trop court");
                    return;
                }

                fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            Toast.makeText(getApplicationContext(), "Login réussi", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),homePage.class));
                        }

                        else
                        {
                            Toast.makeText(getApplicationContext(), "Erreur "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    public boolean onTouchEvent(MotionEvent touchEvent){
        switch(touchEvent.getAction()){
            case MotionEvent.ACTION_DOWN:
                x1 = touchEvent.getX();
                y1 = touchEvent.getY();
                break;
            case MotionEvent.ACTION_UP:
                x2 = touchEvent.getX();
                y2 = touchEvent.getY();
                if(x1<x2)
                {
                    Intent i = new Intent(getApplicationContext(), SignupActivity.class);
                    startActivity(i);
                }
            break;
        }
        return false;
    }

}


