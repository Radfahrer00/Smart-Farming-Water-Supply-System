package com.example.smartfarmingwatersupply.activities;

import android.os.Bundle;

import com.example.smartfarmingwatersupply.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.smartfarmingwatersupply.databinding.ActivityMainBinding;

/**
 * MainActivity is the entry point of the application's main interface.
 * It sets up the bottom navigation bar and configures navigation for the application,
 * managing navigation to different fragments representing different sections of the app.
 * This activity uses the Navigation component from Android Jetpack for managing UI navigation
 * and also demonstrates the use of View Binding for interacting with views more safely and efficiently.
 */
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    /**
     * Called when the activity is starting. This method is where the activity's
     * layout is set up by inflating the layout with {@link ActivityMainBinding},
     * setting up the bottom navigation view with the navController for handling
     * navigation actions, and configuring the AppBar to work with NavigationUI
     * to provide visual structure and behavior for the top app bar.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     * previously being shut down then this Bundle contains the data it most
     * recently supplied in onSaveInstanceState(Bundle). Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

}