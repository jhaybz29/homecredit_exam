package weather.caalim.exam.homecredit.weatherlist;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements FragmentCommunicator
{

    public static final String TAG = "MainActivity";

    private CountDownTimer mSplashCountdown;
    private SplashFragment mSplashFragment;

    private WeatherListFragment mWeatherListFragment;
    private WeatherDetailFragment mWeatherDetailFragment;
    private ArrayList<WeatherListItem> mWeatherListItemsArray = new ArrayList<>();
    private int currentWeatherDetailPosition = 0;

    private FragmentManager mFragmentManager;

    private int taskMode = Constants.TASK_MODE_INITIAL_DOWNLOAD;
    private boolean mIsLoadingErrorOccured = false;
    private boolean mIsLoadingFinished = false;
    private boolean mIsLoadOnAsycn = false;

    private DataLoader mDataLoader;
    private SharedPreferences mSharedPref;
    private SharedPreferences.Editor mSharedPrefEditor;

    private TaskGetOnlineData killableTask;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDataLoader = new DataLoader();
        mSharedPref = getApplicationContext().getSharedPreferences(Constants.SHARED_PREF_NAME, MODE_PRIVATE);
        mSharedPrefEditor = mSharedPref.edit();

        mSplashFragment = new SplashFragment();
        mWeatherListFragment = new WeatherListFragment();
        mWeatherDetailFragment = new WeatherDetailFragment();
        mFragmentManager = getFragmentManager();

        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.add(R.id.id_activity_main, mSplashFragment, SplashFragment.TAG);
        transaction.commit();

        //Starting the timer
        mSplashCountdown = new CountDownTimer(2000, 1000)
        {
            @Override
            public void onTick(long pMillisUntilFinished)
            {

            }

            @Override
            public void onFinish()
            {
                if(mIsLoadingFinished == true)
                {
                    loadLocalData();
                    loadWeatherListFragment();
                }
                else
                {
                    mSplashFragment.setTextLabel(Constants.WARNING_JSONDL_NOT_FINISHED);
                    mIsLoadOnAsycn = true;
                }

                mSplashCountdown.cancel();
            }
        };

    }

    private void loadLocalData()
    {
        String[] londonData = loadJSONStringFromInternal(Constants.INTERNAL_STORAGE_FILE_NAME_LONDON).split("%");
        WeatherListItem londonWeatherListItem = new WeatherListItem();
        londonWeatherListItem.setLocation(londonData[0]);
        londonWeatherListItem.setWeather(londonData[1]);
        londonWeatherListItem.setWeatherDescript(londonData[2]);
        londonWeatherListItem.setTemp(londonData[4]);
        londonWeatherListItem.setIcon(loadWeatherIconFromInternal(Constants.WEATHER_ICON_LONDON_FILENAME));

        String[] pragueData = loadJSONStringFromInternal(Constants.INTERNAL_STORAGE_FILE_NAME_PRAGUE).split("%");
        WeatherListItem pragueWeatherListItem = new WeatherListItem();
        pragueWeatherListItem.setLocation(pragueData[0]);
        pragueWeatherListItem.setWeather(pragueData[1]);
        pragueWeatherListItem.setWeatherDescript(pragueData[2]);
        pragueWeatherListItem.setTemp(pragueData[4]);
        pragueWeatherListItem.setIcon(loadWeatherIconFromInternal(Constants.WEATHER_ICON_PRAGUE_FILENAME));

        String[] sanFraData = loadJSONStringFromInternal(Constants.INTERNAL_STORAGE_FILE_NAME_SANFRA).split("%");
        WeatherListItem sanFraWeatherListItem = new WeatherListItem();
        sanFraWeatherListItem.setLocation(sanFraData[0]);
        sanFraWeatherListItem.setWeather(sanFraData[1]);
        sanFraWeatherListItem.setWeatherDescript(sanFraData[2]);
        sanFraWeatherListItem.setTemp(sanFraData[4]);
        sanFraWeatherListItem.setIcon(loadWeatherIconFromInternal(Constants.WEATHER_ICON_SANFRA_FILENAME));

        mWeatherListItemsArray.add(londonWeatherListItem);
        mWeatherListItemsArray.add(pragueWeatherListItem);
        mWeatherListItemsArray.add(sanFraWeatherListItem);
    }

    private void loadWeatherListFragment()
    {
        mWeatherListFragment.setItemArray(mWeatherListItemsArray);

        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.id_activity_main, mWeatherListFragment, WeatherListFragment.TAG);
        transaction.commit();

    }

    public void loadWeatherDetailFragment(int pPosition)
    {
        currentWeatherDetailPosition = pPosition;
        mWeatherDetailFragment.setData(mWeatherListItemsArray.get(pPosition));
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.addToBackStack(WeatherListFragment.TAG);
        transaction.replace(R.id.id_activity_main, mWeatherDetailFragment, WeatherDetailFragment.TAG);
        transaction.commit();
    }

    public void clearBackStackWeatherList()
    {
        for(int i = 0; i < mFragmentManager.getBackStackEntryCount(); ++i)
        {
            mFragmentManager.popBackStack();
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        if (mSharedPref.getBoolean(Constants.FIRST_RUN_PREFS, true))
        {
            if(isNetworkAvailable() == true)
            {

                Log.i(TAG, "First Time Run Downloading Data online");

                mSplashCountdown.start();
                TaskGetOnlineData startingTask = new TaskGetOnlineData();
                startingTask.execute();
                killableTask = startingTask;

                mSharedPrefEditor.putBoolean(Constants.FIRST_RUN_PREFS, false);
                mSharedPrefEditor.putString(Constants.SHARED_PREF_LAST_DATE_RUN, getCurrentDate());
                mSharedPrefEditor.commit();
            }
            else
            {
                Log.i(TAG, "Warning there is no internet!");
                mSplashFragment.setTextLabel(Constants.WARNINGS_NO_INTERNET);
                mIsLoadingErrorOccured = true;
            }

        }
        else
        {
            String lastDateRun = mSharedPref.getString(Constants.SHARED_PREF_LAST_DATE_RUN, "none?");
            String currentData = getCurrentDate();
            if(lastDateRun.equals(currentData))
            {
                Log.i(TAG, "No need for internet loading local data");
                mIsLoadingFinished = true;
                mSplashCountdown.start();
            }
            else
            {
                if(isNetworkAvailable() == true)
                {
                    Log.i(TAG, "This was the next day, downloading new data");

                    mSplashCountdown.start();
                    TaskGetOnlineData startingTask = new TaskGetOnlineData();
                    startingTask.execute();
                    killableTask = startingTask;
                }
                else
                {
                    Log.i(TAG, "Warning there is no internet!");
                    mSplashFragment.setTextLabel(Constants.WARNINGS_NO_INTERNET);
                    mIsLoadingErrorOccured = true;
                }

            }
        }

    }

    @Override
    protected void onPause()
    {
        super.onPause();
        mSplashCountdown.cancel();
        if(killableTask != null)
        {
            killableTask.cancel(true);
        }
        mSharedPrefEditor.putString(Constants.SHARED_PREF_LAST_DATE_RUN, getCurrentDate());
        mSharedPrefEditor.commit();
    }

    public String getCurrentDate()
    {
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int year = calendar.get(Calendar.YEAR);
        String currDate = ""+month+"-"+day+"-"+year;

        return currDate;
    }

    private boolean isNetworkAvailable()
    {
        ConnectivityManager connectivityManager  = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void saveJSONStringToInternal(String pFileName, String pContent)
    {
        String filename = pFileName;
        String fileContents = pContent;
        FileOutputStream outputStream;

        try
        {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(fileContents.getBytes());
            outputStream.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private String loadJSONStringFromInternal(String pFileName)
    {
        StringBuffer retrivedJSONSB = new StringBuffer("");
        try
        {
            FileInputStream inputStream = openFileInput(pFileName);
            InputStreamReader streamReader = new InputStreamReader(inputStream);
            BufferedReader buffreader = new BufferedReader(streamReader);

            String readString = buffreader.readLine();

            while ( readString != null )
            {
                retrivedJSONSB.append(readString);
                readString = buffreader.readLine ( ) ;
            }
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
        return retrivedJSONSB.toString();
    }

    private void saveWeatherIconToInternal(Bitmap pBitmap, String pName)
    {
        String fileName = pName + ".png";
        File directory = getDir("icons", Context.MODE_PRIVATE);
        File dirFile = new File(directory, fileName);

        FileOutputStream fileOutputStream = null;
        try
        {
            fileOutputStream = new FileOutputStream(dirFile);
            pBitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (fileOutputStream != null)
                {
                    fileOutputStream.close();
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

    }

    public Bitmap loadWeatherIconFromInternal(String pName)
    {
        String fileName = pName + ".png";
        File directory = getDir("icons", Context.MODE_PRIVATE);
        File dirFile = new File(directory, fileName);

        FileInputStream inputStream = null;
        try
        {
            inputStream = new FileInputStream(dirFile);
            Log.i(TAG, "A BITMAP WAS RETURNED!!! "+pName);
            return BitmapFactory.decodeStream(inputStream);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (inputStream != null)
                {
                    inputStream.close();
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return null;
    }

    //Process the JSON String and returns only the needed strings
    public String processJSONStrings(String pJSONStringSet)
    {
        String processedString = "";
        try
        {
            JSONObject weatherDetailJO = new JSONObject(pJSONStringSet);
            String cityLocation = weatherDetailJO.getString(Constants.JSON_REQUEST_CITY_NAME);

            JSONArray weatherDataArray = weatherDetailJO.getJSONArray(Constants.JSON_REQUEST_WEATHER);
            String weather = weatherDataArray.getJSONObject(0).getString(Constants.JSON_REQUEST_WEATHER_MAIN);
            String weatherDescript = weatherDataArray.getJSONObject(0).getString(Constants.JSON_REQUEST_WEATHER_DESCRIPTION);
            String weatherIcon = weatherDataArray.getJSONObject(0).getString(Constants.JSON_REQUEST_WEATHER_ICON);

            JSONObject temperatureData = weatherDetailJO.getJSONObject(Constants.JSON_REQUEST_TEMP);
            String temperature = temperatureData.getString(Constants.JSON_REQUEST_TEMP_KELVIN);

            Log.i(TAG, "cityLocation: "+cityLocation);
            Log.i(TAG, "weather: "+weather);
            Log.i(TAG, "weatherDescript: "+weatherDescript);
            Log.i(TAG, "weatherIcon: "+weatherIcon);
            Log.i(TAG, "temperature: "+temperature);

            //[0, 1, 2, 3, 4]
            processedString = cityLocation+"%"+weather+"%"+weatherDescript+"%"+weatherIcon+"%"+temperature;

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return processedString;
    }

    public String getIconLinkString(String pJSONStringSet)
    {
        String[] stringArray = pJSONStringSet.split("%");
        Log.i(TAG, "Checking the stringArray: "+stringArray[3]);
        String iconURI = "http://openweathermap.org/img/w/"+stringArray[3]+".png";

        return iconURI;
    }

    public void refreshWeatherList()
    {
        //download again and refresh
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.id_activity_main, mSplashFragment, SplashFragment.TAG);
        transaction.commit();

        taskMode = Constants.TASK_MODE_REFRESH_WEATHER_LIST;
        mWeatherListItemsArray.clear();
        TaskGetOnlineData startingTask = new TaskGetOnlineData();
        startingTask.execute();
        killableTask = startingTask;

    }

    public void refreshWeatherDetail()
    {
        //download again and refresh
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.id_activity_main, mSplashFragment, SplashFragment.TAG);
        transaction.commit();

        taskMode = Constants.TASK_MODE_REFRESH_WEATHER_DETAIL;
        mWeatherListItemsArray.clear();
        TaskGetOnlineData startingTask = new TaskGetOnlineData();
        startingTask.execute();
        killableTask = startingTask;
    }

    public void clearAllBackStack()
    {

        if (mFragmentManager.getBackStackEntryCount() > 0)
        {
            FragmentManager.BackStackEntry first = mFragmentManager.getBackStackEntryAt(0);
            mFragmentManager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    //======= AsyncTask =======
    //Downloads the JSON String
    //Downloads all the Icons
    //Saves all the data locally
    private class TaskGetOnlineData extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids)
        {
            downloadAllNetData();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);

            switch (taskMode)
            {
                case Constants.TASK_MODE_INITIAL_DOWNLOAD:
                {
                    mIsLoadingFinished = true;
                    if (mIsLoadOnAsycn == true)
                    {
                        loadLocalData();
                        loadWeatherListFragment();
                    }
                    break;
                }

                case Constants.TASK_MODE_REFRESH_WEATHER_LIST:
                {
                    loadLocalData();
                    loadWeatherListFragment();
                    break;
                }

                case Constants.TASK_MODE_REFRESH_WEATHER_DETAIL:
                {
                    loadLocalData();
                    loadWeatherDetailFragment(currentWeatherDetailPosition);
                    break;
                }
            }

            killableTask = null;
        }

    }

    private void downloadAllNetData()
    {
        HTTPHandler httpHandler = new HTTPHandler();
        String londonWeatherData = httpHandler.makeServiceCallString(Constants.WEATHER_REQUEST_LONDON);
        String pragueWeatherData = httpHandler.makeServiceCallString(Constants.WEATHER_REQUEST_PRAGUE);
        String sanFraWeatherData = httpHandler.makeServiceCallString(Constants.WEATHER_REQUEST_SANFRA);

        if(londonWeatherData != null)
        {
            String processedLondonWeatherData = processJSONStrings(londonWeatherData);
            String londonWeatherIconURI = getIconLinkString(processedLondonWeatherData);
            Bitmap londonWeatherIconBitmap = httpHandler.makeServiceCallBitmap(londonWeatherIconURI);

            saveJSONStringToInternal(Constants.INTERNAL_STORAGE_FILE_NAME_LONDON, processedLondonWeatherData);
            saveWeatherIconToInternal(londonWeatherIconBitmap, Constants.WEATHER_ICON_LONDON_FILENAME);
        }

        if(pragueWeatherData != null)
        {
            String processedPragueWeatherData = processJSONStrings(pragueWeatherData);
            String pragueWeatherIconURI = getIconLinkString(processedPragueWeatherData);
            Bitmap pragueWeatherIconBitmap = httpHandler.makeServiceCallBitmap(pragueWeatherIconURI);

            saveJSONStringToInternal(Constants.INTERNAL_STORAGE_FILE_NAME_PRAGUE, processedPragueWeatherData);
            saveWeatherIconToInternal(pragueWeatherIconBitmap, Constants.WEATHER_ICON_PRAGUE_FILENAME);
        }

        if(sanFraWeatherData != null)
        {
            String processedSanFraWeatherData = processJSONStrings(sanFraWeatherData);
            String sanFraWeatherIconURI = getIconLinkString(processedSanFraWeatherData);
            Bitmap sanFraWeatherIconBitmap = httpHandler.makeServiceCallBitmap(sanFraWeatherIconURI);

            saveJSONStringToInternal(Constants.INTERNAL_STORAGE_FILE_NAME_SANFRA, processedSanFraWeatherData);
            saveWeatherIconToInternal(sanFraWeatherIconBitmap, Constants.WEATHER_ICON_SANFRA_FILENAME);
        }
    }

}
