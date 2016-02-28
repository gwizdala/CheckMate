package com.github.jzarakas.myapplication;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import android.widget.TextView;
import android.widget.EditText;
import android.util.Log;

import com.reimaginebanking.api.java.Constants.BillStatus;
import com.reimaginebanking.api.java.NessieClient;
import com.reimaginebanking.api.java.NessieException;
import com.reimaginebanking.api.java.NessieResultsListener;
import com.reimaginebanking.api.java.models.Bill;
import com.reimaginebanking.api.java.models.Customer;
import com.reimaginebanking.api.java.models.RequestResponse;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class Processed extends Activity implements View.OnClickListener{
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_processed);

        mVisible = true;

        //TODO set values from processing

        findViewById(R.id.btn_calculate).setOnClickListener(this);
        findViewById(R.id.btn_confirm).setOnClickListener(this);

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            EditText total = (EditText)findViewById(R.id.et_total);
            total.setText(Float.toString(extras.getFloat("checkResult")));
        }
        EditText cardNumber = (EditText)findViewById(R.id.et_payment);
        EditText vendor = (EditText)findViewById(R.id.et_storeName);
        cardNumber.setText("1234");
        vendor.setText("Dolci E Caffe");
    }


    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_confirm: {
                //Toast.makeText(getApplicationContext(), "data Confirmed", Toast.LENGTH_SHORT).show();
                Log.i("CheckMate", "data confirmed");
                //post to nessi
                NessieClient nessieClient = NessieClient.getInstance();
                nessieClient.setAPIKey("ba7c2af9690af2027d775938b9e9ea83");

                EditText total = (EditText)findViewById(R.id.et_total);
                EditText cardNumber = (EditText)findViewById(R.id.et_payment);
                EditText vendor = (EditText)findViewById(R.id.et_storeName);
                String vendorName = vendor.getText().toString();//"DPDough";
                Calendar c = Calendar.getInstance();
                String currentDate = String.format("%d-%d-%d",
                        c.get(Calendar.YEAR),
                        c.get(Calendar.MONTH),
                        c.get(Calendar.DATE));
                int cardNum =Integer.parseInt(cardNumber.getText().toString());//6789;
                double cost = Float.parseFloat(total.getText().toString());//123.45;

                Bill bill = new Bill.Builder()
                        .status(BillStatus.PENDING)
                        .payee(String.format("****-****-****-%d", cardNum))
                        .nickname(vendorName)
                        .payment_date(currentDate)
                        .payment_amount(cost)
                        .build();

                nessieClient.createBill("56c66be6a73e492741507e36", bill, new NessieResultsListener() {
                        @Override
                        public void onSuccess(Object result, NessieException e) {
                            if (e == null) {
                                RequestResponse response = (RequestResponse) result;
                                Log.i("info", "Successfully added bill: " + result.toString());
                            }
                            else {
                                Log.e("error", "Failed to add bill: " + e.toString());
                            }
                        }
                });

                        /*
                        nessieClient.getCustomers(new NessieResultsListener() {
                            @Override
                            public void onSuccess(Object result, NessieException e) {
                                Log.i("info", "PETER onSuccess");
                                if (e == null) {
                                    // No errors
                                    ArrayList<Customer> customers = (ArrayList<Customer>) result;
                                    Log.i("info", "PETER" + customers.get(0).getFirst_name());
                                } else {
                                    Log.e("getCustomers Nessie", e.toString());
                                }
                            }
                        });*/
                //get accounts
                //search accounts for nickname == et_payment
                //if no, add account
                //post purchase to account
                //start tableActivity
                //MyItemRecyclerViewAdapter viewControls =
                //startActivity(new Intent(, DataTable.class));
                startActivity(new Intent(getBaseContext(), DataTable.class));
                break;
            }
            case R.id.btn_calculate: {
                //Toast.makeText(getApplicationContext(), "calculating", Toast.LENGTH_SHORT).show();
                Log.i("CheckMate", "calculating!");
                TextView display = (TextView)findViewById(R.id.tv_tip);
                EditText total = (EditText)findViewById(R.id.et_total);
                EditText percent = (EditText)findViewById(R.id.et_tipPercentage);
                if (total.getText().toString().isEmpty()){
                    total.setText("0.00");
                    Log.i("CheckMate", "empty total string, set to \"0.00\" ");
                }
                Log.i("CheckMate", total.getText().toString());
                float tempValue = (Float.parseFloat(total.getText().toString()) * Float.parseFloat(percent.getText().toString()))/100;
                display.setText(String.format("$%.2f", tempValue));
                break;
            }
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar


        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}
