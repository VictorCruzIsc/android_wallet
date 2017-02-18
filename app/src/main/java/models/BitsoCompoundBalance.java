package models;

import java.math.BigDecimal;

/**
 * Created by vicco on 16/02/17.
 */

public class BitsoCompoundBalance {
    private BigDecimal mTotal;
    private BigDecimal mMXNTotal;
    private BigDecimal mETHTotal;
        private BigDecimal mBTCTotal;

        public BitsoCompoundBalance(BigDecimal mxnTotal, BigDecimal ethTotal,
                BigDecimal btcTotal){
            mTotal = new BigDecimal(new String("0"));
        mMXNTotal = mxnTotal;
        mETHTotal = ethTotal;
        mBTCTotal = btcTotal;
    }

    public BigDecimal getTotal() {
        return mTotal;
    }

    public BigDecimal getMXNTotal() {
        return mMXNTotal;
    }

    public BigDecimal getETHTotal() {
        return mETHTotal;
    }

    public BigDecimal getBTCTotal() {
        return mBTCTotal;
    }
}
