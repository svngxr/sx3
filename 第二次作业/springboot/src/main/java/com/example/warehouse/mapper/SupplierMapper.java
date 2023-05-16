package com.example.warehouse.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.warehouse.entity.Supplier;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SupplierMapper extends BaseMapper<Supplier> {
    Supplier getSupplierByName(String supplierName);
}
