package org.noorganization.instalist.controller.implementation;

import com.orm.SugarRecord;
import com.orm.query.Condition;
import com.orm.query.Select;

import org.noorganization.instalist.controller.ICategoryController;
import org.noorganization.instalist.controller.event.CategoryChangedMessage;
import org.noorganization.instalist.controller.event.Change;
import org.noorganization.instalist.controller.event.ListChangedMessage;
import org.noorganization.instalist.model.Category;
import org.noorganization.instalist.model.ShoppingList;

import java.util.List;

import de.greenrobot.event.EventBus;

public class CategoryController implements ICategoryController {
    private static CategoryController mInstance;

    private EventBus mBus;

    private CategoryController() {
        mBus = EventBus.getDefault();
    }

    static CategoryController getInstance() {
        if (mInstance == null) {
            mInstance = new CategoryController();
        }
        return mInstance;
    }

    @Override
    public Category createCategory(String _name) {
        if (_name == null || _name.length() == 0 || nameUsed(_name, null)) {
            return null;
        }

        Category rtn = new Category(_name);
        rtn.save();

        mBus.post(new CategoryChangedMessage(Change.CREATED, rtn));

        return rtn;
    }

    @Override
    public Category renameCategory(Category _toRename, String _newName) {
        if (_toRename == null) {
            return null;
        }

        Category rtn = SugarRecord.findById(Category.class, _toRename.getId());
        if (rtn == null) {
            return null;
        }

        if (_newName != null && _newName.length() > 0 && !nameUsed(_newName, rtn.getId())) {
            rtn.mName = _newName;
            rtn.save();
        }

        mBus.post(new CategoryChangedMessage(Change.CHANGED, rtn));

        return rtn;
    }

    @Override
    public void removeCategory(Category _toRemove) {
        if (_toRemove == null) {
            return;
        }

        if (SugarRecord.findById(Category.class, _toRemove.getId()) != null) {
            List<ShoppingList> listsToUnlink = Select.from(ShoppingList.class).
                    where(Condition.prop(ShoppingList.ATTR_CATEGORY).eq(_toRemove.getId())).list();
            for (ShoppingList currentList : listsToUnlink) {
                currentList.mCategory = null;
            }
            SugarRecord.saveInTx(listsToUnlink);

            _toRemove.delete();

            for (ShoppingList savedList : listsToUnlink) {
                mBus.post(new ListChangedMessage(Change.CHANGED, savedList));
            }
            mBus.post(new CategoryChangedMessage(Change.DELETED, _toRemove));
        }
    }

    private boolean nameUsed(String _search, Long _ignoreId) {
        for (Category toCheck : Select.from(Category.class).
                where(Condition.prop(Category.ATTR_NAME).eq(_search)).list()) {
            if (_ignoreId == null || _ignoreId.compareTo(toCheck.getId()) != 0) {
                return true;
            }
        }

        return false;
    }
}
