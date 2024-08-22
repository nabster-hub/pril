package com.example.tateknew.data

import org.json.JSONObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Repository(private val db: AppDatabase) {

    fun insertObject(obj: JSONObject) {
        val tpsEntity = TpsEntity(
            id = obj.optInt("id"),
            name = obj.optString("name"),
            baseId = obj.optInt("base_id"),
            fullName = obj.optString("full_name"),
            createdAt = obj.optString("created_at"),
            updatedAt = obj.optString("updated_at")
        )

        CoroutineScope(Dispatchers.IO).launch {
            db.objectDao().insertObject(tpsEntity)

            val mtrsArray = obj.optJSONArray("mtrs")
            if (mtrsArray != null) {
                for (i in 0 until mtrsArray.length()) {
                    insertMtr(mtrsArray.getJSONObject(i))
                }
            }
        }
    }

    fun insertMtr(mtr: JSONObject) {
        val mtrEntity = MtrEntity(
            id = mtr.optLong("id"),
            abonentId = mtr.optLong("abonent_id"),
            nobjId = mtr.optInt("nobj_id"),
            baseId = mtr.optInt("base_id"),
            name = mtr.optString("name"),
            puName = mtr.optString("pu_name"),
            itemNo = mtr.optString("item_no"),
            status = mtr.optInt("status"),
            ecapId = mtr.optInt("ecap_id"),
            sredrashod = if (mtr.isNull("sredrashod")) null else mtr.optInt("sredrashod"),
            vl = mtr.optString("vl"),
            ktt = mtr.optInt("ktt"),
            createdAt = mtr.optString("created_at"),
            updatedAt = mtr.optString("updated_at")
        )

        CoroutineScope(Dispatchers.IO).launch {
            db.mtrDao().insertMtr(mtrEntity)

            val abonent = mtr.optJSONObject("abonents")
            if (abonent != null) {
                insertAbonent(abonent)
            }
        }
    }

    fun insertAbonent(abonent: JSONObject) {
        val abonentEntity = AbonentEntity(
            clientId = abonent.optLong("client_id"),
            ctt = abonent.optString("ctt"),
            ct = abonent.optInt("ct"),
            name = abonent.optString("name"),
            clientNo = abonent.optLong("client_no"),
            address = abonent.optString("address"),
            baseId = abonent.optInt("base_id"),
            clientGr = abonent.optString("client_gr"),
            street = abonent.optString("street"),
            home = abonent.optString("home"),
            flat = abonent.optString("flat"),
            createdAt = abonent.optString("created_at"),
            updatedAt = abonent.optString("updated_at")
        )

        CoroutineScope(Dispatchers.IO).launch {
            db.abonentDao().insertAbonent(abonentEntity)
        }
    }
}
