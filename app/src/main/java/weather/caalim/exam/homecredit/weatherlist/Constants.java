package weather.caalim.exam.homecredit.weatherlist;

public class Constants
{
    public static final String FIRST_RUN_PREFS = "isFirstRun";
    public static final String SHARED_PREF_NAME = "weather.caalim.exam.homecredit.weatherlist";
    public static final String SHARED_PREF_LAST_DATE_RUN = "lastDateRun";

    public static final String INTERNAL_STORAGE_FILE_NAME = "homecreditexam";
    public static final String INTERNAL_STORAGE_FILE_NAME_LONDON = "dataLondon";
    public static final String INTERNAL_STORAGE_FILE_NAME_PRAGUE = "dataPrague";
    public static final String INTERNAL_STORAGE_FILE_NAME_SANFRA = "dataSanfra";

    public static final String WEATHER_ICON_LONDON_FILENAME         = "londonWeatherIcon";
    public static final String WEATHER_ICON_PRAGUE_FILENAME         = "pragueWeatherIcon";
    public static final String WEATHER_ICON_SANFRA_FILENAME         = "sanfraWeatherIcon";

    public static final String WARNINGS_NO_INTERNET = "The app initially requires internet to run, please check your internet and try again";
    public static final String WARNING_JSONDL_NOT_FINISHED = "JSON Data not yet finished downloading after 2 seconds, please wait a little longer";

    //Changed the ID if needed. Current id is = e8b5fd8d5a447dda0576012ad928a306
    public static final String WEATHER_REQUEST_LONDON = "http://api.openweathermap.org/data/2.5/weather?id=2643741&appid=e8b5fd8d5a447dda0576012ad928a306";
    public static final String WEATHER_REQUEST_PRAGUE = "http://api.openweathermap.org/data/2.5/weather?id=3067696&appid=e8b5fd8d5a447dda0576012ad928a306";
    public static final String WEATHER_REQUEST_SANFRA = "http://api.openweathermap.org/data/2.5/weather?id=5391959&appid=e8b5fd8d5a447dda0576012ad928a306";

    public static final String JSON_REQUEST_CITY_NAME               = "name";

    public static final String JSON_REQUEST_WEATHER                 = "weather";
    public static final String JSON_REQUEST_WEATHER_MAIN            = "main";
    public static final String JSON_REQUEST_WEATHER_DESCRIPTION     = "description";
    public static final String JSON_REQUEST_WEATHER_ICON            = "icon";

    public static final String JSON_REQUEST_TEMP                    = "main";
    public static final String JSON_REQUEST_TEMP_KELVIN             = "temp";

    public static final int TASK_MODE_INITIAL_DOWNLOAD              = 0;
    public static final int TASK_MODE_REFRESH_WEATHER_LIST          = 1;
    public static final int TASK_MODE_REFRESH_WEATHER_DETAIL        = 2;

}
