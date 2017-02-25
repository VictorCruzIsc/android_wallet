package models;

import java.math.BigDecimal;

import Utils.Helpers;

public abstract class Ticker {

    public BigDecimal last;
    public BigDecimal high;
    public BigDecimal low;
    public BigDecimal vwap;
    public BigDecimal volume;
    public BigDecimal bid;
    public BigDecimal ask;
    public String createdAt;

    public String toString() {
        return Helpers.fieldPrinter(this);
    }
}
