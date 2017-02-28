package com.example.vicco.bitso;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import app.adapters.ListViewCompoundBalanceAdapter;
import app.adapters.ViewPagerAdapter;
import app.fragments.FragmentCard;
import app.fragments.FragmentChat;
import app.fragments.FragmentHome;
import app.fragments.FragmentUserActivity;
import connectivity.HttpHandler;
import models.BitsoBalance;
import models.BitsoTicker;
import models.CompoundBalanceElement;

import Utils.Utils;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener{
    private final String TAG = HomeActivity.class.getSimpleName();

    private List<CompoundBalanceElement> mBalanceListElements;
    ListViewCompoundBalanceAdapter mAdapter;

    private Toolbar iToolbar;
    private TabLayout iTabLayout;
    private ViewPager iViewPager;
    private DrawerLayout iBalanceDrawer;
    private ListView iBalancesList;
    private LinearLayout iProfileLinearLayout;
    private LinearLayout iConfigurationsLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Member elements
        {
            mBalanceListElements =
                    new ArrayList<CompoundBalanceElement>();
            mAdapter = new ListViewCompoundBalanceAdapter(
                    LayoutInflater.from(getApplicationContext()),
                    mBalanceListElements);
        }

        // Interface Elements
        {
            iBalanceDrawer =
                    (DrawerLayout) findViewById(R.id.balanceRightPanelDrawer);
            iToolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(iToolbar);
            iViewPager = (ViewPager) findViewById(R.id.viewPager);
            setupViewPager(iViewPager);
            iTabLayout = (TabLayout) findViewById(R.id.tabs);
            iTabLayout.setupWithViewPager(iViewPager);
            iBalancesList = (ListView) findViewById(R.id.balanceList);
            iProfileLinearLayout = (LinearLayout) findViewById(R.id.balance_profile);
            iConfigurationsLinearLayout = (LinearLayout) findViewById(R.id.balance_configuration);
        }

        // Interface and interactions
        {
            iBalancesList.setAdapter(mAdapter);
            iProfileLinearLayout.setOnClickListener(this);
            iConfigurationsLinearLayout.setOnClickListener(this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_search:
                if(Utils.isNetworkAvailable(this)) {
                    new GetCompoundBalance().execute();
                }else{
                    Log.d(TAG, getResources().getString(R.string.no_internet_connection));
                }

                if (iBalanceDrawer.isDrawerOpen(GravityCompat.START)) {
                    iBalanceDrawer.closeDrawer(GravityCompat.START);
                } else {
                    iBalanceDrawer.openDrawer(GravityCompat.START);
                }

                return Boolean.TRUE;
            default:
                return Boolean.TRUE;
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new FragmentHome(), getResources().getString(R.string.tab_home));
        adapter.addFragment(new FragmentUserActivity(), getResources().getString(R.string.tab_activity));
        adapter.addFragment(new FragmentChat(), getResources().getString(R.string.tab_chat));
        adapter.addFragment(new FragmentCard(), getResources().getString(R.string.tab_card));
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.balance_profile:
                Toast.makeText(this, getResources().getString(R.string.click_profile),
                        Toast.LENGTH_LONG).show();
                break;
            case R.id.balance_configuration:
                Toast.makeText(this, getResources().getString(R.string.click_configurations),
                        Toast.LENGTH_LONG).show();
                break;
        }
    }

    // Inner classes
    private class GetCompoundBalance extends AsyncTask<Void, Void, Void> {
        private List<CompoundBalanceElement> balanceListElements;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            HttpHandler.initHttpHandler(HomeActivity.this);
            balanceListElements = new ArrayList<CompoundBalanceElement>();
        }

        @Override
        protected Void doInBackground(Void... strings) {
            // Get balance
            String balanceResponse = HttpHandler.makeServiceCall("/api/v3/balance/",
                    "GET", "", true);
            // Get ticker
            String tickerResponse =
                    HttpHandler.sendGet("https://api.bitso.com/v3/ticker/", "");
            if(!processCompoundBalance(balanceResponse, tickerResponse)){
                String error = getString(R.string.no_compound_balance);
                Log.e(TAG, error);
                Toast.makeText(HomeActivity.this, error, Toast.LENGTH_LONG).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // Update List
            mAdapter.notifyDataSetChanged();
        }

        private boolean processCompoundBalance(String stringBalance, String stringTicker){
            BitsoBalance balance =  null;
            BitsoTicker[] tickers =  null;
            int totalCurrencyTickers = 0;

            if((stringBalance != null) && (stringTicker != null)){
                Log.d(TAG, stringBalance);
                Log.d(TAG, stringTicker);
                try {
                    // Process balance
                    JSONObject jsonBalance = new JSONObject(stringBalance);
                    balance = new BitsoBalance(jsonBalance);

                    // ProcessTicker
                    JSONObject jsonTicker = new JSONObject(stringTicker);
                    if(jsonTicker.has("success") && jsonTicker.has("payload")){
                        JSONArray currencyTickers = jsonTicker.getJSONArray("payload");
                        totalCurrencyTickers =  currencyTickers.length();
                        tickers = new BitsoTicker[totalCurrencyTickers];
                        for(int i=0; i<totalCurrencyTickers; i++){
                            tickers[i] = new BitsoTicker(currencyTickers.getJSONObject(i));
                        }
                    }

                    // Verification
                    if((balance == null) || (tickers == null)){
                        return false;
                    }

                    // Start building compound balance
                    BigDecimal total = balance.mxnAvailable;
                    BigDecimal mxnAmount =  total;
                    mxnAmount = mxnAmount.setScale(4, RoundingMode.DOWN);
                    balanceListElements.add(
                            new CompoundBalanceElement(
                                    getResources().getString(R.string.mxn_balance),
                                    mxnAmount, R.drawable.balance_divider_mxn));

                    for(int i=0; i<totalCurrencyTickers; i++){
                        BitsoTicker currentTicker =  tickers[i];
                        BigDecimal currentLast = currentTicker.last;
                        String header = "";
                        BigDecimal currencyAmount = null;
                        int drawableElement = -1;
                        switch(currentTicker.book){
                            case BTC_MXN:
                                currencyAmount = balance.btcAvailable;
                                header = getResources().getString(R.string.btc_balance);
                                drawableElement = R.drawable.balance_divider_btc;
                                break;
                            case ETH_MXN:
                                currencyAmount = balance.ethAvailable;
                                header = getResources().getString(R.string.eth_balance);
                                drawableElement = R.drawable.balance_divider_eth;
                                break;
                            default:
                                break;
                        }

                        total = total.add(currencyAmount.multiply(currentLast));

                        currencyAmount = currencyAmount.setScale(4, RoundingMode.DOWN);

                        total = total.setScale(2, RoundingMode.DOWN);

                        balanceListElements.add(new CompoundBalanceElement(
                                header, currencyAmount, drawableElement));
                    }

                    balanceListElements.add(0, new CompoundBalanceElement(
                            getResources().getString(R.string.hdr_balances), total, -1));

                    mAdapter.processList(balanceListElements);

                    return true;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return false;
        }
    }
}