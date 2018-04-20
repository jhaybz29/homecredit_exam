package weather.caalim.exam.homecredit.weatherlist;

public interface FragmentCommunicator
{
    public void refreshWeatherList();
    public void refreshWeatherDetail();
    public void loadWeatherDetailFragment(int pPosition);
    public void clearAllBackStack();
}
