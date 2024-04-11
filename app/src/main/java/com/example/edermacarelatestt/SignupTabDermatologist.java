package com.example.edermacarelatestt;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class SignupTabDermatologist extends Fragment {

    TextView signupredirectLandingPageD;
    Button login_button_dermatologist;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view= inflater.inflate(R.layout.login_btn_dermatologist, container, false);

        signupredirectLandingPageD = view.findViewById(R.id.signupredirectLandingPageD);
        login_button_dermatologist=view.findViewById(R.id.login_button_dermatologist);

        signupredirectLandingPageD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SignUpDermatologistActivity1.class);
                startActivity(intent);
            }
        });

        login_button_dermatologist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), LoginDermatologistActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }
}
