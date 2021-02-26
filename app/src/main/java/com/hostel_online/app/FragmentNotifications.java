package com.hostel_online.app;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class FragmentNotifications extends Fragment {


    private FirestoreRecyclerAdapter adapter;
    private RecyclerView recyclerView;


    public FragmentNotifications() {
        // Required empty public constructor
    }

    private HostelOnlineUser hostelOnlineUser;

    public FragmentNotifications(HostelOnlineUser hostelOnlineUser) {
        this.hostelOnlineUser = hostelOnlineUser;
    }

    public static user_home newInstance(HostelOnlineUser hostelOnlineUser) {
        user_home fragment = new user_home(hostelOnlineUser);
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            hostelOnlineUser = getArguments().getParcelable("HostelOnlineUser");

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragment = inflater.inflate(R.layout.fragment_notifications, container, false);
        ImageView userProfileImage = fragment.findViewById(R.id.user_profile_image);
        String url = hostelOnlineUser.getUserPhotoUrl();
        if (url != null)
            GlideApp.with(FragmentNotifications.this).load(url).into(userProfileImage);

        RecyclerView recyclerView = fragment.findViewById(R.id.studentNoteList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        Query query = firestore
                .collection("Notifications")
                //.whereEqualTo("hostelId","1yfhbdzcxTGnlChR4jWa")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(20);

        FirestoreRecyclerOptions<Notificat> options = new FirestoreRecyclerOptions.Builder<Notificat>()
                .setQuery(query, Notificat.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<Notificat, FragmentNotificationsViewHolder>(options) {
            @NonNull
            @Override
            public FragmentNotificationsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_note_row, parent, false);
                return new FragmentNotificationsViewHolder(view);
            }

            @SuppressLint("SetTextI18n")
            @Override
            protected void onBindViewHolder(@NonNull FragmentNotificationsViewHolder holder, int position, @NonNull Notificat model) {
                holder.textViewTitle.setText(model.getTitle());
                holder.textViewDate.setText(model.getDate());
                holder.textViewHostelId.setText(model.getHostelId());
                holder.textViewMessage.setText(model.getMessage());

            }

        };

        recyclerView.setAdapter(adapter);
        return fragment; // Inflate the layout for this fragment

    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    public void newNotification(View view) {
        NotificationDialog notificationDialog=new NotificationDialog();
        //notificationDialog.show(getSupportFragmentManager(),"Notification");

    }


    static class FragmentNotificationsViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewTitle;
        public TextView textViewHostelId;
        public TextView textViewDate;
        public TextView textViewMessage;

        public FragmentNotificationsViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = (TextView) itemView.findViewById(R.id.title);
            textViewDate = (TextView) itemView.findViewById(R.id.tdate);
            textViewHostelId = (TextView) itemView.findViewById(R.id.hostelId);
            textViewMessage = (TextView) itemView.findViewById(R.id.tmessage);
        }

    }


}