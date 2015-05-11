package org.noorganization.instalist.controller.implementation;

import org.noorganization.instalist.controller.ITagController;
import org.noorganization.instalist.model.Tag;

public class TagController implements ITagController {

    private static TagController mInstance;

    private TagController() {
    }

    static ITagController getInstance() {
        if (mInstance == null) {
            mInstance = new TagController();
        }
        return mInstance;
    }

    @Override
    public Tag createTag(String _title) {

        // TODO dummy implementation.
        return null;
    }

    @Override
    public Tag renameTag(Tag _toRename, String _newTitle) {

        // TODO dummy implementation.
        return null;
    }

    @Override
    public void removeTag(Tag _toRemove) {
        // TODO dummy implementation.
    }
}
