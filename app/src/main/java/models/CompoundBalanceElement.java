package models;

import java.math.BigDecimal;

/**
 * Created by vicco on 17/02/17.
 */

public class CompoundBalanceElement {
    private String mCurrency;
    private BigDecimal mTotal;
    private int mDivider;
    private int mColor;

    public CompoundBalanceElement(String currency, BigDecimal total, int divider,
        int color) {
        mCurrency = currency;
        mTotal = total;
        mDivider = divider;
        mColor = color;
    }

    public String getCurrency() {
        return mCurrency;
    }

    public BigDecimal getTotal() {
        return mTotal;
    }

    public int getDivider() { return mDivider; }

    public int getColor() { return mColor; }

    public void setColor(int color){ mColor = color; }
}
