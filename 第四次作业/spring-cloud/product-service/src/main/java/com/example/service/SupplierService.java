package com.example.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.Supplier;
import com.example.mapper.SupplierMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SupplierService extends ServiceImpl<SupplierMapper, Supplier> {
    @Autowired
    SupplierMapper supplierMapper;

    public Supplier getSupplierByName(String supplierName) {
        return supplierMapper.getSupplierByName(supplierName);
    }
}
