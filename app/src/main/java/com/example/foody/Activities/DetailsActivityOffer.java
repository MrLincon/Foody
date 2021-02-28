package com.example.foody.Activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foody.Models.Comment;
import com.example.foody.Models.CommentAdapter;
import com.example.foody.R;
import com.example.foody.Tabs.FragmentFeed;
import com.example.foody.Tabs.FragmentOffers;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class DetailsActivityOffer extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView toolbarTitle;
    private AppBarLayout appBarLayout;
    private ImageView back, edit, delete, like, send, post_image, user_profile, comment_user_image;
    private EditText et_comment;
    private TextView name, restaurant, details, like_count, views_count, comments_count, tv_comment;
    private String Details;
    private String ID, userID;

    private RecyclerView recyclerView;

    Dialog editPost;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseFirestore firebaseFirestore;
    private DocumentReference document_reference, document_ref, doc_ref;

    private CommentAdapter adapter;

    public static final String EXTRA_ID = "com.example.foody.EXTRA_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        toolbar = findViewById(R.id.toolbar);
        toolbarTitle = findViewById(R.id.toolbar_title);
        appBarLayout = findViewById(R.id.appBarLayout);
        back = findViewById(R.id.back);
        edit = findViewById(R.id.edit);
        delete = findViewById(R.id.delete);
        name = findViewById(R.id.tv_name);
        restaurant = findViewById(R.id.tv_restaurant);
        details = findViewById(R.id.tv_details);

        et_comment = findViewById(R.id.comment);
        tv_comment = findViewById(R.id.tv_comment);
        send = findViewById(R.id.send);
        post_image = findViewById(R.id.post_image);
        user_profile = findViewById(R.id.profile);
        comment_user_image = findViewById(R.id.user_img);
        like = findViewById(R.id.like);
        like_count = findViewById(R.id.like_count);
        comments_count = findViewById(R.id.comments_count);
        views_count = findViewById(R.id.views_count);

        editPost = new Dialog(this);

        recyclerView = findViewById(R.id.comment_recyclerview);

        final Intent intent = getIntent();

        toolbarTitle.setText("Details");

        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getUid();

        ID = intent.getStringExtra(FragmentFeed.EXTRA_ID);

        db = FirebaseFirestore.getInstance();
        document_reference = db.collection("Offer").document(ID);
        firebaseFirestore = FirebaseFirestore.getInstance();
        document_ref = db.collection("UserDetails").document(userID);
        doc_ref = db.collection("Comments").document();

        loadData();
        loadComments();

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userComment();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editPost.setContentView(R.layout.popup_edit_post);
                editPost.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                final EditText et_details = editPost.findViewById(R.id.et_details);
                et_details.setText(Details);
                LinearLayout save = editPost.findViewById(R.id.save);
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        document_reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot documentSnapshot = task.getResult();
                                    if (documentSnapshot != null && documentSnapshot.exists()) {
                                        document_reference.update("details", et_details.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(DetailsActivityOffer.this, "Updated", Toast.LENGTH_SHORT).show();
                                                editPost.dismiss();
                                                loadData();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(DetailsActivityOffer.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }
                                }
                            }
                        });
                    }
                });
                LinearLayout cancel = editPost.findViewById(R.id.cancel);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editPost.dismiss();
                    }
                });

                editPost.show();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DetailsActivityOffer.this);
                builder.setTitle("Are you sure?")
                        .setMessage("If you delete this, this post will no longer be shown in the feed!")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                document_reference.delete();
                                finish();
                                Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_SHORT).show();
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.show();
            }
        });

    }

    private void loadData() {
        document_reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                if (documentSnapshot.exists()) {

                    String Name = documentSnapshot.getString("name");
                    String Restaurant = documentSnapshot.getString("restaurant");
                    Details = documentSnapshot.getString("details");
                    String User_ID = documentSnapshot.getString("user_id");
                    String PostImageUrl = documentSnapshot.getString("postImageUrl");
                    String UserImageUrl = documentSnapshot.getString("userImageUrl");

                    if (User_ID.equals(userID)) {
                        delete.setVisibility(View.VISIBLE);
                        edit.setVisibility(View.VISIBLE);
                    } else {
                        delete.setVisibility(View.GONE);
                        edit.setVisibility(View.GONE);
                    }

                    name.setText(Name);
                    restaurant.setText("@" + Restaurant);
                    details.setText(Details);
                    Glide.with(getApplicationContext()).load(PostImageUrl).into(post_image);
                    Glide.with(getApplicationContext()).load(UserImageUrl).into(user_profile);

                } else {

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(DetailsActivityOffer.this, "Something wrong!", Toast.LENGTH_SHORT).show();
            }
        });

        document_ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                if (documentSnapshot.exists()) {

                    String UserImageUrl = documentSnapshot.getString("userImageUrl");

                    Glide.with(getApplicationContext()).load(UserImageUrl).into(comment_user_image);

                } else {

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(DetailsActivityOffer.this, "Something wrong!", Toast.LENGTH_SHORT).show();
            }
        });

        viewFeatures();
        likeFeatures();
        commentFeatures();


    }

    private void viewFeatures() {
        //View features

        firebaseFirestore.collection("Offer").document(ID).collection("Views").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                Map<String, Object> views = new HashMap<>();
                views.put("timestamp", FieldValue.serverTimestamp());

                firebaseFirestore.collection("Offer").document(ID).collection("Views").document(userID).set(views);

            }
        });

        //Get views Count
        firebaseFirestore.collection("Offer").document(ID).collection("Views").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if (!documentSnapshots.isEmpty()) {
                    String count = String.valueOf(documentSnapshots.size());
                    views_count.setText(count);
                } else {
                    views_count.setText("0");
                }
            }
        });
    }

    private void likeFeatures() {
        //Like features
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseFirestore.collection("Offer").document(ID).collection("Likes").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (!task.getResult().exists()) {

                            Map<String, Object> likes = new HashMap<>();
                            likes.put("timestamp", FieldValue.serverTimestamp());

                            firebaseFirestore.collection("Offer").document(ID).collection("Likes").document(userID).set(likes);

                        } else {

                            firebaseFirestore.collection("Offer").document(ID).collection("Likes").document(userID).delete();

                        }

                    }
                });

            }
        });

        //Update like icon
        firebaseFirestore.collection("Offer").document(ID).collection("Likes").document(userID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {

                if (documentSnapshot.exists()) {

                    like.setImageResource(R.drawable.ic_heart_selected);

                } else {

                    like.setImageResource(R.drawable.ic_heart);

                }
            }
        });

        //Get like Count
        firebaseFirestore.collection("Offer").document(ID).collection("Likes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if (!documentSnapshots.isEmpty()) {
                    String count = String.valueOf(documentSnapshots.size());
                    like_count.setText(count);
                } else {
                    like_count.setText("0");
                }
            }
        });
    }

    private void commentFeatures() {
        //Get comments Count
        firebaseFirestore.collection("Offer").document(ID).collection("Comments").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if (!documentSnapshots.isEmpty()) {
                    String count = String.valueOf(documentSnapshots.size());
                    comments_count.setText(count);
                } else {
                    comments_count.setText("0");
                }
            }
        });
    }

    private void userComment() {

        hideKeyboard(DetailsActivityOffer.this);

        final String id = document_ref.getId();
        final String Comment = et_comment.getText().toString().trim();

        document_ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                if (documentSnapshot.exists()) {

                    String Name = documentSnapshot.getString("name");
                    String UserImageUrl = documentSnapshot.getString("userImageUrl");

                    Map<String, Object> userMap = new HashMap<>();

                    userMap.put("name", Name);
                    userMap.put("comment", Comment);
                    userMap.put("userImageUrl", UserImageUrl);
                    userMap.put("user_id", userID);
                    userMap.put("post_id", ID);
                    userMap.put("id", id);
                    userMap.put("timestamp", FieldValue.serverTimestamp());
                    doc_ref.set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            et_comment.setText("");
                            Toast.makeText(DetailsActivityOffer.this, "Adding..", Toast.LENGTH_LONG).show();

                            firebaseFirestore.collection("Offer").document(ID).collection("Comments").document().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                    Map<String, Object> comments = new HashMap<>();
                                    comments.put("timestamp", FieldValue.serverTimestamp());

                                    firebaseFirestore.collection("Offer").document(ID).collection("Comments").document().set(comments);

                                }
                            });

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(DetailsActivityOffer.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(DetailsActivityOffer.this, "Something wrong!", Toast.LENGTH_SHORT).show();
            }
        });
        Intent restartActivity = new Intent(getApplication(), DetailsActivityOffer.class);
        restartActivity.putExtra(EXTRA_ID, ID);
        startActivity(restartActivity);
        finish();
    }

    private void loadComments() {
        CollectionReference comments = db.collection("Comments");

        Query query = comments.whereEqualTo("post_id", ID).orderBy("timestamp", Query.Direction.DESCENDING);

        PagedList.Config config = new PagedList.Config.Builder()
                .setInitialLoadSizeHint(10)
                .setPageSize(15)
                .build();

        FirestorePagingOptions<Comment> options = new FirestorePagingOptions.Builder<Comment>()
                .setQuery(query, config, Comment.class)
                .build();

        adapter = new CommentAdapter(options);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}