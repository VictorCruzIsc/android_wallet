package app.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.vicco.bitso.R;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by vicco on 16/02/17.
 */

public class RecyclerViewCompoundBalanceAdapter extends
        RecyclerView.Adapter<RecyclerViewCompoundBalanceAdapter.ViewHolder>{
    private final List<CompoundBalanceElement> mObjects;

    public RecyclerViewCompoundBalanceAdapter(List<CompoundBalanceElement> objects) {
        mObjects = objects;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.lv_element_balance, parent, false);
        return new ViewHolder (view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CompoundBalanceElement element = mObjects.get(position);
        holder.mCompoundBalanceElement = element;
        holder.mCurrency.setText(element.getCurrency());
        holder.mTotal.setText(element.getTotal().toString());
    }

    @Override
    public int getItemCount() {
        return mObjects.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private final View mView;
        private final TextView mCurrency;
        private final TextView mTotal;
        private CompoundBalanceElement mCompoundBalanceElement;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mCurrency = (TextView) mView.findViewById(R.id.currency);
            mTotal = (TextView) mView.findViewById(R.id.amount);
        }

        public View getView() {
            return mView;
        }

        public TextView getCurrency() {
            return mCurrency;
        }

        public TextView getTotal() {
            return mTotal;
        }

        public CompoundBalanceElement getCompoundBalanceElement() {
            return mCompoundBalanceElement;
        }
    }

    public class CompoundBalanceElement{
        private String mCurrency;
        private BigDecimal mTotal;

        public CompoundBalanceElement(String currency, BigDecimal total){
            mCurrency = currency;
            mTotal = total;
        }

        public String getCurrency() {
            return mCurrency;
        }

        public BigDecimal getTotal() {
            return mTotal;
        }
    }
}
