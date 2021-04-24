package com.example.myrxjava

import android.os.Handler
import android.os.Looper
import android.util.Log

/**
 * Rxjava核心思想   1.利用递归  完成事件订阅
 * 利用            2.参数的封装传递 完成 观察者上溯
 * 利用            3.接口回调 完成事件响应
 */

/**
 * 被观察者     Rxjava核心思想的类    只有线程的切换 map操作符  和订阅三个功能
 *
 * 对每一个操作符的 重新返回被观察者对象 是为了限制每一次的访问 权限  如果是 返回本身的话
 * 那么此对象就可以拿到全局的资源   但是这并不合理
 * 我们应该让它只关心  被传下来的参数
 */
abstract class Observable<T>{   //泛型类
    companion object{
        /**
         * 所做的事情就是传递本身  且返回本身    真正的作用就是  让用户自己去实现这个被返回对象内部的抽象方法
         * 返回的被观察者的对象 是一个已经激活了的   意思就是其内部的方法是被实现了的  但是这个被实现的方法
         * 再一次接受了一个接口   且是一个并没有被实现的接口   在这个被用户实现的方法里面去调用接口的方法
         * 就可以产生接口回调的效果
         *  create方法的初衷其实就是去实现 抽象类  也就是会去实现抽象类的具体方法  所以才是传递本身 且返回本身
         *  实现的抽象方法里面又有一个未被实现的  接口参数
         */
        public fun<T> create(observable: Observable<T>) = observable
    }

    /**
     * 用来订阅观察者
     */
    public abstract fun subscribe(observer: Observer<T>)


    /**
     * 在这里接受的是一个带两个泛型的接口    这里不实现  留给开发者自己去实现  这里
     * 只需要关心  上一个接口的类型    返回指定的泛型
     */
    fun<R> Map(function: Function<T, R>):Observable<R>{
       /**
        * 重新返回了一个 Observable对象    但是却重写了subscribe方法
        */
        return object : Observable<R>() {
            /**
             * 紧随其后就会再一次调用  Observable的subscribe
             */
            override fun subscribe(observer: Observer<R>) {
                /**
                 * 这个类 在这里其实 是处于未激活 状态   也就相当于 一个
                 * 已经被实现了的 接口  被当做参数  传递下去了
                 * 处于一种等待回调的状态   并且将传进来的  观察者 对象
                 * 用这个 实现的接口做一下包装  揉成一个接口
                 * 再把整个包装起来的接口  用主类的方法  给它当做参数  传递下去
                 */
                val observerB = object :Observer<T>{
                    override fun onNext(t: T) {
                        val r = function.apply(t)
                        observer.onNext(r)
                    }
                    override fun onComplete() {
                        observer.onComplete()
                    }
                    override fun onError(e: Throwable) {
                        observer.onError(e)
                    }
                }
                this@Observable.subscribe(observerB)

            }
        }
    }

    /**
     * 切换到子线程
     */
    fun subscribeOnThread(): Observable<T> {
                return object :Observable<T>(){
                    override fun subscribe(observer: Observer<T>) {
                            val observerB = object : Observer<T> {
                                override fun onNext(t: T) {
                                        Thread{

                                            Log.i("测试", "observer.onNext(t)subscribeOnThread() 应该是子线程" + Thread.currentThread())
                                            observer.onNext(t)
                                        }.start()

                                }

                                override fun onComplete() {
                                    observer.onComplete()
                                }

                                override fun onError(e: Throwable) {
                                    observer.onError(e)
                                }
                            }
                            this@Observable.subscribe(observerB)


                    }
                }
            }


    /**
     * 切换到主线程
     */

    val handler = Handler(Looper.getMainLooper())
    fun observerOnMain(): Observable<T> {
        return object :Observable<T>(){
            override fun subscribe(observer: Observer<T>) {
                        val observerB = object :Observer<T>{
                            override fun onNext(t: T) {
                                handler.post {
                                    observer.onNext(t)
                                }
                            }
                            override fun onComplete() {
                                observer.onComplete()
                            }
                            override fun onError(e: Throwable) {
                                observer.onError(e)
                            }
                        }
                        this@Observable.subscribe(observerB)
                    }

            }

                }
            }






