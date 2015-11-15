package org.noorganization.instalist.presenter.listadapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import org.noorganization.instalist.R;
import org.noorganization.instalist.model.Unit;
import org.noorganization.instalist.presenter.sorting.AlphabeticalUnitComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * An Adapter that is based on a always alphabetically sorted List for Units.
 * Created by daMihe on 22.07.2015.
 */
public class UnitEditorAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int      TYPE_TEXTVIEW = 0;
    private static final int      TYPE_EDITTEXT = 1;

    private Activity              mActivity;
    private List<Unit>            mUnderLyingUnits;
    private ActionMode.Callback   mEditingMode;

    private int                   mEditingPosition;

    public UnitEditorAdapter(Activity _activity, List<Unit> _elements, ActionMode.Callback _editorCallback) {
        mActivity = _activity;
        mUnderLyingUnits = new ArrayList<>();
        if (_elements != null) {
            mUnderLyingUnits.addAll(_elements);
            Collections.sort(mUnderLyingUnits, AlphabeticalUnitComparator.getInstance());
        }
        mEditingPosition = -1;
        mEditingMode = _editorCallback;
    }

    public void add(Unit _unit) {
        if (_unit == null) {
            return;
        }

        Comparator<Unit> comparator = AlphabeticalUnitComparator.getInstance();
        int index = Collections.binarySearch(mUnderLyingUnits, _unit, comparator);
        if (index < 0) {
            index = -index - 1;
        }
        mUnderLyingUnits.add(index, _unit);

        notifyItemInserted(index);
    }

    public Unit get(int _position) {
        return mUnderLyingUnits.get(_position);
    }

    public int getIndexOf(Unit _unit) {
        if (_unit == null) {
            return -1;
        }

        int rtn = Collections.binarySearch(mUnderLyingUnits, _unit, AlphabeticalUnitComparator.
                getInstance());
        if (rtn < 0) {
            rtn = -1;
        }
        return rtn;
    }

    public int getIndexOf(String _id) {
        for (int position = 0; position < mUnderLyingUnits.size(); position++) {
            if (mUnderLyingUnits.get(position).mUUID.compareTo(_id) == 0) {
                return position;
            }
        }
        return -1;
    }

    public int getEditingPosition() {
        return mEditingPosition;
    }

    @Override
    public int getItemCount() {
        return mUnderLyingUnits.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup _parent, int _viewType) {
        LayoutInflater inflater = LayoutInflater.from(_parent.getContext());
        View view;
        switch (_viewType) {
            case TYPE_TEXTVIEW:
                view = inflater.inflate(android.R.layout.simple_list_item_1, _parent, false);
                return new UnitTextHolder(view);
            case TYPE_EDITTEXT:
                view = inflater.inflate(R.layout.list_edittext, _parent, false);
                return new UnitEditorHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder _holder, final int _position) {
        Unit unit = mUnderLyingUnits.get(_position);
        if (_holder instanceof UnitEditorHolder) {
            EditText editor = ((UnitEditorHolder) _holder).mEditor;
            editor.setText(unit.mName);
            editor.requestFocus();
        } else if(_holder instanceof UnitTextHolder) {
            TextView entry = ((UnitTextHolder) _holder).mTextView;
            entry.setText(unit.mName);
            entry.setTag(unit);
            entry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View _view) {
                    if (mEditingMode != null) {
                        mActivity.startActionMode(mEditingMode);
                    }
                    setEditorPosition(_position);
                }
            });
        }
    }

    @Override
    public int getItemViewType(int _position) {
        return (_position == mEditingPosition ? 1 : 0);
    }

    public void remove(Unit _toRemove) {
        int index = getIndexOf(_toRemove);
        if (index > -1) {
            mUnderLyingUnits.remove(index);
            notifyItemRemoved(index);
        }
    }

    public void setEditorPosition(int _position) {
        if (_position < -1 || _position >= mUnderLyingUnits.size()) {
            return;
        }

        if (mEditingPosition != _position) {
            int oldPosition = mEditingPosition;
            mEditingPosition = _position;
            if (_position != -1) {
                notifyItemChanged(_position);
            }
            notifyItemChanged(oldPosition);
        }
    }

    public void update(Unit _toUpdate) {
        Comparator<Unit> comparator = AlphabeticalUnitComparator.getInstance();
        int oldIndex = getIndexOf(_toUpdate.mUUID);
        int newIndex = Collections.binarySearch(mUnderLyingUnits, _toUpdate, comparator);

        if (oldIndex == newIndex) {
            mUnderLyingUnits.set(oldIndex, _toUpdate);
            notifyItemChanged(oldIndex);
            return;
        }
        if (newIndex < 0) {
            newIndex = -newIndex - 1;
        }
        if (newIndex == oldIndex + 1) {
            mUnderLyingUnits.set(oldIndex, _toUpdate);
            notifyItemChanged(oldIndex);
            return;
        }

        notifyItemMoved(oldIndex, newIndex);
        mUnderLyingUnits.remove(oldIndex);
        if (newIndex > mUnderLyingUnits.size()) {
            mUnderLyingUnits.add(_toUpdate);
        } else {
            mUnderLyingUnits.add(newIndex, _toUpdate);
        }
        notifyItemChanged(newIndex);
    }

    private class UnitEditorHolder extends RecyclerView.ViewHolder {
        public EditText mEditor;

        public UnitEditorHolder(View itemView) {
            super(itemView);
            mEditor = (EditText) itemView.findViewById(R.id.edittext);
        }
    }

    private class UnitTextHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;

        public UnitTextHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(android.R.id.text1);
        }
    }
}
