package pers.avc.simple.shard.sample;

import cn.hutool.json.JSONUtil;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import pers.avc.simple.shard.configure.annotation.EnableSimpleSharding;
import pers.avc.simple.shard.sample.mapper.ArtUserImproveMapper;
import pers.avc.simple.shard.sample.model.ArtUserImprove;

/**
 * 类功能描述
 *
 * @author <a href="mailto:jzaofox@foxmail.com">AmVilCresx</a>
 */
@MapperScan(basePackages = {"pers.avc.simple.shard.sample"})
@SpringBootApplication
@EnableSimpleSharding
public class SampleApplication {

    public static void main(String[] args) {
        SpringApplicationBuilder applicationBuilder = new SpringApplicationBuilder(SampleApplication.class);
        ConfigurableApplicationContext applicationContext = applicationBuilder.web(WebApplicationType.SERVLET)
                .run(args);
        ArtUserImproveMapper userImproveMapper = applicationContext.getBean(ArtUserImproveMapper.class);
        ArtUserImprove userImprove = userImproveMapper.selectById(1L);
        System.out.println(JSONUtil.toJsonStr(userImprove));
    }
}
