package models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;

/**
 * Created by vicco on 31/01/17.
 */

public class BitsoOperation {
    public String entryId;
    public String operationDescription;
    public String operationDate;
    public BalanceUpdate[] afterOperationBalances;
    public JSONObject details;

    public BitsoOperation(JSONObject o) throws JSONException {
        entryId = o.getString("eid");
        operationDescription = o.getString("operation");
        operationDate = o.getString("created_at");
        afterOperationBalances = getOperationBalances(o.getJSONArray("balance_updates"));
        details = o.getJSONObject("details");
    }

    private BalanceUpdate[] getOperationBalances(JSONArray array) throws JSONException {
        int totalBalances =  array.length();
        BalanceUpdate[] balances =  new BalanceUpdate[totalBalances];
        for(int i=0; i<totalBalances; i++){
            balances[i] =  new BalanceUpdate(array.getJSONObject(i));
        }
        return balances;
    }

    private class BalanceUpdate{
        String currency;
        BigDecimal amount;

        public BalanceUpdate(JSONObject o) throws JSONException {
            this.currency = o.getString("currency");
            this.amount = new BigDecimal(o.getString("amount"));
        }
    }
}