package com.example.fireapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
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

public class NewItemActivity extends AppCompatActivity {

    private Toolbar newItemToolbar;
    private ProgressBar newItemProgressBar;
    private EditText itemName;
    private EditText itemAmount;
    private EditText itemExpiry;
    private Date expiryDate;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private Button addItemBtn;

    private String user_id;
    private String fridge_id = "default";

    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_item);

        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();

        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();

        newItemToolbar = findViewById(R.id.new_item_toolbar);
        setSupportActionBar(newItemToolbar);
        getSupportActionBar().setTitle("Add New Item");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        newItemProgressBar = findViewById(R.id.new_item_progress_bar);
        itemName = findViewById(R.id.item_name_field);
        itemAmount = findViewById(R.id.item_amount_field);
        itemExpiry = findViewById(R.id.item_expiry_field);

        itemExpiry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        NewItemActivity.this,
                        dateSetListener, year, month, day);
                dialog.show();
            }
        });

        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);
                expiryDate = calendar.getTime();
                month = month + 1;
                String date = day + "/" + month + "/" + year;
                itemExpiry.setText(date);
            }
        };

        addItemBtn = findViewById(R.id.new_item_add_btn);
        addItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String item_name = itemName.getText().toString();
                String item_amount = itemAmount.getText().toString();

                if(!TextUtils.isEmpty(item_name) && !TextUtils.isEmpty(item_amount) && expiryDate != null) {

                    newItemProgressBar.setVisibility(View.VISIBLE);
                    Map<String, Object> itemMap = new HashMap<>();
                    itemMap.put("name", item_name);
                    itemMap.put("amount", item_amount);
                    itemMap.put("expiry", expiryDate);
                    itemMap.put("user_id", user_id);
                    itemMap.put("timestamp", FieldValue.serverTimestamp());

                    firebaseFirestore.collection("Fridges").document(fridge_id)
                            .collection(user_id).document(item_name)
                            .set(itemMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()) {
                                Toast.makeText(NewItemActivity.this, "New item is added", Toast.LENGTH_LONG).show();
                                sendToMain();

                            } else {
                                String error = task.getException().getMessage();
                                Toast.makeText(NewItemActivity.this, "FIRESTORE_error : " + error, Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                    newItemProgressBar.setVisibility(View.INVISIBLE);

                } else {
                    Toast.makeText(NewItemActivity.this, "Incomplete field(s)", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void sendToMain() {
        Intent mainIntent = new Intent(NewItemActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
}
