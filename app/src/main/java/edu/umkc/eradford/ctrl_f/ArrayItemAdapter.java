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


    private List<Integer> selectedItems = new ArrayList<>();

    /**
     * Overrides default ArrayAdapter behavior to allow individual items to have a different background color
     *
     * @param context  The current context.
     * @param resource The resource ID for a layout file containing a TextView to use when
     *                 instantiating views.
     * @param objects  The objects to represent in the ListView.
     */
    public ArrayItemAdapter(Context context, int resource, List objects) {
        super(context, resource, objects);
    }

    /**
     * Returns a view for the item with the given position, highlighted if applicable
     * @param position Item to retrieve view of, specified by index
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent); //Get default view from superclass
        if (selectedItems.contains(position)) {
            view.setBackgroundColor(Color.GREEN); //Highlight the item if it is selected
        }
        return view;

    }

    /**
     * Selects the specified item
     * @param pos The index of the item to select
     */
    public void selectItem(int pos) {
        selectedItems.add(pos);
    }

    /**
     * Clear all item selections
     */
    public void clearSelections() {
        selectedItems.clear();
    }

    /**
     * Returns a list of all selected items
     * @return A list of of indices of all selected items
     */
    public List<Integer> getSelectedItems() {
        return selectedItems;
    }

    /**
     * Clear all selections and select all items specified by selectedItems
     * @param selectedItems A list of item indices to select
     */
    public void setSelectedItems(List<Integer> selectedItems) {
        this.selectedItems = selectedItems;
    }
}
