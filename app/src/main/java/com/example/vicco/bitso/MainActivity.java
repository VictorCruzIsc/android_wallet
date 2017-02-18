package com.example.vicco.bitso;

import android.content.Context;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import app.adapters.ListViewCompoundBalanceAdapter;
import app.adapters.RecyclerViewCompoundBalanceAdapter;
import app.adapters.ViewPagerAdapter;
import app.fragments.FragmentCard;
import app.fragments.FragmentChat;
import app.fragments.FragmentHome;
import app.fragments.FragmentUserActivity;
import models.CompoundBalanceElement;

public class MainActivity extends AppCompatActivity {
    private Context mContext;
    private List<CompoundBalanceElement> mBalanceListElements;

    private Toolbar iToolbar;
    private TabLayout iTabLayout;
    private ViewPager iViewPager;
    private DrawerLayout iBalanceDrawer;
    private ListView iBalancesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Temporal

            CompoundBalanceElement total =
                    new CompoundBalanceElement("Total", new BigDecimal(String.valueOf(1800)));
            CompoundBalanceElement mxn =
                    new CompoundBalanceElement("MXN", new BigDecimal(String.valueOf(600)));
            CompoundBalanceElement btc =
                    new CompoundBalanceElement("BTC", new BigDecimal(String.valueOf(100)));
            CompoundBalanceElement eth =
                    new CompoundBalanceElement("ETH", new BigDecimal(String.valueOf(1100)));


        // Member elements
        {
            mContext = getApplicationContext();
            mBalanceListElements =
                    new ArrayList<CompoundBalanceElement>();
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
            adapter.addSectionHeaderItem(total);
            adapter.addItem(mxn);
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
}