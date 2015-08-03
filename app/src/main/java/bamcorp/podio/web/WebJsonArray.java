
package bamcorp.podio.web;


import org.json.JSONArray;
import org.json.JSONException;

/**
* Wrapper class for JSON array
* */
public class WebJsonArray extends JSONArray
{

    public WebJsonObject getJSONObject(int index) throws JSONException
    {
        return new WebJsonObject(super.getJSONObject(index).toString());
    }

    public WebJsonArray(String json) throws JSONException
    {
        super(json);
    }

}
