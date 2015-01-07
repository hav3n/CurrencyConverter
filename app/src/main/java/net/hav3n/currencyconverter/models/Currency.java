package net.hav3n.currencyconverter.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Nikhil Peter Raj
 */
public class Currency implements Parcelable {

    Long _id;
    double rate;
    String name;
    String code;

    public Currency() {

    }

    public Currency(double rate, String name, String code) {
        this.rate = rate;
        this.name = name;
        this.code = code;

    }

    private Currency(Parcel in) {
        this._id = in.readLong();
        this.rate = in.readDouble();
        this.name = in.readString();
        this.code = in.readString();
    }

    public static ArrayList<Currency> generateCurrencies(HashMap<String, String> mappings, RateResponse response) {

        HashMap<String, Double> rates;

        rates = response.getRates();

        ArrayList<Currency> currencies = new ArrayList<Currency>();

        for (String key : rates.keySet()) {

            double rate = rates.get(key);
            String name = mappings.get(key);

            Currency c = new Currency(rate, name, key);

            currencies.add(c);
        }

        return currencies;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public static final Creator<Currency> CREATOR = new Creator<Currency>() {
        @Override
        public Currency createFromParcel(Parcel source) {
            return new Currency(source);
        }

        @Override
        public Currency[] newArray(int size) {
            return new Currency[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this._id);
        dest.writeDouble(this.rate);
        dest.writeString(this.name);
        dest.writeString(this.code);
    }


    @Override
    public String toString() {
        return "Currency{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", rate=" + rate +
                '}';
    }
}
