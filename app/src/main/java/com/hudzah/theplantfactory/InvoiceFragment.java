package com.hudzah.theplantfactory;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class InvoiceFragment extends Fragment {

    private static final String TAG = "InvoiceFragment";
    String invoiceID;
    RecyclerView recyclerView;
    InvoiceRowAdapter adapter;
    List<Object> itemsList = new ArrayList<>();
    List<Object> ratesList;
    List<Object> quantitiesList;
    TextView dateTextView;
    TextView customerTextView;
    TextView typeTextView;
    TextView subtotalTextView;
    TextView discountRateTextView;
    TextView discountTextView;
    TextView shippingTextView;
    TextView handlingTextView;
    TextView grandTotalTextView;
    Double discountValue;
    Button saveButton;
    ScrollView scrollView;
    View u;

    public InvoiceFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_invoice, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recyclerView);
        dateTextView = view.findViewById(R.id.dateTextView);
        customerTextView = view.findViewById(R.id.customerTextView);
        typeTextView = view.findViewById(R.id.typeTextView);
        subtotalTextView = view.findViewById(R.id.subtotalTextView);
        discountRateTextView = view.findViewById(R.id.discountRateTextView);
        discountTextView = view.findViewById(R.id.discountTextView);
        shippingTextView = view.findViewById(R.id.shippingTextView);
        handlingTextView = view.findViewById(R.id.handlingTextView);
        grandTotalTextView = view.findViewById(R.id.grandTotalTextView);
        saveButton = view.findViewById(R.id.saveButton);
        scrollView = view.findViewById(R.id.scrollview);
        u = view.findViewById(R.id.scrollview);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveImage();
            }
        });

        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);

        if(getArguments() != null) {
            InvoiceFragmentArgs args = InvoiceFragmentArgs.fromBundle(getArguments());
            invoiceID = args.getInvoiceID();
            Log.d(TAG, "onViewCreated: " + invoiceID);
        }

        getData();
        Log.d(TAG, "onViewCreated: List " + itemsList);


    }
    private void getData(){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Orders");
        query.whereEqualTo("invoiceID", invoiceID);
        Log.d(TAG, "getData: InvoiceID" + invoiceID);
        final LoadingDialog loadingDialog = new LoadingDialog(getActivity());
        loadingDialog.startLoadingDialog();

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null){
                    if(objects.size() > 0){
                        for(ParseObject object:objects){
                            itemsList = (object.getList("products"));
                            ratesList = object.getList("rates");
                            quantitiesList = object.getList("quantity");
                            customerTextView.setText("" + object.getString("customer"));
                            if(object.getString("type").equals("B")) typeTextView.setText("Type: BULK");
                            else typeTextView.setText("Type: RETAIL");
                            displayFormattedDate(object.getCreatedAt());
                            discountRateTextView.setText(String.valueOf(object.getNumber("discount")) + "%");
                            discountValue = Double.parseDouble(String.valueOf(object.getNumber("discount")))/100;
                            shippingTextView.setText("LKR " + String.valueOf(object.getNumber("shippingFees")));
                            handlingTextView.setText("LKR " + String.valueOf(object.getNumber("extraFees")));
                            grandTotalTextView.setText("LKR " + object.getNumber("total"));

                        }
                        loadingDialog.dismissDialog();
                        initRecyclerView();

                    }
                }
                else{
                    Log.d(TAG, "done: " + e.getMessage());
                }
            }
        });


    }

    private void initRecyclerView(){
        Log.d(TAG, "getData: ArrayList for items is" + itemsList);
        Log.d(TAG, "getData: ArrayList for prices is" + ratesList);
        Log.d(TAG, "getData: ArrayList for items is" + quantitiesList);

        adapter = new InvoiceRowAdapter(getContext(), itemsList, ratesList, quantitiesList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                subtotalTextView.setText("LKR " + InvoiceRowAdapter.subtotal);
                discountTextView.setText("LKR " + (int) ((InvoiceRowAdapter.subtotal) * Double.parseDouble(String.valueOf(discountValue))));

            }
        }, 300);

    }

    private void displayFormattedDate(Date date){
        SimpleDateFormat dateFormat= new SimpleDateFormat("dd/MM/yyyy");
        dateTextView.setText("Date: " + dateFormat.format(date));

    }

    private void saveImage(){
        // TODO: 7/12/2020 Invoice Generation Into Pdf 
    }



}
