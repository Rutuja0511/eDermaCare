package com.example.edermacarelatestt;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class HistoryFragment extends Fragment {

    private FirebaseFirestore db;
    private View rootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_history, container, false);
        db = FirebaseFirestore.getInstance();

        String userId = getArguments() != null ? getArguments().getString("user_id") : null;
        if (userId != null) {
            loadHistoryData(userId, requireContext());
        }

        return rootView;
    }

    private void loadHistoryData(String userId, Context context) {
        db.collection("patients")
                .document(userId)
                .collection("history")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        LinearLayout historyContainer = rootView.findViewById(R.id.history_container);
                        if (!task.getResult().isEmpty()) {
                            for (DocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                LinearLayout historyItemLayout = createHistoryItemLayout(context, document);
                                historyContainer.addView(historyItemLayout);
                            }
                            int historyCount = historyContainer.getChildCount();
                            Toast.makeText(context, "Loaded " + historyCount + " history records.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "No history records found.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        String errorMessage = "Failed to load history data: " + Objects.requireNonNull(task.getException()).getMessage();
                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to load history data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private LinearLayout createHistoryItemLayout(Context context, DocumentSnapshot document) {
        String result = document.getString("result");
        String date = document.getString("date");
        String time = document.getString("time");
        String imageURL = document.getString("imageURL");

        LayoutInflater inflater = LayoutInflater.from(context);
        LinearLayout historyItemLayout = (LinearLayout) inflater.inflate(R.layout.history_item_layout, null);

        ImageView imageView = historyItemLayout.findViewById(R.id.imageView);
        TextView dateTextView = historyItemLayout.findViewById(R.id.date);
        TextView timeTextView = historyItemLayout.findViewById(R.id.time);
        TextView resultTextView = historyItemLayout.findViewById(R.id.result);

        dateTextView.setText(date);
        timeTextView.setText(time);
        resultTextView.setText(result);

        if (imageURL != null && !imageURL.isEmpty()) {
            Glide.with(context).load(imageURL).into(imageView);
        } else {
            imageView.setImageResource(R.drawable.upload_icon);
        }

        return historyItemLayout;
    }
}
