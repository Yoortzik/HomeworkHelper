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
    private HomeworkDatabase homeworkDatabase;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_history, container, false);

        recyclerView     = view.findViewById(R.id.recyclerView);
        progressBar      = view.findViewById(R.id.progressBar);
        tvEmpty          = view.findViewById(R.id.tvEmpty);
        homeworkDatabase = new HomeworkDatabase();
        View btnBack = view.findViewById(R.id.btnBack);
        if (btnBack != null) btnBack.setVisibility(View.GONE);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        loadHistory();

        return view;
    }

    private void loadHistory() {
        progressBar.setVisibility(View.VISIBLE);

        homeworkDatabase.getHomeworkHistory(new HomeworkDatabase.HistoryCallback() {

            @Override
            public void onLoaded(java.util.List<HomeworkEntry> historyList) {

                if (getActivity() == null) {
                    return;
                }

                getActivity().runOnUiThread(() -> {

                    progressBar.setVisibility(View.GONE);

                    if (historyList.isEmpty()) {
                        tvEmpty.setVisibility(View.VISIBLE);
                        return;
                    }

                    HistoryAdapter adapter = new HistoryAdapter(historyList, entry -> {

                        new AlertDialog.Builder(requireContext())
                                .setTitle("📚 " + entry.getSubject())
                                .setMessage(entry.getAnswer())
                                .setPositiveButton("סגור", null)
                                .setNegativeButton("מחק", (dialog, which) -> {
                                    onFailure(entry.getQuestion());
                                })
                                .show();
                    });

                    recyclerView.setAdapter(adapter);
                });
            }

            @Override
            public void onFailure(String message) {

                if (getActivity() == null) {
                    return;
                }

                getActivity().runOnUiThread(() -> {

                    progressBar.setVisibility(View.GONE);

                    Toast.makeText(
                            requireContext(),
                            "שגיאה: " + message,
                            Toast.LENGTH_SHORT
                    ).show();
                });
            }
        });
    }
}
