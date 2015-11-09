package org.noorganization.instalist.presenter.activity;

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

import com.orm.SugarRecord;
import com.orm.query.Condition;
import com.orm.query.Select;

import org.noorganization.instalist.R;
import org.noorganization.instalist.controller.IUnitController;
import org.noorganization.instalist.controller.event.UnitChangedMessage;
import org.noorganization.instalist.controller.implementation.ControllerFactory;
import org.noorganization.instalist.model.Unit;
import org.noorganization.instalist.presenter.decoration.DividerItemListDecoration;
import org.noorganization.instalist.presenter.listadapter.UnitEditorAdapter;

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
    private UnitEditorAdapter    mUnitAdapter;
    private LinearLayoutManager  mUnitLayoutManager;
    private EventBus             mBus;

    @Override
    public void onCreate(Bundle _savedInstanceState) {
        super.onCreate(_savedInstanceState);

        setContentView(R.layout.activity_w_actionbar_listview);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar bar = getSupportActionBar();
        if (bar == null) {
            Log.e(LOG_TAG, "ActionBar is null.");
            return;
        }
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setTitle(R.string.unit_editor);

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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
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
        mAddButton                    = (FloatingActionButton) findViewById(R.id.action_add_item);

        unitRecyclerView.addItemDecoration(new DividerItemListDecoration(getResources().
                getDrawable(R.drawable.list_divider)));
        mUnitLayoutManager = new LinearLayoutManager(this);
        mUnitLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        unitRecyclerView.setLayoutManager(mUnitLayoutManager);
        mUnitAdapter = new UnitEditorAdapter(this, SugarRecord.listAll(Unit.class), new EditCallback());
        unitRecyclerView.setAdapter(mUnitAdapter);

        mAddButton.setOnClickListener(new onCreateUnitListener());
    }

    private class onCreateUnitListener implements View.OnClickListener {

        private static final @IdRes int ID_NAME = 0x6149c610;

        @Override
        public void onClick(View v) {
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
            public void onShow(DialogInterface dialog) {
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
                IUnitController controller = ControllerFactory.getUnitController();
                if (controller.createUnit(newName) == null) {
                    titleInput.setError(getString(R.string.error_unit_already_exists));
                    return;
                }
                mDialog.dismiss();
            }
        }
    }

    private class EditCallback implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mAddButton.setVisibility(View.GONE);
            mode.getMenuInflater().inflate(R.menu.menu_contextual_actionmode_options, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            menu.removeItem(R.id.menu_cancel_action);
            return true;
        }

        @Override
        public boolean onActionItemClicked(final ActionMode _mode, MenuItem _menuItem) {
            final IUnitController controller = ControllerFactory.getUnitController();
            final Unit unit = mUnitAdapter.get(mUnitAdapter.getEditingPosition());
            switch (_menuItem.getItemId()) {
                case R.id.menu_delete_action:
                    if(!controller.deleteUnit(unit, IUnitController.MODE_BREAK_DELETION)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(UnitEditorActivity.this);
                        builder.setMessage(R.string.remove_unit_question);
                        builder.setPositiveButton(R.string.unlink, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                controller.deleteUnit(unit, IUnitController.MODE_UNLINK_REFERENCES);
                                _mode.finish();
                            }
                        });
                        builder.setNeutralButton(R.string.remove, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                controller.deleteUnit(unit, IUnitController.MODE_DELETE_REFERENCES);
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
                    if (Select.from(Unit.class).where(Condition.prop(Unit.ATTR_NAME).eq(newName)).
                            count() > 0) {
                        editor.setError(getString(R.string.error_unit_already_exists));
                        break;
                    }
                    controller.renameUnit(unit, newName);
                    _mode.finish();

                    break;
                default:
                    return false;
            }
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mAddButton.setVisibility(View.VISIBLE);
            mUnitAdapter.setEditorPosition(-1);
        }
    }
}
