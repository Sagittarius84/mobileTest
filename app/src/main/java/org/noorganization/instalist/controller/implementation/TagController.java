package org.noorganization.instalist.controller.implementation;

import com.orm.SugarRecord;
import com.orm.query.Condition;
import com.orm.query.Select;

import org.noorganization.instalist.controller.IProductController;
import org.noorganization.instalist.controller.ITagController;
import org.noorganization.instalist.controller.event.Change;
import org.noorganization.instalist.controller.event.TagChangedMessage;
import org.noorganization.instalist.model.Tag;
import org.noorganization.instalist.model.TaggedProduct;

import java.util.List;

import de.greenrobot.event.EventBus;

public class TagController implements ITagController {

    private static TagController mInstance;

    private EventBus mBus;

    private TagController() {
        mBus = EventBus.getDefault();
    }

    static ITagController getInstance() {
        if (mInstance == null) {
            mInstance = new TagController();
        }
        return mInstance;
    }

    @Override
    public Tag createTag(String _title) {
        if (_title == null || Select.from(Tag.class).where(Condition.prop("m_name").eq(_title)).
                count() > 0) {
            return null;
        }

        Tag rtn = new Tag(_title);
        rtn.save();

        mBus.post(new TagChangedMessage(Change.CREATED, rtn));

        return rtn;
    }

    @Override
    public Tag renameTag(Tag _toRename, String _newTitle) {
        if (_toRename == null) {
            return null;
        }

        Tag toChange = SugarRecord.findById(Tag.class, _toRename.getId());
        if (toChange == null || _newTitle == null) {
            return toChange;
        }

        for (Tag toCheck : Select.from(Tag.class).where(Condition.prop("m_name").eq(_newTitle)).
                list()) {
            if (toCheck.getId().compareTo(toChange.getId()) != 0) {
                return toChange;
            }
        }

        toChange.mName = _newTitle;
        toChange.save();

        mBus.post(new TagChangedMessage(Change.CHANGED, toChange));

        return toChange;
    }

    @Override
    public void removeTag(Tag _toRemove) {
        if (_toRemove == null) {
            return;
        }

        List<TaggedProduct> taggedProducts = Select.from(TaggedProduct.class).
                where(Condition.prop(TaggedProduct.ATTR_TAG).eq(_toRemove.getId())).list();
        IProductController productController = ControllerFactory.getProductController();
        for (TaggedProduct toNotify : taggedProducts) {
            productController.removeTagFromProduct(toNotify.mProduct, toNotify.mTag);
        }

        _toRemove.delete();
        mBus.post(new TagChangedMessage(Change.DELETED, _toRemove));
    }
}
