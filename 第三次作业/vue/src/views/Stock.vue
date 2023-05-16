<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item label="货架编号" prop="stockId" >
        <el-input
          v-model="queryParams.stockId"
          placeholder="请输入货架编号"
          clearable
          :disabled="querySingle"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="区域" prop="position" >
        <el-input
            v-model="queryParams.position"
            placeholder="请输入区域编号"
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
      <el-table-column label="货架编号" align="center" prop="stockId" />
      <el-table-column label="产品编号" align="center" prop="productId"/>
      <el-table-column label="数量" align="center" prop="stockQuantity"/>
      <el-table-column label="区域" align="center" prop="position"/>
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


    <!-- 添加或修改货架对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="myForm" :rules="rules" label-width="100px">
        <el-form-item label="货架编号" prop="stockId">
          <el-input v-model="myForm.stockId" controls-position="right" :min="0"  :disabled="isDisabled"/>
        </el-form-item>
        <el-form-item label="产品编号" prop="productId">
          <el-input v-model="myForm.productId" controls-position="right" :min="0" />
        </el-form-item>
        <el-form-item label="产品数量" prop="stockQuantity">
          <el-input v-model="myForm.stockQuantity" controls-position="right" :min="0" />
        </el-form-item>
        <el-form-item label="所在区域" prop="position">
          <el-input v-model="myForm.position" controls-position="right" :min="0" />
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
  name: "Stock",
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
      // 入库单项目数据
      itemList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 是否弹出详情层
      detail: false,
      // 是否显示货架编号
      isDisabled: true,
      // 查询互斥单量
      querySingle: false,
      // 查询互斥多量
      queryMutex: false,
      // 查询参数
      queryParams: {
        stockId: undefined,
        inStockCategory: undefined,
        inStockStatus: undefined
      },
      // 表单参数
      myForm: {},
      // 表单校验
      rules: {
        productId: [
          { required: true, message: "产品编号不能为空", trigger: "blur" }
        ],
        stockQuantity: [
          { required: true, message: "存储数量不能为空", trigger: "blur" }
        ],
        position: [
          { required: true, message: "存储区域不能为空", trigger: "blur" }
        ]
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
      axios.get("http://localhost:8181/stocks/").then((response)=>{
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
        stockId: undefined,
        productId: undefined,
        stockQuantity: "",
        position:""
      };
      this.resetForm();
    },
    resetForm(){
      this.queryParams = {
        stockId: undefined,
        position: undefined,

      };
    },
    /** 搜索按钮操作 */
    handleQuery() {
      if (this.queryParams.stockId){
        axios.get("http://localhost:8181/stocks/"+this.queryParams.stockId).then((response)=>{
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
      else if(this.queryParams.position){
        axios.get("http://localhost:8181/stocks/position/"+this.queryParams.position)
            .then((response)=>{
              console.log(response.data);
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
      this.ids = selection.map(item => item.stockId)
      this.single = selection.length!=1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加货架";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      axios.get("http://localhost:8181/stocks/"+row.stockId).then((response)=>{
        console.log(response);
        this.myForm = response.data;
        this.open = true;
        this.title = "修改货架";


      });



    },
    /** 提交按钮 */
    submitForm: function(stock) {
      console.log(stock);
      if (this.myForm.stockId != undefined){
        // 更新
        axios.put('/stocks/', stock).then((response)=>{
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
        axios.post('/stocks/', stock).then((response)=>{
          let flag = response.data;
          if(flag){
            this.$modal.msgSuccess("添加成功!");
          }else {
            this.$modal.msgError("添加失败!");
          }

          this.cancel();
          this.getList();
        }).catch((err) => {
          this.$modal.msgError("添加失败!");
          this.cancel();
          this.getList();
        });
      }

    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const stockIds = row.stockId || this.ids;
      this.$modal.confirm('是否确认删除入库单编号为"' + stockIds + '"的数据项？').then(function() {

      }).then(() => {
        axios.delete('/stocks/'+stockIds).then((response)=>{
          let flag = response.data;
          if(flag){
            this.$modal.msgSuccess("删除成功!");
          }else {
            this.$modal.msgError("删除失败!");
          }

          this.getList();
        });



      }).catch(() => {});
    },
  },
  watch:{
    'queryParams.stockId':{
      handler(newValue){
        if(newValue){
          this.queryMutex = true;
        }else {
          this.queryMutex = false;
        }
      }

},
    'queryParams.position':{
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
