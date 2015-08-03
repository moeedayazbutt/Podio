package bamcorp.podio.ui;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import org.json.JSONException;

import java.util.ArrayList;

import bamcorp.podio.R;
import bamcorp.podio.adapter.ListAdapter;
import bamcorp.podio.data.Item;
import bamcorp.podio.data.ItemType;
import bamcorp.podio.web.WebJsonArray;
import bamcorp.podio.web.WebJsonObject;
import bamcorp.podio.web.WebTask;
import bamcorp.podio.web.WebTask.WebTaskListener;
import bamcorp.podio.web.WebTaskFactory;

/**
*
* Activity class for organizations/workspace list
*
* */
public class ActivityList extends ActionBarActivity
{
    ArrayList<Item> items;
    ListView listView;
    ListAdapter adapter;
    SwipeRefreshLayout swipeRefreshLayout;
    WebTaskListener organizationsWebTaskListener;

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_list_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if (id == R.id.menu_refresh)
        {
            refreshListView();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_list);

        listView = (ListView) findViewById(R.id.list_view);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.primary_darkest);

        items = new ArrayList<Item>();
        adapter = new ListAdapter(ActivityList.this, items);

        listView.setAdapter(adapter);

        organizationsWebTaskListener = new WebTaskListener()
        {

            @Override
            public void onResult(WebJsonObject jsonObject)
            {
                try
                {
                    WebJsonArray arrayResponse = jsonObject.getJSONArray(WebTask.ARRAY_RESPONSE);
                    final int length = arrayResponse.length();

                    items.clear();

                    for (int i = 0; i < length; i++)
                    {
                        WebJsonObject organizationItem = arrayResponse.getJSONObject(i);
                        String organizationName = organizationItem.getString("name");

                        items.add(new Item(ItemType.HEADER, organizationName));

                        WebJsonArray spacesArray = organizationItem.getJSONArray("spaces");
                        final int spacesArrayLength = spacesArray.length();
                        for (int j = 0; j < spacesArrayLength; j++)
                        {
                            WebJsonObject spaceItem = spacesArray.getJSONObject(j);
                            String spaceName = spaceItem.getString("name");

                            items.add(new Item(ItemType.ITEM, spaceName));
                        }

                    }
                } catch (JSONException e)
                {
                    Popup.show(ActivityList.this, e.getMessage());
                    e.printStackTrace();
                } finally
                {
                    ActivityList.this.runOnUiThread(new Runnable()
                    {

                        @Override
                        public void run()
                        {
                            adapter.notifyDataSetChanged();
                            stopRefreshAnimation();
                        }
                    });
                }

            }

            @Override
            public void onCancelled()
            {
            }

            @Override
            public void onError(String errorString)
            {
                stopRefreshAnimation();
            }

        };

        swipeRefreshLayout.setOnRefreshListener(new OnRefreshListener()
        {

            @Override
            public void onRefresh()
            {
                WebTaskFactory.getOrganizationsRequestTaskInstance(ActivityList.this, organizationsWebTaskListener).execute("");
            }
        });

        refreshListView();
    }

    void stopRefreshAnimation()
    {
        if (swipeRefreshLayout.isRefreshing())
        {
            ActivityList.this.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    swipeRefreshLayout.setRefreshing(false);
                }
            });

        }
    }

    void refreshListView()
    {
        swipeRefreshLayout.post(new Runnable()
        {
            @Override
            public void run()
            {
                swipeRefreshLayout.setRefreshing(true);
            }
        });

        WebTaskFactory.getOrganizationsRequestTaskInstance(ActivityList.this, organizationsWebTaskListener).execute("");
    }
}
