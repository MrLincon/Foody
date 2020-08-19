package com.example.foody.Authentication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.foody.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    TextView sign_in;
    Button sign_up;
    TextInputLayout layout_email, layout_password, layout_name, layout_division, layout_country;
    TextInputEditText et_email, et_password, et_name, et_division, et_country;
    ProgressBar progressBar;

    private RadioGroup genderGroup;
    private RadioButton gender;
    private String Gender;
    private CheckBox agree;

    private DatePicker datePicker;

    LinearLayout linearLayout;

    private FirebaseAuth mAuth;
    private String userID;

    private FirebaseFirestore db;
    private DocumentReference document_ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        layout_name = findViewById(R.id.layout_name);
        layout_division = findViewById(R.id.layout_division);
        layout_country = findViewById(R.id.layout_country);
        layout_email = findViewById(R.id.layout_email);
        layout_password = findViewById(R.id.layout_password);

        sign_in = findViewById(R.id.textView_sign_in);
        sign_up = findViewById(R.id.btn_sign_up);

        et_name = findViewById(R.id.name);
        et_division = findViewById(R.id.et_division);
        et_country = findViewById(R.id.et_country);
        et_email = findViewById(R.id.email_sign_up);
        et_password = findViewById(R.id.password_sign_up);

        linearLayout = findViewById(R.id.linearLayout);
        progressBar = findViewById(R.id.sign_up_progressbar);

        datePicker = findViewById(R.id.date);
        genderGroup = findViewById(R.id.gender);
        agree = findViewById(R.id.agree);

        progressBar.setVisibility(View.GONE);
        sign_up.setVisibility(View.VISIBLE);
        linearLayout.setVisibility(View.VISIBLE);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        et_division.setFocusable(false);
        et_country.setFocusable(false);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        et_division.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
                builder.setTitle("Division");
                final String[] division = {"Dhaka", "Chattogram", "Sylhet", "Khulna", "Barisal", "Rajshahi", "Rangpur", "Mymensingh"};
                builder.setSingleChoiceItems(division, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        et_division.setText(division[which]);
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("Cancel", null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        et_country.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
                builder.setTitle("Country");
                final String[] country = {"Bangladesh"};
                builder.setSingleChoiceItems(country, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        et_country.setText(country[which]);
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("Cancel", null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });


        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sign_in = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(sign_in);
                finish();
            }
        });


        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //For closing keyboard if open

                ConstraintLayout mainLayout;
                mainLayout = findViewById(R.id.main_layout);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mainLayout.getWindowToken(), 0);
//..........................

                final String name = et_name.getText().toString().trim();
                final String division = et_division.getText().toString().trim();
                final String country = et_country.getText().toString().trim();
                final String email = et_email.getText().toString().trim();
                final String password = et_password.getText().toString().trim();

                if (name.isEmpty()) {
                    layout_name.setError("Name is required");
                    return;
                }
                if (division.isEmpty()) {
                    layout_division.setError("Division is required");
                    return;
                }
                if (country.isEmpty()) {
                    layout_country.setError("Country is required");
                    return;
                }
                if (email.isEmpty()) {
                    layout_email.setError("E-mail is required");
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    layout_email.setError("Please enter a valid email");
                    return;
                }

                if (password.isEmpty()) {
                    layout_password.setError("Password is required");
                    return;
                }

                if (password.length() < 6) {
                    layout_password.setError("Minimum length of password should be 6");
                    return;
                }
                if (genderGroup.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(SignupActivity.this, "You have to select a gender", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (agree.isChecked() == false) {
                    Toast.makeText(SignupActivity.this, "You have to agree to the terms and conditions of foody", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    sign_up.setVisibility(View.GONE);
                    linearLayout.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);

                    mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                        @Override
                        public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                            boolean check = !task.getResult().getSignInMethods().isEmpty();

                            if (!check) {

                                mAuth.createUserWithEmailAndPassword(email, password)
                                        .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (task.isSuccessful()) {
                                                    sign_up.setVisibility(View.VISIBLE);
                                                    linearLayout.setVisibility(View.VISIBLE);
                                                    progressBar.setVisibility(View.GONE);

                                                    userID = mAuth.getUid();

                                                    String date, month, year;
                                                    String[] month_name = {"January", "February", "March", "April", "May", "June", "July", "August",
                                                            "September", "October", "November", "December"};

                                                    int selectedGender = genderGroup.getCheckedRadioButtonId();
                                                    gender = (RadioButton) findViewById(selectedGender);
                                                    Gender = String.valueOf(gender.getText());

                                                    date = String.valueOf(datePicker.getDayOfMonth());
                                                    month = String.valueOf(datePicker.getMonth() + 1);
                                                    year = String.valueOf(datePicker.getYear());

                                                    SimpleDateFormat Year = new SimpleDateFormat("yyyy");
                                                    SimpleDateFormat Month = new SimpleDateFormat("MMMM");
                                                    String currentYear = Year.format(new Date());
                                                    String currentMonth = Month.format(new Date());

                                                    document_ref = db.collection("UserDetails").document(userID);

                                                    Map<String, String> userMap = new HashMap<>();

                                                    userMap.put("name", name);
                                                    userMap.put("email", email);
                                                    userMap.put("division", division);
                                                    userMap.put("country", country);
                                                    userMap.put("birthday", month_name[Integer.parseInt(month)] + " " + date);
                                                    userMap.put("birthYear", year);
                                                    userMap.put("joined", "Joined " + currentMonth + " " + currentYear);
                                                    userMap.put("gender", Gender);
                                                    userMap.put("usedID", userID);

                                                    document_ref.set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {

                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(SignupActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                                        }
                                                    });

                                                    Intent sign_in = new Intent(SignupActivity.this, ConfirmActivity.class);
                                                    startActivity(sign_in);
                                                    finish();
                                                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                                }
                                            }
                                        });

                            } else {
                                Toast.makeText(SignupActivity.this, "E-mail already exists!", Toast.LENGTH_SHORT).show();
                                sign_up.setVisibility(View.VISIBLE);
                                linearLayout.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });

                }
            }
        });


    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
