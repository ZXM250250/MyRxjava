package com.example.myrxjava


/**
 * 观察者
 */
interface Observer<T> {
    fun onNext(t:T)
    fun onComplete()
    fun onError(e:Throwable)
}