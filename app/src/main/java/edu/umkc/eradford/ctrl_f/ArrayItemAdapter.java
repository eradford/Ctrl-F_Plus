package edu.umkc.eradford.ctrl_f;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ethan on 5/7/15.
 */
public class ArrayItemAdapter<E> extends ArrayAdapter {


    private ArrayList<Integer> selectedItems = new ArrayList<>();

    /**
     * Constructor
     *
     * @param context  The current context.
     * @param resource The resource ID for a layout file containing a TextView to use when
     *                 instantiating views.
     * @param objects  The objects to represent in the ListView.
     */
    public ArrayItemAdapter(Context context, int resource, List objects) {
        super(context, resource, objects);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        if (selectedItems.contains(position)) {
            view.setBackgroundColor(Color.GREEN);
        }
        return view;

    }

    public void selectItem(int pos) {
        selectedItems.add(pos);
    }

    public void clearSelections() {
        selectedItems.clear();
    }

    public ArrayList<Integer> getSelectedItems() {
        return selectedItems;
    }

    public void setSelectedItems(ArrayList<Integer> selectedItems) {
        this.selectedItems = selectedItems;
    }
}
