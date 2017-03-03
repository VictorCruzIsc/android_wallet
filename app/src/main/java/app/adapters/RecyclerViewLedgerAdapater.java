package app.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bitso.BitsoOperation;
import com.example.vicco.bitso.R;

import java.math.BigDecimal;
import java.util.List;

import models.AppBitsoOperation;

/**
 * Created by vicco on 3/02/17.
 */

public class RecyclerViewLedgerAdapater extends RecyclerView.Adapter<RecyclerViewLedgerAdapater.ViewHolder>{
    private final List<AppBitsoOperation> mObjects;

    private Context mContext;

    public RecyclerViewLedgerAdapater(List<AppBitsoOperation> objects, Context context) {
        mObjects = objects;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rv_element_ledger, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        AppBitsoOperation appBitsoOperation = mObjects.get(position);
        holder.setOperationIconValue(appBitsoOperation.getOperationImage());
        holder.setAppBitsoOperation(appBitsoOperation);
        holder.setOperationIconValue(appBitsoOperation.getOperationImage());
        holder.setOperationValue(appBitsoOperation.getOperationDescription());
        holder.setAmountValue(appBitsoOperation.getAmount());
        holder.setTimestampValue(appBitsoOperation.getOperationDate());
        holder.setStatusValue(appBitsoOperation.getStatus());

        /*holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = view.getContext();
                Intent intent = new Intent(context, LedgerDetailActivity.class);
                intent.putExtra(ItemDetailLedgerFragment.LEDGER_DETAIL_ITEM_ID, position);
                context.startActivity(intent);
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return mObjects.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private View mView;
        private ImageView mOperationIcon;
        private TextView mOperation;
        private TextView mAmount;
        private TextView mTimestamp;
        private TextView mStatus;
        private AppBitsoOperation mAppBitsoOperation;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mOperationIcon = (ImageView) mView.findViewById(R.id.ledgerOperationIcon);
            mOperation = (TextView) mView.findViewById(R.id.ledgerOperationDescription);
            mAmount = (TextView) mView.findViewById(R.id.ledgerOperationCurrencyAmount);
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

        public void setAmountValue(String amount) {
            this.mAmount.setText(amount);
            BigDecimal localAmount = new BigDecimal(amount);
            if(localAmount.intValue() < 0){
                mAmount.setTextColor(mContext.getResources().getColor(R.color.bitso_red));
            }else{
                mAmount.setTextColor(mContext.getResources().getColor(R.color.ledger_amount_funding));
            }
        }

        public void setTimestampValue(String timestamp) {
            this.mTimestamp.setText(timestamp);
        }

        public void setStatusValue(String status) {
            this.mStatus.setText(status);
            int color = mContext.getResources().getColor(R.color.ledger_status_ok);
            try {
                BigDecimal localStatus = new BigDecimal(status);
                if(localStatus.intValue() < 0) {
                    color = mContext.getResources().getColor(R.color.bitso_red);
                }
            }catch(Exception e){
            }
            mStatus.setTextColor(color);
        }

        public void setAppBitsoOperation(AppBitsoOperation mAppBitsoOperation) {
            this.mAppBitsoOperation = mAppBitsoOperation;
        }
    }
}
