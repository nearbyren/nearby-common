package ren.nearby.common_module.event;

import androidx.annotation.NonNull;

import io.reactivex.Flowable;
import io.reactivex.processors.FlowableProcessor;
import io.reactivex.processors.PublishProcessor;


/**
 * 处理背压
 * Created by Administrator on 2016/4/28.
 */
public class RxBus2 {
    private static volatile RxBus2 defaultInstance;
    //主题
    public final FlowableProcessor<Object> bus;


    public RxBus2() {
        bus = PublishProcessor.create().toSerialized();
    }


    public static RxBus2 getDefault() {
        RxBus2 rxBus = defaultInstance;
        if (defaultInstance == null) {
            synchronized (RxBus2.class) {
                rxBus = defaultInstance;
                if (defaultInstance == null) {
                    rxBus = new RxBus2();
                    defaultInstance = rxBus;
                }
            }
        }
        return rxBus;
    }


    public void post(@NonNull Object content) {
        bus.onNext(content);
    }

    // 根据传递的 eventType 类型返回特定类型(eventType)的 被观察者
    public <T> Flowable<T> toFlowable(Class<T> eventType) {

        return bus.ofType(eventType);
    }


    public void unregister() {
        bus.onComplete();
    }

    public boolean hasSubscribers() {
        return bus.hasSubscribers();
    }
}
