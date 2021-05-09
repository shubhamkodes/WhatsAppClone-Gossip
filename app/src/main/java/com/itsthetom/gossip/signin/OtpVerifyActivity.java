package com.itsthetom.gossip.signin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.itsthetom.gossip.databinding.ActivityOtpVerifyBinding;

import java.util.concurrent.TimeUnit;

public class OtpVerifyActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    ActivityOtpVerifyBinding binding;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityOtpVerifyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        firebaseAuth=FirebaseAuth.getInstance();

        String phoneNumber=getIntent().getStringExtra("number");


        PhoneAuthOptions phoneAuthOptions=new PhoneAuthOptions.Builder(firebaseAuth)
                .setPhoneNumber(phoneNumber)
                .setActivity(this)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        verifyOtp(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {

                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        token=s;
                    }
                })
                .build();
        PhoneAuthProvider.verifyPhoneNumber(phoneAuthOptions);


        binding.otpView.setOtpCompletionListener((otp)->{
            PhoneAuthCredential phoneAuthCredential=PhoneAuthProvider.getCredential(token,otp);
            verifyOtp(phoneAuthCredential);
        });

    }

    public void verifyOtp(PhoneAuthCredential credential){
        try{
            firebaseAuth.signInWithCredential(credential)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                startSetUpActivity();
                                Log.d("CODEM","Starting setup activity");
                            }
                            else{
                                Toast.makeText(getApplicationContext(),"Logged In Failed",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } catch (Exception e){
            Toast.makeText(this,e.getMessage().toString(),Toast.LENGTH_SHORT).show();
        }

    }
    public void startSetUpActivity(){
        Intent i=new Intent(this,SetupProfileActivity.class);
        startActivity(i);
    }



}