package com.hudzah.theplantfactory;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import static android.content.Context.INPUT_METHOD_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 */
public class NewOrderFragment extends Fragment {

    Button bulkButton;
    Button retailButton;
    ArrayList<String> productsList = new ArrayList<>();
    ArrayList<String> itemsList = new ArrayList<>();
    ArrayList<String> quanList = new ArrayList<>();
    ArrayList<String> PRODUCTS_LIST = new ArrayList<>();
    ArrayList<String> tempListKeys = new ArrayList<>();
    ArrayList<String> tempListEntries = new ArrayList<>();
    private static final String TAG = "NewOrderFragment";
    LinearLayout layout;
    AutoCompleteTextView productEditText;
    EditText quantityEditText;
    Button addProductButton;
    ArrayAdapter<String> productsAdapter;
    String type;
    ArrayList<String> retailPricesList = new ArrayList<>();
    ArrayList<String> bulkPricesList = new ArrayList<>();
    ArrayList<String> customersList = new ArrayList<>();
    ArrayList<String> ratesList = new ArrayList<>();
    Button nextButton;
    EditText discountEditText;
    EditText shippingEditText;
    EditText handlingEditText;
    CardView cardView;
    TextView cardTextView;
    int count = 0;
    AutoCompleteTextView customerTextView;
    Double grossTotal = 0.0;
    TextView totalTextView;
    Button saveButton;
    String invoiceID = "D";
    TextView header;
    ArrayAdapter<String> customerNamesAdapter;
    Double netTotal;
    int clickBlockerCount;
    NavController navController;
    NewOrderFragmentDirections.ActionNewOrderFragmentToInvoiceFragment action;


    public NewOrderFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_order, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        bulkButton = (Button) view.findViewById(R.id.bulkButton);
        retailButton = (Button) view.findViewById(R.id.retailButton);
        addProductButton = (Button) view.findViewById(R.id.addProductButton);
        layout = (LinearLayout) view.findViewById(R.id.layout);
        productEditText = (AutoCompleteTextView) view.findViewById(R.id.productEditText);
        quantityEditText = (EditText) view.findViewById(R.id.quantityEditText);
        nextButton = (Button) view.findViewById(R.id.nextButton);
        discountEditText = (EditText) view.findViewById(R.id.discountEditText);
        shippingEditText = (EditText) view.findViewById(R.id.shippingEditText);
        handlingEditText = (EditText) view.findViewById(R.id.handlingEditText);
        cardView = (CardView) view.findViewById(R.id.cardView);
        cardTextView = (TextView) view.findViewById(R.id.cardTextView);
        customerTextView = (AutoCompleteTextView) view.findViewById(R.id.customerTextView);
        totalTextView = (TextView) view.findViewById(R.id.totalTextView);
        saveButton = (Button) view.findViewById(R.id.saveButton);
        header = (TextView) view.findViewById(R.id.header);

        invoiceGeneration();


        bulkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = "B";
                switchButtons("B");
            }
        });
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(v);
            }
        });

        retailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = "R";
                switchButtons("R");
            }
        });
        addProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    addProduct();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToParse();
            }
        });


        getProducts();
        getRates();
        getCustomers();

        productsAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, productsList);
        productEditText.setAdapter(productsAdapter);

        customerNamesAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, customersList);
        customerTextView.setAdapter(customerNamesAdapter);


    }

    private void switchButtons(String buttonClicked){
        if(buttonClicked.equals("B")){
            bulkButton.setBackgroundResource(R.drawable.half_rounded_button_left_green);
            bulkButton.setTextColor(getResources().getColor(R.color.white));
            retailButton.setTextColor(getResources().getColor(R.color.black));
            retailButton.setBackgroundResource(R.drawable.half_rounded_button_right);
        }
        else if(buttonClicked.equals("R")){
            retailButton.setBackgroundResource(R.drawable.half_rounded_button_right_green);
            retailButton.setTextColor(getResources().getColor(R.color.white));
            bulkButton.setTextColor(getResources().getColor(R.color.black));
            bulkButton.setBackgroundResource(R.drawable.half_rounded_button_left);
        }
    }

    private void getProducts(){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Products");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null){
                    if(objects.size() > 0){
                        for(ParseObject object : objects){
                            productsList.add(object.getString("name"));
                            PRODUCTS_LIST.add(object.getString("name"));
                        }
                    }
                }
                else{
                    Log.d(TAG, "done: " + e.getMessage());
                }
            }
        });
    }

    private void getCustomers(){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Customers");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null){
                    if(objects.size() > 0){
                        for (ParseObject object : objects){
                            customersList.add(object.getString("customerName"));
                        }
                    }
                }
            }
        });
    }

    private void getRates(){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Products");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null){
                    if(objects.size() > 0){
                        for(ParseObject object : objects){
                            retailPricesList.add(String.valueOf(object.getNumber("retailRate")));
                            bulkPricesList.add(String.valueOf(object.getNumber("bulkRate")));
                        }
                    }
                }
            }
        });
    }


    private void hideKeyboard(View view){
        if(view.getId() == R.id.layout){

            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
            if(getActivity().getCurrentFocus() != null) {
                inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
            }
        }
    }

    private void addProduct(){
        addProductButton.setEnabled(false);
        String productName = productEditText.getText().toString();
        String quantity = quantityEditText.getText().toString().trim();
        if(productName.equals("") || quantity.equals("") || !productsList.contains(productName)){
            Toast.makeText(getContext(), "Invalid product", Toast.LENGTH_SHORT).show();
            addProductButton.setEnabled(true);
            return;
        }

        if(type == null){
            Toast.makeText(getContext(), "Select Retail/Bulk before continuing", Toast.LENGTH_SHORT).show();
            addProductButton.setEnabled(true);
            return;
        }
        productEditText.getText().clear();
        quantityEditText.getText().clear();
        productsList.remove(productName);
        productsAdapter.remove(productName);

        quanList.add(quantity);
        itemsList.add(productName);
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                updateCardView();

            }
        }, 300);
        addProductButton.setEnabled(true);
    }

    private void next(){
        if(!verifyDetails()){
            return;
        }

        String text = "";
        Double discount = Double.parseDouble(discountEditText.getText().toString())/ 100 * grossTotal;
        text = text + "Discount " + discountEditText.getText().toString() + "% = (" + discount + ")\n\n";
        Double tempValue = grossTotal;
        tempValue -= discount;
        text += "Shipping " + "LKR " + shippingEditText.getText().toString() + "\n\n";

        text += "Handling " + "LKR " + handlingEditText.getText().toString() + "\n\n";

        netTotal = tempValue + Double.parseDouble(handlingEditText.getText().toString()) + Double.parseDouble(shippingEditText.getText().toString());
        cardTextView.append(text);
        totalTextView.setText("Total: LKR " + Math.round((netTotal * 100)/100));
        saveButton.setVisibility(View.VISIBLE);


    }

    private void updateCardView(){
        String text = "";
        grossTotal = 0.0;
        ratesList.clear();
        cardTextView.setText("");
        for (int i = 0; i < itemsList.size(); i++) {
            String quantity = quanList.get(i);
            String name = itemsList.get(i);
            if(type.equals("R")){
                grossTotal = grossTotal + Double.parseDouble(quantity) * Double.parseDouble(retailPricesList.get(PRODUCTS_LIST.indexOf(name)));
                text = name.trim() + " " + quantity + "kg" + "\nTotal = " + Integer.parseInt(quantity) * Integer.parseInt(retailPricesList.get(PRODUCTS_LIST.indexOf(name))) + "\n\n";
                ratesList.add(retailPricesList.get(PRODUCTS_LIST.indexOf(name)));
                cardTextView.append(text);

            }
            else if(type.equals("B")){
                grossTotal = grossTotal + Double.parseDouble(quantity) * Double.parseDouble(bulkPricesList.get(PRODUCTS_LIST.indexOf(name)));
                Log.d(TAG, "updateCardView: Position is" + productsList.indexOf(name));
                text = name.trim() + " " + quantity + "kg" + "\n Total = " + Integer.parseInt(quantity) * Integer.parseInt(bulkPricesList.get(PRODUCTS_LIST.indexOf(name))) + "\n\n";
                ratesList.add(bulkPricesList.get(PRODUCTS_LIST.indexOf(name)));
                cardTextView.append(text);
            }


        }
    }

    private boolean verifyDetails(){
        if(shippingEditText.getText().toString().equals("") || discountEditText.getText().toString().equals("") || handlingEditText.getText().toString().equals("") || customerTextView.getText().toString().equals("")){
            Toast.makeText(getContext(), "Please fill in the missing fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        else{
            if(itemsList.size() <= 0){
                Toast.makeText(getContext(), "Please add a product first", Toast.LENGTH_SHORT).show();
                return false;
            }

            if(!type.equals("R") && !type.equals("B")){
                Toast.makeText(getContext(), "Please select Retail or Bulk", Toast.LENGTH_SHORT).show();
                return false;
            }
            if(Integer.parseInt(discountEditText.getText().toString())> 100){
                Toast.makeText(getContext(), "Please enter a discount value less than 100", Toast.LENGTH_SHORT).show();
                return false;
            }
            if(!customersList.contains(customerTextView.getText().toString())){
                Toast.makeText(getContext(), "Please select a valid customer", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        return true;


    }

    private void invoiceGeneration(){
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        final LoadingDialog loadingDialog = new LoadingDialog(getActivity());
        loadingDialog.startLoadingDialog();

        String month = new SimpleDateFormat("MM", Locale.getDefault()).format(new Date());

        invoiceID += month;
        invoiceID += String.valueOf(calendar.get(Calendar.YEAR)).substring(2);
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Orders");
        query.countInBackground(new CountCallback() {
            @Override
            public void done(int count, ParseException e) {
                if(e == null){
                    loadingDialog.dismissDialog();
                    invoiceID += count;
                    Log.d(TAG, "done: count" + count);
                }
            }
        });

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                header.setText("InvoiceID: " + invoiceID);
            }
        }, 1000);

    }

    private void saveToParse(){
        hideKeyboard(getView());
        final LoadingDialog loadingDialog = new LoadingDialog(getActivity());
        loadingDialog.startLoadingDialog();
        ParseObject object = new ParseObject("Orders");
        object.put("invoiceID", invoiceID);
        object.put("type", type);
        object.put("customer", customerTextView.getText().toString());
        object.put("discount", Integer.parseInt(discountEditText.getText().toString()));
        object.put("products", itemsList);
        object.put("quantity", quanList);
        object.put("extraFees", Integer.parseInt(handlingEditText.getText().toString()));
        object.put("shippingFees", Integer.parseInt(shippingEditText.getText().toString()));
        object.put("total", netTotal);
        object.put("rates", ratesList);
        object.put("placedBy", ParseUser.getCurrentUser().getUsername());
        object.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null){
                    Toast.makeText(getContext(), "Order Saved!", Toast.LENGTH_SHORT).show();
                    loadingDialog.dismissDialog();
                    action = NewOrderFragmentDirections.actionNewOrderFragmentToInvoiceFragment(invoiceID);
                    action.setInvoiceID(invoiceID);
                    navController.navigate(action);
                }
                else{
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "done: Error" + e.getMessage());
                }
            }
        });
    }


}
