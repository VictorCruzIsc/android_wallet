package com.example.vicco.bitso;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import Utils.Utils;
import Utils.UtilsSharedPreferences;
import app.activities.CaptureActivity;
import app.adapters.ListViewCompoundBalanceAdapter;
import app.adapters.ViewPagerAdapter;
import app.fragments.FragmentCard;
import app.fragments.FragmentChat;
import app.fragments.FragmentHome;
import app.fragments.FragmentUserActivity;
import connectivity.HttpHandler;
import models.CompoundBalanceElement;

public class HomeActivity extends AppCompatActivity {
    public static final String ALIAS_SECRET = "secret";
    public static final String ALIAS_API = "api";
    public static final String ALIAS_CLIENT = "client";
    public static final String SP_SECRET = ALIAS_SECRET;
    public static final String SP_API = ALIAS_API;
    public static final String SP_CLIENT = ALIAS_CLIENT;
    public static final String SP_SET_KEYS = "setKeys";

    private final String TAG = HomeActivity.class.getSimpleName();

    private Context mContext;
    private List<CompoundBalanceElement> mBalanceListElements;
    private Intent mIntent;
    private SharedPreferences mSharedPreferences;

    private Toolbar iToolbar;
    private TabLayout iTabLayout;
    private ViewPager iViewPager;
    private DrawerLayout iBalanceDrawer;
    private ListView iBalancesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Member elements
        {
            mContext = getApplicationContext();
            mBalanceListElements =
                    new ArrayList<CompoundBalanceElement>();
            setUpCredentials();
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
        }

        // Interface and interactions
        {
            ListViewCompoundBalanceAdapter adapter =
                    new ListViewCompoundBalanceAdapter(LayoutInflater
                            .from(getApplicationContext()), mBalanceListElements);

            CompoundBalanceElement total =
                    new CompoundBalanceElement("Saldo Combinado", new BigDecimal(String.valueOf(1900)));
            CompoundBalanceElement mxn =
                    new CompoundBalanceElement("Pesos (MXN)", new BigDecimal(String.valueOf(600)));
            CompoundBalanceElement dlls =
                    new CompoundBalanceElement("Dolares (USD)", new BigDecimal(String.valueOf(100)));
            CompoundBalanceElement btc =
                    new CompoundBalanceElement("Bitcoin (BTC)", new BigDecimal(String.valueOf(1100)));
            CompoundBalanceElement eth =
                    new CompoundBalanceElement("Bitcoin (BTC)", new BigDecimal(String.valueOf(1100)));

            adapter.addSectionHeaderItem(total);
            adapter.addItem(mxn);
            adapter.addItem(dlls);
            adapter.addItem(btc);
            adapter.addItem(eth);
            iBalancesList.setAdapter(adapter);
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
                if (iBalanceDrawer.isDrawerOpen(GravityCompat.END)) {
                    iBalanceDrawer.closeDrawer(GravityCompat.END);
                } else {
                    iBalanceDrawer.openDrawer(GravityCompat.END);
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

    private void setUpCredentials(){
        // Create keys in KeyStore
        Utils.createNewKey(ALIAS_SECRET, this);
        Utils.createNewKey(ALIAS_API, this);
        Utils.createNewKey(ALIAS_CLIENT, this);

        mIntent = getIntent();
        if(mIntent != null){
            String credentialsResponse = mIntent.getStringExtra(CaptureActivity.CREDENTIALS);
            Log.d(TAG, credentialsResponse);
            try {
                JSONObject jsonObject = new JSONObject(credentialsResponse);
                UtilsSharedPreferences.initSharedPreferences(this);
                UtilsSharedPreferences.writeBoolean(SP_SET_KEYS, saveCredentials(jsonObject));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean saveCredentials(JSONObject jsonObject){
        String secret = null;
        String encryptedSecret =  null;
        String api =  null;
        String encryptedAPI = null;
        String client =  null;
        String encryptedClient = null;
        try {
            secret = jsonObject.getString(ALIAS_SECRET);
            encryptedSecret = Utils.encryptString(ALIAS_SECRET, secret);
            secret = "";
            secret =  null;

            api = jsonObject.getString("id");
            encryptedAPI = Utils.encryptString(ALIAS_API, api);
            api = "";
            api = null;

            client = jsonObject.getString(ALIAS_CLIENT);
            encryptedClient = Utils.encryptString(ALIAS_CLIENT, client);
            client = "";
            client = null;

            // Save on shared preferences
            boolean savedSecret = UtilsSharedPreferences.writeString(SP_SECRET, encryptedSecret);
            boolean savedAPI = UtilsSharedPreferences.writeString(SP_API, encryptedAPI);
            boolean savedClient = UtilsSharedPreferences.writeString(SP_CLIENT, encryptedClient);

            Log.d(TAG, "SECRET: " + encryptedSecret + " API: " + encryptedAPI + " CLIENT: " + encryptedClient);

            return (savedSecret & savedAPI & savedClient);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }
}