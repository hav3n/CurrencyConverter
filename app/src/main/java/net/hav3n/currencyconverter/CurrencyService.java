package net.hav3n.currencyconverter;


import net.hav3n.currencyconverter.models.RateResponse;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

import java.util.HashMap;

/**
 * @author Nikhil Peter Raj
 */
public interface CurrencyService {

    @GET("/latest.json")
    public void getRates(@Query("app_id") String key, Callback<RateResponse> callback);

    @GET("/currencies.json")
    public void getCurrencyMappings(@Query("app_id") String key, Callback<HashMap<String, String>> callback);
}
