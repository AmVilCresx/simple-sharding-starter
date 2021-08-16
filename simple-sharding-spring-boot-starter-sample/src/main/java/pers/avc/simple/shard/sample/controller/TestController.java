package pers.avc.simple.shard.sample.controller;

import cn.hutool.json.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.avc.simple.shard.configure.datasource.DynamicDataSourceOperator;
import pers.avc.simple.shard.configure.datasource.annotation.CutoverDBParam;
import pers.avc.simple.shard.configure.datasource.annotation.TargetDataSource;
import pers.avc.simple.shard.configure.datasource.meta.DataSourceMetaProp;
import pers.avc.simple.shard.sample.mapper.ArtUserImproveMapper;
import pers.avc.simple.shard.sample.model.ArtUserImprove;

import javax.sql.DataSource;
import java.util.Objects;

/**
 * 测试 Controller
 *
 * @author <a href="mailto:jzaofox@foxmail.com">AmVilCresx</a>
 */
@RestController
public class TestController {

    @Autowired
    ArtUserImproveMapper artUserImproveMapper;

    @Autowired
    private DynamicDataSourceOperator dynamicDataSourceOperator;

    /**
     * 动态数据源测试
     * @return String
     */
    @GetMapping("/testDynamic")
    @TargetDataSource
    public String testDynamic(@CutoverDBParam String id, String flag) {
        //   DataSourceContextHolder.set(1+""); // 手动切数据源
        if (Objects.equals("select", flag)) {
            ArtUserImprove userImprove = artUserImproveMapper.selectById(1L);
            return JSONUtil.toJsonStr(userImprove);
        }

        if (Objects.equals("deleteAndInsert", flag)) {
            ArtUserImprove userImprove = artUserImproveMapper.selectById(1L);
            artUserImproveMapper.deleteById(1L);
            artUserImproveMapper.insert(userImprove);
        }

        if (Objects.equals("update", flag)) {
            ArtUserImprove userImprove = new ArtUserImprove();
            userImprove.setId(1L);
            userImprove.setFirstName("啊啊啊啊");
            artUserImproveMapper.updateById(userImprove);
        }
        return "success";
    }

    @GetMapping("/dataSourceOperationTest")
    public String dataSourceOperationTest() {
        DataSourceMetaProp sourceMetaProp = new DataSourceMetaProp();
        sourceMetaProp.setUnionKey("UK");
        sourceMetaProp.setDbHost("127.0.0.1");
        sourceMetaProp.setDbPort(3306);
        sourceMetaProp.setDbName("mysql");
        sourceMetaProp.setDbUsername("root");
        sourceMetaProp.setDbPassword("authJzao666");

        System.out.println("开始添加数据源.....");
        dynamicDataSourceOperator.addOne(sourceMetaProp);

        System.out.println("即将移除数据源：baseRouter=" +sourceMetaProp.getUnionKey());
        DataSourceMetaProp oldDs = dynamicDataSourceOperator.remove(sourceMetaProp.getUnionKey());
        System.out.println(oldDs);
        return "success";
    }
}
