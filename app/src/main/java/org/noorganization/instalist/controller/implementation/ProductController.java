package org.noorganization.instalist.controller.implementation;

import com.orm.SugarRecord;
import com.orm.query.Condition;
import com.orm.query.Select;

import org.noorganization.instalist.controller.IProductController;
import org.noorganization.instalist.model.Ingredient;
import org.noorganization.instalist.model.ListEntry;
import org.noorganization.instalist.model.Product;
import org.noorganization.instalist.model.Tag;
import org.noorganization.instalist.model.TaggedProduct;
import org.noorganization.instalist.model.Unit;

import java.util.Iterator;

public class ProductController implements IProductController {

    private static ProductController mInstance;

    public static ProductController getInstance() {
        if (mInstance == null) {
            mInstance = new ProductController();
        }
        return mInstance;
    }

    @Override
    public Product createProduct(String _name, Unit _unit, float _defaultAmount, float _stepAmount) {
        if (_name == null || _name.length() == 0) {
            return null;
        }
        if (Float.isInfinite(_defaultAmount) || Float.isNaN(_defaultAmount) || _defaultAmount <= 0.0f) {
            return null;
        }
        if (Float.isInfinite(_stepAmount) || Float.isNaN(_stepAmount) || _stepAmount < 0.0f) {
            return null;
        }
        if (_unit != null && SugarRecord.findById(Unit.class, _unit.getId()) == null) {
            return null;
        }
        if (Select.from(Product.class).where(Condition.prop("m_name").eq(_name)).count() != 0) {
            return null;
        }

        Product rtn = new Product(_name, _unit, _defaultAmount, _stepAmount);
        rtn.save();

        return rtn;
    }

    @Override
    public Product modifyProduct(Product _toChange) {
        if (_toChange == null) {
            return null;
        }
        Product rtn = SugarRecord.findById(Product.class, _toChange.getId());
        if (rtn == null) {
            return null;
        }
        if (_toChange.mName == null || _toChange.mName.length() == 0) {
            return rtn;
        }
        if (Float.isInfinite(_toChange.mDefaultAmount) || Float.isNaN(_toChange.mDefaultAmount) ||
                _toChange.mDefaultAmount <= 0.0f) {
            return rtn;
        }
        if (Float.isInfinite(_toChange.mStepAmount) || Float.isNaN(_toChange.mStepAmount) ||
                _toChange.mStepAmount < 0.0f) {
            return rtn;
        }
        if (_toChange.mUnit != null &&
                SugarRecord.findById(Unit.class, _toChange.mUnit.getId()) == null) {
            return rtn;
        }

        for (Product productToCheck : Select.from(Product.class).
                where(Condition.prop("m_name").eq(_toChange.mName)).list()) {
            if (productToCheck.getId().compareTo(_toChange.getId()) != 0) {
                return rtn;
            }
        }

        rtn.mName = _toChange.mName;
        rtn.mUnit = _toChange.mUnit;
        rtn.mDefaultAmount = _toChange.mDefaultAmount;
        rtn.mStepAmount = _toChange.mStepAmount;
        rtn.save();

        return rtn;
    }

    @Override
    public boolean removeProduct(Product _toRemove, boolean _deleteCompletely) {
        if (_toRemove == null) {
            return false;
        }

        if (!_deleteCompletely) {
            // first, here were also counted references of TaggedProducts. That's not useful.
            long countOfRefs = Select.from(ListEntry.class).
                    where(Condition.prop("m_product").eq(_toRemove.getId())).count();
            countOfRefs += Select.from(Ingredient.class).
                    where(Condition.prop("m_product").eq(_toRemove.getId())).count();
            if (countOfRefs > 0) {
                return false;
            }
        }

        SugarRecord.deleteAll(ListEntry.class, "m_product = ?", _toRemove.getId() + "");
        SugarRecord.deleteAll(TaggedProduct.class, "m_product = ?", _toRemove.getId() + "");
        SugarRecord.deleteAll(Ingredient.class, "m_product = ?", _toRemove.getId() + "");
        _toRemove.delete();

        return true;
    }

    @Override
    public boolean addTagToProduct(Product _product, Tag _tag) {
        if (_product == null || _tag == null ||
                SugarRecord.findById(Product.class, _product.getId()) == null ||
                SugarRecord.findById(Tag.class, _tag.getId()) == null) {
            return false;
        }

        long existingTagCount = Select.from(TaggedProduct.class).where(
                Condition.prop("m_product").eq(_product.getId())).and(
                Condition.prop("m_tag").eq(_tag.getId())).count();
        if (existingTagCount == 0) {
            TaggedProduct newProductsTag = new TaggedProduct(_tag, _product);
            newProductsTag.save();
        }

        return true;
    }

    @Override
    public void removeTagFromProduct(Product _product, Tag _tag) {
        if (_product == null || _tag == null) {
            return;
        }

        SugarRecord.deleteAll(TaggedProduct.class, "m_product = ? and m_tag = ?",
                _product.getId()+"", _tag.getId()+"");
    }


}
