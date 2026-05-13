package com.example.homeworkhelper;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirestoreManager {

    // שם הקולקציה במסד הנתונים
    private static final String COLLECTION = "homework_history";

    private final FirebaseFirestore db;

    public FirestoreManager() {
        db = FirebaseFirestore.getInstance();
    }

    // ─── ממשקים לקריאה חזרה ───────────────────────────────────────────

    public interface SaveCallback {
        void onSuccess();
        void onError(String error);
    }

    public interface LoadCallback {
        void onSuccess(List<HomeworkEntry> entries);
        void onError(String error);
    }

    // ─── שמירת שאלה ותשובה ────────────────────────────────────────────

    /**
     * שומר רשומה חדשה ב-Firestore
     * @param subject  המקצוע שזוהה (למשל "מתמטיקה")
     * @param question תיאור השאלה
     * @param answer   תשובת ה-AI
     */
    public void saveEntry(String subject, String question, String answer, SaveCallback callback) {
        Map<String, Object> entry = new HashMap<>();
        entry.put("subject", subject);
        entry.put("question", question);
        entry.put("answer", answer);
        entry.put("timestamp", Timestamp.now()); // זמן השמירה

        db.collection(COLLECTION)
                .add(entry)
                .addOnSuccessListener(ref -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // ─── טעינת כל ההיסטוריה ──────────────────────────────────────────

    /**
     * מחזיר את כל הרשומות מסודרות מהחדש לישן
     */
    public void loadHistory(LoadCallback callback) {
        db.collection(COLLECTION)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<HomeworkEntry> entries = new ArrayList<>();
                    snapshot.forEach(doc -> {
                        HomeworkEntry entry = new HomeworkEntry(
                                doc.getId(),
                                doc.getString("subject"),
                                doc.getString("question"),
                                doc.getString("answer"),
                                doc.getTimestamp("timestamp")
                        );
                        entries.add(entry);
                    });
                    callback.onSuccess(entries);
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // ─── מחיקת רשומה ─────────────────────────────────────────────────

    public void deleteEntry(String documentId, SaveCallback callback) {
        db.collection(COLLECTION)
                .document(documentId)
                .delete()
                .addOnSuccessListener(v -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }
}
