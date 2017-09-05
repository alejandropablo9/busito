package mashup.tecemer.com.busito.login;

import com.google.android.gms.auth.api.signin.GoogleSignInResult;

import mashup.tecemer.com.busito.BasePresenter;
import mashup.tecemer.com.busito.BaseView;

/**
 * Interacción MVP en Login
 */
public interface LoginContract {

    interface View extends BaseView<Presenter> {
        void showProgress(boolean show);

        void setEmailError(String error);

        void setPasswordError(String error);

        void showLoginError(String msg);

        void showViewPrincipal();

        void showGooglePlayServicesDialog(int errorCode);

        void showGooglePlayServicesError();

        void showNetworkError();
    }

    interface Presenter extends BasePresenter{
        void attemptLogin(String email, String password);
        void attemptLoginGoogle(GoogleSignInResult result);
    }
}
