package fr.bl00k.lafac.activity;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import fr.bl00k.lafac.R;

public class SignupActivity extends AppCompatActivity {

    public  final String TAG = "TAG";
    private EditText mName;
    private EditText mEmail;
    private EditText mPassword;

    private TextView SignUp;
    private float x1,x2,y1,y2;
    FirebaseFirestore fstore;
    String userID;
    FirebaseAuth fAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_activity);

        mName=findViewById(R.id.name);
        mEmail=findViewById(R.id.Email);
        mPassword=findViewById(R.id.Password);
        SignUp=findViewById(R.id.btn_signup);
        fAuth=FirebaseAuth.getInstance();
        fstore=FirebaseFirestore.getInstance();

        if (fAuth.getCurrentUser()!=null)
        {
            startActivity(new Intent(getApplicationContext(),homePage.class));
            finish();
        }

        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 String email = mEmail.getText().toString().trim();
                 String password = mPassword.getText().toString().trim();
                 String name = mName.getText().toString().trim();

                if (TextUtils.isEmpty(email))
                {
                    mEmail.setError("L'email est recquis");
                    return;
                }
                if (TextUtils.isEmpty(password))
                {
                    mPassword.setError("Le mot de passe est recquis");
                    return;
                }
                if (password.length()<4)
                {
                    mPassword.setError("Le mot de passe est trop court");
                    return;
                }

                fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            FirebaseUser fUser= fAuth.getCurrentUser();

                            //Mettre à jour le nom de l'utilisateur
                            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
                            fUser.updateProfile(profileChangeRequest);

                            //Entrer l'utilisateur dans la base de données
                            ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(email);

                            DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Utilisateur enregistré");
                            referenceProfile.child(fUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    fUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(getApplicationContext(), "Email de confirmation envoyé", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d(TAG,"OnFailure : Email non envoyé"+e.getMessage());
                                        }
                                    });

                                    Toast.makeText(SignupActivity.this, "Veuiller vérifier votre email", Toast.LENGTH_SHORT).show();
                                }
                            });



                            Toast.makeText(getApplicationContext(), "Utilisateur créé", Toast.LENGTH_SHORT).show();
                            userID= fAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = fstore.collection("user").document(userID);
                            Map<String,Object> user = new HashMap<>();
                            user.put("fName",name);
                            user.put("email",email);
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d(TAG,"OnSuccess: Utilisateur créé pour "+userID);
                                }

                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG,"OnFailure: "+e.toString());
                                }
                            });

                            startActivity(new Intent(getApplicationContext(),homePage.class));
                        }
                        else
                        {
                            Toast.makeText(SignupActivity.this, "Erreur"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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
                if(x1>x2){
                    Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(i);
                }
            break;
        }
        return false;
    }

}
