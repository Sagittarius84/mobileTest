package org.noorganization.instalist.view.listadapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import org.noorganization.instalist.R;
import org.noorganization.instalist.model.Tag;

import java.util.List;

/**
 * Created by TS on 28.05.2015.
 */
public class TagAdapter extends ArrayAdapter<Tag> {

    private List<Tag> mTags;
    private Context mContext;
    private int mResourceId;

    public TagAdapter(Context context, int resource, int textViewResourceId, List<Tag> _Tags) {
        super(context, resource, textViewResourceId);
        mTags = _Tags;
        mContext = context;
        mResourceId = R.layout.list_tag_enry;
    }

    @Override
    public View getView(int _Position, View _ConvertView, ViewGroup _Parent) {

        View view = null;

        if(_ConvertView == null){
            LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
            view = inflater.inflate(mResourceId, null);
        }else{
            view = _ConvertView;
        }

        TextView tagText = (TextView) view.findViewById(R.id.list_tag_entry_tag_text);
        Button tagDeleteButton = (Button) view.findViewById(R.id.list_tag_entry_delete_button);
        Tag tag = mTags.get(_Position);

        tagDeleteButton.setOnClickListener(new TagClickListener(tag));
        return view;
    }

    private class TagClickListener implements View.OnClickListener{

        private Tag mTag;

        public TagClickListener(Tag _Tag){
            mTag = _Tag;
        }

        @Override
        public void onClick(View v) {
            mTags.remove(mTag);
        }
    }
}
