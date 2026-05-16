package com.tdep.casecenter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tdep.casecenter.entity.CaseInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 案件主表 Mapper。
 */
@Mapper
public interface CaseInfoMapper extends BaseMapper<CaseInfo> {
}
