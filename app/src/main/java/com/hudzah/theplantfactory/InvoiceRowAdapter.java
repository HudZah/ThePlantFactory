package com.hudzah.theplantfactory;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class InvoiceRowAdapter extends RecyclerView.Adapter<InvoiceRowAdapter.ViewHolder>  {

    private static final String TAG = "InvoiceRowAdapter";
    private Context context;
    private List<Object> items = new ArrayList<>();
    private List<Object> rates = new ArrayList<>();
    private List<Object> quantities = new ArrayList<>();
    static Integer subtotal;

    public InvoiceRowAdapter(Context context, List<Object> items, List<Object> rates, List<Object> quantities) {
        this.context = context;
        this.items = items;
        this.rates = rates;
        this.quantities = quantities;
        subtotal = 0;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_invoice_row, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: Binded");

        if(position % 2 == 0){
            holder.layout1.setBackgroundResource(R.drawable.bg_white_border);
            holder.layout2.setBackgroundResource(R.drawable.bg_white_border);
            holder.layout3.setBackgroundResource(R.drawable.bg_white_border);
            holder.layout4.setBackgroundResource(R.drawable.bg_white_border);
        }
        else{

            holder.layout1.setBackgroundResource(R.drawable.bg_grey_border);
            holder.layout2.setBackgroundResource(R.drawable.bg_grey_border);
            holder.layout3.setBackgroundResource(R.drawable.bg_grey_border);
            holder.layout4.setBackgroundResource(R.drawable.bg_grey_border);
        }

        holder.itemTextView.setText((CharSequence) items.get(position));
        holder.rateTextView.setText("LKR " + (CharSequence) rates.get(position));
        holder.quantityTextView.setText((CharSequence) quantities.get(position));
        holder.priceTextView.setText("LKR " +  String.valueOf(Integer.parseInt(String.valueOf(quantities.get(position))) * Integer.parseInt(String.valueOf(rates.get(position)))));
        subtotal += Integer.parseInt(String.valueOf(quantities.get(position))) * Integer.parseInt(String.valueOf(rates.get(position)));
        Log.d(TAG, "onBindViewHolder: Price" + subtotal);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView itemTextView;
        TextView rateTextView;
        TextView quantityTextView;
        TextView priceTextView;
        RelativeLayout layout1;
        RelativeLayout layout2;
        RelativeLayout layout3;
        RelativeLayout layout4;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemTextView = itemView.findViewById(R.id.itemTextView);
            rateTextView = itemView.findViewById(R.id.rateTextView);
            quantityTextView = itemView.findViewById(R.id.quantityTextView);
            priceTextView = itemView.findViewById(R.id.priceTextView);
            layout1 = itemView.findViewById(R.id.layout1);
            layout2 = itemView.findViewById(R.id.layout2);
            layout3 = itemView.findViewById(R.id.layout3);
            layout4 = itemView.findViewById(R.id.layout4);
        }
    }
}


