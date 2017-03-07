package com.example.vicco.bitso;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import app.adapters.ListViewCompoundBalanceAdapter;
import app.adapters.RecyclerViewCompoundBalanceAdapter;
import app.adapters.RecyclerViewLedgerAdapater;
import app.adapters.ViewPagerAdapter;
import app.fragments.FragmentCard;
import app.fragments.FragmentChat;
import app.fragments.FragmentHome;
import app.fragments.FragmentUserActivity;
import connectivity.HttpHandler;
import models.AppBitsoOperation;
import models.BitsoBalance;
import models.BitsoTicker;
import models.CompoundBalanceElement;

import Utils.Utils;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private final String TAG = HomeActivity.class.getSimpleName();
    private final int RETRIEVE_PERIOD = 100_000;

    public static List<AppBitsoOperation> slistElements;

    private List<CompoundBalanceElement> mBalanceListElements;
    private ListViewCompoundBalanceAdapter mListViewCompoundBalanceAdapter;
    private RecyclerViewLedgerAdapater mRecyclerViewLedgerAdapter;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private Timer mTimer;
    private TimerTask mTimerTask;
    private String mToolBarBalance;
    private boolean mDownloadingBalances; // Prevents extra download of user balances

    private CoordinatorLayout iCoordinatorLayout;
    private Toolbar iToolbar;
    private DrawerLayout iBalanceDrawer;
    private ListView iBalancesList;
    private LinearLayout iProfileLinearLayout;
    private LinearLayout iConfigurationsLinearLayout;
    private ImageView iDotsMenu;
    private RecyclerView iRecyclerView;
    private TextView iToolBarCurrencyAmount;
    private TextView iToolbarCurrencyAmountLbl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Interface Elements
        {
            iCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);
            iBalanceDrawer =
                    (DrawerLayout) findViewById(R.id.balanceRightPanelDrawer);
            iToolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(iToolbar);
            iBalancesList = (ListView) findViewById(R.id.balanceList);
            iProfileLinearLayout = (LinearLayout) findViewById(R.id.balance_profile);
            iConfigurationsLinearLayout = (LinearLayout) findViewById(R.id.balance_configuration);
            iDotsMenu = (ImageView) findViewById(R.id.dots_icon);
            iRecyclerView = (RecyclerView) findViewById(R.id.item_list);
            iToolBarCurrencyAmount = (TextView) findViewById(R.id.toolbarCurrencyAmount);
            iToolbarCurrencyAmountLbl = (TextView) findViewById(R.id.toolbarCurrencyAmountLbl);
        }

        // Member elements
        {
            slistElements = new ArrayList<AppBitsoOperation>();

            mBalanceListElements = new ArrayList<CompoundBalanceElement>();
            mActionBarDrawerToggle = getActionBarDrawerToggle();
            mRecyclerViewLedgerAdapter = new RecyclerViewLedgerAdapater(slistElements,
                    HomeActivity.this);
            mListViewCompoundBalanceAdapter = new ListViewCompoundBalanceAdapter(
                    LayoutInflater.from(getApplicationContext()),
                    mBalanceListElements);
            mDownloadingBalances = false;
        }

        // Interface and interactions
        {
            iBalancesList.setAdapter(mListViewCompoundBalanceAdapter);
            iRecyclerView.setAdapter(mRecyclerViewLedgerAdapter);

            iBalancesList.setOnItemClickListener(this);

            iProfileLinearLayout.setOnClickListener(this);
            iConfigurationsLinearLayout.setOnClickListener(this);
            iDotsMenu.setOnClickListener(this);

            iBalanceDrawer.setDrawerListener(mActionBarDrawerToggle);
        }

        // Processes
        {
            getLedgers();
            retrieveCompoundBalancePeriodically();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.balance_profile:
                Toast.makeText(this, getResources().getString(R.string.click_profile),
                        Toast.LENGTH_LONG).show();
                break;
            case R.id.balance_configuration:
                Toast.makeText(this, getResources().getString(R.string.click_configurations),
                        Toast.LENGTH_LONG).show();
                break;
            case R.id.dots_icon:
                if(!mDownloadingBalances) {
                    mDownloadingBalances = true;

                    if (Utils.isNetworkAvailable(this)) {
                        new CompoundBalanceTask().execute();
                    } else {
                        Log.d(TAG, getResources().getString(R.string.no_internet_connection));
                    }

                    if (iBalanceDrawer.isDrawerOpen(GravityCompat.END)) {
                        iBalanceDrawer.closeDrawer(GravityCompat.END);
                    } else {
                        iBalanceDrawer.openDrawer(GravityCompat.END);
                    }
                }else{
                    Log.d(TAG, "Not able to execute CompoundBalanceTask on onClick(), downloadingBalances");
                }

                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CompoundBalanceElement element = mBalanceListElements.get(position);
        iToolBarCurrencyAmount.setText("$" + element.getTotal().toString());
        iToolBarCurrencyAmount.setTextColor(element.getColor());
        iToolbarCurrencyAmountLbl.setText("TOTAL EN " + element.getCurrency().toUpperCase());

        if (iBalanceDrawer.isDrawerOpen(GravityCompat.END)) {
            iBalanceDrawer.closeDrawer(GravityCompat.END);
        } else {
            iBalanceDrawer.openDrawer(GravityCompat.END);
        }

    }

    private ActionBarDrawerToggle getActionBarDrawerToggle() {
        // When the dots icon is clicked onDrawerSlide method is called
        // multiple times, when the max offset in x is reached
        // onDrawerOpened method is called.
        return new ActionBarDrawerToggle(
                this, iBalanceDrawer, iToolbar, R.string.open_drower,
                R.string.close_drower) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);

                // Ask coordinator to move menu width from rigth to left
                iCoordinatorLayout.setTranslationX(-(slideOffset * drawerView.getWidth()));
                iBalanceDrawer.bringChildToFront(drawerView);
                iBalanceDrawer.requestLayout();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if(!mDownloadingBalances) {
                    mDownloadingBalances = true;
                    if (Utils.isNetworkAvailable(HomeActivity.this)) {
                        new CompoundBalanceTask().execute();
                    } else {
                        Log.d(TAG, getResources().getString(R.string.no_internet_connection));
                    }
                }else{
                    Log.d(TAG, "Not able to execute CompoundBalanceTask on onDrawerOpened(), downloadingBalances");
                }
            }
        };
    }

    private void getLedgers() {
        if(!mDownloadingBalances){
            mDownloadingBalances = true;
            new FillListAsyncTask().execute("/api/v3/ledger", "GET", "");
        }else{
            Log.d(TAG, "Not able to execute FillAsyncTask on getLedgers(), downloadingBalances");
        }
    }

    private void retrieveCompoundBalancePeriodically() {
        final Handler handler = new Handler();
        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            if(!mDownloadingBalances) {
                                mDownloadingBalances = true;
                                new CompoundBalanceTask().execute();
                            }else{
                                Log.d(TAG, "Not able to execute CompoundBalanceTask on Timer(), downloadingBalances");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        };
        mTimer.schedule(mTimerTask, 0, RETRIEVE_PERIOD);
    }

    // Methods called in AsyncTasks
    private boolean asyncMethodProcessCompoundBalance(){
        BitsoBalance balance = null;
        BitsoTicker[] tickers = null;
        String stringBalance = null;
        String stringTicker =  null;
        int totalCurrencyTickers = 0;
        List<CompoundBalanceElement> balanceListElements = null;

        // Compound balance variables
        BigDecimal total = null;
        BitsoTicker ticker = null;
        BigDecimal last = null;
        String header = null;
        BigDecimal currencyAmount = null;
        int drawableElement = -1;
        int color = -1;

        // Get balance
        stringBalance = HttpHandler.makeServiceCall("/api/v3/balance/",
                "GET", "", true);

        // Get ticker
        stringTicker = HttpHandler.sendGet("https://api.bitso.com/v3/ticker/", "");

        if ((stringBalance != null) && (stringTicker != null)) {
            Log.d(TAG, stringBalance);
            Log.d(TAG, stringTicker);

            try {
                JSONObject jsonBalance = new JSONObject(stringBalance);
                JSONObject jsonTicker = new JSONObject(stringTicker);

                if(jsonBalance.has("error") || jsonTicker.has("error")){
                    Log.d(TAG, getString(R.string.no_valid_response));
                    return false;
                }

                balance = new BitsoBalance(jsonBalance);

                if(jsonTicker.has("success") && jsonTicker.has("payload")) {
                    JSONArray currencyTickers = jsonTicker.getJSONArray("payload");
                    totalCurrencyTickers = currencyTickers.length();
                    tickers = new BitsoTicker[totalCurrencyTickers];
                    for (int i = 0; i < totalCurrencyTickers; i++) {
                        tickers[i] = new BitsoTicker(currencyTickers.getJSONObject(i));
                    }
                }

                // Verification
                if ((balance == null) || (tickers == null)) {
                    return false;
                }

                balanceListElements = new ArrayList<CompoundBalanceElement>();

                // Start building compound balance
                total  = balance.mxnAvailable;

                balanceListElements.add(
                        new CompoundBalanceElement(
                                getResources().getString(R.string.mxn_balance),
                                total.setScale(4, RoundingMode.DOWN),
                                R.drawable.balance_divider_mxn,
                                R.color.balance_mxn));

                for (int i = 0; i < totalCurrencyTickers; i++) {
                    ticker = tickers[i];
                    last = ticker.last;
                    switch (ticker.book) {
                        case BTC_MXN:
                            header = getResources().getString(R.string.btc_balance);
                            currencyAmount = balance.btcAvailable;
                            drawableElement = R.drawable.balance_divider_btc;
                            color = R.color.balance_btc;
                            break;
                        case ETH_MXN:
                            header = getResources().getString(R.string.eth_balance);
                            currencyAmount = balance.ethAvailable;
                            drawableElement = R.drawable.balance_divider_eth;
                            color = R.color.balance_eth;
                            break;
                        default:
                            break;
                    }

                    total = total.add(currencyAmount.multiply(last));
                    currencyAmount = currencyAmount.setScale(4, RoundingMode.DOWN);
                    balanceListElements.add(new CompoundBalanceElement(header,
                            currencyAmount, drawableElement, color));
                }

                balanceListElements.add(0, new CompoundBalanceElement(
                        getResources().getString(R.string.hdr_balances), total, -1, R.color.balance_amount));

                // Update ToolBar Balance
                {
                    total = total.setScale(2, RoundingMode.DOWN);
                    mToolBarBalance = "$" + total.toString();
                }

                mListViewCompoundBalanceAdapter.processList(balanceListElements);

                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private boolean asyncMethodProcessLedger(String... strings) {
        String jsonResponse = HttpHandler.makeServiceCall(strings[0],
                strings[1], strings[2], true);

        if (jsonResponse != null) {
            Log.d(TAG, jsonResponse);
            try {
                JSONObject jsonObject = new JSONObject(jsonResponse);

                if(jsonObject.has("error")){
                    Log.d(TAG, getString(R.string.no_valid_response));
                    return false;
                }

                if (jsonObject.has("payload")) {
                    try {
                        JSONArray jsonArray = jsonObject.getJSONArray("payload");
                        int totalElements = jsonArray.length();
                        Log.d(TAG, "Total elements in payload:" + totalElements);
                        for (int i = 0; i < totalElements; i++) {
                            slistElements.add(new AppBitsoOperation(jsonArray.getJSONObject(i)));
                        }
                        return Boolean.TRUE;
                    } catch (final JSONException e) {
                        Log.e(TAG, "JSON Object parsing error: " + e.getMessage());
                        Toast.makeText(HomeActivity.this, "Json parsing error: " +
                                        "Json parsing error: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    Log.e(TAG, "JSON does not contain payload key.");
                    Toast.makeText(HomeActivity.this, "Json parsing error: " +
                            "Bad JSON response, not payload key found", Toast.LENGTH_LONG).show();
                }
            } catch (final JSONException e) {
                Log.e(TAG, "JSON Object parsing error: " + e.getMessage());
                Toast.makeText(HomeActivity.this, "Json parsing error: " +
                        e.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else {
            Log.e(TAG, "Couldn't get json from server.");
            Toast.makeText(HomeActivity.this, "Json parsing error: " +
                            "Couldn't get json from server. Check LogCat for possible errors!",
                    Toast.LENGTH_LONG).show();
        }

        return Boolean.FALSE;
    }

    // Async tasks
    private class CompoundBalanceTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            HttpHandler.initHttpHandler(HomeActivity.this);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            return asyncMethodProcessCompoundBalance();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            if(result) {
                // Update List
                mListViewCompoundBalanceAdapter.notifyDataSetChanged();

                // Update Balance in toolbar
                TextView textView = (TextView) findViewById(R.id.toolbarCurrencyAmount);
                textView.setText(mToolBarBalance);
            }else{
                Log.d(TAG, getString(R.string.no_compound_balance));
            }

            mDownloadingBalances = false;
        }
    }

    private class FillListAsyncTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            HttpHandler.initHttpHandler(HomeActivity.this);
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            boolean ledgerProcessed = asyncMethodProcessLedger(strings);
            boolean compoundBalanceProcessed = asyncMethodProcessCompoundBalance();
            return (ledgerProcessed && compoundBalanceProcessed);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if(result) {
                // Update ledger Recycler View
                mRecyclerViewLedgerAdapter.notifyDataSetChanged();

                // Update compound balance ListView
                mListViewCompoundBalanceAdapter.notifyDataSetChanged();

                // Update Balance in toolbar
                TextView textView = (TextView) findViewById(R.id.toolbarCurrencyAmount);
                textView.setText(mToolBarBalance);
            }else{
                Log.d(TAG, getString(R.string.no_ledgder_fetched));
            }

            mDownloadingBalances = false;
        }
    }
}