package com.example.fireapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;

public class ItemRecyclerAdapter extends RecyclerView.Adapter<ItemRecyclerAdapter.ViewHolder> {

    public List<Item> itemList;
    public Context context;
    public ViewHolder viewHolder;

    private int editIndex;
    private String fridge_id = "default";

    private FirebaseFirestore firebaseFirestore;

    public ItemRecyclerAdapter(List<Item> itemList) {

           this.itemList = itemList;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, final int i) {

        firebaseFirestore = FirebaseFirestore.getInstance();
        context = viewGroup.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {

        viewHolder.setIsRecyclable(false);

        this.viewHolder = viewHolder;

        final String name_data = itemList.get(i).getName();
        viewHolder.setName(name_data);

        String amount_data = itemList.get(i).getAmount();
        viewHolder.setAmount(amount_data);

        long expiry_data = itemList.get(i).getExpiry().getTime();
        String expiryString = DateFormat.format("dd/MM/yyyy", new Date(expiry_data)).toString();
        viewHolder.setExpiry(expiryString);

        final String user_id = itemList.get(i).getUser_id();

        long date_added_data = itemList.get(i).getTimestamp().getTime();
        String dateString = DateFormat.format("dd/MM/yyyy", new Date(date_added_data)).toString();
        viewHolder.setDateAdded(dateString);

        viewHolder.editView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editPopup popup = new editPopup();
                popup.name = name_data;
                popup.show(HomeFragment.fm, "Edit Popup");

            }
        });

        viewHolder.deleteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseFirestore.collection("Fridges").document(fridge_id)
                        .collection(user_id).document(name_data).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        itemList.remove(i);
                        Toast.makeText(context, "The item is removed", Toast.LENGTH_SHORT);

                    }
                });

            }
        });

    }
    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void edit(String amount) {
        viewHolder.setAmount(amount);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private View mView;
        private TextView nameView;
        private TextView amountView;
        private TextView expiryView;
        private TextView dateAddedView;
        private ImageView editView;
        private ImageView deleteView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            editView = mView.findViewById(R.id.edit_view);
            deleteView = mView.findViewById(R.id.delete_view);
        }

        public void setName(String name) {
            nameView = mView.findViewById(R.id.item_name);
            nameView.setText(name);
        }

        public void setAmount(String amount) {
            amountView = mView.findViewById(R.id.item_amount);
            amountView.setText("Amount: " + amount);
        }

        public void setExpiry(String expiry) {
            expiryView = mView.findViewById(R.id.item_expiry);
            expiryView.setText("Expiry date: " + expiry);
        }

        public void setDateAdded(String date) {
            dateAddedView = mView.findViewById(R.id.item_date_added);
            dateAddedView.setText("Date Added: " + date);
        }

    }
}
