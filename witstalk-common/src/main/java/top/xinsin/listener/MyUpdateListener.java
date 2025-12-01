package top.xinsin.listener;

import com.mybatisflex.annotation.UpdateListener;
import top.xinsin.entity.BaseEntity;
import top.xinsin.util.SecurityUtil;

public class MyUpdateListener implements UpdateListener {
    @Override
    public void onUpdate(Object entity) {
        BaseEntity baseEntity = (BaseEntity) entity;
        if (SecurityUtil.getLoginUser() != null) {
            baseEntity.setUpdateBy(SecurityUtil.getLoginUser().getNickName());
        }
    }
}
