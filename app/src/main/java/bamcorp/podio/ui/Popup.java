
package bamcorp.podio.ui;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import bamcorp.podio.R;

/**
* Utility popup class
* */
public class Popup
{
    public static void show(final Activity activity, final String message)
    {
        activity.runOnUiThread(new Runnable() {

            @Override
            public void run()
            {
                final AlertDialog alertDialog;
                alertDialog = new AlertDialog.Builder(activity).create();
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.setCancelable(false);
                alertDialog.setMessage(message);

                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, activity.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        dialog.dismiss();
                    }
                });

                alertDialog.show();
            }
        });
    }

}
