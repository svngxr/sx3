package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.InStockItem;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface InStockItemMapper extends BaseMapper<InStockItem> {
    List<InStockItem> getByInStockId(int inStockId);
}
