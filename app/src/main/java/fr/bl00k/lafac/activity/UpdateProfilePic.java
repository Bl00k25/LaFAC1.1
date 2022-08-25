package fr.bl00k.lafac.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import fr.bl00k.lafac.R;

public class UpdateProfilePic extends AppCompatActivity {

    private Button choosePic;
    private Button uploadPic;
    private ProgressBar progressBar;
    private ImageView picImageView;
    private FirebaseAuth fAuth;
    private StorageReference storageReference;
    private FirebaseUser fUser;
    private Uri uriImage;
    private static final int PICK_IMAGE_REQUEST=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_profile_pic);

        choosePic=findViewById(R.id.upload_pic_choose_button);
        uploadPic=findViewById(R.id.upload_pic_button);
        progressBar=findViewById(R.id.progressBar);
        picImageView=findViewById(R.id.imageView_profile_dp);

        getSupportActionBar().setTitle("Importer profil");
        fAuth=FirebaseAuth.getInstance();
        fUser=fAuth.getCurrentUser();

        storageReference = FirebaseStorage.getInstance().getReference("Display pics");

        Uri uri = fUser.getPhotoUrl();

        //Mettre l'image dans ImageVIew si ficiher deja uploadé
        Picasso.with(UpdateProfilePic.this).load(uri).into(picImageView);

        choosePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChoser();
            }
        });

        uploadPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                UploadPic();
            }
        });

    }

    private void UploadPic()
    {
        if (uriImage != null)
        {
            //Enregistrer l'image avec l'UID de l'utilisateur
            StorageReference fileReference= storageReference.child(fAuth.getCurrentUser().getUid() + "." + getFileExtension(uriImage));

            //Upload image to storage
            fileReference.putFile(uriImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Uri downloadUri = uri;
                            fUser = fAuth.getCurrentUser();

                            //Finally set the display image
                            UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                                    .setPhotoUri(downloadUri).build();
                            fUser.updateProfile(profileUpdate);
                        }
                    });

                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(UpdateProfilePic.this, "Upload réussi", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(getApplicationContext() ,profileActivity.class);
                    startActivity(intent);
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(UpdateProfilePic.this, e.getMessage() , Toast.LENGTH_SHORT).show();
                }
            });
        }
        else
        {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(UpdateProfilePic.this, "Aucun fichier séléctionné", Toast.LENGTH_SHORT).show();
        }
    }

    //Obtention de l'extension de l'image
    private String getFileExtension(Uri uri)
    {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void openFileChoser()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.common_menu , menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null )
        {
            uriImage = data.getData();
            picImageView.setImageURI(uriImage);
        }
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