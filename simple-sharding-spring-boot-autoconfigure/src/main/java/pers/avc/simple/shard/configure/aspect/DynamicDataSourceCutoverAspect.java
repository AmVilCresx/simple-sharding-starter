package pers.avc.simple.shard.configure.aspect;

import cn.hutool.core.util.ArrayUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import pers.avc.simple.shard.configure.datasource.DynamicDataSourceContextHolder;
import pers.avc.simple.shard.configure.datasource.annotation.CutoverDBParam;
import pers.avc.simple.shard.configure.datasource.annotation.TargetDataSource;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * 动态数据源切换 切面
 *
 * @author <a href="mailto:jzaofox@foxmai.com">AmVilCresx</a>
 * @see TargetDataSource
 */
@Aspect
public class DynamicDataSourceCutoverAspect {

    /**
     * 切入点， 拦截 {@link TargetDataSource} 注解
     */
    @Pointcut("@annotation(pers.avc.simple.shard.configure.datasource.annotation.TargetDataSource)")
    public void pointCut() {
    }

    @Around("pointCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Object[] args = joinPoint.getArgs();
        TargetDataSource targetDataSource = method.getAnnotation(TargetDataSource.class);
        if (targetDataSource.toDefault()) {
            DynamicDataSourceContextHolder.reset();
            return joinPoint.proceed();
        }

        if (ArrayUtil.isEmpty(args)) {
            throw new IllegalArgumentException("切库方法在非默认库情况下，参数不可为空");
        }

        String target = null;
        Annotation[][] annotations = method.getParameterAnnotations();
        for (int i = 0; i < annotations.length; i++) {
            // 只解析第一个标注了 @CutoverDBParam 注解的参数
            if (StringUtils.hasText(target)) {
                break;
            }
            for (Annotation annotation : annotations[i]) {
                if (!(annotation instanceof CutoverDBParam)) {
                    continue;
                }

                String fieldName = ((CutoverDBParam) annotation).fieldName();
                if (StringUtils.hasText(fieldName)) {
                    Class<?> clazz = args[i].getClass();
                    Field decField = clazz.getDeclaredField(fieldName);
                    if (checkFieldClass(decField.getType())) {
                        // 如果不是 基本（包装）类型 或者 CharSequence
                        throw new IllegalArgumentException("@CutoverDBParam fieldName 不能是非 CharSequence 体系的 对象类型");
                    }
                    boolean isAccess = decField.isAccessible();
                    if (!isAccess) {
                        decField.setAccessible(true);
                    }
                    Object value = decField.get(args[i]);
                    // 恢复原访问性
                    decField.setAccessible(isAccess);
                    // 不可为空
                    if (ObjectUtils.isEmpty(value)) {
                        throw new IllegalArgumentException("@CutoverDBParam fieldName 对应的值不可为空");
                    }
                    target = String.valueOf(value);
                    // 只取第一个标注了 @CutoverDBParam 注解参数的 第一个字段
                    break;
                } else {
                    if (checkFieldClass(args[i].getClass())) {
                        // 如果不是 基本（包装）类型 或者 CharSequence
                        throw new IllegalArgumentException("@CutoverDBParam fieldName 不能是非 CharSequence 体系的 对象类型");
                    }
                    target = String.valueOf(args[i]);
                }
            }
        }

        if (!StringUtils.hasText(target)) {
            // 说明没有参数标注 @CutoverDBParam 注解， 此时去第一个参数
            Object firstParam = args[0];
            if (Objects.isNull(firstParam) || checkFieldClass(firstParam.getClass())) {
                // 如果不是 基本（包装）类型 或者 CharSequence
                throw new IllegalArgumentException("@CutoverDBParam fieldName 不能是非 CharSequence 体系的 对象类型");
            }
            target = String.valueOf(firstParam);
        }

        DynamicDataSourceContextHolder.set(target);
        return joinPoint.proceed();
    }

    /**
     * 基本（包装）类型 或者 CharSequence
     *
     * @param clazz 待check 的 Class 对象
     * @return Boolean
     */
    private boolean checkFieldClass(Class<?> clazz) {
        return !ClassUtils.isPrimitiveOrWrapper(clazz) && !CharSequence.class.isAssignableFrom(clazz);
    }
}
