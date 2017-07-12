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
import android.support.annotation.StringRes;
import android.support.v4.util.TimeUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.orhanobut.logger.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by yishangfei on 2017/7/11 0011.
 * 个人主页：http://yishangfei.me
 * Github:https://github.com/yishangfei
 */
public class Learn2 extends AppCompatActivity {

    private Disposable disposable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        distinct();
        filter();
        buffer();
        timer();
        interval();
        doOnNext();
        skip();
        take();
    }

    //去掉重复的字符
    private void distinct() {
        Observable.just(1, 1, 2, 2, 3, 4, 5)
                .distinct()
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(@NonNull Integer integer) throws Exception {
                        Logger.d("Distinct :"+integer);
                    }
                });
    }

    //过滤掉不符合条件的值
    private void filter() {
        Observable.just(1,20,9,-4,31)
                .filter(new Predicate<Integer>() {
                    @Override
                    public boolean test(@NonNull Integer integer) throws Exception {
                        return integer>=10;
                    }
                }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(@NonNull Integer integer) throws Exception {
                Logger.d("Filter :"+ integer);
            }
        });
    }

    // buffer的第一个参数是count，代表最大取值，然后skip是每次跳过个事件
    private void buffer() {
        Observable.just(1,2,3,4,5)
                .buffer(3,3)
                .subscribe(new Consumer<List<Integer>>() {
                    @Override
                    public void accept(@NonNull List<Integer> integers) throws Exception {
                        Logger.d("buffer size : " + integers.size());
                        for (Integer i : integers){
                            Logger.d(i);
                        }
                    }
                });
    }

    //延时操作
    private void timer() {
        Logger.d(getNowStrTime());
        Observable.timer(10, TimeUnit.SECONDS)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())//timer默认在新线程，所以需要切换回主线程
        .subscribe(new Consumer<Long>() {
            @Override
            public void accept(@NonNull Long aLong) throws Exception {
                Logger.d("timer :" + aLong + " at " + getNowStrTime());
            }
        });
    }

    //interval操作符用于间隔时间执行某个操作，三个参数，分别是发送延迟，间隔时间，时间单位。
    //这个是间隔执行，所以当Activity都销毁的时候，实际上这个操作还依然在进行,所以利用Disposable销毁
    private void interval(){
        Logger.d(getNowStrTime());
        disposable= Observable.interval(3,2,TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(@NonNull Long aLong) throws Exception {
                        Logger.d("timer :" + aLong + " at " + getNowStrTime());
                    }
                });
    }

    //doOnNext它的作用是让订阅者在接收到数据之前干点事情
    private void doOnNext(){
        Observable.just(1,2,3,4,5)
                .doOnNext(new Consumer<Integer>() {
                    @Override
                    public void accept(@NonNull Integer integer) throws Exception {
                        Logger.d("doOnNext 保存 :"+integer+"  成功");
                    }
                }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(@NonNull Integer integer) throws Exception {
                Logger.d("doOnNext :"+integer);
            }
        });
    }

    //skip其实作用就是接受一个 long型参数count,代表跳过count个数目开始接收
    private void skip(){
        Observable.just(1,2,3,4,5)
                .skip(2)
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(@NonNull Integer integer) throws Exception {
                        Logger.d("skip :"+integer);
                    }
                });
    }

    //take接受一个long型参数count,代表至多接收count个数据
    private void take(){
        Observable.just(1,2,3,4,5)
                .take(3)
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(@NonNull Integer integer) throws Exception {
                           Logger.d("take :"+ integer);
                    }
                });
    }

    //一个简单的发射器依次调用onNext()方法
    private void just(){
        Observable.just(1,2,3,4,5)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(@NonNull Integer integer) throws Exception {
                          Logger.d("just :"+integer);
                    }
                });
    }

    public String getNowStrTime() {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String time=sdf.format(new Date());
        return time;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (disposable !=null && !disposable.isDisposed()){
            disposable.dispose();
        }
    }
}
