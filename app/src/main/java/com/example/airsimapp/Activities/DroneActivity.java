package com.example.airsimapp.Activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentTransaction;

import com.example.airsimapp.Fragments.DronePhoneFragment;
import com.example.airsimapp.R;

import java.net.URI;

import okhttp3.OkHttpClient;
import okhttp3.WebSocket;

public class DroneActivity extends AppCompatActivity {
    private DronePhoneFragment dronePhoneFragment;
    private FragmentTransaction fragmentTransaction;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_drone);

        dronePhoneFragment = new DronePhoneFragment();
        if (savedInstanceState == null) {
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.drone_fragment_container, dronePhoneFragment);
            fragmentTransaction.commit();
//        private final OkHttpClient client;
        }

    }
}