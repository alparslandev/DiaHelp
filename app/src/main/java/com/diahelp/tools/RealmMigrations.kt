package com.diahelp.tools

import io.realm.DynamicRealm
import io.realm.RealmMigration

class RealmMigrations : RealmMigration {
    override fun migrate(realm: DynamicRealm, oldVersion: Long, newVersion: Long) {
        val schema = realm.schema

        if (oldVersion == 0L) {


            /*schema.create("BloodGlucose").addField("mealPlan", String.class);

            schema.create("Foods")
            .addField("bloodGlucoseDate", Date.class);*/

            /* schema.create("Foods")
                    .removeField("meals")
                    .addRealmListField("mealIds", schema.get("StringRealm")); */

            /*schema.create("BloodGlucose")
                    .addField("Quantity", Integer.class)
                    .addField("Date", Date.class);
            oldVersion++;*/

        }
    }
}