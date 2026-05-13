package com.example.homeworkhelper;

import com.google.firebase.Timestamp;

// מחלקה המייצגת רשומה אחת בהיסטוריה
public class HomeworkEntry {

    private String id;
    private String subject;
    private String question;
    private String answer;
    private Timestamp timestamp;

    public HomeworkEntry(String id, String subject, String question,
                         String answer, Timestamp timestamp) {
        this.id = id;
        this.subject = subject;
        this.question = question;
        this.answer = answer;
        this.timestamp = timestamp;
    }

    // Getters
    public String getId()        { return id; }
    public String getSubject()   { return subject; }
    public String getQuestion()  { return question; }
    public String getAnswer()    { return answer; }
    public Timestamp getTimestamp() { return timestamp; }
}
