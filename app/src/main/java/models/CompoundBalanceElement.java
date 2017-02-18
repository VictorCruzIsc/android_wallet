package models;

import java.math.BigDecimal;

/**
 * Created by vicco on 17/02/17.
 */

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
