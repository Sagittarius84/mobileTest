package org.noorganization.instalist.model;

import android.test.AndroidTestCase;

import com.orm.SugarRecord;

import org.noorganization.instalist.test.utils.helper.ProductMatchingTags;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tinos_000 on 20.05.2015.
 */
public class TaggedProductTest extends AndroidTestCase {

    private final String TEST_TAG = "TEST";

    private final Product[] mProducts = new Product[3];
    private final Tag[] mTags = new Tag[2];
    private final ProductMatchingTags[] mProductTagPairArray = new ProductMatchingTags[3];

    @Override
    protected void setUp() throws Exception {

        List<Tag> tagsForFirstProduct   = new ArrayList<Tag>();
        List<Tag> tagsForSecondProduct  = new ArrayList<Tag>();
        List<Tag> tagsForThirdProduct   = new ArrayList<Tag>();

            mProducts[0] = new Product(TEST_TAG.concat("Butterkaese"),null);
            mProducts[1] = new Product(TEST_TAG.concat("Chateau Camembert"), null);
            mProducts[2] =  new Product(TEST_TAG.concat("Campagne Camembert"), null);

            mTags[0] = new Tag(TEST_TAG.concat("Cheese"));
            mTags[1] = new Tag(TEST_TAG.concat("Camembert"));

        mProductTagPairArray[0] = new ProductMatchingTags();
        mProductTagPairArray[1] = new ProductMatchingTags();
        mProductTagPairArray[2] = new ProductMatchingTags();

        mProductTagPairArray[0].mProduct = mProducts[0];
        mProductTagPairArray[1].mProduct = mProducts[1];
        mProductTagPairArray[2].mProduct = mProducts[2];

            // add a tag
            Tag cheeseTag =  mTags[0];
            Tag specialCheeseTag = mTags[1];

            // save the entries
            cheeseTag.save();
            specialCheeseTag.save();

            Product cheese1 =  mProducts[0];
            Product cheese2 =  mProducts[1];
            Product cheese3 =  mProducts[2];

            cheese1.save();
            cheese2.save();
            cheese3.save();

            TaggedProduct taggedProduct1 = new TaggedProduct(cheeseTag, cheese1);
            tagsForFirstProduct.add(cheeseTag);
            TaggedProduct taggedProduct2 = new TaggedProduct(cheeseTag, cheese2);
        tagsForSecondProduct.add(cheeseTag);
            TaggedProduct taggedProduct3 = new TaggedProduct(cheeseTag, cheese3);
tagsForThirdProduct.add(cheeseTag);
            taggedProduct1.save();
            taggedProduct2.save();
            taggedProduct3.save();

            TaggedProduct taggedProduct4 = new TaggedProduct(specialCheeseTag, cheese2);
        tagsForSecondProduct.add(specialCheeseTag);
            TaggedProduct taggedProduct5 = new TaggedProduct(specialCheeseTag, cheese3);
        tagsForThirdProduct.add(specialCheeseTag);
            taggedProduct4.save();
            taggedProduct5.save();

        mProductTagPairArray[0].mTagList = tagsForFirstProduct;
        mProductTagPairArray[1].mTagList = tagsForSecondProduct;
        mProductTagPairArray[2].mTagList = tagsForThirdProduct;

        }


    @Override
    public void tearDown() {
        List<Product> productList = Product.find(Product.class, "m_name LIKE '" + TEST_TAG + "%'");
        List<Tag> tagList         = Tag.find(Tag.class, "m_name LIKE '" + TEST_TAG + "%'");

        for(Product product : productList){
            SugarRecord.deleteAll(TaggedProduct.class, "m_product = ?",
                    product.getId().toString());
        }

        for(Tag tag : tagList){
            SugarRecord.deleteAll(TaggedProduct.class, "m_tag = ?", tag.getId().toString());
        }

        SugarRecord.deleteAll(Product.class, "m_name LIKE '" + TEST_TAG + "%'");
        SugarRecord.deleteAll(Tag.class, "m_name LIKE '" + TEST_TAG + "%'");
    }

    public void testFindByProduct(){
        List<TaggedProduct> testPRoducts = TaggedProduct.find(TaggedProduct.class, null, null);

        for(ProductMatchingTags matchingTags : mProductTagPairArray){
            List<TaggedProduct> taggedProducts = TaggedProduct.findTaggedProductsByProduct(matchingTags.mProduct);
            for(TaggedProduct taggedProduct : taggedProducts){
                boolean found = false;
                for(Tag tag : matchingTags.mTagList){
                    if(taggedProduct.mTag.mName.equals(tag.mName)){
                        found = true;
                    }
                }
                assertTrue(found);
            }

        }

    }
}
