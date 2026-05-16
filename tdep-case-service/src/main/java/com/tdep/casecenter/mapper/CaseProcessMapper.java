package com.tdep.casecenter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tdep.casecenter.entity.CaseProcess;
import org.apache.ibatis.annotations.Mapper;

/**
 * 案件流程记录 Mapper。
 */
@Mapper
public interface CaseProcessMapper extends BaseMapper<CaseProcess> {
}
