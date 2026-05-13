package com.example.homeworkhelper;

import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ApiClient {

    private static final String TAG = "ApiClient";
    private static final String API_KEY = "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";
    private static final String API_URL = "https://api.groq.com/openai/v1/chat/completions";
    private static final String MODEL = "meta-llama/llama-4-scout-17b-16e-instruct";

    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
            .build();

    public interface Callback {
        void onSuccess(String response);
        void onError(String error);
    }
    public void analyzeHomework(byte[] imageBytes, String mimeType, Callback callback) {
        String base64Image = Base64.encodeToString(imageBytes, Base64.NO_WRAP);
        String dataUrl = "data:" + mimeType + ";base64," + base64Image;

        new Thread(() -> {
            try {
                JSONObject imageUrlObj = new JSONObject();
                imageUrlObj.put("url", dataUrl);

                JSONObject imageContent = new JSONObject();
                imageContent.put("type", "image_url");
                imageContent.put("image_url", imageUrlObj);
                JSONObject textContent = new JSONObject();
                textContent.put("type", "text");
                textContent.put("text",
                        "אתה מורה פרטי מומחה. ענה תמיד בעברית בלבד. " +
                                "1. זהה את המקצוע (מתמטיקה, מדעים, היסטוריה, עברית, אנגלית וכו'). " +
                                "2. קרא את השאלה בעיון. " +
                                "3. תן הסבר ברור עם פתרון שלב אחר שלב. " +
                                "4. התחל את התשובה שלך עם: 'מקצוע: [שם המקצוע]' בשורה הראשונה. " +
                                "היה מעודד וחינוכי — עזור לתלמיד להבין, לא רק להעתיק."
                );
                JSONArray contentArray = new JSONArray();
                contentArray.put(textContent);
                contentArray.put(imageContent);
                JSONObject message = new JSONObject();
                message.put("role", "user");
                message.put("content", contentArray);

                JSONArray messagesArray = new JSONArray();
                messagesArray.put(message);
                JSONObject requestBody = new JSONObject();
                requestBody.put("model", MODEL);
                requestBody.put("max_tokens", 1500);
                requestBody.put("messages", messagesArray);

                // HTTP request
                RequestBody body = RequestBody.create(
                        requestBody.toString(),
                        MediaType.parse("application/json")
                );

                Request request = new Request.Builder()
                        .url(API_URL)
                        .addHeader("Authorization", "Bearer " + API_KEY) // Groq uses Bearer token
                        .addHeader("Content-Type", "application/json")
                        .post(body)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        callback.onError("API error: " + response.code() + " " + response.message());
                        return;
                    }

                    String responseString = response.body().string();
                    JSONObject json = new JSONObject(responseString);
                    String text = json
                            .getJSONArray("choices")
                            .getJSONObject(0)
                            .getJSONObject("message")
                            .getString("content");

                    callback.onSuccess(text);
                }

            } catch (Exception e) {
                Log.e(TAG, "Error calling Groq API", e);
                callback.onError("Error: " + e.getMessage());
            }
        }).start();
    }
}
