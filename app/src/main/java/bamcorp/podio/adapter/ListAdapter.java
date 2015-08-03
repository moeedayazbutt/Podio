package bamcorp.podio.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import bamcorp.podio.R;
import bamcorp.podio.data.Item;
import bamcorp.podio.data.ItemType;

/**
* Custom list adapter for organizations/workspaces list view
* */
public class ListAdapter extends BaseAdapter
{
    ArrayList<Item> items;
    LayoutInflater layoutInflater;

    public static class ViewHolder
    {
        TextView textView;
    }

    public ListAdapter(Activity activity, ArrayList<Item> items)
    {
        this.items = items;
        layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getItemViewType(int position)
    {
        int type = ItemType.ITEM.getValue();

        try
        {
            type = items.get(position).getType().getValue();
        } catch (IndexOutOfBoundsException e)
        {
            //catch an index out of bounds exception in case notifyDataSetChanged() is called and list size changes
            e.printStackTrace();
        }

        return type;
    }

    @Override
    public int getViewTypeCount()
    {
        return ItemType.values().length;
    }

    @Override
    public int getCount()
    {
        return items.size();
    }

    @Override
    public Object getItem(int position)
    {
        return items.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Item item = null;

        try
        {
            item = items.get(position);
        } catch (IndexOutOfBoundsException e)
        {
            //catch an index out of bounds exception in case notifyDataSetChanged() is called and list size changes
            e.printStackTrace();

            return convertView;
        }

        int rowType = getItemViewType(position);
        String name = item.getName();

        ViewHolder viewHolder = null;
        if (convertView == null)
        {
            viewHolder = new ViewHolder();

            if (rowType == ItemType.HEADER.getValue())
            {
                convertView = layoutInflater.inflate(R.layout.item_header, parent, false);
                viewHolder.textView = (TextView) convertView.findViewById(R.id.text_view_header);
            } else if (rowType == ItemType.ITEM.getValue())
            {
                convertView = layoutInflater.inflate(R.layout.item_item, parent, false);
                viewHolder.textView = (TextView) convertView.findViewById(R.id.text_view_item);
            }

            convertView.setTag(viewHolder);

        } else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.textView.setText(name);

        return convertView;
    }

}
