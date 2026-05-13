package com.example.homeworkhelper;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        TextView tvResult      = findViewById(R.id.tvResult);
        TextView tvSubjectTag  = findViewById(R.id.tvSubjectTag);
        MaterialButton btnBack = findViewById(R.id.btnBack);

        String resultText = getIntent().getStringExtra("RESULT_TEXT");
        if (resultText == null) resultText = "לא התקבלה תשובה.";

        // חילוץ המקצוע מהשורה הראשונה
        String subject = "עזרה בשיעורי בית";
        if (resultText.startsWith("מקצוע:")) {
            int newline = resultText.indexOf('\n');
            if (newline != -1) {
                subject = resultText.substring(0, newline).replace("מקצוע:", "").trim();
                resultText = resultText.substring(newline + 1).trim();
            }
        }

        tvSubjectTag.setText("📚 " + subject);
        tvResult.setText(resultText);

        // ─── שמירה ל-Firestore ───────────────────────────────────────
        FirestoreManager firestoreManager = new FirestoreManager();
        String finalSubject = subject;
        String finalAnswer  = resultText;

        firestoreManager.saveEntry(
                finalSubject,
                "שאלה מהמצלמה", // אפשר בהמשך לחלץ את השאלה מהתמונה
                finalAnswer,
                new FirestoreManager.SaveCallback() {
                    @Override
                    public void onSuccess() {
                        // נשמר בשקט ברקע
                    }
                    @Override
                    public void onError(String error) {
                        Toast.makeText(ResultActivity.this,
                                "שגיאה בשמירה: " + error,
                                Toast.LENGTH_SHORT).show();
                    }
                }
        );

        btnBack.setOnClickListener(v -> finish());
    }
}
