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

package org.noorganization.instalist.view.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import org.noorganization.instalist.R;
import org.noorganization.instalist.model.Unit;
import org.noorganization.instalist.presenter.IUnitController;
import org.noorganization.instalist.presenter.event.UnitChangedMessage;
import org.noorganization.instalist.presenter.implementation.ControllerFactory;
import org.noorganization.instalist.view.decoration.DividerItemListDecoration;
import org.noorganization.instalist.view.listadapter.UnitEditorAdapter;

import de.greenrobot.event.EventBus;

/**
 * The editor for units. Since fragments are in this special case not useful (more overhead than
 * use), we use are separate Activity.
 * Note 2015-07-31: this will be migrated to a fragment based activity for consistency.
 * Created by daMihe on 22.07.2015.
 */
public class UnitEditorActivity extends AppCompatActivity {

    private static final String LOG_TAG = UnitEditorActivity.class.getCanonicalName();

    private FloatingActionButton mAddButton;
    private UnitEditorAdapter mUnitAdapter;
    private LinearLayoutManager mUnitLayoutManager;
    private EventBus mBus;
    private IUnitController mUnitController;

    private ActionBar mActionBar;

    @Override
    public void onCreate(Bundle _savedInstanceState) {
        super.onCreate(_savedInstanceState);

        setContentView(R.layout.activity_w_actionbar_listview);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mUnitController = ControllerFactory.getUnitController(this);

        mActionBar = getSupportActionBar();
        if (mActionBar == null) {
            Log.e(LOG_TAG, "ActionBar is null.");
            return;
        }
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setTitle(R.string.unit_editor);
        initViews();

        mBus = EventBus.getDefault();
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(UnitChangedMessage _message) {
        switch (_message.mChange) {
            case CREATED:
                mUnitAdapter.add(_message.mUnit);
                break;
            case CHANGED:
                mUnitAdapter.update(_message.mUnit);
                break;
            case DELETED:
                mUnitAdapter.remove(_message.mUnit);
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem _item) {
        switch (_item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(_item);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mBus.unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mBus.register(this);
    }

    private void initViews() {
        RecyclerView unitRecyclerView = (RecyclerView) findViewById(R.id.main_list);
        mAddButton = (FloatingActionButton) findViewById(R.id.action_add_item);

        unitRecyclerView.addItemDecoration(new DividerItemListDecoration(getResources().
                getDrawable(R.drawable.list_divider)));
        mUnitLayoutManager = new LinearLayoutManager(this);
        mUnitLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        unitRecyclerView.setLayoutManager(mUnitLayoutManager);
        mUnitAdapter = new UnitEditorAdapter(this, mUnitController.listAll(Unit.COLUMN.NAME, true), new EditCallback());
        unitRecyclerView.setAdapter(mUnitAdapter);

        mAddButton.setOnClickListener(new onCreateUnitListener());
    }

    private class onCreateUnitListener implements View.OnClickListener {

        private static final
        @IdRes
        int ID_NAME = 0x6149c610;

        @Override
        public void onClick(View _view) {
            AlertDialog.Builder builder = new AlertDialog.Builder(UnitEditorActivity.this);
            builder.setTitle(R.string.new_unit);

            TextInputLayout newTitleInputLayout = new TextInputLayout(UnitEditorActivity.this);
            int padding = getResources().getDimensionPixelSize(R.dimen.base_margin);
            newTitleInputLayout.setPadding(padding, padding, padding, padding);
            EditText newTitleInput = new EditText(UnitEditorActivity.this);
            newTitleInput.setHint(getString(R.string.new_name));
            newTitleInput.setId(ID_NAME);
            newTitleInputLayout.addView(newTitleInput);
            builder.setView(newTitleInputLayout);

            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Just a dummy, will be replaced when showing.
                }
            });

            AlertDialog dialog = builder.create();
            dialog.setOnShowListener(new OkayButtonAssignee(dialog));
            dialog.show();
        }

        private class OkayButtonAssignee implements DialogInterface.OnShowListener {

            private final AlertDialog mDialog;

            public OkayButtonAssignee(AlertDialog _dialog) {
                super();
                mDialog = _dialog;
            }

            @Override
            public void onShow(DialogInterface _dialog) {
                mDialog.getButton(DialogInterface.BUTTON_POSITIVE).
                        setOnClickListener(new OnOkayClickListener(mDialog));
            }
        }

        private class OnOkayClickListener implements View.OnClickListener {

            private final AlertDialog mDialog;

            public OnOkayClickListener(AlertDialog _dialog) {
                super();
                mDialog = _dialog;
            }

            @Override
            public void onClick(View _view) {
                EditText titleInput = (EditText) mDialog.findViewById(ID_NAME);
                String newName = titleInput.getText().toString().trim();
                if (newName.length() == 0) {
                    titleInput.setError(getString(R.string.error_no_input));
                    return;
                }
                if (mUnitController.createUnit(newName) == null) {
                    titleInput.setError(getString(R.string.error_unit_already_exists));
                    return;
                }
                mDialog.dismiss();
            }
        }
    }

    private class EditCallback implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode _mode, Menu _menu) {
            mAddButton.setVisibility(View.GONE);
            _mode.getMenuInflater().inflate(R.menu.menu_contextual_actionmode_options, _menu);
            _mode.setTitle(getString(R.string.edit_unit));
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode _mode, Menu _menu) {
            _menu.removeItem(R.id.menu_cancel_action);
            return true;
        }

        @Override
        public boolean onActionItemClicked(final ActionMode _mode, MenuItem _menuItem) {
            final Unit unit = mUnitAdapter.get(mUnitAdapter.getEditingPosition());
            switch (_menuItem.getItemId()) {
                case R.id.menu_delete_action:
                    if (!mUnitController.deleteUnit(unit, IUnitController.MODE_BREAK_DELETION)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(UnitEditorActivity.this);
                        builder.setMessage(R.string.remove_unit_question);
                        builder.setPositiveButton(R.string.unlink, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mUnitController.deleteUnit(unit, IUnitController.MODE_UNLINK_REFERENCES);
                                _mode.finish();
                            }
                        });
                        builder.setNeutralButton(R.string.remove, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mUnitController.deleteUnit(unit, IUnitController.MODE_DELETE_REFERENCES);
                                _mode.finish();
                            }
                        });
                        builder.setNegativeButton(android.R.string.cancel,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        _mode.finish();
                                    }
                                });
                        builder.show();
                    } else {
                        _mode.finish();
                    }
                    break;
                case R.id.menu_add_action:
                    EditText editor = (EditText) mUnitLayoutManager.
                            findViewByPosition(mUnitAdapter.getEditingPosition()).
                            findViewById(R.id.edittext);
                    String newName = editor.getText().toString().trim();
                    if (newName.equals(unit.mName)) {
                        _mode.finish();
                        break;
                    }
                    if (newName.length() == 0) {
                        editor.setError(getString(R.string.error_no_input));
                        break;
                    }
                    if (mUnitController.findByName(newName) != null) {
                        editor.setError(getString(R.string.error_unit_already_exists));
                        break;
                    }
                    mUnitController.renameUnit(unit, newName);
                    _mode.finish();

                    break;
                default:
                    return false;
            }
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode _mode) {
            mAddButton.setVisibility(View.VISIBLE);
            mUnitAdapter.setEditorPosition(-1);
        }
    }
}
