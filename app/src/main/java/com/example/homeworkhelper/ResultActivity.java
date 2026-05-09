package com.example.homeworkhelper;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.homeworkhelper.R;
import com.google.android.material.button.MaterialButton;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        TextView tvResult     = findViewById(R.id.tvResult);
        TextView tvSubjectTag = findViewById(R.id.tvSubjectTag);
        MaterialButton btnBack = findViewById(R.id.btnBack);

        String resultText = getIntent().getStringExtra("RESULT_TEXT");
        if (resultText == null) resultText = "No response received.";

        // Parse the subject from first line: "Subject: Math"
        String subject = "Homework Help";
        if (resultText.startsWith("מקצוע:")) {
            int newline = resultText.indexOf('\n');
            if (newline != -1) {
                subject = resultText.substring(0, newline).replace("מקצוע:", "").trim();
            }}

        tvSubjectTag.setText("📚 " + subject);
        tvResult.setText(resultText);

        btnBack.setOnClickListener(v -> finish());
    }
}
