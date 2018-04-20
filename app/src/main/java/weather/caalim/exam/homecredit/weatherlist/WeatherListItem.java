package weather.caalim.exam.homecredit.weatherlist;

import android.graphics.Bitmap;

public class WeatherListItem
{

    private Bitmap mIcon;
    private String mLocation = "Location";
    private String mWeather = "Weather";
    private String mWeatherDescript = "Weather Description";
    private String mTemp = "Temperature";

    public WeatherListItem()
    {
    }

    public Bitmap getIcon()
    {
        return mIcon;
    }

    public void setIcon(Bitmap pBitmap)
    {
        mIcon = pBitmap;
    }

    public String getLocation()
    {
        return mLocation;
    }

    public void setLocation(String pLocation)
    {
        mLocation = pLocation;
    }

    public String getWeather()
    {
        return mWeather;
    }

    public void setWeather(String pWeather)
    {
        mWeather = pWeather;
    }

    public String getWeatherDescript()
    {
        return mWeatherDescript;
    }

    public void setWeatherDescript(String pWeatherDescript)
    {
        mWeatherDescript = pWeatherDescript;
    }

    public String getTemp()
    {
        return mTemp;
    }

    public void setTemp(String pTemp)
    {
        mTemp = pTemp;
    }
}
