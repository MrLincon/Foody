package com.example.foody.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foody.Models.FavResRecyclerDecoration;
import com.example.foody.Models.FavouriteRestaurantAdapter;
import com.example.foody.Models.FavouriteRestaurants;
import com.example.foody.Models.Feed;
import com.example.foody.Models.FeedRecyclerDecoration;
import com.example.foody.Models.MyPostAdapter;
import com.example.foody.Models.MyPosts;
import com.example.foody.R;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;


public class ProfileActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private AppBarLayout appBarLayout;
    private TextView toolbarTitle, save;
    private ImageView close, profile;
    private CardView editProfile;
    private RecyclerView recyclerView, fav_recyclerView;

    private TextView favourites_count, posts_count;

    private TextView name,email,location,birthday,joined;

    private FirebaseAuth mAuth;
    private String userID;

    private FirebaseFirestore db;
    private FirebaseFirestore firebaseFirestore;
    private DocumentReference document_reference;

    private MyPostAdapter adapter;
    private FavouriteRestaurantAdapter adapter2;

    private Uri mImageUri;
    private static final int PICK_IMAGE_REQUEST = 1;

    private StorageReference mStorageRef;
    private StorageTask mUploadTask;

    public static final String EXTRA_ID_POST = "com.example.foody.EXTRA_ID";
    public static final String EXTRA_ID = "com.example.foody.EXTRA_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        toolbar = findViewById(R.id.toolbar);
        appBarLayout = findViewById(R.id.appBarLayout);
        toolbarTitle = findViewById(R.id.toolbar_title);
        save = findViewById(R.id.save);
        close = findViewById(R.id.close);
        editProfile =  findViewById(R.id.edit_profile);
        profile =  findViewById(R.id.profile);

        favourites_count = findViewById(R.id.favourites_count);
        posts_count = findViewById(R.id.posts_count);

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        location = findViewById(R.id.location);
        birthday = findViewById(R.id.birthday);
        joined = findViewById(R.id.joined);

        recyclerView = findViewById(R.id.my_posts_recyclerview);
        int topPadding = getResources().getDimensionPixelSize(R.dimen.topPadding);
        int bottomPadding = getResources().getDimensionPixelSize(R.dimen.bottomPadding);
        recyclerView.addItemDecoration(new FeedRecyclerDecoration(topPadding, bottomPadding));

        fav_recyclerView = findViewById(R.id.favourite_recyclerview);
        int leftPadding = getResources().getDimensionPixelSize(R.dimen.leftPadding);
        int rightPadding = getResources().getDimensionPixelSize(R.dimen.rightPadding);
        fav_recyclerView.addItemDecoration(new FavResRecyclerDecoration(leftPadding, rightPadding));

