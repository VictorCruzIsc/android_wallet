package models;

/**
 * Created by vicco on 24/02/17.
 */

public enum BitsoBook {
    BTC_MXN, ETH_MXN;

    public String toString() {
        return this.name().toLowerCase();
    }
}
