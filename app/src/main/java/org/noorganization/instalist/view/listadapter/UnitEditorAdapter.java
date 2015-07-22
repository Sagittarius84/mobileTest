package org.noorganization.instalist.view.listadapter;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import org.noorganization.instalist.model.Unit;
import org.noorganization.instalist.view.sorting.AlphabeticalUnitComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * An Adapter that is based on a always alphabetically sorted List for Units.
 * Created by daMihe on 22.07.2015.
 */
public class UnitEditorAdapter implements ListAdapter {

    private Context               mContext;
    private List<DataSetObserver> mObservers;
    private List<Unit>            mUnderLyingUnits;

    public UnitEditorAdapter(Context _context, List<Unit> _elements) {
        mContext = _context;
        mUnderLyingUnits = new ArrayList<>();
        if (_elements != null) {
            mUnderLyingUnits.addAll(_elements);
            Collections.sort(mUnderLyingUnits, AlphabeticalUnitComparator.getInstance());
        }
        mObservers = new LinkedList<>();
    }

    public void add(Unit _unit) {
        if (_unit == null) {
            return;
        }

        int binaryMin = 0;
        int binaryMax = mUnderLyingUnits.size();
        Comparator<Unit> comparator = AlphabeticalUnitComparator.getInstance();
        while (binaryMin != binaryMax) {
            int currentCenter = binaryMin + ((binaryMax - binaryMin) / 2);
            int comparison = comparator.compare(_unit, mUnderLyingUnits.get(currentCenter));
            if (comparison == 0) {
                binaryMax = binaryMin = currentCenter;
            } else if (comparison < 0) {
                binaryMax = currentCenter;
            } else {
                binaryMin = currentCenter + 1;
            }
        }
        mUnderLyingUnits.add(binaryMin, _unit);

        notifyObserversAboutChange();
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public int getCount() {
        return mUnderLyingUnits.size();
    }

    @Override
    public Unit getItem(int _position) {
        return mUnderLyingUnits.get(_position);
    }

    @Override
    public long getItemId(int _position) {
        return getItem(_position).getId();
    }

    @Override
    public int getItemViewType(int _position) {
        return 0;
    }

    @Override
    public View getView(int _position, View _convertView, ViewGroup _parent) {
        //LayoutInflater li = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TextView justAView = new TextView(mContext);
        justAView.setText(getItem(_position).mName);
        return justAView;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isEmpty() {
        return mUnderLyingUnits.isEmpty();
    }

    @Override
    public boolean isEnabled(int _position) {
        return (_position >= 0 && _position < mUnderLyingUnits.size());
    }

    @Override
    public void registerDataSetObserver(DataSetObserver _observer) {
        mObservers.add(_observer);
    }

    public void remove(Unit _toRemove) {
        mUnderLyingUnits.remove(_toRemove);
        notifyObserversAboutChange();
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver _observer) {
        mObservers.remove(_observer);
    }

    private void notifyObserversAboutChange() {
        for (DataSetObserver observer : mObservers) {
            observer.onChanged();
        }
    }

    private void notifyObserversAboutInvalidation() {
        for (DataSetObserver observer : mObservers) {
            observer.onInvalidated();
        }
    }
}
