package top.xinsin.listener;

import com.mybatisflex.annotation.InsertListener;
import top.xinsin.entity.BaseEntity;
import top.xinsin.util.SecurityUtil;

public class MyInsertListener implements InsertListener {
    @Override
    public void onInsert(Object entity) {
        BaseEntity baseEntity = (BaseEntity) entity;
        if (SecurityUtil.getLoginUser() != null) {
            baseEntity.setCreateBy(SecurityUtil.getLoginUser().getNickName());
        }
    }
}
