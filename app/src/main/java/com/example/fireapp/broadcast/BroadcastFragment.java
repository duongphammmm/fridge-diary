package com.example.fireapp.broadcast;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fireapp.R;
import com.example.fireapp.item.Item;
import com.example.fireapp.item.ItemRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;


/**
 * A simple {@link Fragment} subclass.
 */
public class BroadcastFragment extends Fragment {

    private RecyclerView broadcastListView;
    private List<Broadcast> broadcastList;

    private String user_id;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    private BroadcastRecyclerAdapter broadcastRecyclerAdapter;

    private DocumentSnapshot lastVisible;
    private Boolean isFirstPageFirstLoad = true;

    public BroadcastFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_broadcast, container, false);

        broadcastList = new ArrayList<>();
        broadcastListView = view.findViewById(R.id.broadcast_list_view);

        broadcastRecyclerAdapter = new BroadcastRecyclerAdapter(broadcastList);
        broadcastListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        broadcastListView.setAdapter(broadcastRecyclerAdapter);

        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();

        firebaseFirestore = FirebaseFirestore.getInstance();

        broadcastListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                Boolean reachedBottom = !recyclerView.canScrollVertically(1);
                if(reachedBottom) {
                    loadMoreItem();

                }

            }
        });

        Query firstQuery = firebaseFirestore.collection("Users").document(user_id)
                .collection("Broadcasts");

        firstQuery.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if(queryDocumentSnapshots != null) {

                    if(isFirstPageFirstLoad) {
                        if(queryDocumentSnapshots.size() > 0) {
                            lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                        }
                    }
                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            Broadcast broadcast = doc.getDocument().toObject(Broadcast.class);

                            if(isFirstPageFirstLoad) {
                                broadcastList.add(broadcast);

                            } else {
                                broadcastList.add(0, broadcast);
                            }
                            broadcastRecyclerAdapter.notifyDataSetChanged();
                        }
                    }
                    isFirstPageFirstLoad = false;
                }
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    public void loadMoreItem() {

        Query nextQuery = firebaseFirestore.collection("Users")
                .document(user_id).collection("Broadcasts")
                .startAfter().limit(5);

        nextQuery.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(queryDocumentSnapshots != null) {
                    lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            Broadcast broadcast = doc.getDocument().toObject(Broadcast.class);
                            broadcastList.add(broadcast);
                            broadcastRecyclerAdapter.notifyDataSetChanged();

                        }

                    }
                }
            }
        });

    }

}
