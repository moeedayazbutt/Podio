
package bamcorp.podio.data;


import android.content.Context;
import android.content.SharedPreferences.Editor;

/**
* Shared preferences manager
* TODO: implement encryption/decryption for password
* */
public class ManagerPreferences
{
    public final static String USER_NAME = "_USER_NAME_";
    public final static String PASSWORD = "_PASSWORD_";
    public final static String ACCESS_TOKEN = "_ACCESS_TOKEN_";

    private static void apply(Editor editor)
    {
        editor.commit();
    }

    public static void remove(Context context, String key)
    {
        apply(context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE).edit().remove(key));
    }

    public static void saveBoolean(Context context, String key, boolean value)
    {
        apply(context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE).edit().putBoolean(key, value));
    }

    public static boolean loadBoolean(Context context, String key, boolean defaultValue)
    {
        return context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE).getBoolean(key, defaultValue);
    }

    public static void saveString(Context context, String key, String value)
    {
        apply(context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE).edit().putString(key, value));
    }

    public static String loadString(Context context, String key)
    {
        return context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE).getString(key, null);
    }
}
