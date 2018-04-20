package weather.caalim.exam.homecredit.weatherlist;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SplashFragment extends Fragment
{

    public static final String TAG = "SplashFragment";

    private TextView mSplashLabel;

    @Override
    public View onCreateView(LayoutInflater pInflater, ViewGroup pContainer, Bundle pSavedInstanceState)
    {
        return pInflater.inflate(R.layout.splash_layout, pContainer, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        mSplashLabel = getActivity().findViewById(R.id.splash_label);
    }

    public void setTextLabel(String pTextLabel)
    {
        mSplashLabel.setText(pTextLabel);
    }

}
