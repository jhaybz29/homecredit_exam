package weather.caalim.exam.homecredit.weatherlist;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.logging.Logger;

/**
 * Very Primitive HTTP handler with some error checks
 */
public class HTTPHandler
{
    private String className = this.getClass().getSimpleName();

    public HTTPHandler()
    {
    }

    public String makeServiceCallString(String reqUrl) {

        String response = null;

        try
        {
            URL url = new URL(reqUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            InputStream in = new BufferedInputStream(conn.getInputStream());
            response = convertStreamToString(in);
        }
        catch (MalformedURLException e)
        {
            Log.e(className, "MalformedURLException: " + e.getMessage());
        }
        catch (ProtocolException e)
        {
            Log.e(className, "ProtocolException: " + e.getMessage());
        }
        catch (IOException e)
        {
            Log.e(className, "IOException: " + e.getMessage());
        }
        catch (Exception e)
        {
            Log.e(className, "Exception: " + e.getMessage());
        }

        return response;
    }

    public Bitmap makeServiceCallBitmap(String reqUrl)
    {

        Bitmap bitmap = null;
        InputStream inputStream = null;
        URL url = null;

        try
        {

            url = new URL(reqUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            inputStream = urlConnection.getInputStream();
            bitmap = BitmapFactory.decodeStream(inputStream);

        }
        catch (MalformedURLException e)
        {
            Log.e(className, "MalformedURLException: " + e.getMessage());
        }
        catch (IOException e)
        {
            Log.e(className, "IOException: " + e.getMessage());
        }
        finally
        {
            try
            {
                inputStream.close();
            }
            catch (IOException e)
            {
                Log.e(className, "IOException: " + e.getMessage());
            }
        }

        return bitmap;
    }

    private String convertStreamToString(InputStream is)
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try
        {
            while ((line = reader.readLine()) != null)
            {
                sb.append(line).append('\n');
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                is.close();
            }
            catch (IOException e)
            {
                Log.e(className, "Could not convert: " + e.getMessage());
            }
        }

        return sb.toString();
    }
}

