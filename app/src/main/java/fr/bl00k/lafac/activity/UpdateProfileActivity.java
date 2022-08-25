package fr.bl00k.lafac.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import fr.bl00k.lafac.R;

public class UpdateProfileActivity extends AppCompatActivity {

    private EditText editName;
    private String stringName;
    private FirebaseAuth fAuth;
    private FirebaseUser fUser;
    private Button editProfilName , goToUpload;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        getSupportActionBar().setTitle("Changer de nom");
        editName=findViewById(R.id.editProfileName);

        fAuth = FirebaseAuth.getInstance();
        fUser = fAuth.getCurrentUser();

        //Show profile data
        showProfile(fUser);

        //BoutonUploadPic
        goToUpload = findViewById(R.id.uploadPicButton);
        goToUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),UpdateProfilePic.class));
                finish();
            }
        });

        //BoutonEditProfileName
        editProfilName=findViewById(R.id.editProfileNameButton);
        editProfilName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile(fUser);
            }
        });
    }

    private void updateProfile(FirebaseUser firebaseUser) {
        if(TextUtils.isEmpty(stringName))
        {
            Toast.makeText(getApplicationContext(), "Veuillez entrer votre nom", Toast.LENGTH_SHORT).show();
            editName.setError("Votre nom est requis");
            editName.requestFocus();
        }
        else
        {
            stringName = editName.getText().toString();

            ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails();

            //Entrer les données saisis par l'user
            DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Utilisateur enregistré");

            String userID = firebaseUser.getUid();

            referenceProfile.child(userID).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful())
                    {
                        //display name
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(stringName).build();
                        firebaseUser.updateProfile(profileUpdates);

                        Toast.makeText(UpdateProfileActivity.this, "Mise à jour effectué avec succès", Toast.LENGTH_SHORT).show();

                        //Empêcher l'user de retourner sur cette page
                        Intent intent = new Intent(getApplicationContext(),profileActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                    else
                    {
                        try {
                            throw task.getException();
                        }catch (Exception e)
                        {
                            Toast.makeText(UpdateProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }

    //fetch data from firebase and display
    private void showProfile(FirebaseUser fUser)
    {
        String userIDofRegistered = fUser.getUid();

        //Extracting User reference from database for "Registered Users"
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Utilisateur enregistré");

        referenceProfile.child(userIDofRegistered).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                /*ReadWriteUserDetails readUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                if (readUserDetails != null)
                {
                    stringName = fUser.getDisplayName();
                    editName.setText(stringName);
                }
                else
                {
                    Toast.makeText(UpdateProfileActivity.this, "Une erreur s'est produite", Toast.LENGTH_SHORT).show();
                }*/
                stringName = fUser.getDisplayName();
                editName.setText(stringName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpdateProfileActivity.this, "Une erreur s'est produite", Toast.LENGTH_SHORT).show();
            }
        });
    }


}