package models;

import com.bitso.BitsoOperation;
import com.example.vicco.bitso.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;

import Utils.Utils;

/**
 * Created by vicco on 31/01/17.
 */

public class AppBitsoOperation extends BitsoOperation {
    private int mOperationImage;
    private String mAmount;
    private String mCurrency;
    private String mStatus;
    private int mAmountCurrencyColor;
    private int mStatusColor;

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

    public int getAmountCurrencyColor() { return mAmountCurrencyColor; }

    public int getStatusColor() { return mStatusColor; }

    public void setOperationImage(int mOperationImage) {
        this.mOperationImage = mOperationImage;
    }

    public String getOperationDescription(){
        return operationDescription;
    }

    public String getOperationDate(){
        return Utils.formatDate(operationDate, Utils.LEDGER_DATE_FORMAT);
    }

    private void processAppBitsoOperation(JSONObject jsonObject){
        int totalUpdates = 0;
        BigDecimal positiveAmount = null;
        BigDecimal negativeAmount = null;
        BigDecimal atPrice = null;
        String secondCurrency;

        mAmountCurrencyColor = R.color.ledger_amount_funding;
        mStatusColor = R.color.ledger_status_ok;

        try {
            balanceUpdates = getOperationBalances(jsonObject.getJSONArray("balance_updates"));
            totalUpdates = balanceUpdates.length;

            if(totalUpdates == 1){
                BigDecimal amount = balanceUpdates[0].amount;

                mCurrency = balanceUpdates[0].currency;
                mAmount = processDecimals(amount, mCurrency).toString();

                if(amount.doubleValue() < 0){
                    mAmountCurrencyColor = R.color.bitso_red;
                }
            }

            switch (operationDescription){
                case "funding":
                    mStatus = "Deposito completado";
                    operationDescription = BitsoLedgerOperations.FUNDING.toString();
                    mOperationImage = processOperationImage(mCurrency);
                    break;
                case "withdrawal":
                    mStatus = "Retiro completado";
                    operationDescription = BitsoLedgerOperations.WITHDRAWAL.toString();
                    mOperationImage = processOperationImage(mCurrency);
                    break;
                case "fee":
                    mStatus = "";
                    operationDescription = BitsoLedgerOperations.FEE.toString();
                    mOperationImage = R.drawable.user_convert;
                    break;
                case "trade":
                    operationDescription = BitsoLedgerOperations.TRADE.toString();
                    mOperationImage = R.drawable.user_convert;
                    if(totalUpdates == 2){
                        if((balanceUpdates[0].amount.doubleValue()) > 0){
                            positiveAmount = balanceUpdates[0].amount;
                            mCurrency = balanceUpdates[0].currency;
                            negativeAmount = balanceUpdates[1].amount;
                            secondCurrency = balanceUpdates[1].currency;
                        }else{
                            positiveAmount = balanceUpdates[1].amount;
                            mCurrency = balanceUpdates[1].currency;
                            negativeAmount = balanceUpdates[0].amount;
                            secondCurrency = balanceUpdates[0].currency;
                        }

                        positiveAmount = processDecimals(positiveAmount, mCurrency);
                        negativeAmount = processDecimals(negativeAmount, secondCurrency);

                        mAmount = positiveAmount.toString();
                        mStatus = negativeAmount.toString() + " " + secondCurrency.toUpperCase();

                        mStatusColor = R.color.bitso_red;
                    }
                    break;
            }

            mCurrency = mCurrency.toUpperCase();

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

    private BigDecimal processDecimals(BigDecimal bigDecimal, String currency) {
        switch (currency) {
            case "eth":
            case "btc":
                return bigDecimal.setScale(8, RoundingMode.DOWN);
            default:
                return bigDecimal.setScale(2, RoundingMode.DOWN);
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