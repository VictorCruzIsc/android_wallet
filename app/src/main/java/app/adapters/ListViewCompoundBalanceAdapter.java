package app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.vicco.bitso.R;

import java.util.HashMap;
import java.util.List;

import models.CompoundBalanceElement;

/**
 * Created by vicco on 17/02/17.
 */

public class ListViewCompoundBalanceAdapter extends BaseAdapter{
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_HEADER = 1;

    private LayoutInflater mLayoutInflater;
    private List<CompoundBalanceElement> mObjects;
    private HashMap<Integer, Boolean> mSectionHeaders;

    public ListViewCompoundBalanceAdapter(LayoutInflater layoutInflater,
                                          List<CompoundBalanceElement> mObjects) {
        this.mLayoutInflater = layoutInflater;
        this.mObjects = mObjects;
        mSectionHeaders = new HashMap<Integer, Boolean>();
    }

    public void addItem(CompoundBalanceElement item){
        mObjects.add(item);
    }

    public void addSectionHeaderItem(CompoundBalanceElement item){
        mObjects.add(item);
        mSectionHeaders.put((mObjects.size() - 1), Boolean.TRUE);
    }

    public void processList(List<CompoundBalanceElement> elements){
        int totalElements = elements.size();
        if(totalElements > 0) {
            mObjects.clear();
            addSectionHeaderItem(elements.get(0));
            for(int i=1; i<totalElements; i++){
                addItem(elements.get(i));
            }
        }
    }

    // Required implementation methods

    @Override
    public int getCount() {
        return mObjects.size();
    }

    @Override
    public Object getItem(int i) {
        return mObjects.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        int rowType = getItemViewType(position);

        if(view == null){
            holder = new ViewHolder();
            switch (rowType){
                case TYPE_HEADER:
                    view = mLayoutInflater.inflate(R.layout.lv_header_balance, null);
                    holder.mCurrency = (TextView) view.findViewById(R.id.currency_header);
                    holder.mTotal = (TextView) view.findViewById(R.id.amount_header);
                    break;
                case TYPE_ITEM:
                    view = mLayoutInflater.inflate(R.layout.lv_element_balance, null);
                    holder.mCurrency = (TextView) view.findViewById(R.id.currency);
                    holder.mTotal = (TextView) view.findViewById(R.id.amount);
                    break;
            }
            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }

        CompoundBalanceElement element = (CompoundBalanceElement) getItem(position);
        holder.mCurrency.setText(element.getCurrency());
        holder.mTotal.setText(element.getTotal().toString());
        return  view;
    }

    // Required overriding methods

    @Override
    public int getItemViewType(int position) {
        return mSectionHeaders.containsKey(position) ? TYPE_HEADER : TYPE_ITEM;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    // ViewHolder
    public class ViewHolder{
        TextView mCurrency;
        TextView mTotal;

        public ViewHolder(){}

        public ViewHolder(TextView currency, TextView total) {
            mCurrency = currency;
            mTotal = total;
        }

        public TextView getCurrency() {
            return mCurrency;
        }

        public TextView getTotal() {
            return mTotal;
        }
    }
}