package com.example.fireapp.broadcast;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fireapp.HomeFragment;
import com.example.fireapp.R;
import com.example.fireapp.item.Item;
import com.example.fireapp.item.ItemRecyclerAdapter;
import com.example.fireapp.item.editPopup;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.List;

public class BroadcastRecyclerAdapter extends RecyclerView.Adapter<BroadcastRecyclerAdapter.ViewHolder> {

    public List<Broadcast> broadcastList;
    public Context context;
    public ViewHolder viewHolder;

    private FirebaseFirestore firebaseFirestore;

    public BroadcastRecyclerAdapter(List<Broadcast> broadcastList) {

        this.broadcastList = broadcastList;
    }

    @Override
    public BroadcastRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, final int i) {

        firebaseFirestore = FirebaseFirestore.getInstance();
        context = viewGroup.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.broadcast_list, viewGroup, false);
        return new BroadcastRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final BroadcastRecyclerAdapter.ViewHolder viewHolder, final int i) {

        viewHolder.setIsRecyclable(false);

        this.viewHolder = viewHolder;

        final String title_data = broadcastList.get(i).getTitle();
        viewHolder.setTitle(title_data);

        String text_data = broadcastList.get(i).getText();
        viewHolder.setText(text_data);

    }

    @Override
    public int getItemCount() {
        return broadcastList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private View mView;
        private TextView titleView;
        private TextView textView;

        public ViewHolder(@NonNull View broadcastView) {
            super(broadcastView);
            mView = broadcastView;
        }

        public void setTitle(String title) {
            titleView = mView.findViewById(R.id.broadcast_title);
            titleView.setText(title);
        }

        public void setText(String text) {
            textView = mView.findViewById(R.id.broadcast_text);
            textView.setText(text);
        }

    }

}
