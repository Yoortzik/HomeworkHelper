package com.example.homeworkhelper;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class HistoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private FirestoreManager firestoreManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_history, container, false);

        recyclerView     = view.findViewById(R.id.recyclerView);
        progressBar      = view.findViewById(R.id.progressBar);
        tvEmpty          = view.findViewById(R.id.tvEmpty);
        firestoreManager = new FirestoreManager();

        // הסתר את כפתור החזרה — לא צריך ב-Fragment
        View btnBack = view.findViewById(R.id.btnBack);
        if (btnBack != null) btnBack.setVisibility(View.GONE);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        loadHistory();

        return view;
    }

    private void loadHistory() {
        progressBar.setVisibility(View.VISIBLE);

        firestoreManager.loadHistory(new FirestoreManager.LoadCallback() {
            @Override
            public void onSuccess(java.util.List<HomeworkEntry> entries) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    if (entries.isEmpty()) {
                        tvEmpty.setVisibility(View.VISIBLE);
                        return;
                    }
                    HistoryAdapter adapter = new HistoryAdapter(entries, entry -> {
                        new AlertDialog.Builder(requireContext())
                                .setTitle("📚 " + entry.getSubject())
                                .setMessage(entry.getAnswer())
                                .setPositiveButton("סגור", null)
                                .setNegativeButton("מחק", (d, w) -> deleteEntry(entry))
                                .show();
                    });
                    recyclerView.setAdapter(adapter);
                });
            }

            @Override
            public void onError(String error) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(requireContext(),
                            "שגיאה: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void deleteEntry(HomeworkEntry entry) {
        firestoreManager.deleteEntry(entry.getId(), new FirestoreManager.SaveCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(requireContext(), "נמחק בהצלחה", Toast.LENGTH_SHORT).show();
                loadHistory();
            }
            @Override
            public void onError(String error) {
                Toast.makeText(requireContext(), "שגיאה במחיקה: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
