package com.myFirstApp1.AttendanceDiary;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


import com.bumptech.glide.Glide;
import com.myFirstApp1.AttendanceDiary.Models.Home;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link homeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class homeFragment extends Fragment  {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int PICK_FILE_RESULT_CODE = 8778;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    EditText name, standard, enrollNo, college;
    TextView nameText, classText, enrollText, collegeText, header_name, header_enroll;

    String userName = "name", userClass, userEnroll = "Enroll No.", userCollege;
    NavigationView nav;
    Toolbar toolbar;
    ImageView profile, save, edit, header_image;
    String name1, standard1, enroll1, college1;
    Uri imageUri, resultUri, uri;
    FirebaseDatabase database;
    DatabaseReference ref;
    FirebaseUser mAuth;
    FirebaseStorage storage;
    String userId, userMail, downloadUrl = null;
    private static final String TAG = "homeFragment";
    String pic;
    Exception error;
    ProgressDialog dialog;
    View header;
    StorageReference storageRef;
    String imagePath;
    private static final int STORAGE_PERMISSION_CODE = 101;

    public homeFragment() {
        // Required empty public constructor

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment homeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static homeFragment newInstance(String param1, String param2) {
        homeFragment fragment = new homeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        name = view.findViewById(R.id.name);
        standard = view.findViewById(R.id.standard);
        enrollNo = view.findViewById(R.id.rollNo);
        college = view.findViewById(R.id.college);
        profile = view.findViewById(R.id.profile);
        nameText = view.findViewById(R.id.nameText);
        classText = view.findViewById(R.id.classText);
        enrollText = view.findViewById(R.id.enrollText);
        collegeText = view.findViewById(R.id.collegeText);
        nav = getActivity().findViewById(R.id.nav_menu);
        toolbar = view.findViewById(R.id.toolbar);
        save = view.findViewById(R.id.save);
        edit = view.findViewById(R.id.edit);

        header = nav.getHeaderView(0);
        header_name = header.findViewById(R.id.header_name);
        header_enroll = header.findViewById(R.id.header_enroll);
        header_image = header.findViewById(R.id.header_image);

        database = FirebaseDatabase.getInstance();
        ref = database.getReference();
        storage = FirebaseStorage.getInstance();
        mAuth = FirebaseAuth.getInstance().getCurrentUser();
        userId = mAuth.getUid();
        storageRef = storage.getReference().child(userId).child("UserProfile");
        getData();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_menu_24);
        AppCompatActivity actionBar = (AppCompatActivity) getActivity();
        actionBar.setSupportActionBar(toolbar);

        DrawerLayout drawer = actionBar.findViewById(R.id.draw_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                getActivity(), drawer, toolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        toolbar.setNavigationIcon(null);

        dialog = new ProgressDialog(getContext());
        dialog.setTitle("Image Upload");
        save.setOnClickListener(v -> {

            userName = name.getText().toString();
            userClass = standard.getText().toString();
            userEnroll = enrollNo.getText().toString();
            userCollege = college.getText().toString();

            Home home = new Home(userName, userClass, userEnroll, userCollege, downloadUrl);
            if (userName.isEmpty() || userClass.isEmpty() || userEnroll.isEmpty() || userCollege.isEmpty()) {
                Toast.makeText(getContext(), "Fill the empty field", Toast.LENGTH_SHORT).show();
            } else {
                header_name.setText(userName);
                header_enroll.setText(userEnroll);
                save.setVisibility(View.GONE);
                edit.setVisibility(View.VISIBLE);
                name.setVisibility(View.GONE);
                standard.setVisibility(View.GONE);
                enrollNo.setVisibility(View.GONE);
                college.setVisibility(View.GONE);
                nameText.setVisibility(View.VISIBLE);
                classText.setVisibility(View.VISIBLE);
                enrollText.setVisibility(View.VISIBLE);
                collegeText.setVisibility(View.VISIBLE);

                ref.child(userId).child("UserProfile").setValue(home);
                ft.detach(this).attach(this).commit();
                getData();
            }

        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit.setVisibility(View.GONE);
                save.setVisibility(View.VISIBLE);
                nameText.setVisibility(View.GONE);
                classText.setVisibility(View.GONE);
                enrollText.setVisibility(View.GONE);
                collegeText.setVisibility(View.GONE);
                name.setVisibility(View.VISIBLE);
                standard.setVisibility(View.VISIBLE);
                enrollNo.setVisibility(View.VISIBLE);
                college.setVisibility(View.VISIBLE);
                name.setText(name1);
                name.requestFocus();
                standard.setText(standard1);
                enrollNo.setText(enroll1);
                college.setText(college1);

                profile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        checkPermission(
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                STORAGE_PERMISSION_CODE);
                    }
                });

            }
        });

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        Log.d(TAG, "onActivityResult: requestCode: "+requestCode+" result code: "+resultCode);
        if(resultCode == 0) {
            dialog.dismiss();
            dialog.cancel();
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        }
        if (requestCode == PICK_FILE_RESULT_CODE && resultCode == RESULT_OK) {
            imageUri = data.getData();

            CropImage.activity(imageUri).setGuidelines(CropImageView.Guidelines.ON).
                    setActivityTitle("Crop image").setCropShape(CropImageView.CropShape.OVAL).setAspectRatio(1, 1).
                    setCropMenuCropButtonTitle("Done").start(getContext(), this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
                imagePath = resultUri.toString();


                StorageReference filePath = storage.getReference(userId);

                filePath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                downloadUrl = uri.toString();
                                Glide.with(getContext()).load(downloadUrl).into(profile);
                                dialog.dismiss();
                                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                Toast.makeText(getContext(),"Image uploaded.",Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        float percent = (100*snapshot.getBytesTransferred())/snapshot.getTotalByteCount();
                        dialog.setMessage("Uploaded: "+(int)percent+" %");
                    }
                });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
                error = result.getError();

        }
    }

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        gallery.addCategory(Intent.CATEGORY_OPENABLE);
        gallery.setType("image/*");
        startActivityForResult(gallery, PICK_FILE_RESULT_CODE);
    }

    public void getData() {

        Query query = ref.child(userId);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    Home home = new Home();

                    if (dataSnapshot.child("college").getValue() != null) {
                        college1 = dataSnapshot.child("college").getValue().toString();

                    }
                    if (dataSnapshot.child("name").getValue() != null)
                        name1 = dataSnapshot.child("name").getValue().toString();
                    if (dataSnapshot.child("standard").getValue() != null)
                        standard1 = dataSnapshot.child("standard").getValue().toString();
                    if (dataSnapshot.child("enroll").getValue() != null)
                        enroll1 = dataSnapshot.child("enroll").getValue().toString();
                    if (dataSnapshot.child("imageUrl").getValue() != null) {
                        downloadUrl = dataSnapshot.child("imageUrl").getValue().toString();

                        if (getContext()!=null){
                        Glide.with(getContext()).load(downloadUrl).into(profile);
                        Glide.with(getContext()).load(downloadUrl).into(header_image);}
                    }
                    home.setName(name1);
                    home.setStandard(standard1);
                    home.setEnroll(enroll1);
                    home.setCollege(college1);
                    home.setImageUrl(downloadUrl);
                }

                nameText.setText(name1);
                classText.setText(standard1);
                enrollText.setText(enroll1);
                collegeText.setText(college1);
                header_name.setText(name1);
                header_enroll.setText(enroll1);


                if (name1 == null || standard1 == null || enroll1 == null || college1 == null) {
                    toolbar.setNavigationIcon(null);
                    name.setVisibility(View.VISIBLE);
                    standard.setVisibility(View.VISIBLE);
                    enrollNo.setVisibility(View.VISIBLE);
                    college.setVisibility(View.VISIBLE);
                    nameText.setVisibility(View.GONE);
                    classText.setVisibility(View.GONE);
                    enrollText.setVisibility(View.GONE);
                    collegeText.setVisibility(View.GONE);
                    edit.setVisibility(View.GONE);
                    save.setVisibility(View.VISIBLE);
                } else {

                    toolbar.setNavigationIcon(R.drawable.ic_baseline_menu_24);
                    name.setVisibility(View.GONE);
                    standard.setVisibility(View.GONE);
                    enrollNo.setVisibility(View.GONE);
                    college.setVisibility(View.GONE);
                    nameText.setVisibility(View.VISIBLE);
                    classText.setVisibility(View.VISIBLE);
                    enrollText.setVisibility(View.VISIBLE);
                    collegeText.setVisibility(View.VISIBLE);
                    edit.setVisibility(View.VISIBLE);
                    save.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "Storage Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Storage Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(getContext(), permission) == PackageManager.PERMISSION_DENIED) {
            // Requesting the permission
            ActivityCompat.requestPermissions(getActivity(), new String[]{permission}, requestCode);
        } else {
            openGallery();
        }
    }
}
