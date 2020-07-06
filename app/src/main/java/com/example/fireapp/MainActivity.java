package com.example.fireapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.fireapp.account.LoginActivity;
import com.example.fireapp.account.SetupActivity;
import com.example.fireapp.broadcast.BroadcastFragment;
import com.example.fireapp.item.NewItemActivity;
import com.example.fireapp.item.editPopup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements editPopup.EditPopupListener {

    private android.support.v7.widget.Toolbar mainToolbar;
    private FirebaseAuth mAuth;

    private BottomNavigationView mainBottomNav;

    private FloatingActionButton addItemBtn;

    private HomeFragment homeFragment;
    private BroadcastFragment broadcastFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        mainToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle("Fridge Diary");

        if (mAuth.getCurrentUser() != null) {
            mainBottomNav = findViewById(R.id.bottom_nav_btn);

            //FRAGMENTS
            homeFragment = new HomeFragment();
            broadcastFragment = new BroadcastFragment();

            replaceFragment(homeFragment);

            mainBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                    switch (menuItem.getItemId()) {

                        case R.id.bottom_home:
                            replaceFragment(homeFragment);
                            return true;

                        case R.id.bottom_broadcast:
                            replaceFragment(broadcastFragment);
                            return true;

                        default:
                            return false;

                    }

                }
            });

            addItemBtn = findViewById(R.id.add_item_btn);
            addItemBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent newItemIntent = new Intent(MainActivity.this, NewItemActivity.class);
                    startActivity(newItemIntent);
                    finish();

                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
           sendToLogin();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            
            case R.id.action_logout_btn:
                logOut();
                return true;

            case R.id.action_setting_btn:

                Intent settingsIntent = new Intent(MainActivity.this, SetupActivity.class);
                startActivity(settingsIntent);
                return true;

            default:
                return false;
        }
    }



    private void sendToLogin() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }

    private void logOut() {
        mAuth.signOut();
        sendToLogin();
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_layout, fragment);
        fragmentTransaction.commit();

    }

    public FragmentManager getManager() {
        return getSupportFragmentManager();
    }

    @Override
    public void edit(String amount) {
        HomeFragment.edit(amount);
    }
}
