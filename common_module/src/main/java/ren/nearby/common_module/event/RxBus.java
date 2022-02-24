package ren.nearby.common_module.event;

import androidx.annotation.NonNull;

import com.facebook.stetho.common.LogUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;


/**
 * 无背压处理
 * Created by Administrator on 2016/4/28.
 */
public class RxBus {
    private static volatile RxBus defaultInstance;
    //主题
    public final Subject<Object> bus;




    public RxBus() {
        bus = PublishSubject.create().toSerialized();
    }


    public static RxBus getDefault() {
        RxBus rxBus = defaultInstance;
        if (defaultInstance == null) {
            synchronized (RxBus.class) {
                rxBus = defaultInstance;
                if (defaultInstance == null) {
                    rxBus = new RxBus();
                    defaultInstance = rxBus;
                }
            }
        }
        return rxBus;
    }


    //提供一个新的事件
    public void post(@NonNull Object content) {
        post(content.getClass().getName(), content);
    }
    /**
     * 触发事件
     *
     * @param tag key
     * @param content
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void post(@NonNull Object tag, @NonNull Object content) {
        List<Subject> subjectList = subjectMapper.get(tag);
        if(!isEmpty(subjectList)) {
            for(Subject subject : subjectList) {
                subject.onNext(content);
            }
        }
    }

    // 根据传递的 eventType 类型返回特定类型(eventType)的 被观察者
    public <T> Observable<T> toObservable(Class<T> eventType) {

        return bus.ofType(eventType);
    }



    @SuppressWarnings("rawtypes")
    private ConcurrentHashMap<Object, List<Subject>> subjectMapper = new ConcurrentHashMap<Object, List<Subject>>();

    /**
     * 注册事件源
     *
     * @param tag
     * @return
     */
    @SuppressWarnings({"rawtypes"})
    public <T> Observable<T> register(@NonNull Object tag) {
        List<Subject> subjectList = subjectMapper.get(tag);

        if (null == subjectList) {
            subjectList = new ArrayList<Subject>();
            subjectMapper.put(tag, subjectList);
        }
        Subject<T> subject;
        subjectList.add(subject = PublishSubject.create());
        LogUtil.d("register", tag + "  size:" + subjectList.size());
        return subject;
    }

    /**
     * 取消监听
     *
     * @param tag
     * @param observable
     * @return
     */
    @SuppressWarnings("rawtypes")
    public RxBus unregister(@NonNull Object tag,
                            @NonNull Observable<?> observable) {
        if (null == observable)
            return RxBus.getDefault();
        List<Subject> subjects = subjectMapper.get(tag);
        if (null != subjects) {
            subjects.remove((Subject<?>) observable);
            if (isEmpty(subjects)) {
                subjectMapper.remove(tag);
                LogUtil.d("unregister", tag + "  size:" + subjects.size());
            }
        }
        return RxBus.getDefault();
    }

    @SuppressWarnings("rawtypes")
    public static boolean isEmpty(Collection<Subject> collection) {
        return null == collection || collection.isEmpty();
    }
}
