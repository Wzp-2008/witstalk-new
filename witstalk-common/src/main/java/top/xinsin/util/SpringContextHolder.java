package top.xinsin.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Spring上下文工具类，用于在静态方法中获取Bean
 */
@Component
public class SpringContextHolder implements ApplicationContextAware {

    // 静态持有ApplicationContext引用
    private static ApplicationContext applicationContext;

    /**
     * 设置ApplicationContext（由Spring自动调用）
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextHolder.applicationContext = applicationContext;
    }

    /**
     * 获取ApplicationContext
     */
    public static ApplicationContext getApplicationContext() {
        checkApplicationContext();
        return applicationContext;
    }

    /**
     * 根据Bean名称获取Bean
     */
    public static Object getBean(String name) {
        checkApplicationContext();
        return applicationContext.getBean(name);
    }

    /**
     * 根据Bean类型获取Bean
     */
    public static <T> T getBean(Class<T> clazz) {
        checkApplicationContext();
        return applicationContext.getBean(clazz);
    }

    /**
     * 根据名称和类型获取Bean
     */
    public static <T> T getBean(String name, Class<T> clazz) {
        checkApplicationContext();
        return applicationContext.getBean(name, clazz);
    }

    /**
     * 清除ApplicationContext引用
     */
    public static void cleanApplicationContext() {
        applicationContext = null;
    }

    /**
     * 检查ApplicationContext是否为空
     */
    private static void checkApplicationContext() {
        if (applicationContext == null) {
            throw new IllegalStateException("ApplicationContext未初始化，请确保SpringContextHolder已被Spring管理");
        }
    }
}
