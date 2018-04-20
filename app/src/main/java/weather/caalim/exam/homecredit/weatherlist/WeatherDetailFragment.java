package weather.caalim.exam.homecredit.weatherlist;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

public class WeatherDetailFragment extends Fragment
{
    public static final String TAG = "WeatherDetailFragment";

    private View mLocalView;
    private ImageView mIcon;
    private TextView mLocationText;
    private TextView mWeatherText;
    private TextView mWeatherDescriptText;
    private TextView mTempText;
    private Button mRefreshButton;

    private WeatherListItem mWeatherItem;

    @Override
    public View onCreateView(LayoutInflater pInflater, ViewGroup pContainer, Bundle pSavedInstanceState)
    {
        mLocalView = pInflater.inflate(R.layout.weatherdetail_layout, pContainer, false);

        mIcon = mLocalView.findViewById(R.id.weather_detail_icon);
        mLocationText = mLocalView.findViewById(R.id.weather_detail_location_label);
        mWeatherText = mLocalView.findViewById(R.id.weather_detail_weather_label);
        mWeatherDescriptText = mLocalView.findViewById(R.id.weather_detail_weather_detail_label);
        mTempText = mLocalView.findViewById(R.id.weather_detail_temp_label);
        mRefreshButton = mLocalView.findViewById(R.id.weather_detail_refresh_button);

        Bitmap smallIcon = mWeatherItem.getIcon();
        Bitmap resizedIcon = smallIcon.createScaledBitmap(smallIcon, 500, 500, false);
        mIcon.setImageBitmap(resizedIcon);
        mLocationText.setText(mWeatherItem.getLocation());
        mWeatherText.setText(mWeatherItem.getWeather());
        mWeatherDescriptText.setText(mWeatherItem.getWeatherDescript());

        float tempK = Float.parseFloat(mWeatherItem.getTemp());
        float tempC = tempK - 273.15f;
        String tempFinal = String.format(Locale.ROOT,"%.2f", tempC);

        mTempText.setText(tempFinal+" (Celsius)");

        mRefreshButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                FragmentCommunicator fragCom = (FragmentCommunicator) getActivity();
                fragCom.refreshWeatherDetail();
            }
        });

        return mLocalView;
    }

    //Call this first
    public void setData(WeatherListItem pWeatherItem)
    {
        mWeatherItem = pWeatherItem;
    }

}