//        For Action Bar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getUid();

        db = FirebaseFirestore.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        document_reference = db.collection("UserDetails").document(userID);
        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


       editProfile.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent edit_profile = new Intent(ProfileActivity.this,EditProfileActivity.class);
               startActivity(edit_profile);
           }
       });

       save.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               uploadFile();
           }
       });


        document_reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                if (documentSnapshot.exists()) {


                    String Name = documentSnapshot.getString("name");
                    String Email = documentSnapshot.getString("email");
                    String Division = documentSnapshot.getString("division");
                    String Country = documentSnapshot.getString("country");
                    String Birthday = documentSnapshot.getString("birthday");
                    String Joined = documentSnapshot.getString("joined");
                    String Gender = documentSnapshot.getString("gender");
                    String UserImageUrl = documentSnapshot.getString("userImageUrl");

                    toolbarTitle.setText(Name);
                    name.setText(Name);
                    email.setText(Email);
                    location.setText(Division+", "+Country);
                    birthday.setText(Birthday);
                    joined.setText(Joined);
                    Glide.with(getApplicationContext()).load(UserImageUrl).into(profile);

                } else {
                    Toast.makeText(ProfileActivity.this, "Something wrong!", Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });


        loadMyPosts();


        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (Math.abs(verticalOffset) == appBarLayout.getTotalScrollRange()) {
                    toolbarTitle.setVisibility(View.VISIBLE);
                } else if (verticalOffset == 0) {
                    toolbarTitle.setVisibility(View.GONE);
                }
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
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
        save.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();
            Glide.with(this).load(mImageUri).into(profile);
        }
    }

    private void uploadFile() {
        save.setVisibility(View.INVISIBLE);
        if (mImageUri != null) {
            final String currentTimeMillis = String.valueOf(System.currentTimeMillis());
            final StorageReference fileReference = mStorageRef.child(currentTimeMillis);
            mUploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Toast.makeText(ProfileActivity.this, "Upload successful", Toast.LENGTH_LONG).show();

                            StorageReference storageReference = FirebaseStorage.getInstance().getReference();

                            mStorageRef.child(currentTimeMillis).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    String img_url = String.valueOf(uri);

                                    document_reference.update("userImageUrl", img_url).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
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
                            Toast.makeText(ProfileActivity.this, " e.getMessage()", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadMyPosts() {

        CollectionReference myPosts = db.collection("Feed");

        Query query = myPosts.whereEqualTo("user_id",userID)
        .orderBy("timestamp", Query.Direction.DESCENDING);

        PagedList.Config config = new PagedList.Config.Builder()
                .setInitialLoadSizeHint(10)
                .setPageSize(5)
                .build();

        FirestorePagingOptions<MyPosts> options = new FirestorePagingOptions.Builder<MyPosts>()
                .setQuery(query, config, MyPosts.class)
                .build();

        adapter = new MyPostAdapter(options);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(adapter);
        adapter.startListening();

        adapter.setOnItemClickListener(new MyPostAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot) {
                Feed feed = documentSnapshot.toObject(Feed.class);
                String id = documentSnapshot.getId();

                Intent intent = new Intent(ProfileActivity.this, DetailsActivity.class);

                intent.putExtra(EXTRA_ID_POST, id);

                startActivity(intent);
            }
        });


        //Get favourites Count
        firebaseFirestore.collection("UserDetails").document(userID).collection("Favourites").addSnapshotListener( new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if(!documentSnapshots.isEmpty()){

                    String count = String.valueOf(documentSnapshots.size());
                    favourites_count.setText(count);
                } else {
                    favourites_count.setText("0");
                }

            }
        });

        //Get posts Count
        firebaseFirestore.collection("UserDetails").document(userID).collection("Posts").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if (!documentSnapshots.isEmpty()) {
                    String count = String.valueOf(documentSnapshots.size());
                    posts_count.setText(count);
                } else {
                    posts_count.setText("0");
                }
            }
        });

        //Load favourite restaurants
        favouriteRestaurants();
    }

    private void favouriteRestaurants(){
        CollectionReference favRestaurants = db.collection("UserDetails").document(userID).collection("Favourites");

        Query query = favRestaurants.orderBy("timestamp", Query.Direction.ASCENDING);

        PagedList.Config config = new PagedList.Config.Builder()
                .setInitialLoadSizeHint(10)
                .setPageSize(5)
                .build();

        FirestorePagingOptions<FavouriteRestaurants> options = new FirestorePagingOptions.Builder<FavouriteRestaurants>()
                .setQuery(query, config, FavouriteRestaurants.class)
                .build();

        adapter2 = new FavouriteRestaurantAdapter(options);
        fav_recyclerView.setHasFixedSize(true);
        fav_recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL,false));
        fav_recyclerView.setAdapter(adapter2);
        adapter2.startListening();

        adapter2.setOnItemClickListener(new FavouriteRestaurantAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot) {
                String id = documentSnapshot.getId();

                Intent intent = new Intent(ProfileActivity.this, ProfileActivityView.class);
                intent.putExtra(EXTRA_ID, id);

                startActivity(intent);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
        adapter2.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
        adapter2.stopListening();
    }

    @Override
    public void finish() {
        super.finish();
        Intent i = new Intent(ProfileActivity.this, MainActivity.class);
        startActivity(i);
    }
}