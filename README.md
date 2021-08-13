# simple-sharding-starter
>  结合spring boot实现多**MySQL**数据源【理论上无个数限制】动态切换，利用Mybatis实现轻量级分表。依赖于Spring环境，可运行于`非Reactive` 环境。

## 0. 环境版本说明

> * JDK 版本： `1.8`
> * Spring boot 版本：`2.4.1`
> * Mybatis 版本：`3.5.6`

## 1. 主要特性

> * 支持自定义数据源路由规则
> * 支持自定义分表策略
> * 提供注解使用方式，实现灵活快捷开发

## 2. 使用方式

* **打包后引入Maven依赖**

```xml
<dependency>
    <groupId>pers.avc.simple.shard</groupId>
    <artifactId>simple-sharding-spring-boot-starter</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

- **激活自动装配**

  `@EnableSimpleSharding`

  ![image-20210813152820628](https://i.loli.net/2021/08/13/RKdeMPSmXr3gnsf.png)

  > 注意： **如果没有激活自动装配，则需要使用 `spring.datasource.xxx` 的配置方式配置数据源，如果激活了，则只需要根据下面的方式配置默认数据源即可，无需再做额外配置**。

- **配置默认数据源**

  ```properties
  default.datasource.url=jdbc:mysql://localhost:3306/improve?useSSL=false&useUnicode=true&characterEncoding=utf8&allowMultiQueries=true&autoReconnect=true&serverTimezone=Asia/Shanghai
  default.datasource.username=root
  default.datasource.password=xxxx
  ```

- **激活自动分表**(非必须)

  `mybatis.table.shard.enable=true` 默认是`false`

  > 如果开启分表开关，则会在执行 SQL 时拦截目标表名，按照分表策略对原始表名进行替换，如果要实现自定义分表策略，需要实现 `ITableShardStrategy`接口。
  >
  > ![image-20210813153742583](https://i.loli.net/2021/08/13/fUtwHb6SiJIQqZn.png)

* 切库

  * 手动切库:  `DynamicDataSourceContextHolder.set(routerValue)`.

    > `routerValue ` 是在构建数据源时，每个数据源的唯一标识，一般为全局性的唯一ID。

  * 使用注解： `@TargetDataSource` 结合 `@CutoverDBParam`

    > 1. 如果 TargetDataSource 的`toDefault` 属性为 `true`, 表示切换到默认数据库。
    >
    > 2. 在`toDefault` 为 `false` 的情况下，在切面会解析目标方法的参数，**只会** 解析**第一个** 标注了 `@CutoverDBParam`的参数，此时：
    >
    >    * 如果 `@CutoverDBParam` 的 `fieldName`属性为空，则该参数本身必须是**基本类型（或包装类型）或者是String**，否则或抛异常，
    >
    >    * 如果`field` 字段 不为空，则`fieldName` 对应的属性类型必须是**基本类型（或包装类型）或者是String**。
    >
    >    *  如果没有标注 `@CutoverDBParam`的参数， 则默认取第一个参数作为路由key，此时，第一个参数必须是**基本类型（或包装类型）或者是String**。
    >
    >      ![image-20210813170655970](https://i.loli.net/2021/08/13/7gIyPOfUXNHouxd.png)

    > **PS： 注意切换不生效的场景，如目标方法内部调用，没有使用代理对象，此时不会触发AOP**

## 3. 核心接口/类说明

| 核心接口                         | 说明                                                         | 栗子🌰                                 |
| -------------------------------- | ------------------------------------------------------------ | ------------------------------------- |
| `DataSourceRoutingRuler`         | 用于自定义数据源切换规则                                     | `IdModDataSourceRoutingRuler`         |
| `LoadDataSourceMetaPropFactory`  | 用于从特定的地方加载数据源信息                               | `FromDBLoadDataSourceMetaPropFactory` |
| `DataSourceStorage`              | 用于存储数据源的信息                                         | `InMemoryDataSourceStorage`           |
| `DynamicRoutingDataSource`       | 继承了`AbstractRoutingDataSource`类，实现动态数据源的切换功能 |                                       |
| `DynamicDataSourceCutoverAspect` | 切面，用于拦截标注了`TargetDataSource`注解的方法，实现数据源的切换 |                                       |
| `ITableShardStrategy`            | 用于自定义分表策略                                           | `DayTableShardStrategy`               |
| `TableShardInterceptor`          | 实现了mybatis 拦截器，用于拦截并根据分表策略改写目标SQL      |                                       |

## 4. 其他

* 参考链接：[mybatis-sharding](https://github.com/bytearch/mybatis-sharding/blob/master/src/main/java/com/bytearch/mybatis/sharding/plugin/ShardingInterceptor.java)