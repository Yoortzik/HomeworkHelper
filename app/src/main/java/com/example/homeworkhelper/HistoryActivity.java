package com.example.homeworkhelper;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private HistoryAdapter adapter;
    private FirestoreManager firestoreManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        recyclerView    = findViewById(R.id.recyclerView);
        progressBar     = findViewById(R.id.progressBar);
        tvEmpty         = findViewById(R.id.tvEmpty);
        MaterialButton btnBack = findViewById(R.id.btnBack);

        firestoreManager = new FirestoreManager();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        btnBack.setOnClickListener(v -> finish());

        loadHistory();
    }

    private void loadHistory() {
        progressBar.setVisibility(View.VISIBLE);

        firestoreManager.loadHistory(new FirestoreManager.LoadCallback() {
            @Override
            public void onSuccess(java.util.List<HomeworkEntry> entries) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);

                    if (entries.isEmpty()) {
                        tvEmpty.setVisibility(View.VISIBLE);
                        return;
                    }

                    // לחיצה על פריט — מציג את התשובה המלאה
                    adapter = new HistoryAdapter(entries, entry -> {
                        new AlertDialog.Builder(HistoryActivity.this)
                                .setTitle("📚 " + entry.getSubject())
                                .setMessage(entry.getAnswer())
                                .setPositiveButton("סגור", null)
                                .setNegativeButton("מחק", (dialog, which) ->
                                        deleteEntry(entry))
                                .show();
                    });

                    recyclerView.setAdapter(adapter);
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(HistoryActivity.this,
                            "שגיאה בטעינה: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void deleteEntry(HomeworkEntry entry) {
        firestoreManager.deleteEntry(entry.getId(), new FirestoreManager.SaveCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(HistoryActivity.this, "נמחק בהצלחה", Toast.LENGTH_SHORT).show();
                loadHistory(); // רענן את הרשימה
            }
            @Override
            public void onError(String error) {
                Toast.makeText(HistoryActivity.this,
                        "שגיאה במחיקה: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
