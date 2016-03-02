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

package org.noorganization.instalist.view.customview;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * A layout that shows the hint dynamically. Since the found solutions where either overpowered or
 * were not flexible enough (especially their layouts!), this needed to be implemented ourself.
 * This solution is quite near to following gist:
 * https://gist.github.com/chrisbanes/11247418
 * Update 2015-07-22: Since we could apply the Android Support Design Library successfully this
 * class is not more required.
 * Created by daMihe on 27.06.2015.
 */
@Deprecated
public class FloatingLabelLayout extends LinearLayout {
    private EditText mEditText;
    private TextView mLabel;

    public FloatingLabelLayout(Context _context, AttributeSet attrs) {
        super(_context, attrs);

        setOrientation(LinearLayout.VERTICAL);

        mLabel = new TextView(_context);
        mLabel.setVisibility(View.GONE);

        addView(mLabel, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    public final void addView(View _child, int _index, ViewGroup.LayoutParams _params) {
        if (_child instanceof EditText) {
            setEditText((EditText) _child);
        }

        // Carry on adding the View...
        super.addView(_child, _index, _params);
    }

    public EditText getEditText() {
        return mEditText;
    }

    private void setEditText(EditText _editText) {
        if (mEditText != null) {
            throw new IllegalArgumentException("There is only one EditText allowed.");
        }
        mEditText = _editText;
        mEditText.addTextChangedListener(new LabelVisibilitySwitcher());
    }

    private class LabelVisibilitySwitcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // do nothing.
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // do nothing.
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (mLabel.getVisibility() == View.GONE && s.length() > 0) {
                mLabel.setText(mEditText.getHint());
                mLabel.setVisibility(View.VISIBLE);
            }
            if (s.length() == 0) {
                mLabel.setVisibility(View.GONE);
            }
        }
    }
}
