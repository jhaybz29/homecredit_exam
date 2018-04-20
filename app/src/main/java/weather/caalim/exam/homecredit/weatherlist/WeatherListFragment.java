package weather.caalim.exam.homecredit.weatherlist;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class WeatherListFragment extends Fragment
{
    public static final String TAG = "WeatherListFragment";

    private View mLocalView;
    private ListView mWeatherList;
    private ArrayList<WeatherListItem> itemArrays;
    private Button mRefreshButton;

    @Override
    public View onCreateView(LayoutInflater pInflater, ViewGroup pContainer, Bundle pSavedInstanceState)
    {

        mLocalView = pInflater.inflate(R.layout.weatherlist_layout, pContainer, false);
        mWeatherList = mLocalView.findViewById(R.id.weather_list_view);
        mRefreshButton = mLocalView.findViewById(R.id.weather_list_refresh_button);

        mRefreshButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                FragmentCommunicator fragCom = (FragmentCommunicator) getActivity();
                fragCom.refreshWeatherList();
            }
        });

        WeatherListAdapter weatherListAdapter = new WeatherListAdapter(getActivity(), R.layout.weather_list_item, itemArrays);
        mWeatherList.setAdapter(weatherListAdapter);

        mWeatherList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                FragmentCommunicator fragCom = (FragmentCommunicator) getActivity();
                fragCom.loadWeatherDetailFragment(position);
            }
        });

        return mLocalView;
    }

    public void setItemArray(ArrayList<WeatherListItem> pItemArray)
    {
        itemArrays = pItemArray;
    }
}
