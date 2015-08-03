package bamcorp.podio.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import org.json.JSONException;

import bamcorp.podio.R;
import bamcorp.podio.data.ManagerPreferences;
import bamcorp.podio.web.WebJsonObject;
import bamcorp.podio.web.WebTask;
import bamcorp.podio.web.WebTask.WebTaskListener;
import bamcorp.podio.web.WebTaskFactory;

/**
* Activity class for login
* */
public class ActivityLogin extends ActionBarActivity
{
    Button buttonLogin;
    EditText editTextUsername;
    EditText editTextPassword;
    ImageView imageViewLogo;
    WebTaskListener loginWebTaskListener;

    @Override
    protected void onPause()
    {
        super.onPause();

        String username = editTextUsername.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        ManagerPreferences.saveString(getApplicationContext(), ManagerPreferences.USER_NAME, username);
        ManagerPreferences.saveString(getApplicationContext(), ManagerPreferences.PASSWORD, password);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        editTextUsername = (EditText) findViewById(R.id.edit_text_username);
        editTextPassword = (EditText) findViewById(R.id.edit_text_password);
        imageViewLogo = (ImageView) findViewById(R.id.image_view_logo);
        buttonLogin = (Button) findViewById(R.id.button_login);

        editTextUsername.setText(ManagerPreferences.loadString(getApplicationContext(), ManagerPreferences.USER_NAME));
        editTextPassword.setText(ManagerPreferences.loadString(getApplicationContext(), ManagerPreferences.PASSWORD));

        loginWebTaskListener = new WebTask.WebTaskListener()
        {

            @Override
            public void onResult(final WebJsonObject jsonObject)
            {
                try
                {
                    String accessToken = jsonObject.getString("access_token");
                    ManagerPreferences.saveString(getApplicationContext(), ManagerPreferences.ACCESS_TOKEN, accessToken);

                    Intent intent = new Intent(getApplicationContext(), ActivityList.class);
                    startActivity(intent);

                    finish();
                } catch (JSONException e)
                {
                    Popup.show(ActivityLogin.this, e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled()
            {
            }

            @Override
            public void onError(String errorString)
            {
            }

        };

        buttonLogin.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                String username = editTextUsername.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();
                String clientId = getResources().getString(R.string.data_client_id);
                String clientSecret = getResources().getString(R.string.data_client_secret);

                ManagerPreferences.saveString(getApplicationContext(), ManagerPreferences.USER_NAME, username);

                if (username == null || password == null || username.isEmpty() || password.isEmpty())
                {
                    Popup.show(ActivityLogin.this, getResources().getString(R.string.error_blank_username_password));
                    return;
                }

                WebTaskFactory.getLoginRequestTaskInstance(ActivityLogin.this, loginWebTaskListener, username, password, clientId, clientSecret).execute("");
            }
        });

        editTextPassword.setOnEditorActionListener(new OnEditorActionListener()
        {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                if (actionId == EditorInfo.IME_ACTION_GO)
                {
                    InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(editTextPassword.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                    buttonLogin.performClick();

                    return true;
                }
                return false;
            }
        });

        imageViewLogo.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View view)
            {
                editTextUsername.setText("moeedayazbutt@gmail.com");

                //umm I shouldn't probably hard-code passwords in code :-P
                editTextPassword.setText("k5451288");

                return false;
            }
        });

    }

}
