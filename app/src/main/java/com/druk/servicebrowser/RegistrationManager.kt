package com.druk.servicebrowser

import android.content.Context
import com.github.druk.rx2dnssd.BonjourService
import com.walhalla.whatismyipaddress.TApp.Companion.getRxDnssd
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.subjects.PublishSubject
import java.util.Collections

class RegistrationManager {
    private val mRegistrations: MutableMap<BonjourService?, Disposable?> =
        HashMap<BonjourService?, Disposable?>()

    fun register(context: Context, bonjourService: BonjourService): Observable<BonjourService?> {
        val subject = PublishSubject.create<BonjourService?>()
        val subscriptions = arrayOfNulls<Disposable>(1)
        subscriptions[0] = getRxDnssd(context)!!.register(bonjourService)
            .doOnNext(Consumer { service: BonjourService? ->
                mRegistrations.put(
                    service,
                    subscriptions[0]
                )
            })
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribe(Consumer { t: BonjourService? -> subject.onNext(t!!) })
        return subject
    }

    fun unregister(service: BonjourService?) {
        val subscription = mRegistrations.remove(service)
        subscription!!.dispose()
    }

    val registeredServices: MutableList<BonjourService>
        get() = Collections.unmodifiableList<BonjourService>(
            ArrayList<BonjourService?>(
                mRegistrations.keys
            )
        )
}
