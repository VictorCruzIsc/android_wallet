package app.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vicco.bitso.R;

import java.util.List;

import models.BitsoOperation;

/**
 * Created by vicco on 3/02/17.
 */

public class RecyclerViewLedgerAdapater extends RecyclerView.Adapter<RecyclerViewLedgerAdapater.ViewHolder>{
    private final List<BitsoOperation> mObjects;

    public RecyclerViewLedgerAdapater(List<BitsoOperation> objects) {
        this.mObjects = objects;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rv_element_ledger, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.mBitsoOperation = mObjects.get(position);
        holder.mOperatinDate.setText(holder.mBitsoOperation.operationDate);
        holder.mOperation.setText(holder.mBitsoOperation.operationDescription);

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
        public final View mView;
        public final ImageView mOperationIcon;
        public final TextView mOperation;
        public final TextView mOperatinDate;
        public BitsoOperation mBitsoOperation;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mOperationIcon = (ImageView) mView.findViewById(R.id.operationIcon);
            mOperation = (TextView) mView.findViewById(R.id.operation);
            mOperatinDate = (TextView) mView.findViewById(R.id.operationDate);
        }
    }
}
