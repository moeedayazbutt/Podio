package bamcorp.podio.web;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import bamcorp.podio.R;
import bamcorp.podio.data.ManagerPreferences;
import bamcorp.podio.ui.Popup;

/**
* Performs a single web service request task
* */
@SuppressWarnings("deprecation")
public class WebTask extends AsyncTask<String, Void, Void>
{
    /**
    * Listener to get callback for web service request task
    * */
    public interface WebTaskListener
    {
        /**
        * Gets called whenever a web service request task is successful
        *
        * @param
        * jsonObject  The JSON object received. In case the return value is a JSON array, it is automatically put into a JSON object with key = "ARRAY_RESPONSE"
        * */
        public void onResult(WebJsonObject jsonObject);

        /**
        * Gets called whenever a web service request task is cancelled
        * */
        public void onCancelled();


        /**
        * Gets called whenever a web service request task displays an error
        * */
        public void onError(String errorString);
    }

    public enum REQUEST_TYPE
    {
        GET,
        POST
    }

    WebTask base;
    private WebTaskListener webTaskListener;
    private REQUEST_TYPE type;
    private Activity baseActivity;
    private String serviceUrl = "";
    private ProgressDialog progressDialog;

    private final static int TIMEOUT = 120000;

    private String jsonResponse;

    boolean doPostInBackground = true;
    boolean showProgressDialog = true;
    boolean isCancelable = true;
    boolean isCancelled = false;
    boolean isInternalError = true;

    public final static String ARRAY_RESPONSE = "ARRAY_RESPONSE";

    public void init(Activity activity, String url, REQUEST_TYPE type)
    {
        base = this;

        this.baseActivity = activity;
        this.serviceUrl = activity.getResources().getString(R.string.data_base_url) + url;
        this.type = type;
    }

    protected void onPreExecute()
    {
        if (showProgressDialog)
        {
            progressDialog = new ProgressDialog(baseActivity);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(isCancelable);
            progressDialog.setMessage(baseActivity.getResources().getString(R.string.loading));

            progressDialog.setOnCancelListener(new OnCancelListener()
            {

                @Override
                public void onCancel(DialogInterface dialog)
                {
                    progressDialog.dismiss();
                    base.cancel(true);
                    isCancelled = true;
                    webTaskListener.onCancelled();
                }
            });

            progressDialog.show();
        }

    }

    @Override
    protected Void doInBackground(String... params)
    {
        isInternalError = true;
        jsonResponse = baseActivity.getResources().getString(R.string.error_connection) + " ";

        switch (type)
        {
            case POST:
                doWebPostTask(params[0]);
                break;
            case GET:
                doWebGetTask();
                break;
        }

        if (doPostInBackground)
            onTaskPostExecute();

        return null;
    }

    protected void onPostExecute(Void unused)
    {
        if (!doPostInBackground)
        {
            baseActivity.runOnUiThread(new Runnable()
            {

                @Override
                public void run()
                {
                    onTaskPostExecute();
                }
            });

        }
    }

    public void disableProgressDialog()
    {
        this.showProgressDialog = false;
    }

    private void doWebGetTask()
    {
        try
        {
            String accessToken = ManagerPreferences.loadString(baseActivity, ManagerPreferences.ACCESS_TOKEN);

            Log.v("GET request: ", serviceUrl + ", access token: " + accessToken);

            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(serviceUrl);

            httpGet.setHeader("Authorization", "OAuth2 " + accessToken);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();
            jsonResponse = convertStreamToString(httpEntity.getContent());
            isInternalError = false;

            Log.v("GET response: ", jsonResponse);
        } catch (Exception e)
        {
            jsonResponse += e.getMessage().toString();
            e.printStackTrace();
        }

    }

    private void doWebPostTask(final String jsonString)
    {
        try
        {
            HttpPost post = new HttpPost(serviceUrl);
            post.setHeader("Content-type", "application/json; charset=utf-8");

            post.setEntity(new StringEntity(jsonString, HTTP.UTF_8));

            HttpParams httpParameters = new BasicHttpParams();

            HttpConnectionParams.setConnectionTimeout(httpParameters, TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpParameters, TIMEOUT);
            HttpProtocolParams.setContentCharset(httpParameters, "utf-8");

            HttpClient client = new DefaultHttpClient(httpParameters);

            Log.v("POST request: ", serviceUrl + ": " + jsonString);
            HttpResponse httpresponse = client.execute(post);

            if (httpresponse != null)
            {
                HttpEntity entity = httpresponse.getEntity();
                jsonResponse = convertStreamToString(entity.getContent());
                Log.v("POST response: ", jsonResponse);
                isInternalError = false;
            }
        } catch (Exception e)
        {
            jsonResponse += e.getMessage().toString();
            e.printStackTrace();
        }
    }

    void onTaskPostExecute()
    {
        if (isCancelled)
            return;

        if (showProgressDialog)
            progressDialog.dismiss();

        if (isInternalError)
        {
            Popup.show(baseActivity, jsonResponse);
            webTaskListener.onError(jsonResponse);

        } else
        {
            try
            {
                Object rawJsonObject = new JSONTokener(jsonResponse).nextValue();
                WebJsonObject webJsonObject = null;

                if (rawJsonObject instanceof JSONObject)
                {
                    webJsonObject = new WebJsonObject(jsonResponse);
                } else if (rawJsonObject instanceof JSONArray)
                {
                    webJsonObject = new WebJsonObject();
                    webJsonObject.put(ARRAY_RESPONSE, new WebJsonArray(jsonResponse));
                }

                if (webJsonObject != null)
                {
                    String errorValue = webJsonObject.getString("error_description");
                    if (errorValue != null)
                    {
                        Popup.show(baseActivity, errorValue);
                        webTaskListener.onError(errorValue);
                    } else
                    {
                        webTaskListener.onResult(webJsonObject);
                    }
                } else
                {
                    Popup.show(baseActivity, baseActivity.getResources().getString(R.string.error_json_parsing));
                    webTaskListener.onError(baseActivity.getResources().getString(R.string.error_json_parsing));
                }
            } catch (JSONException e)
            {
                Popup.show(baseActivity, e.getMessage());
                webTaskListener.onError(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private static String convertStreamToString(InputStream inputStream) throws IOException
    {
        if (inputStream == null)
        {
            throw new IOException("No Response");
        }

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line = null;
        try
        {
            while ((line = bufferedReader.readLine()) != null)
            {
                stringBuilder.append(line + "\n");
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        } finally
        {
            inputStream.close();
        }
        return stringBuilder.toString();
    }

    public WebTaskListener getWebTaskListener()
    {
        return webTaskListener;
    }

    public void setWebTaskListener(WebTaskListener webTaskListener)
    {
        this.webTaskListener = webTaskListener;
    }
}
