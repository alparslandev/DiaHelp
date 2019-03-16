package com.diahelp.tools

import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.diahelp.model.Identity
import io.realm.RealmList

fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}


fun <T:Identity> List<T>.updateById(id: Int, model: T, list: RealmList<T>) : RealmList<T> {
    for (m in list) {
        if (id == m.Id) {
            list.set(list.indexOf(m), model)
            break
        }
    }
    return list
}

fun <T:Identity> List<T>.updateById(model: T, list: RealmList<T>) : RealmList<T> {
    for (m in list) {
        if (model.Id == m.Id) {
            list.set(list.indexOf(m), model)
            break
        }
    }
    return list
}