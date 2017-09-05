package mashup.tecemer.com.busito.login;

import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.WindowManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import mashup.tecemer.com.busito.R;

public class LoginActivity extends AppCompatActivity implements LoginFragment.Callback, GoogleApiClient.OnConnectionFailedListener{

    public static final int REQUEST_GOOGLE_PLAY_SERVICES = 1;
    public static final int SIGN_IN_CODE = 777;
    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setupTranslucentStatusBar();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        LoginFragment loginFragment = (LoginFragment) getSupportFragmentManager()
                .findFragmentById(R.id.login_container);
        if (loginFragment == null) {
            loginFragment = LoginFragment.newInstance(googleApiClient);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.login_container, loginFragment)
                    .commit();
        }

        LoginInteractor loginInteractor = new LoginInteractor(
                getApplicationContext(), FirebaseAuth.getInstance());
        new LoginPresenter(loginFragment, loginInteractor);
    }

    private void setupTranslucentStatusBar() {
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    @Override
    public void onInvokeGooglePlayServices(int errorCode) {
        showPlayServicesErrorDialog(errorCode);
    }

    void showPlayServicesErrorDialog(
            final int errorCode) {
        Dialog dialog = GoogleApiAvailability.getInstance()
                .getErrorDialog(
                        LoginActivity.this,
                        errorCode,
                        REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void singOut(){
        FirebaseAuth.getInstance().signOut();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
