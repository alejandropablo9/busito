package mashup.tecemer.com.busito.login;


import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import mashup.tecemer.com.busito.MainActivity;
import mashup.tecemer.com.busito.R;

public class LoginFragment extends Fragment implements LoginContract.View {


    private LoginContract.Presenter mPresenter;
    private TextInputEditText mEmail;
    private TextInputEditText mPassword;
    private Button mSignInButton;
    private SignInButton mSignInButtonGoogle;
    private View mLoginForm;
    private View mLoginProgress;
    private TextInputLayout mEmailError;
    private TextInputLayout mPasswordError;
    private Callback mCallback;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static GoogleApiClient googleApiClient;

    public static LoginFragment newInstance(GoogleApiClient mGoogleApiClient) {
        LoginFragment fragment = new LoginFragment();
        // Setup de argumentos en caso de que los haya
        googleApiClient = mGoogleApiClient;
        return fragment;
    }

    public LoginFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            // Extracción de argumentos en caso de que los haya
        }

        // Obtener instancia FirebaseAuth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    showViewPrincipal();
                } else {
                    // El usuario no está logueado
                }
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_login, container, false);

        setupLoginBackground(container);
        setupTitle(root);

        mLoginForm = root.findViewById(R.id.login_form);
        mLoginProgress = root.findViewById(R.id.login_progress);

        mEmail = (TextInputEditText) root.findViewById(R.id.tv_email);
        mPassword = (TextInputEditText) root.findViewById(R.id.tv_password);
        mEmailError = (TextInputLayout) root.findViewById(R.id.til_email_error);
        mPasswordError = (TextInputLayout) root.findViewById(R.id.til_password_error);

        mSignInButton = (Button) root.findViewById(R.id.b_sign_in);
        mSignInButtonGoogle = (SignInButton) root.findViewById(R.id.signInButtonGoogle);
        mSignInButtonGoogle.setSize(SignInButton.SIZE_WIDE);
        mSignInButtonGoogle.setColorScheme(SignInButton.COLOR_DARK);

        // Eventos
        mEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mEmailError.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mPasswordError.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                boolean procesado = false;

                if (i == EditorInfo.IME_ACTION_UNSPECIFIED) {

                    attemptLogin();

                    // Ocultar teclado virtual
                    InputMethodManager imm =
                            (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);

                    procesado = true;
                }
                return procesado;
            }
        });
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mSignInButtonGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(intent, LoginActivity.SIGN_IN_CODE);
            }
        });
        return root;
    }

    private void setupLoginBackground(final View root) {
        Glide.with(this)
                .load(R.drawable.login_background)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(new SimpleTarget<GlideDrawable>() {
                    @Override
                    public void onResourceReady(GlideDrawable resource,
                                                GlideAnimation<? super GlideDrawable> glideAnimation) {
                        final int sdk = android.os.Build.VERSION.SDK_INT;
                        if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                            root.setBackgroundDrawable(resource);
                        } else {
                            root.setBackground(resource);
                        }
                    }
                });
    }

    private void setupTitle(View root) {
        ((TextView) root.findViewById(R.id.tv_logo))
                .setTypeface(Typeface.createFromAsset(
                        getActivity().getAssets(), "fonts/fjalla_on.otf"));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Callback) {
            mCallback = (Callback) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " debe implementar Callback");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case LoginActivity.REQUEST_GOOGLE_PLAY_SERVICES:
                attemptLogin();
                break;
            case LoginActivity.SIGN_IN_CODE:
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                attemptLoginGoogle(result);
                break;
        }
    }

    private void attemptLogin() {
        mPresenter.attemptLogin(
                mEmail.getText().toString(),
                mPassword.getText().toString());
    }

    private void attemptLoginGoogle(GoogleSignInResult result){
        mPresenter.attemptLoginGoogle(result);
    }

    @Override
    public void setPresenter(LoginContract.Presenter presenter) {
        if (presenter != null) {
            mPresenter = presenter;
        } else {
            throw new RuntimeException("El presenter no puede ser nulo");
        }
    }

    @Override
    public void showProgress(boolean show) {
        mLoginForm.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginProgress.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setEmailError(String error) {
        mEmailError.setError(error);
    }

    @Override
    public void setPasswordError(String error) {
        mPasswordError.setError(error);
    }

    @Override
    public void showLoginError(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showViewPrincipal() {
        //startActivity(new Intent(getActivity(), PushNotificationsActivity.class));
        startActivity(new Intent(getActivity(), MainActivity.class));
        getActivity().finish();
    }

    @Override
    public void showGooglePlayServicesDialog(int codeError) {
        mCallback.onInvokeGooglePlayServices(codeError);
    }

    @Override
    public void showGooglePlayServicesError() {
        Toast.makeText(getActivity(),
                "Se requiere Google Play Services para usar la app", Toast.LENGTH_LONG)
                .show();
    }

    @Override
    public void showNetworkError() {
        Toast.makeText(getActivity(),
                "La red no está disponible. Conéctese y vuelva a intentarlo", Toast.LENGTH_LONG)
                .show();
    }

    interface Callback {
        void onInvokeGooglePlayServices(int codeError);
    }

}
