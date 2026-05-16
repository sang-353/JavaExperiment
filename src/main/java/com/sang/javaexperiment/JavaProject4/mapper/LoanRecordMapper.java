package com.sang.javaexperiment.JavaProject4.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sang.javaexperiment.JavaProject4.entity.LoanRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author xie ying
 * AI coding by GitHub Copilot GPT-5.2
 * 贷款记录数据访问层（DAO）
 * 继承 BaseMapper<LoanRecord> 以获得通用的增删改查功能
 */
@Mapper
public interface LoanRecordMapper extends BaseMapper<LoanRecord> {
}

