package com.hudzah.theplantfactory;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {

    private static final String TAG = "MainFragment";
    private ArrayList<Boolean> completed = new ArrayList<>();
    private ArrayList<String> invoiceIDList = new ArrayList<>();
    private ArrayList<String> cusNames = new ArrayList<>();
    private ArrayList<String> dates = new ArrayList<>();
    private ArrayList<String> totals = new ArrayList<>();
    private TextView header;
    private Button viewOrdersButton;
    RecyclerView recyclerView;
    FloatingActionButton fabLeave;
    int max = 0;


    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerView);
        header = view.findViewById(R.id.header);
        header.setText("Welcome "+ ParseUser.getCurrentUser().getUsername());
        fabLeave = view.findViewById(R.id.fabLeave);
        viewOrdersButton = view.findViewById(R.id.viewOrdersButton);

        viewOrdersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = Navigation.findNavController(view);
                navController.navigate(R.id.ordersListFragment);
            }
        });

        fabLeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLogout();
            }
        });

        getLatestOrders();
        initRecyclerView();
    }

    private void getLatestOrders(){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Orders");
        query.orderByDescending("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null){
                    if(objects.size() > 0){
                        if(objects.size() > 5){
                            max = 5;
                        } else max = objects.size();

                        for (int i = 0; i < max; i++){
                            Log.d(TAG, "done: In here");
                            completed.add(objects.get(i).getBoolean("completed"));
                            invoiceIDList.add(objects.get(i).getString("invoiceID"));
                            cusNames.add(objects.get(i).getString("customer"));
                            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                            String dateString = formatter.format(objects.get(i).getCreatedAt());
                            dates.add(dateString);
                            Log.d(TAG, "done: " + dateString);
                            totals.add("LKR " + String.valueOf(objects.get(i).getNumber("total")));
                            Log.d(TAG, "initRecyclerView: " + invoiceIDList.get(0));
                        }
                    }
                }
                else{
                    Log.d(TAG, "done: " + e.getMessage());
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initRecyclerView(){
        Log.d(TAG, "initRecyclerView: Init");
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        HorizontalRecyclerViewAdapter adapter = new HorizontalRecyclerViewAdapter(getContext(), completed, invoiceIDList, cusNames, dates, totals);
        recyclerView.setAdapter(adapter);

    }

    private void userLogout(){
        ParseUser.logOut();
        Intent intent = new Intent(getContext(), LoginActivity.class);
        startActivity(intent);
    }
}
