package pers.avc.simple.shard.configure.datasource.event;

import org.springframework.context.ApplicationEvent;

/**
 * 移除数据源事件通知对象
 *
 * @author <a href="mailto:jzaofox@foxmail.com">AmVilCresx</a>
 */

public class RemoveDataSourceEvent extends ApplicationEvent {

    private final String lookUpKey;

    public RemoveDataSourceEvent(String lookUpKey) {
        super(lookUpKey);
        this.lookUpKey = lookUpKey;
    }

    public String getLookUpKey() {
        return lookUpKey;
    }
}
