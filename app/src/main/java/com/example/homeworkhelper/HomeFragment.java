package com.example.homeworkhelper;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import com.example.homeworkhelper.R;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private static final int REQUEST_CAMERA_PERMISSION  = 100;
    private static final int REQUEST_STORAGE_PERMISSION = 101;
    private static final int REQUEST_IMAGE_CAPTURE      = 1;
    private static final int REQUEST_PICK_IMAGE         = 2;

    private ImageView imagePreview;
    private MaterialButton btnAnalyze;
    private ProgressBar progressBar;

    private Uri photoUri;
    private byte[] imageBytes;
    private final String imageMimeType = "image/jpeg";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        imagePreview = view.findViewById(R.id.imagePreview);
        btnAnalyze   = view.findViewById(R.id.btnAnalyze);
        progressBar  = view.findViewById(R.id.progressBar);

        MaterialButton btnTakePhoto   = view.findViewById(R.id.btnTakePhoto);
        MaterialButton btnPickGallery = view.findViewById(R.id.btnPickGallery);

        btnTakePhoto.setOnClickListener(v -> checkCameraPermission());
        btnPickGallery.setOnClickListener(v -> checkStoragePermission());
        btnAnalyze.setOnClickListener(v -> analyzeImage());

        return view;
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        } else {
            launchCamera();
        }
    }

    private void launchCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
            File photoFile = createImageFile();
            if (photoFile != null) {
                photoUri = FileProvider.getUriForFile(requireContext(),
                        requireContext().getPackageName() + ".fileprovider", photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                .format(new Date());
        File storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        try {
            return File.createTempFile("HOMEWORK_" + timeStamp, ".jpg", storageDir);
        } catch (IOException e) {
            Toast.makeText(requireContext(), "לא ניתן ליצור קובץ תמונה", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private void checkStoragePermission() {
        String permission = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                ? Manifest.permission.READ_MEDIA_IMAGES
                : Manifest.permission.READ_EXTERNAL_STORAGE;

        if (ContextCompat.checkSelfPermission(requireContext(), permission)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{permission}, REQUEST_STORAGE_PERMISSION);
        } else {
            launchGallery();
        }
    }

    private void launchGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) return;

        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            loadImageFromUri(photoUri);
        } else if (requestCode == REQUEST_PICK_IMAGE && data != null) {
            loadImageFromUri(data.getData());
        }
    }

    private void loadImageFromUri(Uri uri) {
        try {
            InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            imagePreview.setImageBitmap(bitmap);
            imagePreview.setVisibility(View.VISIBLE);
            btnAnalyze.setVisibility(View.VISIBLE);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, baos);
            imageBytes = baos.toByteArray();
        } catch (IOException e) {
            Toast.makeText(requireContext(), "שגיאה בטעינת תמונה", Toast.LENGTH_SHORT).show();
        }
    }

    private void analyzeImage() {
        if (imageBytes == null) {
            Toast.makeText(requireContext(), "צלם או בחר תמונה", Toast.LENGTH_SHORT).show();
            return;
        }

        btnAnalyze.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);

        ApiClient apiClient = new ApiClient();
        apiClient.analyzeHomework(imageBytes, imageMimeType, new ApiClient.Callback() {
            @Override
            public void onSuccess(String response) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    btnAnalyze.setEnabled(true);
                    String subject = "שיעורי בית";
                    if (response.startsWith("מקצוע:")) {
                        int newline = response.indexOf('\n');
                        if (newline != -1) {
                            subject = response.substring(0, newline).replace("מקצוע:", "").trim();
                        }}
                    ResultFragment resultFragment = ResultFragment.newInstance(response);
                    requireActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragmentContainer, resultFragment)
                            .addToBackStack(null)
                            .commit();
                });
            }

            @Override
            public void onError(String error) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    btnAnalyze.setEnabled(true);
                    Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show();
                });
            }
        });
    }
}