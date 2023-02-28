package com.example.projectestimoteapplication.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.projectestimoteapplication.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;


public class Historyfragment extends Fragment {
    DatabaseReference mRef;
    ListView myListview;
    ArrayList<String> myArrayList = new ArrayList<String>();

    ArrayAdapter<String> arrayAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_historyfragment, container, false);
        
        ArrayAdapter<String> myArrayAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,myArrayList);

        myListview = (ListView) v.findViewById(R.id.Historylistview);
        myListview.setAdapter(myArrayAdapter);
        mRef=FirebaseDatabase.getInstance().getReference("History");
        mRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String name = snapshot.getKey();
                String value = snapshot.getValue(String.class);

                myArrayList.add(name+value);
                Collections.sort(myArrayList);
                Collections.reverse(myArrayList);

                myArrayAdapter.notifyDataSetChanged();


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String name = snapshot.getKey();
                String value = snapshot.getValue(String.class);

                myArrayList.add(name+value);

                Collections.reverse(myArrayList);

                myArrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("bye", "onCancelled: ");

            }
        });
        // Inflate the layout for this fragment
        return v;

    }
}