package net.hav3n.currencyconverter;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Nikhil Peter Raj
 */
public class Highlighter {

    private Context context;

    public Highlighter(Context context) {
        this.context = context;
    }

    public Spannable highlight(String text, String searchQuery) {
        Spannable highlight = Spannable.Factory.getInstance().newSpannable(text);

        if (searchQuery == null) {
            return highlight;
        }

        Pattern pattern = Pattern.compile("(?i)(" + searchQuery.trim().replaceAll("\\s+", "|") + ")");
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            highlight.setSpan(
                    new ForegroundColorSpan(Color.parseColor("#33b5e5")),
                    matcher.start(),
                    matcher.end(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        return highlight;
    }
}
