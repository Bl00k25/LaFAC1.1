package fr.bl00k.lafac.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import fr.bl00k.lafac.R;

public class homePage extends AppCompatActivity {

    private static TextView HPname , HPemail;
    private String Sname,Semail;
    private ImageView profileView;
    private FirebaseAuth fAuth;
    private FirebaseUser fUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        HPname= findViewById(R.id.textViewName);
        HPemail= findViewById(R.id.textViewEmail);
        fAuth =FirebaseAuth.getInstance();
        fUser = fAuth.getCurrentUser();
        showUserProfile(fUser);



        profileView=findViewById(R.id.pic_profil);

        profileView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), profileActivity.class));
            }
        });
    }

    public void goInMessenger(View view) {
        startActivity(new Intent(getApplicationContext(), messenger.class));
    }

    public void goToLibrary(View view) {
        startActivity(new Intent(getApplicationContext(), libraryActivity.class));
    }

    public void goInClass(View view) {
        startActivity(new Intent(getApplicationContext(), classActivity.class));
    }

    public void goToCafeteria(View view) {
        startActivity(new Intent(getApplicationContext(),cafeteriaActivity.class));
    }

    private void showUserProfile(FirebaseUser firebaseUser) {
        String userID = firebaseUser.getUid();

        //Traire les données de la base de données
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Utilisateur enregistré");
        referenceProfile.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                if (readUserDetails !=null)
                {
                    Sname=firebaseUser.getDisplayName();
                    Semail=firebaseUser.getEmail();

                    HPemail.setText(Semail);
                    HPname.setText(Sname);

                    //setuser DP
                    Uri uri =firebaseUser.getPhotoUrl();

                    //ImageView setImageuRI() tsy azo ampiarahana amin"ny uri tsotra
                    Picasso.with(getApplicationContext()).load(uri).into(profileView);
                }
                else
                {
                    Toast.makeText(homePage.this, "Une erreur s'est produite", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(homePage.this, "Une erreur s'est produite!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
