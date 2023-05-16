<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item label="商品编号" prop="productId" >
        <el-input
          v-model="queryParams.productId"
          placeholder="请输入商品编号"
          clearable
          :disabled="querySingle"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>

      <el-form-item label="供货商编号" prop="supplierId" >
        <el-input
            v-model="queryParams.supplierId"
            placeholder="请输入供货商编号"
            clearable
            :disabled="queryMutex"
            @keyup.enter.native="handleQuery"
        />
      </el-form-item>

      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"

        >删除</el-button>
      </el-col>

    </el-row>

    <el-table v-loading="loading" :data="postList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="商品编号" align="center" prop="productId" />
      <el-table-column label="商品名称" align="center" prop="productName"/>
      <el-table-column label="供货商编号" align="center" prop="supplierId"/>
      <el-table-column label="价格" align="center" prop="price"/>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"

          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"

          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 添加或修改产品对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="myForm" :rules="rules" label-width="80px">
        <el-form-item label="产品名称" prop="productName">
          <el-input v-model="myForm.productName" placeholder="请输入产品名称"/>
        </el-form-item>
        <el-form-item label="供货商编号" prop="supplierId">
          <el-input v-model="myForm.supplierId" placeholder="请输入供货商编号"/>
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input v-model="myForm.price" placeholder="请输入价格"/>
        </el-form-item>

      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm(myForm)">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>
import axios from "axios";
axios.defaults.headers.post['Content-Type']='application/json;charset=UTF-8';


export default {
  name: "Product",
  dicts: ['sys_normal_disable'],
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 入库单表格数据
      postList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 是否弹出详情层
      detail: false,
      // 查询互斥单量
      querySingle: false,
      // 查询互斥多量
      queryMutex: false,
      // 查询参数
      queryParams: {
        productId: undefined,
        supplierId: undefined
      },
      // 表单参数
      myForm: {},
      // 表单校验
      rules: {
        productName: [
          { required: true, message: "产品名称不能为空", trigger: "blur" }
        ],
        supplierId: [
          { required: true, message: "供货商不能为空", trigger: "blur" },
        ],
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询岗位列表 */
    getList() {
      this.loading = true;
      axios.get("http://localhost:8181/products/").then((response)=>{
        console.log('我tm来啦');
        if(Array.isArray(response.data)){
          this.postList = response.data;
        }else {
          this.postList = [];
          this.postList.push(response.data);
        }
      });
      this.loading = false;

    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.detail = false;

      this.reset();
      this.getList();
    },
    // 表单重置
    reset() {
      this.myForm = {
        productName: undefined,
        supplierId: undefined,
        price:undefined
      };
      this.resetForm();
    },
    resetForm(){
      this.queryParams = {
        productId: undefined,
        type: undefined
      };
    },
    /** 搜索按钮操作 */
    handleQuery() {
      if (this.queryParams.productId){
        axios.get("http://localhost:8181/products/"+this.queryParams.productId).then((response)=>{
          console.log(response);
          this.loading = true;
          // 判断是否为空
          if(response.data===""){
            this.postList = [];
          }else {
            console.log(response);
            this.postList = [];
            this.postList.push(response.data);
          }

          this.loading = false;
        });
      }
      else if(this.queryParams.supplierId){
        axios.get("http://localhost:8181/products/supplier/"+this.queryParams.supplierId)
            .then((response)=>{
              this.loading = true;
              // 判断是不是列表
              if(Array.isArray(response.data)){
                this.postList = response.data;
              }else {
                this.postList = [];
                this.postList.push(response.data);

              }
              this.loading = false;
            });
      }
      else {
        this.getList();
      }


    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm();
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.productId)
      this.single = selection.length!=1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加商品";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      axios.get("http://localhost:8181/products/"+row.productId).then((response)=>{
          console.log(response);
          this.myForm = response.data;
          this.open = true;
          this.title = "修改商品";


        });



    },
    /** 提交按钮 */
    submitForm: function(product) {
      console.log(product);
      if (this.myForm.productId != undefined){
        // 更新
        axios.put('/products/', product).then((response)=>{
          let flag = response.data;
          if(flag){
            this.$modal.msgSuccess("更新成功!");
          }else {
            this.$modal.msgError("更新失败!");
          }

          this.cancel();
          this.getList();
        });
      }else {
        // 添加
        axios.post('/products/', product).then((response)=>{
          let flag = response.data;
          if(flag){
            this.$modal.msgSuccess("添加成功!");
          }else {
            this.$modal.msgError("添加失败!");
          }

          this.cancel();
          this.getList();
        });
      }

    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const productIds = row.productId || this.ids;
      this.$modal.confirm('是否确认删除商品编号为"' + productIds + '"的数据项？').then(function() {

      }).then(() => {
        axios.delete('/products/'+productIds).then((response)=>{
          let flag = response.data;
          if(flag){
            this.$modal.msgSuccess("删除成功!");
          }else {
            this.$modal.msgError("删除失败!");
          }

          this.getList();
        });



      }).catch(() => {});
    }
  },
  watch:{
    'queryParams.productId':{
      handler(newValue){
        if(newValue){
          this.queryMutex = true;
        }else {
          this.queryMutex = false;
        }
      }

},
    'queryParams.supplierId':{
      handler(newValue){
        if(newValue){
          this.querySingle = true;
        }else {
          this.querySingle = false;
        }
      }

    },

  }
};
</script>
