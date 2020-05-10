package parimal.examples.studymania;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.Arrays;

public class SignInActivity extends AppCompatActivity {

    GoogleSignInClient mGoogleSignInClient;
    private SignInButton g_sign_in_button;
    private LoginButton fb_login_button;
    CallbackManager callbackManager;
    private static final int RC_SIGN_IN=1;
    public static final String TAG="SignInActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        //inflate the fb signin anf gmail signin buttons.
        g_sign_in_button=(SignInButton)findViewById(R.id.g_sign_in_button);
        fb_login_button=(LoginButton)findViewById(R.id.fb_login_button);


        //method call for fb and google signins
        signInWithGoogle();
        signInWithFb();
    }

    public void signInWithGoogle()
    {
        //create a new GSO with email request.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        //create a new google client for google sign in
        mGoogleSignInClient= GoogleSignIn.getClient(this,gso);

        //set on click listener on google signin button
        g_sign_in_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }

    public void signIn()
    {
        //open a new intent and retrieve the results of google sign in
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //get the google signed in user details from this method
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
        //transfer the results of fb sign in to callback manager
        else
        {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }
//

    //get the google signed in user's account details and move to the MainActivity after login
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            startActivity(new Intent(SignInActivity.this,MainActivity.class));
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(getApplicationContext(),"Authentication Failed",Toast.LENGTH_LONG).show();
        }
    }

    //method for fb signin
    public void signInWithFb()
    {
        //create a callback manager and register it to receive user account details.
        callbackManager=CallbackManager.Factory.create();
        fb_login_button.setReadPermissions(Arrays.asList("email","public_profile"));
        fb_login_button.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            //start the MainActivity after login
            public void onSuccess(LoginResult loginResult) {
                startActivity(new Intent(SignInActivity.this,MainActivity.class));
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }



    //close the application if back button is pressed
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
