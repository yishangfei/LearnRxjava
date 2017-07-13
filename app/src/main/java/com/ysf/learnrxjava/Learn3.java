package com.ysf.learnrxjava;
////////////////////////////////////////////////////////////////////
//                          _ooOoo_                               //
//                         o8888888o                              //
//                         88" . "88                              //
//                         (| ^_^ |)                              //
//                         O\  =  /O                              //
//                      ____/`---'\____                           //
//                    .'  \\|     |//  `.                         //
//                   /  \\|||  :  |||//  \                        //
//                  /  _||||| -:- |||||-  \                       //
//                  |   | \\\  -  /// |   |                       //
//                  | \_|  ''\---/''  |   |                       //
//                  \  .-\__  `-`  ___/-. /                       //
//                ___`. .'  /--.--\  `. . ___                     //
//              ."" '<  `.___\_<|>_/___.'  >'"".                  //
//            | | :  `- \`.;`\ _ /`;.`/ - ` : | |                 //
//            \  \ `-.   \_ __\ /__ _/   .-` /  /                 //
//      ========`-.____`-.___\_____/___.-`____.-'========         //
//                           `=---='                              //
//      ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^        //
//         佛祖保佑       永无BUG     永不修改                  //
////////////////////////////////////////////////////////////////////

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.orhanobut.logger.Logger;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by yishangfei on 2017/7/12 0012.
 * 个人主页：http://yishangfei.me
 * Github:https://github.com/yishangfei
 */
public class Learn3 extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Single();
        debounce();
        defer();
        last();
        merge();
        reduce();
        scan();
        window();
    }

    //Single接收一个参数,而SingleObserver只会调用onError或者onSuccess
    private void Single() {
        Single.just(new Random().nextInt())
                .subscribe(new SingleObserver<Integer>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NonNull Integer integer) {
                        Logger.d("single : onSuccess : " + integer);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Logger.d("single : onError : " + e.getMessage());
                    }
                });
    }

    //去除发送间隔时间小于200毫秒的发射事件
    private void debounce() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                Thread.sleep(100);
                e.onNext(2);
                Thread.sleep(200);
                e.onNext(3);
                Thread.sleep(300);
                e.onNext(4);
                Thread.sleep(400);
                e.onNext(5);
                Thread.sleep(500);
                e.onComplete();
            }
        }).debounce(200, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(@NonNull Integer integer) throws Exception {
                        Logger.d("debounce :" + integer);
                    }
                });
    }

    //每次订阅都会创建一个新的Observable,如果没有被订阅,就不会产生新的Observable
    private void defer() {
        Observable<Integer> observable = Observable.defer(new Callable<ObservableSource<Integer>>() {
            @Override
            public ObservableSource<Integer> call() throws Exception {
                return Observable.just(1, 2, 3, 4, 5);
            }
        });

        observable.subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull Integer integer) {
                Logger.d("defer : onNext" + integer);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Logger.d("defer : onError" + e.getMessage());
            }

            @Override
            public void onComplete() {
                Logger.d("defer : onComplete");
            }
        });

    }

    //last操作符仅取出可观察到的最后一个值,或者是满足某些条件的最后一项
    private void last() {
        Observable.just(1, 2, 3)
                .last(4)
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(@NonNull Integer integer) throws Exception {
                        Logger.d("last :" + integer);
                    }
                });
    }

    //merge的作用是把多个Observable结合起来,接受可变参数,也支持迭代器集合。注意它和concat的区别在于,
    //不用等到发射器A发送完所有的事件再进行发射器B的发送。
    private void merge() {
        Observable.merge(Observable.just(1, 2), Observable.just(3, 4, 5))
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(@NonNull Integer integer) throws Exception {
                        Logger.d("merge :" + integer);
                    }
                });
    }

    //reduce每次用一个方法处理一个值,可以有一个seed作为初始值
    private void reduce() {
        Observable.just(1, 2, 3)
                .reduce(new BiFunction<Integer, Integer, Integer>() {
                    @Override
                    public Integer apply(@NonNull Integer integer, @NonNull Integer integer2) throws Exception {
                        return integer + integer2;
                    }
                }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(@NonNull Integer integer) throws Exception {
                Logger.d("reduce :"+integer);
            }
        });
    }

    //scan作用和上面的reduce一致,scan会始终如一地把每一个步骤都输出
    private void scan() {
        Observable.just(1,2,3,4,5)
                .scan(new BiFunction<Integer, Integer, Integer>() {
                    @Override
                    public Integer apply(@NonNull Integer integer, @NonNull Integer integer2) throws Exception {
                        return integer+integer2;
                    }
                }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(@NonNull Integer integer) throws Exception {
                Logger.d("scan :"+integer);
            }
        });
    }

    //按照实际划分窗口，将数据发送给不同的Observable
    private void window() {
                  Observable.interval(1,TimeUnit.SECONDS)// 间隔一秒发一次
                          .take(15)// 最多接收15个
                          .window(3,TimeUnit.SECONDS)
                          .subscribeOn(Schedulers.io())
                          .observeOn(AndroidSchedulers.mainThread())
                          .subscribe(new Consumer<Observable<Long>>() {
                              @Override
                              public void accept(@NonNull Observable<Long> longObservable) throws Exception {
                                  Logger.d("Sub Divide begin...");
                                  longObservable.subscribeOn(Schedulers.io())
                                          .observeOn(AndroidSchedulers.mainThread())
                                          .subscribe(new Consumer<Long>() {
                                              @Override
                                              public void accept(@NonNull Long aLong) throws Exception {
                                                        Logger.d("window :"+aLong);
                                              }
                                          });
                              }
                          });


    }
}
