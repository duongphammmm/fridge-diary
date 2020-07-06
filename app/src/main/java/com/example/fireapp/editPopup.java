package com.example.fireapp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class editPopup extends AppCompatDialogFragment {

    private ProgressBar editItemProgressBar;
    private EditText editAmount;
    private EditPopupListener listener;

    private Context context;

    public String name;
    private String user_id;
    private String fridge_id = "default";

    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();

        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();

        context = getActivity();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.popup_edit_item, null);

        builder.setView(view)
                .setTitle(name)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String new_amount = editAmount.getText().toString();

                        if(!TextUtils.isEmpty(new_amount)) {

                            editItemProgressBar.setVisibility(View.VISIBLE);

                            firebaseFirestore.collection("Fridges").document(fridge_id)
                                    .collection(user_id).document(name)
                                    .update("amount", new_amount).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()) {
                                        Toast.makeText(context, "Item is edited", Toast.LENGTH_LONG).show();

                                    } else {
                                        String error = task.getException().getMessage();
                                        Toast.makeText(context, "FIRESTORE_error : " + error, Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                            editItemProgressBar.setVisibility(View.INVISIBLE);

                        } else {
                            Toast.makeText(getContext(), "Incomplete field(s)", Toast.LENGTH_SHORT).show();
                        }

                        listener.edit(new_amount);
                    }
                });

        editItemProgressBar = view.findViewById(R.id.edit_item_progress_bar);
        editAmount = view.findViewById(R.id.edit_amount_field);

        editItemProgressBar.setVisibility(View.INVISIBLE);

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (EditPopupListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement EditPopupListener");
        }
    }

    public interface EditPopupListener {
        void edit(String amount);
    }

}
