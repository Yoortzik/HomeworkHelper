package com.example.homeworkhelper;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class MainActivity extends AppCompatActivity {

    private TextView welcomeTitle;
    private TextView welcomeSubtitle;
    private CardView mathCard;
    private CardView scienceCard;
    private CardView englishCard;
    private CardView historyCard;
    private Button getStartedBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        welcomeTitle = findViewById(R.id.welcomeTitle);
        welcomeSubtitle = findViewById(R.id.welcomeSubtitle);
        mathCard = findViewById(R.id.mathCard);
        scienceCard = findViewById(R.id.scienceCard);
        englishCard = findViewById(R.id.englishCard);
        historyCard = findViewById(R.id.historyCard);
        getStartedBtn = findViewById(R.id.getStartedBtn);
    }

    private void setupClickListeners() {
        mathCard.setOnClickListener(v -> openSubject("Math"));
        scienceCard.setOnClickListener(v -> openSubject("Science"));
        englishCard.setOnClickListener(v -> openSubject("English"));
        historyCard.setOnClickListener(v -> openSubject("History"));

        getStartedBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NewActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void openSubject(String subject) {
        Intent intent = new Intent(MainActivity.this, SubjectActivity.class);
        intent.putExtra("subject", subject);
        startActivity(intent);
    }
}