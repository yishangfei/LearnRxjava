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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by yishangfei on 2017/7/10 0010.
 * 个人主页：http://yishangfei.me
 * Github:https://github.com/yishangfei
 */
public class Learn1 extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Create();
        Map();
        Zip();
        Concat();
        FlatMap();
        concatMap();
    }

    //Disposable，可以直接调用切断，当isDisposed()返回为false的时候，
    //接收器能正常接收事件，但当其为true的时候，接收器停止了接收。可以通过此参数动态控制接收事件了
    private void Create() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> e) throws Exception {
                Logger.d("Observable 1");
                e.onNext(1);
                Logger.d("Observable 2");
                e.onNext(2);
                Logger.d("Observable 3");
                e.onNext(3);
            }
        }).subscribe(new Observer<Integer>() {
            private int i;
            private Disposable mDisposable;

            @Override
            public void onSubscribe(@NonNull Disposable d) {
                Logger.d("onSubscribe" + " " + d.isDisposed());
                mDisposable = d;
            }

            @Override
            public void onNext(@NonNull Integer integer) {
                Logger.d("onNext" + " " + integer);
                i++;
                if (i == 2) {
                    // 在RxJava 2.x 中，新增的Disposable可以做到切断的操作，让Observer观察者不再接收上游事件
                    mDisposable.dispose();
                    Logger.d("onNext : isDisposable : " + mDisposable.isDisposed());
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Logger.d("onError" + " " + e.getMessage());
            }

            @Override
            public void onComplete() {
                Logger.d("onComplete");
            }
        });
    }

    //Map的作用是对发射时间发送的每一个事件应用一个函数，是的每一个事件都按照指定的函数去变化
    private void Map() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
            }
        }).map(new Function<Integer, String>() {
            @Override
            public String apply(@NonNull Integer integer) throws Exception {
                return "This is result " + integer;
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(@NonNull String s) throws Exception {
                Logger.d("accept " + s);
            }
        });
    }

    //zip用于合并事件，最终配对出的Observable发射事件数目只和少的那个相同
    private void Zip() {
        Observable.zip(getStringObservable(), getIntegerObservable(), new BiFunction<String, Integer, String>() {

            @Override
            public String apply(@NonNull String s, @NonNull Integer integer) throws Exception {
                return s + integer;
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(@NonNull String s) throws Exception {
                Logger.d("zip : " + s);
            }
        });
    }

    private Observable<String> getStringObservable() {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                if (!e.isDisposed()) {
                    e.onNext("A");
                    Logger.d("String : A");
                    e.onNext("B");
                    Logger.d("String : B");
                    e.onNext("C");
                    Logger.d("String : C");
                }
            }
        });
    }

    private Observable<Integer> getIntegerObservable() {
        return Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> e) throws Exception {
                if (!e.isDisposed()) {
                    e.onNext(1);
                    Logger.d("Integer : 1");
                    e.onNext(2);
                    Logger.d("Integer : 2");
                    e.onNext(3);
                    Logger.d("Integer : 3");
                    e.onNext(4);
                    Logger.d("Integer : 4");
                    e.onNext(5);
                    Logger.d("Integer : 5");
                }
            }
        });
    }

    //单一的把两个发射器连接成一个发射器,有条不紊的排序接收
    private void Concat() {
        Observable.concat(Observable.just(1, 2, 3), Observable.just(4, 5, 6))
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(@NonNull Integer integer) throws Exception {
                        Logger.d("concat :" + integer);
                    }
                });
    }

    //FlatMap可以把一个发射器Observable 通过某种方法转换为多个Observables，
    //然后再把这些分散的Observables装进一个单一的发射器Observable
    //FlatMap并不能保证事件的顺序
    private void FlatMap() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
            }
        }).flatMap(new Function<Integer, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(@NonNull Integer integer) throws Exception {
                List<String> list = new ArrayList<String>();
                for (int i = 0; i < 3; i++) {
                    list.add("I am value :" + integer);
                }
                int delayTime = (int) (1 + Math.random() * 10);
                return Observable.fromIterable(list).delay(delayTime, TimeUnit.MILLISECONDS);
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        Logger.d("FlatMap :" + s);
                    }
                });
    }

    //和FlatMap一样的，但是ConcatMap能保证事件的顺序
    private void concatMap() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
            }
        }).concatMap(new Function<Integer, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(@NonNull Integer integer) throws Exception {
                List<String> list=new ArrayList<String>();
                for (int i = 0; i < 3; i++) {
                    list.add("I am value :" + integer);
                }
                int delayTime= (int) (1+Math.random()*10);
                return Observable.fromIterable(list).delay(delayTime,TimeUnit.MILLISECONDS);
            }
        }).subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<String>() {
            @Override
            public void accept(@NonNull String s) throws Exception {
                Logger.d("ConcatMap :"+s);
            }
        });
    }

}
