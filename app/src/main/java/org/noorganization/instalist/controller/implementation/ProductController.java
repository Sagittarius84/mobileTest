package org.noorganization.instalist.controller.implementation;

import org.noorganization.instalist.controller.IProductController;
import org.noorganization.instalist.model.Product;
import org.noorganization.instalist.model.Tag;
import org.noorganization.instalist.model.Unit;

public class ProductController implements IProductController {
    @Override
    public Product createProduct(String _name, Unit _unit, float _defaultAmount, float _stepAmount) {
        // TODO This method is a stub.
        return null;
    }

    @Override
    public Product modifyProduct(Product _toChange) {
        // TODO This method is a stub.
        return null;
    }

    @Override
    public boolean removeProduct(Product _toRemove, boolean _deleteCompletely) {
        // TODO This method is a stub.
        return false;
    }

    @Override
    public boolean addTagToProduct(Product _product, Tag _tag) {
        // TODO This method is a stub.
        return false;
    }

    @Override
    public void removeTagFromProduct(Product _product, Tag _tag) {
        // TODO This method is a stub.
    }


}
