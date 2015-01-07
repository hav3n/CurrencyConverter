package net.hav3n.currencyconverter.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import net.hav3n.currencyconverter.*;
import net.hav3n.currencyconverter.adapters.CurrencyAdapter;
import net.hav3n.currencyconverter.models.Currency;
import net.hav3n.currencyconverter.models.RateResponse;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


public class MainActivity extends ActionBarActivity {

    private static final String TAG = "MainActivity";

    public static final String KEY_CURRENCY = "key_currency";
    public static final String KEY_AMOUNT = "key_amount";

    public static final String KEY_TIMESTAMP = "key_timestamp";

    private CurrencyService mCurrencyService;
    private CurrencySQLiteOpenHelper mHelper;
    CurrencyAdapter mCurrencyAdapter;

    HashMap<String, String> mCurrencyMappings;
    ArrayList<Currency> mCurrencies;
    RateResponse mResponse;

    ListView mCurrencyList;
    EditText mCurrencyEditText;
    SearchView mSearchView;
    SmoothProgressBar mProgressBar;

    String key;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        key = getString(R.string.key);

        mCurrencyList = (ListView) findViewById(R.id.list_currencies);

        mCurrencyEditText = (EditText) findViewById(R.id.edit_amount);
        mSearchView = (SearchView) findViewById(R.id.search_filter);
        mProgressBar = (SmoothProgressBar) findViewById(R.id.progress_loading);

        mProgressBar.setVisibility(View.INVISIBLE);


        final RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint("http://openexchangerates.org/api")
                .build();

        mCurrencyService = adapter.create(CurrencyService.class);

        mHelper = CurrencySQLiteOpenHelper.getInstance(this);


        if (mHelper.isDatabaseEmpty()) {
            //Download the data
            Log.i(TAG, "Downloading data");

            downloadData();
        } else {
            //Db has data

            Log.i(TAG, "Loading data from db");

            mCurrencyAdapter = new CurrencyAdapter(this);
            mCurrencyList.setAdapter(mCurrencyAdapter);
        }

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mCurrencyAdapter.updateAdapter(newText);
                return true;
            }
        });

        mSearchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    mCurrencyAdapter.resetAdapter();
                    mSearchView.setQuery("", false);
                }

            }
        });

        mCurrencyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mCurrencyEditText.getText().toString().isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Please Enter Amount")
                            .setMessage("Amount cannot be empty!")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });

                    builder.show();
                } else {
                    Currency currency = (Currency) mCurrencyList.getItemAtPosition(position);

                    Intent intent = new Intent(MainActivity.this, DisplayActivity.class);
                    intent.putExtra(KEY_CURRENCY, currency);
                    intent.putExtra(KEY_AMOUNT, Double.parseDouble(mCurrencyEditText.getText().toString()));
                    startActivity(intent);
                }
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_refresh:
                downloadData();
                return true;

            case R.id.action_about:
                AboutDialogFragment fragment = AboutDialogFragment.newInstance();
                fragment.show(getFragmentManager(), "FRAGMENT_ABOUT");
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void downloadData() {

        if (!isNetworkConnected()) {
            ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(
                    getString(R.string.title_error_no_network)
                    , getString(R.string.message_error_no_network));

            fragment.show(getFragmentManager(), "FRAGMENT_ERROR");
        } else {

            mProgressBar.setVisibility(View.VISIBLE);

            mCurrencyService.getCurrencyMappings(key,new Callback<HashMap<String, String>>() {
                @Override
                public void success(HashMap<String, String> hashMaps, Response response) {
                    Log.i(TAG, "Got rates:" + hashMaps.toString());
                    mCurrencyMappings = hashMaps;

                    mCurrencyService.getRates(key, new Callback<RateResponse>() {
                        @Override
                        public void success(RateResponse rateResponse, Response response) {
                            Log.i(TAG, "Got names: " + rateResponse.getRates().toString());

                            mResponse = rateResponse;

                            Log.i(TAG, "Timestamp: " + rateResponse.getTimestamp());

                            SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
                            prefs.edit()
                                    .putLong(KEY_TIMESTAMP, rateResponse.getTimestamp())
                                    .apply();

                            if (mCurrencyMappings != null) {
                                mCurrencies = Currency.generateCurrencies(mCurrencyMappings, mResponse);

                                Log.i(TAG, "Generated Currencies: " + Arrays.toString(mCurrencies.toArray()));

                                mHelper.addCurrencies(mCurrencies);

                                mCurrencyAdapter = new CurrencyAdapter(MainActivity.this);
                                mCurrencyList.setAdapter(mCurrencyAdapter);

                                mProgressBar.setVisibility(View.INVISIBLE);
                            }

                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Log.e(TAG, error.getLocalizedMessage());
                            mProgressBar.setVisibility(View.INVISIBLE);
                        }
                    });
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e(TAG, error.getLocalizedMessage());
                }
            });
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

}
