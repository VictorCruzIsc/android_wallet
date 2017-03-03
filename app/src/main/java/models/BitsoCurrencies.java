package models;

/**
 * Created by vicco on 2/03/17.
 */

public enum BitsoCurrencies{
    BTC("btc"), ETH("eth"), SP("sp"), MXN("mxn");

    private final String mName;

    private BitsoCurrencies(String name){
        mName = name;
    }

    public String toString(){
        return mName;
    }
}
