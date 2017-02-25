package Utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import Exceptions.BitsoExceptionNotExpectedValue;
import models.BitsoBook;

public class Helpers {
    private static final String PATH = "src/test/java/JSONFiles/";

    private static final List<Field> getAllFields(List<Field> fields, Class<?> type) {
        fields.addAll(Arrays.asList(type.getDeclaredFields()));
        if (type.getSuperclass() != null) {
            fields = getAllFields(fields, type.getSuperclass());
        }
        return fields;
    }

    public static final String fieldPrinter(Object obj) {
        StringBuilder sb = new StringBuilder();
        sb.append("==============");
        List<Field> fields = getAllFields(new ArrayList<Field>(), obj.getClass());
        for (Field f : fields) {
            try {
                Object o = f.get(obj);
                sb.append('\n');
                sb.append(f.getName());
                sb.append(": ");
                sb.append(o);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        sb.append("\n==============\n");
        return sb.toString();
    }

    public static final void printStackTrace(PrintStream out) {
        StringBuilder sb = new StringBuilder();
        sb.append("Printing Stack Trace\n");
        for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {
            sb.append('\t');
            sb.append(ste);
            sb.append('\n');
        }
        out.print(sb);
    }

    public static final void printStackTrace() {
        printStackTrace(System.err);
    }

    public static JSONObject parseJson(String json) {
        if (json == null) {
            return null;
        }
        try {
            return new JSONObject(json);
        } catch (JSONException e) {
            System.err.println("Unable to parse json: " + json);
            e.printStackTrace();
        }
        return null;
    }

    public static JSONArray parseJsonArray(String json) {
        if (json == null) {
            return null;
        }
        try {
            return new JSONArray(json);
        } catch (JSONException e) {
            System.err.println("Unable to parse json array: " + json);
            e.printStackTrace();
        }
        return null;
    }
    
    public static int getInt(JSONObject o, String key) throws JSONException {
        if (o.has(key)) {
            return o.getInt(key);
        } else {
            System.err.println("No " + key + ": " + o);
            Helpers.printStackTrace();
        }
        return -1;
    }

    public static String getString(JSONObject o, String key) throws JSONException {
        if (o.has(key)) {
            return o.getString(key);
        } else {
            System.err.println("No " + key + ": " + o);
            Helpers.printStackTrace();
        }
        return null;
    }

    public static BigDecimal getBD(JSONObject o, String key) throws JSONException {
        if (o.has(key)) {
            return new BigDecimal(o.getString(key));
        } else {
            System.err.println("No " + key + ": " + o);
            Helpers.printStackTrace();
        }
        return null;
    }

    public static Integer getInteger(JSONObject o, String key) throws JSONException {
        if(o.has(key)){
            return o.getInt(key);
        }else{
            System.err.println("No " + key + ": " + o);
            Helpers.printStackTrace();
        }
        return null;
    }

    public static String[] parseJSONArray(JSONArray arrray) throws JSONException {
        int totalElements = arrray.length();
        String[] elements =  new String[totalElements];
        for(int i=0; i<totalElements; i++){
            elements[i] = arrray.getString(i);
        }
        return elements;
    }

    public static JSONObject getJSONFromFile(String fileName) {
        String jsonString = getJSONString(fileName);
        if(jsonString == null){
            return null;
        }
        return Helpers.parseJson(jsonString);
    }

    private static String getJSONString(String fileName) {
        BufferedReader br = null;
        String line = "";
        StringBuffer sb = new StringBuffer();
        try {
            FileReader fr = new FileReader(PATH + fileName);
            br = new BufferedReader(fr);
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            line = sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
            line = null;
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return line;
    }

    public static BitsoBook getBook(String book) {
        switch (book) {
            case "btc_mxn":
                return BitsoBook.BTC_MXN;
            case "eth_mxn":
                return BitsoBook.ETH_MXN;
            default:
                String exceptionMessage = book + "is not a supported book";
                throw new BitsoExceptionNotExpectedValue(exceptionMessage);
        }
    }
}
