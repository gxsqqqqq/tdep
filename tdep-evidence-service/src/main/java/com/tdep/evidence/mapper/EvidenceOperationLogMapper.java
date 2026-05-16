package com.tdep.evidence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tdep.evidence.entity.EvidenceOperationLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 电子证据操作日志 Mapper。
 */
@Mapper
public interface EvidenceOperationLogMapper extends BaseMapper<EvidenceOperationLog> {
}
