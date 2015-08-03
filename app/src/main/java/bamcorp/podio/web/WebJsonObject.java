
package bamcorp.podio.web;


import org.json.JSONException;
import org.json.JSONObject;


/**
* Wrapper class for JSON object
* */
public class WebJsonObject extends JSONObject
{
    public WebJsonObject(String stringObj) throws JSONException
    {
        super(stringObj);
    }

    public WebJsonObject()
    {
        super();
    }

    @Override
    public String getString(String name) throws JSONException
    {
        if (super.has(name))
        {
            return super.getString(name);
        }
        else
        {
            return null;
        }
    }

    public WebJsonArray getJSONArray(String name) throws JSONException
    {
        return new WebJsonArray(super.getJSONArray(name).toString());
    }

}
