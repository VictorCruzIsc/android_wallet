package models;

/**
 * Created by vicco on 2/03/17.
 */

public enum BitsoLedgerOperations {
    FUNDING("Funding"), WITHDRAWAL("Withdrawal"), TRADE("Trade"), FEE("Fee");

    private final String mName;

    private BitsoLedgerOperations(String name){
        mName = name;
    }

    public String toString(){
        return mName;
    }
}
