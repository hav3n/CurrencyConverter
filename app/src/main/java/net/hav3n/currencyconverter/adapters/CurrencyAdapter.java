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
public class CurrencyAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<Currency> currencies;

    private CurrencySQLiteOpenHelper helper;

    private Highlighter highlighter;

    private String queryText = null;

    private class ViewHolder {
        public TextView currencyCodeView;
        public TextView currencyNameView;
    }

    public CurrencyAdapter(Context context) {
        this.context = context;
        helper = CurrencySQLiteOpenHelper.getInstance(context);
        highlighter = new Highlighter(context);
        this.currencies = helper.getCurrencies();
        inflater = LayoutInflater.from(context);
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
            convertView = inflater.inflate(R.layout.list_currency_item, parent, false);
            holder = new ViewHolder();
            holder.currencyCodeView = (TextView) convertView.findViewById(R.id.text_currency_code);
            holder.currencyNameView = (TextView) convertView.findViewById(R.id.text_currency_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Currency currency = currencies.get(position);

        holder.currencyCodeView.setText(highlighter.highlight(currency.getCode(),queryText));
        holder.currencyNameView.setText(highlighter.highlight(currency.getName(),queryText));

        return convertView;

    }
}
