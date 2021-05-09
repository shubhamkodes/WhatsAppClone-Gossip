package com.itsthetom.gossip.signin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.itsthetom.gossip.MainActivity;
import com.itsthetom.gossip.databinding.ActivitySignInBinding;

public class SignInActivity extends AppCompatActivity {
    ActivitySignInBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivitySignInBinding.inflate(getLayoutInflater());


        binding.btnSubmitNo.setOnClickListener((view)->{
            String phoneNo=binding.edNumber.getText().toString();
            if(phoneNo.length()<10){
                binding.edNumber.setError("Required 10 digits phone number");
            }else{
                Intent i=new Intent(this,OtpVerifyActivity.class);
                i.putExtra("number","+91"+phoneNo);
                startActivity(i);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            Intent i=new Intent(this, MainActivity.class);
            startActivity(i);
            finishAffinity();
        }
        else{
            setContentView(binding.getRoot());
        }

    }
}