package com.example.controller;

import com.example.entity.Product;
import com.example.entity.Stock;
import com.example.entity.Supplier;
import com.example.service.ProductService;
import com.example.service.StockService;
import com.example.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ProductController {
    @Autowired
    private ProductService productService;
    @Autowired
    private StockService stockService;
    @Autowired
    private SupplierService supplierService;

    //通过ID获取产品
    @GetMapping("/products/{productId}")
    public Product getProductById(@PathVariable int productId) {
        return productService.selectDetailsById(productId);
    }

    //通过供货商id获取产品
    @GetMapping("/products/supplier/{supplierId}")
    public List<Product> getProductBySupplierId(@PathVariable int supplierId) {
        return productService.getProductBySupplierId(supplierId);
    }


    //获取所有产品
    @GetMapping("/products/")
    public List<Product> getAllProducts() {
        return productService.selectDetails();
    }

    //分页获取产品
    @GetMapping("/products/page")
    public Map<String, Object> getProductList(@RequestParam(name = "page", defaultValue = "1") int pageNum,
                                              @RequestParam(name = "size", defaultValue = "10") int pageSize) {
        pageNum = (pageNum - 1) * pageSize;
        Map<String, Object> result = new HashMap<>();
        int total = productService.getCount();
        List<Product> products = productService.selectDetailsPage(pageNum, pageSize);
        result.put("total", total);
        result.put("products", products);
        return result;
    }

    //添加产品
    @PostMapping("/products/")
    public boolean addProduct(@RequestBody Product product) {
        return productService.save(product);
    }

    //更新产品
    @PutMapping("/products/")
    public boolean updateProduct(@RequestBody Product product) {
        return productService.updateById(product);
    }

    //删除产品
    @DeleteMapping("/products/{productId}")
    public boolean deleteProductById(@PathVariable int productId) {
        return productService.removeById(productId);
    }

    //通过ID获取库存
    @GetMapping("/stocks/{stockId}")
    public Stock getStockByProductId(@PathVariable int stockId) {
        return stockService.getById(stockId);
    }

    //通过区域获取库存
    @GetMapping("/stocks/position/{position}")
    public List<Stock> getStockByPosition(@PathVariable String position) {
        System.out.println("qweqweqweqwe");
        return stockService.getStockByPosition(position);
    }

    //获取所有库存
    @GetMapping("/stocks/")
    public List<Stock> getAllStocks() {
        return stockService.list();
    }

    //添加库存
    @PostMapping("/stocks/")
    public boolean addStock(@RequestBody Stock stock) {

        return stockService.save(stock);
    }

    //更新库存
    @PutMapping("/stocks/")
    public boolean updateStock(@RequestBody Stock stock) {

        return stockService.updateById(stock);
    }

    //删除库存
    @DeleteMapping("/stocks/{stockId}")
    public boolean deleteStock(@PathVariable int stockId) {
        return stockService.removeById(stockId);
    }

    //通过ID获取供应商
    @GetMapping("/suppliers/{supplierId}")
    public Supplier getSupplierById(@PathVariable int supplierId) {
        return supplierService.getById(supplierId);
    }


    //通过名字获取供应商
    @GetMapping("/suppliersName/{supplierName}")
    public Supplier getSupplierByName(@PathVariable String supplierName) {
        return supplierService.getSupplierByName(supplierName);
    }

    //获取所有供应商
    @GetMapping("/suppliers/")
    public List<Supplier> getAllSuppliers() {
        return supplierService.list();
    }

    //添加供应商
    @PostMapping("/suppliers/")
    public boolean addSupplier(@RequestBody Supplier supplier) {
        return supplierService.save(supplier);
    }

    //更新供应商
    @PutMapping("/suppliers/")
    public boolean updateSupplier(@RequestBody Supplier supplier) {
        return supplierService.updateById(supplier);
    }

    //删除供应商
    @DeleteMapping("/suppliers/{supplierId}")
    public boolean deleteSupplier(@PathVariable int supplierId) {
        return supplierService.removeById(supplierId);
    }

}
