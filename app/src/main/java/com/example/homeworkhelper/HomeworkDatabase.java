package com.example.homeworkhelper;

import android.util.Log;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class HomeworkDatabase {

    private static final String TAG = "HomeworkDatabase";
    private static final String HISTORY_COLLECTION = "homework_history";

    private FirebaseFirestore firestore;

    public HomeworkDatabase() {
        firestore = FirebaseFirestore.getInstance();
    }

    public interface DatabaseCallback {
        void onSuccess();
        void onFailure(String message);
    }

    public interface HistoryCallback {
        void onLoaded(List<HomeworkEntry> historyList);
        void onFailure(String message);
    }

    public void saveHomework(String subject,
                             String question,
                             String answer,
                             DatabaseCallback callback) {

        if (question == null || question.trim().isEmpty()) {
            callback.onFailure("Question is empty");
            return;
        }

        HomeworkEntry entry = new HomeworkEntry(
                null,
                subject,
                question,
                answer,
                Timestamp.now()
        );

        firestore.collection(HISTORY_COLLECTION)
                .add(entry)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Homework saved successfully");
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to save homework", e);
                    callback.onFailure(e.getMessage());
                });
    }

    public void getHomeworkHistory(HistoryCallback callback) {

        firestore.collection(HISTORY_COLLECTION)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    List<HomeworkEntry> history = new ArrayList<>();

                    for (var doc : querySnapshot.getDocuments()) {

                        HomeworkEntry item = new HomeworkEntry(
                                doc.getId(),
                                doc.getString("subject"),
                                doc.getString("question"),
                                doc.getString("answer"),
                                doc.getTimestamp("timestamp")
                        );

                        history.add(item);
                    }

                    Log.d(TAG, "Loaded " + history.size() + " homework entries");
                    callback.onLoaded(history);

                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading history", e);
                    callback.onFailure(e.getMessage());
                });
    }

    public void removeHomework(String documentId,
                               DatabaseCallback callback) {

        firestore.collection(HISTORY_COLLECTION)
                .document(documentId)
                .delete()
                .addOnSuccessListener(unused -> {
                    Log.d(TAG, "Homework deleted");
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Delete failed", e);
                    callback.onFailure(e.getMessage());
                });
    }
}
