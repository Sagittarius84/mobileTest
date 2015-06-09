package org.noorganization.instalist.model;

import android.test.AndroidTestCase;

import com.orm.SugarConfig;
import com.orm.SugarRecord;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.io.File;

/**
 * This test is not written to check whether SugarORM works like it should (the project gets already
 * tested) but for finding out how to use it.
 * Created by michi on 15.04.2015.
 * @see  <a href="https://travis-ci.org/satyan/sugar">SugarORM official tests</a>
 */
public class SugarORMTest extends AndroidTestCase {

    final static String DB_NAME = "shopping.db";
    File defaultDbPath;
    File backupDbPath;

    @Override
    public void setUp() {
        /*defaultDbPath = getContext().getDatabasePath(DB_NAME);
        backupDbPath = new File(defaultDbPath.getPath() + File.pathSeparator + "testBackup");
        if(defaultDbPath.exists())
            defaultDbPath.renameTo(backupDbPath);*/
        
        // Das mag SugarORM scheinbar nicht. Da es offensichtlich keine Möglichkeit gibt, zwischen
        // Test- und Produktiv-Datenbank zu unterscheiden, muss leider die Produktiv-Datenbank
        // herhalten.
    }

    @Override
    public void tearDown() {
        /*if (backupDbPath.exists())
            backupDbPath.renameTo(defaultDbPath);*/
    }

    public void testCreatingAndGettingAnObject() throws Exception {
        Unit justAUnit = new Unit("kg");
        justAUnit.save();

        Unit foundUnit = Select.from(Unit.class).where(Condition.prop("m_name").eq("kg")).first();
        assertEquals("kg", foundUnit.mName);
    }

    public void testRelationAndUpdate() throws Exception {
        Product justAProduct = new Product("Reis", null);
        justAProduct.save();

        Unit justAUnit = new Unit("kg");
        justAUnit.save();

        justAProduct.mUnit = justAUnit;
        justAProduct.save();

        Product testProduct = Select.from(Product.class).
                where(Condition.prop("id").eq(justAProduct.getId())).
                first();
        assertEquals("kg", testProduct.mUnit.mName);
        assertEquals(justAUnit.getId(), testProduct.mUnit.getId());
    }

    public void testNameConversion() throws Exception {
        assertEquals("SHOPPING_LIST", SugarRecord.getTableName(ShoppingList.class));
    }
}
