package com.hostel_online.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;

public class StudentNotifications extends AppCompatActivity {
    private FirestoreRecyclerAdapter adapter;
    private RecyclerView recyclerView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_notifications);


        // Context context;
        RecyclerView recyclerView = findViewById(R.id.studentNoteList);
        //recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        Query query = firestore
                .collection("Notifications")
                //.whereEqualTo("hostelId","1yfhbdzcxTGnlChR4jWa")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(20);

        FirestoreRecyclerOptions<Notificat> options = new FirestoreRecyclerOptions.Builder<Notificat>()
                .setQuery(query, Notificat.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<Notificat, NotificatViewHolder>(options) {
            @NonNull
            @Override
            public NotificatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_note_row, parent, false);
                return new NotificatViewHolder(view);
            }

            @SuppressLint("SetTextI18n")
            @Override
            protected void onBindViewHolder(@NonNull NotificatViewHolder holder, int position, @NonNull Notificat model) {
                holder.textViewTitle.setText(model.getTitle());
                holder.textViewDate.setText(model.getDate());
                holder.textViewHostelId.setText(model.getHostelId());
                holder.textViewMessage.setText(model.getMessage());

            }

        };

        recyclerView.setAdapter(adapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    public void newNotification(View view) {
NotificationDialog notificationDialog=new NotificationDialog();
notificationDialog.show(getSupportFragmentManager(),"Notification");

    }

    public void createNotificattion(View view) {
    }


    static class NotificatViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewTitle;
        public TextView textViewHostelId;
        public TextView textViewDate;
        public TextView textViewMessage;

        public NotificatViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = (TextView) itemView.findViewById(R.id.title);
            textViewDate = (TextView) itemView.findViewById(R.id.tdate);
            textViewHostelId = (TextView) itemView.findViewById(R.id.hostelId);
            textViewMessage = (TextView) itemView.findViewById(R.id.tmessage);
        }

    }

}




