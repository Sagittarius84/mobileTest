package org.noorganization.instalist.view.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.orm.SugarRecord;

import org.noorganization.instalist.R;
import org.noorganization.instalist.controller.IUnitController;
import org.noorganization.instalist.controller.event.UnitChangedMessage;
import org.noorganization.instalist.controller.implementation.ControllerFactory;
import org.noorganization.instalist.model.Unit;
import org.noorganization.instalist.view.listadapter.UnitEditorAdapter;

import de.greenrobot.event.EventBus;

/**
 * The editor for units. Since fragments are in this special case not useful (more overhead than
 * use), we use are seperate Activity.
 * Created by daMihe on 22.07.2015.
 */
public class UnitEditorActivity extends AppCompatActivity {

    private static final String LOG_TAG = UnitEditorActivity.class.getCanonicalName();

    private UnitEditorAdapter mUnitAdapter;
    private EventBus          mBus;

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

    public void onEventMainThread(UnitChangedMessage _message) {
        switch (_message.mChange) {
            case CREATED:
                mUnitAdapter.add(_message.mUnit);
                break;
            case CHANGED:
                Log.d("Implementation missing", "@ UnitEditorActivity#onEventMainThread");
                break;
            case DELETED:
                mUnitAdapter.remove(_message.mUnit);
                break;
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
        ListView unitListView              = (ListView) findViewById(R.id.main_list);
        FloatingActionButton unitAddButton = (FloatingActionButton) findViewById(R.id.action_add_item);

        mUnitAdapter = new UnitEditorAdapter(this, SugarRecord.listAll(Unit.class));
        unitListView.setAdapter(mUnitAdapter);

        unitAddButton.setOnClickListener(new onCreateUnitListener());
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

            private AlertDialog mDialog;

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

            private AlertDialog mDialog;

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
}
