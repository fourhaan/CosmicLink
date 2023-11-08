package com.example.volunteerapp.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.volunteerapp.MainActivity;
import com.example.volunteerapp.R;
import com.google.firebase.auth.FirebaseAuth;

public class OrgProfileFragment extends Fragment {

    private Button logoutButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_org_profile, container, false);

        logoutButton = view.findViewById(R.id.org_profile_log_out);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform the logout action here
                FirebaseAuth Auth = FirebaseAuth.getInstance();
                Auth.signOut();

                // Navigate to the MainActivity
                Intent intent = new Intent(requireActivity(), MainActivity.class);
                startActivity(intent);
                requireActivity().finish();
            }
        });

        return view;
    }
}