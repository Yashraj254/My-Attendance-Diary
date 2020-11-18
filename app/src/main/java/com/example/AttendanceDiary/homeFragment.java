package com.example.AttendanceDiary;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.AttendanceDiary.Room.DbHelper;
import com.google.android.material.navigation.NavigationView;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageActivity;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link homeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class homeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    EditText name, standard, enrollNo, college;
    TextView nameText, classText, enrollText, collegeText, header_name, header_enroll;
    String userName = "name", userClass, userEnroll = "Enroll No.", userCollege;
    NavigationView nav;
    ActionBarDrawerToggle bar;
    DrawerLayout layout;
    Toolbar toolbar;
    ImageView profile, save, edit, header_image;
    private static final int PICK_IMAGE = 1;
    Uri imageUri, resultUri;
    Bitmap image, bmp;
    DbHelper db;
    private static final String TAG = "homeFragment";
    byte[] pic;
    Exception error;
    View header;
    public static final int ACCESS_FILE = 43;

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
        db = new DbHelper(getContext());
        try {
            show();
            header_name.setText(userName);
            header_enroll.setText(userEnroll);
            if (bmp != null)
                header_image.setImageBitmap(bmp);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (db.countTable3() == 0) {
            Log.d(TAG, "if: " + db.countTable3());
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
            Log.d(TAG, "else: " + db.countTable3());
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


        save.setOnClickListener(v -> {

            userName = name.getText().toString();
            userClass = standard.getText().toString();
            userEnroll = enrollNo.getText().toString();
            userCollege = college.getText().toString();


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

                db.clearAll();

                ft.detach(this).attach(this).commit();
                db.insertUserDetails(userName, userClass, userEnroll, userCollege, imageViewToByte(profile));

                Log.d(TAG, "onCreateView: "+imageViewToByte(profile));
                try {
                    show();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
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
                name.setText(userName);
                name.requestFocus();
                standard.setText(userClass);
                enrollNo.setText(userEnroll);
                college.setText(userCollege);
                profile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openGallery();

                    }
                });
            }
        });

        return view;
    }

    public byte[] imageViewToByte(ImageView img) {
        if (image != null) {
            image = ((BitmapDrawable) img.getDrawable()).getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            return byteArray;
        } else
            return null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ACCESS_FILE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            CropImage.activity(imageUri).setGuidelines(CropImageView.Guidelines.ON).
                    setActivityTitle("Crop image").setCropShape(CropImageView.CropShape.OVAL).setAspectRatio(1, 1).
                    setCropMenuCropButtonTitle("Done").start(getContext(), this);

            try {
                image = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
                profile.setImageURI(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
                error = result.getError();
        }
    }

    private void openGallery() {
        Intent gallery = new Intent();
        gallery.setType("image/*");
        gallery.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(gallery, "Gallery"), ACCESS_FILE);
    }

    public void show() throws UnsupportedEncodingException {
        Cursor cursor = db.showUserDetails();

        if (cursor.getCount() == 0) {
            Toast.makeText(getContext(), "No data", Toast.LENGTH_SHORT).show();
        } else {
            while ((cursor.moveToNext())) {
                userName = cursor.getString(1);
                userClass = cursor.getString(2);
                userEnroll = cursor.getString(3);
                userCollege = cursor.getString(4);
                pic = cursor.getBlob(5);
                Log.d(TAG, "show: "+pic);
            }
            if (pic != null)
                bmp = BitmapFactory.decodeByteArray(pic, 0, pic.length);
            nameText.setText(userName);
            classText.setText(userClass);
            enrollText.setText(userEnroll);
            collegeText.setText(userCollege);
            if (bmp != null)
                profile.setImageBitmap(bmp);
        }
    }
}