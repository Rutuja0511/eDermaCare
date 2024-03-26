package com.example.edermacarelatestt;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DermatologistVerification {

    private static final String TAG = "DermatologistVerification";

    public static void performVerification(String registrationNo, String yearOfRegistration, String stateMedicalCouncil, VerificationCallback callback) {
        new APICallTask(callback).execute(registrationNo, yearOfRegistration, stateMedicalCouncil);
    }

    public interface VerificationCallback {
        void onVerificationComplete(JSONObject result);
        void onVerificationFailed(String errorMessage);
    }

    private static class APICallTask extends AsyncTask<String, Void, JSONObject> {
        private final VerificationCallback callback;

        APICallTask(VerificationCallback callback) {
            this.callback = callback;
        }

        protected JSONObject doInBackground(String... params) {
            HttpURLConnection connection = null;
            try {
                String registrationNo = params[0];
                String yearOfRegistration = params[1];
                String stateMedicalCouncil = params[2];

                URL url = new URL("https://eve.idfy.com/v3/tasks/sync/verify_with_source/nmc_doctor");
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("api-key", "7df0bcae-0a25-44b4-8f06-edbaf0f373d6");
                connection.setDoOutput(true);

                String payload = "{\n" +
                        "    \"task_id\": \"74f4c926-250c-43ca-9c53-453e87ceacd1\",\n" +
                        "    \"group_id\": \"8e16424a-58fc-4ba4-ab20-5bc8e7c3c41e\",\n" +
                        "    \"data\": {\n" +
                        "        \"registration_no\": \"" + registrationNo + "\",\n" +
                        "        \"year_of_registration\": \"" + yearOfRegistration + "\",\n" +
                        "        \"council_name\": \"" + stateMedicalCouncil + "\"\n" +
                        "    }\n" +
                        "}";

                try (DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream())) {
                    outputStream.writeBytes(payload);
                    outputStream.flush();
                }

                int responseCode = connection.getResponseCode();
                Log.d(TAG, "Response Code: " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = in.readLine()) != null) {
                            response.append(line);
                        }
                        return new JSONObject(response.toString());
                    }
                } else {
                    Log.e(TAG, "HTTP error code: " + responseCode);
                    return null;
                }
            } catch (Exception e) {
                Log.e(TAG, "Exception occurred: " + e.getMessage());
                return null;
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }

        protected void onPostExecute(JSONObject result) {
            if (result != null) {
                callback.onVerificationComplete(result);
            } else {
                callback.onVerificationFailed("Verification failed");
            }
        }
    }
}
