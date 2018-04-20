package weather.caalim.exam.homecredit.weatherlist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

public class WeatherListAdapter extends ArrayAdapter<WeatherListItem>
{

    private Context mContext;
    private int mResource;

    public WeatherListAdapter(Context pContext, int pResource, ArrayList<WeatherListItem> pObjects)
    {
        super(pContext, pResource, pObjects);
        mContext = pContext;
        mResource = pResource;
    }

    @Override
    public View getView(int pPosition, View pConvertView, ViewGroup pParent)
    {
        String location = getItem(pPosition).getLocation();
        String weather = getItem(pPosition).getWeather();
        String temp = getItem(pPosition).getTemp();

        LayoutInflater inflater = LayoutInflater.from(mContext);
        pConvertView = inflater.inflate(mResource, pParent, false);

        TextView locationLabel = pConvertView.findViewById(R.id.location_label);
        TextView weatherLabel = pConvertView.findViewById(R.id.weather_label);
        TextView tempLabel = pConvertView.findViewById(R.id.temp_label);

        float tempK = Float.parseFloat(temp);
        float tempC = tempK - 273.15f;
        String tempFinal = String.format(Locale.ROOT,"%.2f", tempC);

        locationLabel.setText(location);
        weatherLabel.setText(weather);
        tempLabel.setText(tempFinal+" (Celsius)");

        return pConvertView;
    }
}
