# LearnRxjava

Rxjava2 操作符学习

## [Learn1:](https://github.com/yishangfei/LearnRxjava/blob/master/app/src/main/java/com/ysf/learnrxjava/Learn1.java "Learn1")


#### Create
     Disposable，可以直接调用切断，当isDisposed()返回为false的时候，接收器能正常接收事件，但当其为true的时候，
     接收器停止了接收。可以通过此参数动态控制接收事件了
 
#### Map
	Map的作用是对发射时间发送的每一个事件应用一个函数，是的每一个事件都按照指定的函数去变化
 
#### Zip
	 zip用于合并事件，最终配对出的Observable发射事件数目只和少的那个相同

#### Concat
	单一的把两个发射器连接成一个发射器,有条不紊的排序接收

#### FlatMap
	FlatMap可以把一个发射器Observable 通过某种方法转换为多个Observables，然后再把这些分散的Observables装进一个单一的发射器Observable。    	      FlatMap并不能保证事件的顺序

#### concatMap
	和FlatMap一样的，但是ConcatMap能保证事件的顺序

## [Learn2:](https://github.com/yishangfei/LearnRxjava/blob/master/app/src/main/java/com/ysf/learnrxjava/Learn2.java "Learn2")


#### distinct
	去掉重复的字符

#### filter
	过滤掉不符合条件的值

#### buffer
	buffer的第一个参数是count，代表最大取值，然后skip是每次跳过个事件

#### timer
	延时操作

#### interval
	interval操作符用于间隔时间执行某个操作，三个参数，分别是发送延迟，间隔时间，时间单位。
	这个是间隔执行，所以当Activity都销毁的时候，实际上这个操作还依然在进行,所以利用Disposable销毁

#### doOnNext
	doOnNext它的作用是让订阅者在接收到数据之前干点事情

#### skip
	skip其实作用就是接受一个 long型参数count,代表跳过count个数目开始接收

#### take
	take接受一个long型参数count,代表至多接收count个数据

#### just
	一个简单的发射器依次调用onNext()方法

## [Learn3:](https://github.com/yishangfei/LearnRxjava/blob/master/app/src/main/java/com/ysf/learnrxjava/Learn3.java "Learn3")

#### Single
	Single接收一个参数,而SingleObserver只会调用onError或者onSuccess
	
#### debounce
	去除发送间隔时间小于200毫秒的发射事件
	
#### defer
	每次订阅都会创建一个新的Observable,如果没有被订阅,就不会产生新的Observable
	
#### last
	last操作符仅取出可观察到的最后一个值,或者是满足某些条件的最后一项
	
#### merge
	merge的作用是把多个Observable结合起来,接受可变参数,也支持迭代器集合。
	注意它和concat的区别在于,不用等到发射器A发送完所有的事件再进行发射器B的发送
	
#### reduce
	reduce每次用一个方法处理一个值,可以有一个seed作为初始值
	
#### scan
	scan作用和上面的reduce一致,scan会始终如一地把每一个步骤都输出
	
#### window
	按照实际划分窗口，将数据发送给不同的Observable
