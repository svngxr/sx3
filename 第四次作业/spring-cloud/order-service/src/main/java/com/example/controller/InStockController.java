package com.example.controller;

import com.example.entity.InStock;
import com.example.entity.InStockItem;
import com.example.service.InStockItemService;
import com.example.service.InStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class InStockController {
    @Autowired
    private InStockService inStockService;
    @Autowired
    private InStockItemService inStockItemService;

    //通过ID获取入库单
    @GetMapping("/instocks/{inStockId}")
    public InStock getInStockById(@PathVariable int inStockId) {
        return inStockService.selectDetailsById(inStockId);
    }

    //通过类型获取入库单
    @GetMapping("/instocks/category/{inStockCategory}")
    public List<InStock> getInStockByCategory(@PathVariable String inStockCategory) {
        return inStockService.getInStockByCategory(inStockCategory);
    }

    //通过状态获取入库单
    @GetMapping("/instocks/status/{inStockStatus}")
    public List<InStock> getInStockByStatus(@PathVariable String inStockStatus) {
        return inStockService.getInStockByStatus(inStockStatus);
    }

    //通过类型+状态获取入库单
    @GetMapping("/instocks/category/{inStockCategory}/{inStockStatus}")
    public List<InStock> getInStockByCategoryAndStatus(@PathVariable String inStockCategory,
                                                       @PathVariable String inStockStatus) {
        return inStockService.getInStockByCategoryAndStatus(inStockCategory, inStockStatus);
    }

    //获取所有入库单
    @GetMapping("/instocks/")
    public List<InStock> getAllInStocks() {
        return inStockService.selectDetails();
    }

    //添加入库单
    @PostMapping("/instocks/")
    public boolean addInStock(@RequestBody InStock inStock) throws InterruptedException {
        System.out.println("qweqweqweqweqweqwe");
        System.out.println(inStock.getUserId());


        Boolean flag1 = inStockService.save(inStock);
        List<InStock> inStockList = inStockService.selectDetails();
        int inStockId = 0;
        for (InStock inS : inStockList
        ) {
            if (inStockId < inS.getInStockId()) {
                inStockId = inS.getInStockId();
            }

        }

        // 保存inStockId
        for (InStockItem inStockItem : inStock.getInStockItems()
        ) {
            inStockItem.setInStockId(inStockId);
            Boolean flag2 = inStockItemService.save(inStockItem);
            if (!flag2) {
                return flag2;
            }
        }

        // 更新库存
        inStockService.updateStockByInStockId(inStockId);
        return flag1;
    }

    //更新入库单
    @PutMapping("/instocks/")
    public boolean updateInStock(@RequestBody InStock inStock) {
        return inStockService.updateById(inStock);
    }

    //删除入库单
    @DeleteMapping("/instocks/{inStockId}")
    public boolean deleteInStock(@PathVariable int inStockId) {
        return inStockService.removeById(inStockId);
    }

    //通过入库单更新库存
    @PostMapping("/instocks/{inStockId}/updateStock")
    public boolean updateStockByInStockId(@PathVariable int inStockId) {
        return inStockService.updateStockByInStockId(inStockId);
    }

    //通过ID获取入库单项目
    @GetMapping("/instockitems/{inStockItemId}")
    public InStockItem getInStockItemById(@PathVariable int inStockItemId) {
        return inStockItemService.getById(inStockItemId);
    }

    //通过入库单ID获取入库单项目
    @GetMapping("/instockitems/instock/{inStockId}")
    public List<InStockItem> getInStockItemByInStockId(@PathVariable int inStockId) {
        System.out.println("qeqeqweqweqweqweqweq");
        System.out.println(inStockId);
        return inStockItemService.getByInStockId(inStockId);
    }

    //获取所有入库单项目
    @GetMapping("/instockitems/")
    public List<InStockItem> getAllInStockItems() {
        return inStockItemService.list();
    }

    //添加入库单项目
    @PostMapping("/instockitems/")
    public boolean addInStockItem(@RequestBody InStockItem inStockItem) {
        return inStockItemService.save(inStockItem);
    }

    //更新入库单项目
    @PutMapping("/instockitems/")
    public boolean updateInStockItem(@RequestBody InStockItem inStockItem) {

        return inStockItemService.updateById(inStockItem);
    }

    //删除入库单项目
    @DeleteMapping("/instockitems/{inStockItemId}")
    public boolean deleteInStockItem(@PathVariable int inStockItemId) {
        return inStockItemService.removeById(inStockItemId);
    }

}
