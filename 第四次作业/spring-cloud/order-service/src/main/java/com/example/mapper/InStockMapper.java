package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.InStock;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface InStockMapper extends BaseMapper<InStock> {
    InStock selectDetailsById(int inStockId);

    List<InStock> selectDetails();

    List<InStock> getInStockByCategory(String inStockCategory);

    List<InStock> getInStockByCategoryAndStatus(String inStockCategory, String inStockStatus);

    List<InStock> getInStockByStatus(String inStockStatus);

}
