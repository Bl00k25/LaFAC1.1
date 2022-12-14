package fr.bl00k.lafac.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import fr.bl00k.lafac.R;
import fr.bl00k.lafac.util.DateAndTime;
import fr.bl00k.lafac.util.PrefUtils;

public class SplashActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private static final int RC_SIGN_IN = 101;
    private GoogleSignInClient mGoogleSignInClient;
    private DatabaseReference mDatabaseReference;
    private FirebaseDatabase mFirebaseDatabase;
    private GoogleApiClient googleApiClient;
    private PrefUtils pr;
    String idToken;
    private ProgressBar progressBar;
    private ImageView app_icon_image;
    private SignInButton signInButton;
    private DateAndTime dt;


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        signInButton=findViewById(R.id.sign_in_button);
        pr = new PrefUtils(this);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        dt = new DateAndTime();

        progressBar = findViewById(R.id.splash_activity_progress_bar);
        app_icon_image = findViewById(R.id.app_icon_image);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app_icon_image.setVisibility(View.GONE);
                signInButton.setEnabled(false);
                Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(intent, RC_SIGN_IN);
            }
        });

        if (getIntent().getStringExtra("mode") != null) {
            if (getIntent().getStringExtra("mode").equals("logout")) {
                googleApiClient.connect();
                googleApiClient
                        .registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                            @Override
                            public void onConnected(@Nullable Bundle bundle) {
                                mFirebaseAuth.signOut();
                                Auth.GoogleSignInApi.signOut(googleApiClient);
                                setStatus();
                                pr.eraseDetails();
//                                startActivity(new Intent(SplashActivity.this, SplashActivity.class));
//                                finish();
                            }

                            @Override
                            public void onConnectionSuspended(int i) {

                            }
                        });
            }
        }

    }

    private void setStatus() {

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("users/" + PrefUtils.getUserId());
        Map<String, Object> mp = new HashMap<>();
        mp.put("isOnline", "Last seen as " + dt.getTime());
        ref.updateChildren(mp);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            if(resultCode == RESULT_OK) {
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                handleSignInResult(result);
            }
            else{
                signInButton.setEnabled(true);
                progressBar.setVisibility(View.GONE);
                app_icon_image.setVisibility(View.VISIBLE);
            }
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if(result.isSuccess()){
            GoogleSignInAccount account = result.getSignInAccount();
            idToken = account.getIdToken();
            String name = account.getDisplayName();
            String email = account.getEmail();
            String image = account.getPhotoUrl().toString();
            String uid = account.getId();
            AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
            firebaseAuthWithGoogle(credential, email, name, image, uid);
        }
        else{
            Toast.makeText(this, "Login Failed!", Toast.LENGTH_SHORT).show();
        }
    }

    private void firebaseAuthWithGoogle(AuthCredential credential, final String email,
                                        final String name, final String photoUrl, final String uid) {
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            pr.SaveDetails(name, uid, email, photoUrl);
                            Toast.makeText(SplashActivity.this, "Logged in!", Toast.LENGTH_SHORT).show();
                            mDatabaseReference = FirebaseDatabase.getInstance().getReference("users");
                            mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(
                                        @NotNull DataSnapshot snapshot) {
                                    if (!snapshot.hasChild(uid)) {
                                        mDatabaseReference.child(uid + "/" + "name").setValue(name);
                                        mDatabaseReference.child(uid + "/" + "image").setValue(photoUrl);
                                        mDatabaseReference.child(uid + "/" + "uid").setValue(uid);
                                        mDatabaseReference.child(uid + "/" + "email").setValue(email);
                                        mDatabaseReference.child(uid + "/" + "isOnline").setValue("Online");
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Toast.makeText(SplashActivity.this, "An error occurred!",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                            intent.putExtra(MainActivity.UPDATE_STATUS, "NO_CHANGE");
                            startActivity(intent);
                            finish();
                        }
                        else{
                            Toast.makeText(SplashActivity.this, "Authentication Failed!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.

        FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
        if(currentUser != null){
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            Log.e("CALLED", "onStart: CALLED!!!!!");
            finish();
        }
    }

}