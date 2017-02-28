package models;

import java.math.BigDecimal;

/**
 * Created by vicco on 17/02/17.
 */

public class CompoundBalanceElement{
    private String mCurrency;
    private BigDecimal mTotal;
    private int mDivider;

    public CompoundBalanceElement(String currency, BigDecimal total, int divider){
        mCurrency = currency;
        mTotal = total;
        mDivider = divider;
    }

    public String getCurrency() {
        return mCurrency;
    }

    public BigDecimal getTotal() {
        return mTotal;
    }

    public int getDivider(){ return mDivider; }
}
