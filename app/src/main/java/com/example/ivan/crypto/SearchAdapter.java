package com.example.ivan.crypto;

import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Courtesy of Francis Fish.
 * https://gist.github.com/fjfish/3024308
 */

public class SearchAdapter extends BaseAdapter implements Filterable {
    private Map<String, String> filteredMapData = new HashMap<>();
    private List<String> originalIdList, originalNameList, filteredNameList, filteredIdList;
    private LayoutInflater mInflater;
    private ItemFilter mFilter = new ItemFilter();

    public SearchAdapter(Context context, Map<String, String> originalMapData) {
        this.originalIdList = new ArrayList<>(originalMapData.keySet());
        this.originalNameList = new ArrayList<>(originalMapData.values());
        this.filteredNameList = originalNameList;
        this.filteredIdList = originalIdList;
        mInflater = LayoutInflater.from(context);
    }

    public List<String> getFilteredIdList(){
        return filteredIdList;
    }

    public List<String> getFilteredNameList() {
        return filteredNameList;
    }

    public int getCount() {
        return filteredNameList.size();
    }

    public Object getItem(int position) {
        return filteredNameList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item, null);
            holder = new ViewHolder();
            holder.text = convertView.findViewById(R.id.list_view);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.text.setText(filteredNameList.get(position));
        return convertView;
    }

    static class ViewHolder {
        CheckedTextView text;
    }

    public Filter getFilter() {
        return mFilter;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            Map<String, String> filteredMapData = new HashMap<>();
            String filterString = constraint.toString().toLowerCase();
            FilterResults results = new FilterResults();
            String filterableString;
            for (int i = 0; i < originalNameList.size(); i++) {
                filterableString = originalNameList.get(i);
                if (filterableString.toLowerCase().contains(filterString)) {
                    filteredMapData.put(originalIdList.get(i),filterableString);
                }
            }
            results.values = filteredMapData;
            results.count = filteredMapData.size();
            return results;
        }
        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            Map<String, String> filteredMapData = (HashMap<String, String>) results.values;
            filteredNameList = new ArrayList<>(filteredMapData.values());
            filteredIdList = new ArrayList<>(filteredMapData.keySet());
            notifyDataSetChanged();
        }

    }
}
