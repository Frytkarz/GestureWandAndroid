package pl.chipsoft.gesturewand.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import pl.chipsoft.gesturewand.R;
import pl.chipsoft.gesturewand.application.MyApp;
import pl.chipsoft.gesturewand.fragments.DrawerFragment;
import pl.chipsoft.gesturewand.fragments.GesturesFragment;
import pl.chipsoft.gesturewand.fragments.SettingsFragment;
import pl.chipsoft.gesturewand.fragments.SummaryFragment;

public class MainActivity extends AppCompatActivity
        implements DrawerFragment.OnFragmentInteractionListener{
    //UI
    private DrawerLayout drawer;
    private FloatingActionButton fab;
    private Toolbar toolbar;
    private NavigationView navigationView;

    //pola
    private Handler handler;
    private DrawerFragment fragment;

    //listenery
    private NavigationView.OnNavigationItemSelectedListener onMenuListener
            = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            fragment = getFragment(item);

//            item.setChecked(!item.isChecked());
//            item.setChecked(true);
            loadFragment(fragment);
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }
    };

    public View.OnClickListener onBtnActionClick = view -> {
        Intent intent = new Intent(MainActivity.this, NewGestureActivity.class);
        getSupportActionBar().setTitle(NewGestureActivity.TITLE);
        startActivity(intent);
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                    .setAction("Action", null).show();
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.amToolBar);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);


        handler = new Handler();

        setSupportActionBar(toolbar);
        fab.setOnClickListener(onBtnActionClick);
        navigationView.setNavigationItemSelectedListener(onMenuListener);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApp.getInstance().onResume();

        if(fragment == null)
            fragment = new SummaryFragment();
        loadFragment(fragment);
    }

    @Override
    public void onBackPressed() {
        //GestureManager.getInstance().learn();
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }

        if(fragment.getIndex() != DrawerFragment.HOME_INDEX){
            fragment = new SummaryFragment();
            loadFragment(fragment);
            return;
        }

        super.onBackPressed();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(fragment.onKeyDown(keyCode, event))
            return true;
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(fragment.onKeyUp(keyCode, event))
            return true;
        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onPause() {
        MyApp.getInstance().onPause();
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.amOptSettings) {
            fragment = new SettingsFragment();
            loadFragment(fragment);
            return true;
        }else if(id == R.id.amOptExit){
            Process.killProcess(Process.myPid());
            System.exit(1);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        //TODO
    }

    private DrawerFragment getFragment(MenuItem item){
        int id = item.getItemId();

        if (id == R.id.mItGestures)
            return new GesturesFragment();
        if (id == R.id.mItSettings)
           return new SettingsFragment();

        return new SummaryFragment();
    }

    private void loadFragment(final DrawerFragment fragment){
        navigationView.getMenu().getItem(fragment.getIndex()).setChecked(true);
        getSupportActionBar().setTitle(fragment.getTitle(MainActivity.this));

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                FragmentTransaction fragmentTransaction =
                        getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.amFragmentFrame,
                        fragment, fragment.getTag());
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        handler.post(runnable);
    }
}
