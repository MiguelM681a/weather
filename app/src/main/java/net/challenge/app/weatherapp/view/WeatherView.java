package net.challenge.app.weatherapp.view;


import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.challenge.app.weatherapp.R;

public class WeatherView extends LinearLayout {

    public TextView hourText;
    public ImageView imageForecast;
    public TextView temperatureText;
    /**
     * Inflates the views in the layout.
     *
     * @param context
     *           the current context for the view.
     */
    public WeatherView(Context context) {
        super(context);
        initializeViews(context);
    }

    public WeatherView(Context context, AttributeSet attrs){
        super(context, attrs);
        initializeViews(context);
    }

    public WeatherView(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
        initializeViews(context);
    }


    /**
     * Inflates the views in the layout.
     *
     * @param context
     *           the current context for the view.
     */
    private void initializeViews(Context context){
        LayoutInflater layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.weather_view, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.hourText = this.findViewById(R.id.hour);
        this.imageForecast = this.findViewById(R.id.image_forecast);
        this.temperatureText = this.findViewById(R.id.temperature);
    }
}

