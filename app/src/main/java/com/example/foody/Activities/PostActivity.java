package com.example.foody.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.foody.Authentication.SignupActivity;
import com.example.foody.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class PostActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView toolbarTitle;
    private ImageView back, imageUpload, profile_image;

    private TextView name, email;
    private EditText restaurant, division, details;
    private CardView post;

    private LinearLayout uploadHint;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseFirestore firebaseFirestore;
    private DocumentReference document_ref, document_reference;
    private String userID;

    private Uri mImageUri;
    private static final int PICK_IMAGE_REQUEST = 1;

    private StorageReference mStorageRef;
    private StorageTask mUploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        toolbar = findViewById(R.id.toolbar_simple);
        toolbarTitle = findViewById(R.id.toolbar_title);
        back = findViewById(R.id.back);
        post = findViewById(R.id.btn_post);

        name = findViewById(R.id.tv_name);
        email = findViewById(R.id.tv_email);

        division = findViewById(R.id.et_division);
        restaurant = findViewById(R.id.et_restaurant);
        details = findViewById(R.id.et_details);

        imageUpload = findViewById(R.id.image_upload);
        uploadHint = findViewById(R.id.upload_hint);
        profile_image = findViewById(R.id.profile_img);

        division.setFocusable(false);

        toolbarTitle.setText("Post");


        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getUid();

        db = FirebaseFirestore.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        document_reference = db.collection("UserDetails").document(userID);
        document_ref = db.collection("Feed").document();
        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");

        division.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PostActivity.this);
                builder.setTitle("Division");
                final String[] Division = {"Dhaka", "Chattogram", "Sylhet", "Khulna", "Barisal", "Rajshahi", "Rangpur", "Mymensingh"};
                builder.setSingleChoiceItems(Division, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        division.setText(Division[which]);
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("Cancel", null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent close = new Intent(PostActivity.this, MainActivity.class);
                startActivity(close);
                finish();
            }
        });

        loadUserData();

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                document_reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        if (documentSnapshot.exists()) {

                            String Name = name.getText().toString();
                            String Email = email.getText().toString();
                            String Restaurant = restaurant.getText().toString();
                            String Division = division.getText().toString();
                            String Details = details.getText().toString();
                            String UserImageUrl = documentSnapshot.getString("userImageUrl");

                            final String id = document_ref.getId();
                            Map<String, Object> userMap = new HashMap<>();

                            userMap.put("name", Name);
                            userMap.put("email", Email);
                            userMap.put("restaurant", Restaurant);
                            userMap.put("division", Division);
                            userMap.put("details", Details);
                            userMap.put("userImageUrl", UserImageUrl);
                            userMap.put("user_id", userID);
                            userMap.put("id", id);
                            userMap.put("timestamp", FieldValue.serverTimestamp());
                            document_ref.set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    firebaseFirestore.collection("UserDetails").document(userID).collection("Posts").document().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                            Map<String, Object> comments = new HashMap<>();
                                            comments.put("timestamp", FieldValue.serverTimestamp());

                                            firebaseFirestore.collection("UserDetails").document(userID).collection("Posts").document().set(comments);


                                        }
                                    });

//                        Toast.makeText(PostActivity.this, "Adding..", Toast.LENGTH_LONG).show();
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(PostActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });


                        } else {
                            Toast.makeText(PostActivity.this, "Something wrong!", Toast.LENGTH_LONG).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

                //Upload to firebase storage
                uploadFile();

            }
        });


        imageUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        uploadHint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();
//            Picasso.get().load(mImageUri).into(imageUpload);
            Glide.with(this).load(mImageUri).into(imageUpload);
            uploadHint.setVisibility(View.INVISIBLE);
        }
    }

    private void uploadFile() {
        if (mImageUri != null) {
            final String currentTimeMillis = String.valueOf(System.currentTimeMillis());
            final StorageReference fileReference = mStorageRef.child(currentTimeMillis);
            mUploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Toast.makeText(PostActivity.this, "Upload successful", Toast.LENGTH_LONG).show();

                            StorageReference storageReference = FirebaseStorage.getInstance().getReference();

                            mStorageRef.child(currentTimeMillis).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    String img_url = String.valueOf(uri);

                                    document_ref.update("postImageUrl", img_url).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(PostActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {

                                }
                            });


                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(PostActivity.this, " e.getMessage()", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadUserData() {

        document_reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                if (documentSnapshot.exists()) {


                    String Name = documentSnapshot.getString("name");
                    String Email = documentSnapshot.getString("email");
                    String Division = documentSnapshot.getString("division");
                    String UserImageUrl = documentSnapshot.getString("userImageUrl");

                    name.setText(Name);
                    email.setText(Email);
                    division.setText(Division);
                    Glide.with(getApplicationContext()).load(UserImageUrl).into(profile_image);

                } else {
                    Toast.makeText(PostActivity.this, "Something wrong!", Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        Intent i = new Intent(PostActivity.this, MainActivity.class);
        startActivity(i);
    }
}