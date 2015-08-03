
package bamcorp.podio.web;


import android.app.Activity;
import bamcorp.podio.web.WebTask.WebTaskListener;

/**
*
* Provides an implemented instance for each web service request task
* */
public class WebTaskFactory
{

    public static WebTask getOrganizationsRequestTaskInstance(Activity activity, WebTaskListener webTaskListener)
    {
        WebTask organizationsRequestTask = new WebTask();
        organizationsRequestTask.init(activity, "org/", WebTask.REQUEST_TYPE.GET);
        organizationsRequestTask.setWebTaskListener(webTaskListener);
        organizationsRequestTask.disableProgressDialog();
        return organizationsRequestTask;
    }

    public static WebTask getLoginRequestTaskInstance(Activity activity, WebTaskListener webTaskListener, String username, String password, String clientId, String clientSecret)
    {
        //life saver --> https://help.podio.com/hc/communities/public/questions/202668283-How-does-Podio-OAuth-Work-In-this-Case-?locale=en-us
        WebTask loginRequestTask = new WebTask();
        loginRequestTask.init(activity, "oauth/token?grant_type=password&username=" + username + "&password=" + password + "&client_id=" + clientId + "&client_secret=" + clientSecret, WebTask.REQUEST_TYPE.POST);
        loginRequestTask.setWebTaskListener(webTaskListener);
        return loginRequestTask;
    }
}
