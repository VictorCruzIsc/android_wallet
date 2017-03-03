package models;

import com.bitso.BitsoOperation;
import com.example.vicco.bitso.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

/**
 * Created by vicco on 31/01/17.
 */

public class AppBitsoOperation extends BitsoOperation {
    private int mOperationImage;
    private String mAmount;
    private String mCurrency;
    private String mStatus;
    private AppBalanceUpdate[] balanceUpdates;

    public AppBitsoOperation(JSONObject o){
        super(o);
        processAppBitsoOperation(o);
    }

    public int getOperationImage() {
        return mOperationImage;
    }

    public String getAmount() {
        return mAmount;
    }

    public String getCurrency() {
        return mCurrency;
    }

    public String getStatus() {
        return mStatus;
    }

    public void setOperationImage(int mOperationImage) {
        this.mOperationImage = mOperationImage;
    }

    public String getOperationDescription(){
        return operationDescription;
    }

    public String getOperationDate(){
        return operationDate.toString();
    }

    private void processAppBitsoOperation(JSONObject jsonObject){
        int totalUpdates = 0;
        BigDecimal positiveAmount = null;
        BigDecimal negativeAmount = null;
        BigDecimal atPrice = null;
        try {

            balanceUpdates = getOperationBalances(jsonObject.getJSONArray("balance_updates"));

            totalUpdates = balanceUpdates.length;

            switch (operationDescription){
                case "funding":
                    mStatus = "Deposito completado";
                    operationDescription = BitsoLedgerOperations.FUNDING.toString();
                    if(totalUpdates == 1){
                        mAmount = balanceUpdates[0].amount.setScale(2,
                                RoundingMode.DOWN).toString();
                        mCurrency = balanceUpdates[0].currency;
                    }
                    mOperationImage = processOperationImage(mCurrency);
                    break;

                case "withdrawal":
                    mStatus = "Retiro completado";
                    operationDescription = BitsoLedgerOperations.WITHDRAWAL.toString();
                    if(totalUpdates == 1){
                        mAmount = balanceUpdates[0].amount.setScale(2,
                                RoundingMode.DOWN).toString();
                        mCurrency = balanceUpdates[0].currency;
                    }
                    mOperationImage = processOperationImage(mCurrency);
                    break;
                case "trade":
                    operationDescription = BitsoLedgerOperations.TRADE.toString();
                    mOperationImage = R.drawable.user_convert;
                    if(totalUpdates == 2){
                        if((balanceUpdates[0].amount.intValue()) > 0){
                            positiveAmount = balanceUpdates[0].amount;
                            negativeAmount = balanceUpdates[1].amount;
                        }else{
                            positiveAmount = balanceUpdates[1].amount;
                            negativeAmount = balanceUpdates[0].amount;
                        }

                        //atPrice = positiveAmount.divide(negativeAmount.abs());

                        positiveAmount.setScale(2,RoundingMode.DOWN);
                        negativeAmount.setScale(2,RoundingMode.DOWN);
                        //atPrice.setScale(2,RoundingMode.DOWN);

                        operationDescription += "(@" + "atPrice.toString()" + ")";
                        mAmount = positiveAmount.toString();
                        mStatus = negativeAmount.toString();
                    }
                    break;

                case "fee":
                    mStatus = "";
                    operationDescription = BitsoLedgerOperations.FEE.toString();
                    mOperationImage = R.drawable.user_convert;
                    if(totalUpdates == 1){
                        mAmount = balanceUpdates[0].amount.setScale(2,
                                RoundingMode.DOWN).toString();
                        mCurrency = balanceUpdates[0].currency;
                    }
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private AppBalanceUpdate[] getOperationBalances(JSONArray array) throws JSONException {
        int totalBalances =  array.length();
        AppBalanceUpdate[] balances =  new AppBalanceUpdate[totalBalances];
        for(int i=0; i<totalBalances; i++){
            balances[i] =  new AppBalanceUpdate(array.getJSONObject(i));
        }
        return balances;
    }

    private int processOperationImage(String currency){
        switch (currency){
            case "mxn":
            case "sp":
                return R.drawable.user_spei;
            case "eth":
                return R.drawable.user_eth;
            case "btc":
                return R.drawable.user_btc;
            default:
                return R.mipmap.ic_launcher;
        }
    }

    protected class AppBalanceUpdate{
        protected String currency;
        protected BigDecimal amount;

        public AppBalanceUpdate(JSONObject o) throws JSONException {
            this.currency = o.getString("currency");
            this.amount = new BigDecimal(o.getString("amount"));
        }
    }
}