package com.example.fireapp;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
public class HomeFragment extends Fragment {

    private RecyclerView itemListView;
    private List<Item> itemList;
    public static FragmentManager fm;

    private String user_id;
    private String fridge_id = "default";

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    private static ItemRecyclerAdapter itemRecyclerAdapter;

    private DocumentSnapshot lastVisible;
    private Boolean isFirstPageFirstLoad = true;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        fm = getActivity().getSupportFragmentManager();
        itemList = new ArrayList<>();
        itemListView = view.findViewById(R.id.item_list_view);

        itemRecyclerAdapter = new ItemRecyclerAdapter(itemList);
        itemListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        itemListView.setAdapter(itemRecyclerAdapter);

        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();

        firebaseFirestore = FirebaseFirestore.getInstance();

        itemListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                Boolean reachedBottom = !recyclerView.canScrollVertically(1);
                if(reachedBottom) {
                    loadMoreItem();

                }

            }
        });

        Query firstQuery = firebaseFirestore.collection("Fridges").document(fridge_id)
                .collection(user_id).orderBy("expiry", Query.Direction.ASCENDING);

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

                            Item item = doc.getDocument().toObject(Item.class);

                            if(isFirstPageFirstLoad) {
                                itemList.add(item);

                            } else {
                                itemList.add(0, item);
                            }
                            itemRecyclerAdapter.notifyDataSetChanged();
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

        Query nextQuery = firebaseFirestore.collection("Fridges")
                .document(fridge_id).collection(user_id)
                .orderBy("expiry", Query.Direction.ASCENDING)
                .startAfter().limit(5);

        nextQuery.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(queryDocumentSnapshots != null) {
                    lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            Item item = doc.getDocument().toObject(Item.class);
                            itemList.add(item);
                            itemRecyclerAdapter.notifyDataSetChanged();

                        }

                    }
                }
            }
        });

    }

    public static void edit(String amount) {
        itemRecyclerAdapter.edit(amount);
    }
}
