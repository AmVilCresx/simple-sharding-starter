package pers.avc.simple.shard.sample.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import pers.avc.simple.shard.configure.mybatis.annotation.TableShard;
import pers.avc.simple.shard.configure.mybatis.interceptor.strategy.DayTableShardStrategy;
import pers.avc.simple.shard.sample.model.ArtUserImprove;

/**
 * 功能描述
 *
 * @author <a href="mailto:jzaofox@foxmai.com">AmVilCresx</a>
 */
@Repository
@TableShard(tableNames = {"art_user_improve"}, strategy = DayTableShardStrategy.class)
public interface ArtUserImproveMapper extends BaseMapper<ArtUserImprove> {

    ArtUserImprove selectById(@Param("id") Long id);

    void deleteById(@Param("id") Long id);
}
