package app.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vicco.bitso.R;

import java.util.HashMap;
import java.util.List;

import models.AppBitsoOperation;

/**
 * Created by vicco on 3/02/17.
 */

public class RecyclerViewLedgerAdapater extends RecyclerView.Adapter<RecyclerViewLedgerAdapater.ViewHolder>{
    private final String TAG = RecyclerViewLedgerAdapater.class.getSimpleName();

    private final int TYPE_ITEM = 0;
    private final int TYPE_DATE = 1;
    private final int TYPE_TRANSACTION = 2;

    private HashMap<Integer, Integer> mElementTypes;
    private List<AppBitsoOperation> mObjects;
    private Context mContext;

    public RecyclerViewLedgerAdapater(List<AppBitsoOperation> objects, Context context) {
        mElementTypes = new HashMap<Integer, Integer>();
        mObjects = objects;
        mContext = context;
    }

    public void addOperationElement(AppBitsoOperation appBitsoOperation){
        mObjects.add(appBitsoOperation);
    }

    public void addDateElement(AppBitsoOperation appBitsoOperation){
        addOperationElement(appBitsoOperation);
        mElementTypes.put((mObjects.size() - 1), TYPE_DATE);
    }

    public void addTransactionElement(AppBitsoOperation appBitsoOperation){
        addOperationElement(appBitsoOperation);
        mElementTypes.put((mObjects.size() - 1), TYPE_TRANSACTION);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        switch (viewType){
            case TYPE_DATE:
            case TYPE_TRANSACTION:
            case TYPE_ITEM:
                view = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.rv_element_ledger, parent, false);
                break;
        }
        return new ViewHolder(view);
    }

    public void orderOperationsByDate(){
        Log.d(TAG, "Ordering information by date");
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        AppBitsoOperation appBitsoOperation = mObjects.get(position);
        int rowType = getItemViewType(position);
        switch (rowType){
            case TYPE_DATE:
            case TYPE_TRANSACTION:
            case TYPE_ITEM:
                holder.setOperationIconValue(appBitsoOperation.getOperationImage());
                holder.setAppBitsoOperation(appBitsoOperation);
                holder.setOperationIconValue(appBitsoOperation.getOperationImage());
                holder.setOperationValue(appBitsoOperation.getOperationDescription());
                holder.setAmountValue(appBitsoOperation.getAmount(), appBitsoOperation.getAmountCurrencyColor());
                holder.setCurrency(appBitsoOperation.getCurrency(), appBitsoOperation.getAmountCurrencyColor());
                holder.setTimestampValue(appBitsoOperation.getOperationDate());
                holder.setStatusValue(appBitsoOperation.getStatus(), appBitsoOperation.getStatusColor());
                break;
        }

        /*
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = view.getContext();
                Intent intent = new Intent(context, LedgerDetailActivity.class);
                intent.putExtra(ItemDetailLedgerFragment.LEDGER_DETAIL_ITEM_ID, position);
                context.startActivity(intent);
            }
        });
        */
    }

    @Override
    public int getItemCount() {
        return mObjects.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(mElementTypes.containsKey(position)) {
            return mElementTypes.get(position);
        }
        return TYPE_ITEM;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private View mView;
        private ImageView mOperationIcon;
        private TextView mOperation;
        private TextView mAmount;
        private TextView mCurrency;
        private TextView mTimestamp;
        private TextView mStatus;
        private AppBitsoOperation mAppBitsoOperation;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mOperationIcon = (ImageView) mView.findViewById(R.id.ledgerOperationIcon);
            mOperation = (TextView) mView.findViewById(R.id.ledgerOperationDescription);
            mAmount = (TextView) mView.findViewById(R.id.ledgerOperationCurrencyAmount);
            mCurrency = (TextView) mView.findViewById(R.id.ledgerCurrencyDescriptor);
            mTimestamp = (TextView) mView.findViewById(R.id.ledgerOperationTimestamp);
            mStatus = (TextView) mView.findViewById(R.id.ledgerOperationStatus);
        }

        public View getView() {
            return mView;
        }

        public ImageView getOperationIcon() {
            return mOperationIcon;
        }

        public TextView getOperation() {
            return mOperation;
        }

        public TextView getAmount() {
            return mAmount;
        }

        public TextView getCurrency() {
            return mCurrency;
        }

        public TextView getTimestamp() {
            return mTimestamp;
        }

        public TextView getStatus() {
            return mStatus;
        }

        public AppBitsoOperation getAppBitsoOperation() {
            return mAppBitsoOperation;
        }

        public void setView(View mView) {
            this.mView = mView;
        }

        public void setOperationIconValue(int imageId) {
            this.mOperationIcon.setImageResource(imageId);
        }

        public void setOperationValue(String operation) {
            this.mOperation.setText(operation);
        }

        public void setAmountValue(String amount, int color) {
            this.mAmount.setText(amount);
            mAmount.setTextColor(mContext.getResources().getColor(color));
        }

        public void setCurrency(String currency, int color){
            this.mCurrency.setText(currency);
            mCurrency.setTextColor(mContext.getResources().getColor(color));
        }

        public void setTimestampValue(String timestamp) {
            this.mTimestamp.setText(timestamp);
        }

        public void setStatusValue(String status, int color) {
            this.mStatus.setText(status);
            mStatus.setTextColor(mContext.getResources().getColor(color));
        }

        public void setAppBitsoOperation(AppBitsoOperation mAppBitsoOperation) {
            this.mAppBitsoOperation = mAppBitsoOperation;
        }
    }
}