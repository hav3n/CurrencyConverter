package net.hav3n.currencyconverter.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import net.hav3n.currencyconverter.CurrencySQLiteOpenHelper;
import net.hav3n.currencyconverter.Highlighter;
import net.hav3n.currencyconverter.R;
import net.hav3n.currencyconverter.models.Currency;

import java.util.List;

/**
 * @author Nikhil Peter Raj
 */
public class DisplayAdapter extends BaseAdapter {

    private Context c;
    private double rate;
    private double amount;
    private LayoutInflater inflater;
    private List<Currency> currencies;

    private CurrencySQLiteOpenHelper helper;
    private Highlighter highlighter;

    private String queryText = null;

    public DisplayAdapter(Context c, double rate, double amount) {
        this.c = c;
        this.rate = rate;
        this.amount = amount;
        inflater = LayoutInflater.from(c);
        highlighter = new Highlighter(c);
        helper = CurrencySQLiteOpenHelper.getInstance(c);
        currencies = helper.getCurrencies();
    }

    private class ViewHolder {
        public TextView displayName;
        public TextView displayCurrency;
    }

    public void updateAdapter(String query) {
        queryText = query;
        this.currencies = helper.getCurrenciesWithCode(queryText);
        notifyDataSetChanged();
    }

    public void resetAdapter() {
        this.currencies = helper.getCurrencies();
        queryText = null;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return currencies.size();
    }

    @Override
    public Object getItem(int position) {
        return currencies.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.list_display_item, parent, false);
            holder.displayCurrency = (TextView) convertView.findViewById(R.id.text_display_currency);
            holder.displayName = (TextView) convertView.findViewById(R.id.text_display_name);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Currency currency = currencies.get(position);

        double calculatedAmount = currency.getRate() * (1 / rate) * amount;

        holder.displayName.setText(highlighter.highlight(String.format("%.2f", calculatedAmount) + " " + currency.getCode(), queryText));
        holder.displayCurrency.setText(highlighter.highlight(currency.getName(), queryText));

        return convertView;

    }
}
