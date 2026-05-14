package com.example.homeworkhelper;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;

public class ResultFragment extends Fragment {

    private static final String ARG_RESULT = "result_text";

    // Factory method — הדרך הנכונה ליצור Fragment עם נתונים
    public static ResultFragment newInstance(String resultText) {
        ResultFragment fragment = new ResultFragment();
        Bundle args = new Bundle();
        args.putString(ARG_RESULT, resultText);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_result, container, false);

        TextView tvResult     = view.findViewById(R.id.tvResult);
        TextView tvSubjectTag = view.findViewById(R.id.tvSubjectTag);
        MaterialButton btnBack = view.findViewById(R.id.btnBack);

        String resultText = getArguments() != null
                ? getArguments().getString(ARG_RESULT, "") : "";

        // חילוץ המקצוע
        String subject = "עזרה בשיעורי בית";
        if (resultText.startsWith("מקצוע:")) {
            int newline = resultText.indexOf('\n');
            if (newline != -1) {
                subject   = resultText.substring(0, newline).replace("מקצוע:", "").trim();
                resultText = resultText.substring(newline + 1).trim();
            }
        }

        tvSubjectTag.setText("📚 " + subject);
        tvResult.setText(resultText);

        // שמירה ל-Firestore
        String finalSubject = subject;
        String finalAnswer  = resultText;
        FirestoreManager firestoreManager = new FirestoreManager();
        firestoreManager.saveEntry(finalSubject, "שאלה מהמצלמה", finalAnswer,
                new FirestoreManager.SaveCallback() {
                    @Override public void onSuccess() {}
                    @Override public void onError(String error) {
                        Toast.makeText(requireContext(),
                                "שגיאה בשמירה: " + error, Toast.LENGTH_SHORT).show();
                    }
                });

        // חזרה ל-HomeFragment
        btnBack.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack());

        return view;
    }
}
