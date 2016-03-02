/*
 * Copyright 2016 Tino Siegmund, Michael Wodniok
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
 * Adapter to list all tags.
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
        mResourceId = R.layout.list_tag_entry;
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
        tagText.setText(tag.mName);
        
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
