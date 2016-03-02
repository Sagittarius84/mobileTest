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

package org.noorganization.instalist.view.spinneradapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.noorganization.instalist.model.Product;

import java.util.List;

/**
 * Use it for displaying Product with a spinner.
 * Created by TS on 25.05.2015.
 */
public class ProductSpinnerAdapter extends ArrayAdapter<Product> {

    private final List<Product> mListOfProducts;
    private final Activity mContext;

    public ProductSpinnerAdapter(Activity _Context, List<Product> _ListOfProducts){
        super(_Context, android.R.layout.simple_list_item_1, _ListOfProducts);
        this.mContext = _Context;
        this.mListOfProducts = _ListOfProducts;
    }


    @Override
    public View getView(int _Position, View _ConvertView, ViewGroup _Parent) {
        View view = null;

        if(_ConvertView == null){
            LayoutInflater shoppingListNamesInflater = mContext.getLayoutInflater();
            view = shoppingListNamesInflater.inflate(android.R.layout.simple_list_item_1, null);
        }else{
            view = _ConvertView;
        }

        String listName     = mListOfProducts.get(_Position).mName;
        TextView textView   = (TextView) view.findViewById(android.R.id.text1);
        textView.setText(listName);

        return view;
    }

    @Override
    public Product getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public View getDropDownView(int _Position, View _ConvertView, ViewGroup _Parent) {
        View view = null;

        if(_ConvertView == null){
            LayoutInflater shoppingListNamesInflater = mContext.getLayoutInflater();
            view = shoppingListNamesInflater.inflate(android.R.layout.simple_list_item_1, null);
        }else{
            view = _ConvertView;
        }

        String listName     = mListOfProducts.get(_Position).mName;
        TextView textView   = (TextView) view.findViewById(android.R.id.text1);
        textView.setText(listName);

        return view;
    }
}
