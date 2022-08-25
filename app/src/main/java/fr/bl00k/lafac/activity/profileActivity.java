package fr.bl00k.lafac.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

public class profileActivity extends AppCompatActivity {
    private Button deleteAcc;
    private Button updateAcc;
    private Button logoutAcc;

    private static TextView textviewSlogan, textviewName,textviewEmail;
    private static String PAname , PAemail;
    private static ImageView imageView;
    private FirebaseAuth fAuth;
    private  FirebaseUser fUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);

        getSupportActionBar().setTitle("Profil");

        //Boutons de UD
        logoutAcc=findViewById(R.id.signOutButton);
        deleteAcc=findViewById(R.id.deleteButton);
        updateAcc=findViewById(R.id.updateButton);

        //Définitions des éléments de l'activité
        imageView =findViewById(R.id.pic_profil);
        textviewSlogan = findViewById(R.id.Slogan);
        textviewName = findViewById(R.id.show_name);
        textviewEmail = findViewById(R.id.show_email);
        fAuth = FirebaseAuth.getInstance();
        fUser = fAuth.getCurrentUser();

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),UpdateProfilePic.class));
            }
        });

        if (fUser == null)
        {
            Toast.makeText(getApplicationContext(), "Une erreur s'est produite , les infos d'utilisateurs ne sont pas disponibles", Toast.LENGTH_SHORT).show();
        }
        else
        {
            showUserProfile(fUser);
        }

        updateAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),UpdateProfileActivity.class));
            }
        });

       /* deleteAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),));
            }
        });*/

        logoutAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fAuth.signOut();
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            }
        });


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
                    PAname=firebaseUser.getDisplayName();
                    PAemail=firebaseUser.getEmail();

                    textviewEmail.post(new Runnable() {
                        @Override
                        public void run() {
                            textviewEmail.setText(PAemail);
                        }
                    });
                    textviewSlogan.setText(String.format("Bienvenue , %s !", PAname));
                    textviewName.setText(PAname);

                    //set user DP
                    Uri uri =fUser.getPhotoUrl();

                    //ImageView setImageuRI() tsy azo ampiarahana amin"ny uri tsotra
                    Picasso.with(getApplicationContext()).load(uri).into(imageView);
                }
                else
                {
                    Toast.makeText(profileActivity.this, "Une erreur s'est produite", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(profileActivity.this, "Une erreur s'est produite!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.common_menu , menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemID = item.getItemId();

        if (itemID == R.id.refresh)
        {
             startActivity(getIntent());
             finish();
             overridePendingTransition(0,0);
        }
        return super.onOptionsItemSelected(item);
    }
}
