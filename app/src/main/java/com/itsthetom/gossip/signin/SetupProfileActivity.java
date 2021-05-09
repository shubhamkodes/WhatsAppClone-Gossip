package com.itsthetom.gossip.signin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthSettings;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.itsthetom.gossip.MainActivity;
import com.itsthetom.gossip.R;
import com.itsthetom.gossip.databinding.ActivitySetupProfileBinding;
import com.itsthetom.gossip.model.User;

import java.util.Objects;

public class SetupProfileActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    FirebaseStorage firebaseStorage;
    FirebaseDatabase firebaseDatabase;
    ActivitySetupProfileBinding binding;
    User user;
    public static final int IMG=98;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivitySetupProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        firebaseAuth=FirebaseAuth.getInstance();
        firebaseStorage=FirebaseStorage.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();

        user=new User();
       // user.setProfileImgUrl(String.valueOf(R.drawable.aavatar));
        user.setUid(firebaseAuth.getUid());
        user.setPhoneNumber(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getPhoneNumber());


        binding.setProfileImage.setOnClickListener((view)->
        {
            Intent i=new Intent();
            i.setAction(Intent.ACTION_GET_CONTENT);
            i.setType("image/*");
            startActivityForResult(i,IMG);
        });

        binding.setUpProfile.setOnClickListener((view)->{
            Intent i=new Intent();
            i.setAction(Intent.ACTION_GET_CONTENT);
            i.setType("image/*");
            startActivityForResult(i,IMG);
        });

        binding.btnProfile.setOnClickListener((view)->{
            String name=binding.edName.getText().toString();
            if(name.length()!=0){
                user.setName(name);
                firebaseDatabase.getReference().child("users")
                        .child(Objects.requireNonNull(firebaseAuth.getUid()))
                        .setValue(user)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(getApplicationContext(),"Profile Setup Successfully",Toast.LENGTH_SHORT).show();
                                    startMainActivity();
                                }
                            }
                        });

            }else{
                binding.edName.setError("Name Required...");
            }
        });

    }

    public void startMainActivity(){
         Intent i=new Intent(this, MainActivity.class);
        startActivity(i);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data!=null){
            if(data.getData()!=null){
                binding.profileImage.setImageURI(data.getData());
                StorageReference reference=firebaseStorage.getReference().child("profiles").child(firebaseAuth.getUid());
                reference.putFile(data.getData())
                        .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        user.setProfileImgUrl(uri.toString());
                                    }
                                });

                            }
                        });
            }
        }
    }
}