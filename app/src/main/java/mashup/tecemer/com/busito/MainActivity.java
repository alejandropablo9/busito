package mashup.tecemer.com.busito;

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;


import com.estimote.sdk.EstimoteSDK;
import com.estimote.sdk.SystemRequirementsChecker;
import com.google.firebase.auth.FirebaseAuth;

import mashup.tecemer.com.busito.adaptador.SectionAdapter;
import mashup.tecemer.com.busito.login.LoginActivity;
import mashup.tecemer.com.busito.ui.BusFragment;
import mashup.tecemer.com.busito.ui.MapFragment;
import mashup.tecemer.com.busito.ui.PerfilFragment;

public class MainActivity extends AppCompatActivity {

    private ViewPager pager;
    private TabLayout tabs;
    private AppBarLayout appBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EstimoteSDK.initialize(
                getApplicationContext(),
                "alejandropablo9-gmail-com--47u",
                "f0d1f082a881c6fe221c306d27d5df66");

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        setToolbar();
        insertarTabs();
        pager = (ViewPager) findViewById(R.id.pager);
        poblarViewPager(pager);
        tabs.setupWithViewPager(pager);
    }

    @Override
    protected void onResume() {
        super.onResume();

        SystemRequirementsChecker.checkWithDefaultDialogs(this);
    }

    private void goLogInScreen() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void singOut(){
        FirebaseAuth.getInstance().signOut();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_log_out:
                showSnackBar("¡Adios!");
                singOut();
                goLogInScreen();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSnackBar(String msg) {
        Snackbar
                .make(findViewById(R.id.pager), msg, Snackbar.LENGTH_LONG)
                .show();
    }

    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void poblarViewPager(ViewPager viewPager) {
        SectionAdapter adapter = new SectionAdapter(getSupportFragmentManager());
        adapter.addFragment(new MapFragment(), getString(R.string.titulo_tab_mapa));
        //adapter.addFragment(new BusFragment(), getString(R.string.titulo_tab_bus));
        adapter.addFragment(new PerfilFragment(), getString(R.string.titulo_tab_perfil));
        viewPager.setAdapter(adapter);
    }

    private void insertarTabs() {
        tabs = (TabLayout) findViewById(R.id.tabs);
        appBar = (AppBarLayout) findViewById(R.id.appBar);
        tabs.setTabTextColors(Color.parseColor("#FFFFFF"), Color.parseColor("#FFFFFF"));
        appBar.removeView(tabs);
        appBar.addView(tabs);
    }


}
