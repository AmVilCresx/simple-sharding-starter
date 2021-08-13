package pers.avc.simple.shard.configure.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;
import pers.avc.simple.shard.configure.datasource.DynamicDataSourceContextHolder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 请求进来时，重置数据源
 *
 * @author <a href="mailto:jzaofox@foxmai.com">AmVilCresx</a>
 */
public class ResetDataSourceHandlerInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 重置数据源，防止线程复用带来的影响
        DynamicDataSourceContextHolder.reset();
        return true;
    }
}
