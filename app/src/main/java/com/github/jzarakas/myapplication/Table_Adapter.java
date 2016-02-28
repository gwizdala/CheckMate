/**
 * Created by badda_000 on 2/27/2016.
 */
package com.github.jzarakas.myapplication;

import com.reimaginebanking.api.java.NessieClient;
import com.reimaginebanking.api.java.NessieException;
import com.reimaginebanking.api.java.NessieResultsListener;
import com.reimaginebanking.api.java.models.Customer;
import com.reimaginebanking.api.java.models.Bill;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.app.Activity;

import java.util.ArrayList;


//import capital one bills
public class Table_Adapter extends ArrayAdapter<Bill> {
    private final Context context;
    private ArrayList<Bill> values;

    public Table_Adapter(Context context, ArrayList<Bill> newValues) {
        super(context, -1, newValues);
        this.context = context;
        this.values = newValues;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View tableItem = inflater.inflate(R.layout.table_item, parent, false);
        TextView textView_account = (TextView) tableItem.findViewById(R.id.tv_account);
        TextView textView_amount = (TextView) tableItem.findViewById(R.id.tv_amount);
        TextView textView_merchant = (TextView) tableItem.findViewById(R.id.tv_merchant);
        textView_account.setText(this.values.get(position).getPayee());
        textView_amount.setText(String.format("$%.2f", this.values.get(position).getPayment_amount()));
        textView_merchant.setText(this.values.get(position).getNickname());
        // change the icon for Windows and iPhone


        return tableItem;
    }
}

        /*
        nessieClient.getBills("56c66be6a73e492741507e36", new NessieResultsListener() {
@Override
public void onSuccess(Object result, NessieException e) {
        if(e == null){
        ArrayList<Bill> bills = (ArrayList<Bill>) result;
        System.out.println("BILLS TEST");
        System.out.println(bills.size());
        for(Bill bill:bills){
        System.out.println(bill.toString());
        }
        } else {
        System.out.println(e.toString());
        }
        }
        });

                        */