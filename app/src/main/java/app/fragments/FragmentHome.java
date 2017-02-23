package app.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.vicco.bitso.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import app.adapters.RecyclerViewLedgerAdapater;
import connectivity.HttpHandler;
import models.BitsoOperation;

/**
 * Created by vicco on 2/02/17.
 */

public class FragmentHome extends Fragment {
    public final String TAG = FragmentHome.class.getSimpleName();

    public static List<BitsoOperation> slistElements;

    public ProgressDialog mProgressDialog;
    public RecyclerViewLedgerAdapater mAdapter;
    public Activity mActivity;

    public RecyclerView iRecyclerView;

    public FragmentHome() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Members initialization
        mActivity = this.getActivity();

        slistElements = new ArrayList<BitsoOperation>();
        mAdapter = new RecyclerViewLedgerAdapater(slistElements);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        // Interface initialization
        iRecyclerView = (RecyclerView) view.findViewById(R.id.item_list);
        assert iRecyclerView != null;

        // Setting interface interactions
        iRecyclerView.setAdapter(mAdapter);

        // Activity methods
        getLedgers();

        return view;
    }

    private void getLedgers(){
        new FillListAsyncTask().execute("/api/v3/ledger", "GET", "");
    }

    // Inner classes
    private class FillListAsyncTask extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog =  new ProgressDialog(getActivity());
            mProgressDialog.setMessage("Fetching user ledger operations");
            mProgressDialog.setCancelable(Boolean.FALSE);
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(String... strings) {
            HttpHandler.initHttpHandler(getActivity());
            String jsonResponse = HttpHandler.makeServiceCall(strings[0],
                    strings[1], strings[2], true);
            if(jsonResponse != null){
                Log.d(TAG, jsonResponse);
                try {
                    JSONObject jsonObject = new JSONObject(jsonResponse);
                    processResponse(jsonObject);
                } catch (final JSONException e) {
                    Log.e(TAG, "JSON Object parsing error: " + e.getMessage());
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mActivity.getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });
                }
            }else{
                Log.e(TAG, "Couldn't get json from server.");
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mActivity.getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // Dismiss progress dialog
            if(mProgressDialog.isShowing()){
                mProgressDialog.dismiss();
            }

            // Update List
            mAdapter.notifyDataSetChanged();
        }

        private void processResponse(JSONObject jsonObject){
            if(jsonObject.has("payload")){
                try {
                    JSONArray jsonArray = jsonObject.getJSONArray("payload");
                    int totalElements =  jsonArray.length();
                    for(int i=0; i<totalElements; i++){
                        slistElements.add(new BitsoOperation(jsonArray.getJSONObject(i)));
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "JSON Object parsing error: " + e.getMessage());
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mActivity.getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });
                }
            }else{
                Log.e(TAG, "JSON does not contain payload key.");
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mActivity.getApplicationContext(),
                                "Bad JSON response, not payload key found",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });
            }
        }
    }
}
