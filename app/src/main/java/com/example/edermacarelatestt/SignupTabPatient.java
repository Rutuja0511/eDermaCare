package com.example.edermacarelatestt;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

public class SignupTabPatient extends Fragment {

    TextView signupredirectLandingPageP;
    Button login_button_patient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_btn_patient, container, false);

        signupredirectLandingPageP = view.findViewById(R.id.signupredirectLandingPageP);
        login_button_patient = view.findViewById(R.id.login_button_patient);

        signupredirectLandingPageP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SignUpPatientActivity.class);
                startActivity(intent);
            }
        });

        login_button_patient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), LoginPatientActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }
}


