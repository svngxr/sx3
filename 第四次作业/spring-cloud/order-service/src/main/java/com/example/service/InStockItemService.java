package com.example.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.InStockItem;
import com.example.mapper.InStockItemMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InStockItemService extends ServiceImpl<InStockItemMapper, InStockItem> {
    @Autowired
    private InStockItemMapper inStockItemMapper;

    public List<InStockItem> getByInStockId(int inStockId) {
        return inStockItemMapper.getByInStockId(inStockId);
    }

}
