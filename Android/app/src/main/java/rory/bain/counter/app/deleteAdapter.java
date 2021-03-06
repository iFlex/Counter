package rory.bain.counter.app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.content.Context;
import android.view.*;
import android.widget.TextView;
import android.widget.Button;

import java.util.ArrayList;

/**
 * Created by rorybain on 16/03/15.
 */
public class deleteAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<String> list = new ArrayList<String>();
    private Context context;
    private ArrayList<Integer> ids;

    public deleteAdapter (ArrayList<String> list, Context context, ArrayList<Integer> id) {
        this.list = list;
        this.context = context;
        this.ids = id;
    }

    @Override
    public int getCount(){
        return list.size();
    }

    @Override
    public Object getItem(int pos){
        return list.get(pos);
    }

    @Override
    public long getItemId(int pos){
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_item_layout, null);
        }

        //Handle TextView and display string from your list
        TextView listItemText = (TextView)view.findViewById(R.id.text1);
        listItemText.setText(list.get(position));

        //Handle buttons and add onClickListeners
        Button deleteBtn = (Button)view.findViewById(R.id.deleteItem);

        deleteBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //do something
                //Send message

                new AlertDialog.Builder(deleteAdapter.this.context)
                        .setTitle("Delete entry")
                        .setMessage("Are you sure you want to delete this entry?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d("Position deletedis", "" + position);
                                list.remove(position);
                                MainActivity.libraryDB.open();
                                MainActivity.libraryDB.deleteRow(ids.get(position));
                                MainActivity.libraryDB.close();
                                notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

        return view;
    }





}
