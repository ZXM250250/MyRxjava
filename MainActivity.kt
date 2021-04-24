package com.example.myrxjava

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.i("测试", "活动启动" + Thread.currentThread())

        /**
         * 这里的第一步实际上是   实现了抽象类->(那也就是去实现抽象类里面的  抽象方法   也就是像是
         * 一个匿名内部类)   但是并没有去调用 类里面的方法    也就是说 现在只是处于未激活状态
         * 我们自己实现这个类的方法 真正的调用其实是在后面的subscribe(Observer)
         * 传递进来了 一个观察者的参数（尽管是一个接口  但是已经是被实现了的）
         * 在这个时候  创建类里面的方法  才是被正式调用的时候
         * 已经实现了的这个方法被调用   而又是给它传递了 一个接口  调用这个方法的时候
         * 其实也就是调用了  接口里面的方法   发生了接口回调
         * 所以  就可以 把需要的数据传递到下游
         * 产生链式调度
         */
        Observable.create(object : Observable<String>() {
            /**
             *这里是匿名内部类   其内部方法被实现  当这个匿名内部类  在外边调用这个
             * 方法时  这个地方就会执行  然后因为参数是接口  所以  会回调回去
             */
            override fun subscribe(observer: Observer<String>) {
                observer.onNext("123")
            }
        }).

        Map(object : Function<String, Int> {
            override fun apply(t: String): Int {
                Log.i("测试", "直接打印的线程subscribeOnThread()之前" + Thread.currentThread())
                return t.toInt()
            }
        })
                .subscribe(object : Observer<Int> {
            override fun onNext(t: Int) {
                Log.i("测试", "observerOnMain()之后.这里回调了吗应该是主线程" + Thread.currentThread().name)

            }

            override fun onComplete() {

            }

            override fun onError(e: Throwable) {

            }
        })

    }
    }
