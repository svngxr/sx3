package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.OutStockItem;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OutStockItemMapper extends BaseMapper<OutStockItem> {
    List<OutStockItem> getByOutStockId(int outStockId);
}
