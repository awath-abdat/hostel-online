package com.hostel_online.app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.awt.font.NumericShaper;
import java.util.HashMap;

import static android.content.ContentValues.TAG;

public class NotificationDialog extends AppCompatDialogFragment{
    private FirebaseFirestore firestore;
    EditText newMessage,newTitle,newDate,newhostelId;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_notification, null);

         newMessage = view.findViewById(R.id.inmessage);
         newTitle = view.findViewById(R.id.intitle);
         newDate = view.findViewById(R.id.indate);
         newhostelId = view.findViewById(R.id.inhostelid);
        newDate.setText(java.time.LocalDate.now().toString());
        newhostelId.setText("1yfhbdzcxTGnlChR4jWa");

        builder.setView(view).setTitle("NEW NOTIFICATION")
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setPositiveButton("DONE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                 createNotification();
            }

            private void createNotification() {

                HashMap<String, Object> notification = new HashMap<>();
                notification.put("date", java.time.LocalDate.now());
                notification.put("hostelId", newhostelId.getText().toString());
                notification.put("message", newMessage.getText().toString());
                notification.put("title", newTitle.getText().toString());
                db.collection("Notifications")
                        .add(notification).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());

                        //Toast.makeText(NotificationDialog.this, "Notification Sent", Toast.LENGTH_SHORT).show();

                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error adding document", e);
                            }
                        });

            }


        });



        return builder.create();
    }

}
