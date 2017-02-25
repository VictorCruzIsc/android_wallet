package models;

import org.json.JSONException;
import org.json.JSONObject;

import Utils.Helpers;

public class BitsoTicker extends Ticker {

    public BitsoBook book;

    public BitsoTicker(JSONObject o) throws JSONException {
        last = Helpers.getBD(o, "last");
        high = Helpers.getBD(o, "high");
        low = Helpers.getBD(o, "low");
        vwap = Helpers.getBD(o, "vwap");
        volume = Helpers.getBD(o, "volume");
        bid = Helpers.getBD(o, "bid");
        ask = Helpers.getBD(o, "ask");
        createdAt = Helpers.getString(o, "created_at");
        book = BitsoBook.valueOf(Helpers.getString(o, "book").toUpperCase());
    }
}
