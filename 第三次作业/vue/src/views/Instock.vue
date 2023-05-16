<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item label="入库单编号" prop="inStockId" >
        <el-input
          v-model="queryParams.inStockId"
          placeholder="请输入入库单编号"
          clearable
          :disabled="querySingle"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="入库单类型" prop="inStockCategory">
        <el-select v-model="queryParams.inStockCategory" placeholder="入库单类型" clearable :disabled="queryMutex">
          <el-option
              key="1"
              label="个人入库"
              value="purchase"
          />
          <el-option
              key="2"
              label="公司入库"
              value="production"
          />
          <el-option
              key="3"
              label="其他入库"
              value="other"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="inStockStatus">
        <el-select v-model="queryParams.inStockStatus" placeholder="入库单状态" clearable :disabled="queryMutex">
          <el-option
            key="1"
            label="进行中"
            value="uncompleted"
          />
          <el-option
              key="2"
              label="已完成"
              value="completed"
          />
          <el-option
              key="3"
              label="已取消"
              value="cancelled"
          />
        </el-select>
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

        >取消</el-button>
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
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
        >导出</el-button>
      </el-col>
    </el-row>

    <el-table v-loading="loading" :data="postList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="入库单编号" align="center" prop="inStockId" />
      <el-table-column label="入库单类型" align="center" prop="inStockCategory">
        <template slot-scope="scope">
          {{scope.row.inStockCategory | categoryFormater}}
        </template>
      </el-table-column>
      <el-table-column label="创建者编号" align="center" prop="userId"/>
      <el-table-column label="状态" align="center" prop="inStockStatus">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.inStockStatus==='completed'" type="success">
            已完成
          </el-tag>
          <el-tag v-else-if="scope.row.inStockStatus==='uncompleted'" type="warning">
            进行中
          </el-tag>
          <el-tag v-else type="info">
            已取消
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center"  width="180">
        <template slot-scope="scope">
          {{scope.row.inStockTime | timeFormater}}
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
              size="mini"
              type="text"
              icon="el-icon-more"
              @click="handleDetail(scope.row)"

          >详情</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"

          >取消</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"

          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 添加或修改入库单对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="800px" append-to-body>
      <el-form ref="myForm" :model="myForm" label-width="100px">
        <el-form-item label="入库单类型" prop="inStockCategory">
          <el-select v-model="myForm.inStockCategory" placeholder="入库单类型" clearable >
            <el-option
                label="个人入库"
                value="purchase"
            />
            <el-option
                label="公司入库"
                value="production"
            />
            <el-option
                label="其他入库"
                value="other"
            />
          </el-select>
        </el-form-item>
        <el-form-item
            v-for="(product,index) in myForm.inStockItems"
            :label="'商品'+index"
            :key="product.productId"
            >
          <el-row :gutter="24">
            <el-col :span="7"><el-select v-model="product.productId" placeholder="商品名称" clearable :disabled="queryMutex">
              <el-option
                  v-for="item in products"
                  :key="item.productId"
                  :label="item.productName"
                  :value="item.productId"
              ></el-option>
            </el-select></el-col>
            <el-col :span="7"><el-input v-model="product.inStockQuantity" placeholder="数量" ></el-input></el-col>
            <el-col :span="8"><el-button @click.prevent="removeProduct(product)">删除</el-button></el-col>
          </el-row>
        </el-form-item>

      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm(myForm)">确 定</el-button>
        <el-button @click="addProduct">新增商品</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog title="表单详情" :visible.sync="detail" width="1000px" append-to-body>
      <el-table v-loading="loading" :data="itemList" height="400px">

        <el-table-column label="单项编号" align="center" prop="inStockItemId" />
        <el-table-column label="入库单编号" align="center" prop="inStockId" />
        <el-table-column label="产品编号" align="center" prop="productId"/>
        <el-table-column label="数量" align="center" prop="inStockQuantity"/>
        <el-table-column label="单价" align="center" prop="inStockPrice"/>



      </el-table>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>
import axios from "axios";
axios.defaults.headers.post['Content-Type']='application/json;charset=UTF-8';
import dayjs from "dayjs";

export default {
  name: "Instock",
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
      // 商品数据
      products:[],
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
        inStockId: undefined,
        inStockCategory: undefined,
        inStockStatus: undefined
      },
      // 表单参数
      myForm: {
        userId:"",
        inStockStatus:"uncompleted",
        inStockCategory:"",

        inStockItems:[{
          inStockId:"",
          productName:"",
          inStockQuantity:"",
          inStockPrice:""

        }]
      },

    };
  },
  computed:{
    querySelect(){
      let value1 = this.queryParams.inStockCategory;
      let value2 = this.queryParams.inStockStatus;
      return{value1,value2};
    }
  },
  created() {
    this.getList();
    this.getProducts();
  },
  methods: {
    /** 查询入库单列表 */
    getList() {
      this.loading = true;
      axios.get("http://localhost:8181/instocks/").then((response)=>{
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
      this.myForm ={
        userId:"",
        inStockStatus:"uncompleted",
        inStockCategory:"",

        inStockItems:[{
          inStockId:"",
          productName:"",
          inStockQuantity:"",
          inStockPrice:""

        }]
      };
      this.itemList = [];
      this.resetForm("form");
    },
    resetForm(form){
      this.form = form;
      this.queryParams = {
        inStockId: undefined,
        inStockCategory: undefined,
        inStockStatus: undefined
      };
    },
    /** 搜索按钮操作 */
    handleQuery() {
      if (this.queryParams.inStockId){
        axios.get("http://localhost:8181/instocks/"+this.queryParams.inStockId).then((response)=>{
          this.loading = true;
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
      else if(this.queryParams.inStockCategory){
        if(this.queryParams.inStockStatus){
          axios.get("http://localhost:8181/instocks/category/"+this.queryParams.inStockCategory+"/"+this.queryParams.inStockStatus)
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
        }else {
          axios.get("http://localhost:8181/instocks/category/"+this.queryParams.inStockCategory).then((response)=>{
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
      }
      else if(this.queryParams.inStockStatus){
        axios.get("http://localhost:8181/instocks/status/"+this.queryParams.inStockStatus).then((response)=>{
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
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.inStockId)
      this.single = selection.length!=1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加入库单";
    },
    /** 取消按钮操作 */
    handleUpdate(row) {
      let inStock = {
        inStockId: row.inStockId,
        userId:row.userId,
        inStockStatus:"cancelled",
        inStockCategory:row.inStockCategory,
        inStockTime:row.inStockTime
      };

      this.reset();
      axios.put('/instocks/', inStock).then((response)=>{
        let flag = response.data;
        if(flag){
          this.$modal.msgSuccess("取消成功!");
        }else {
          this.$modal.msgError("取消失败!");
        }

        this.cancel();
        this.getList();
      });

    },
    /** 提交按钮 */
    async submitForm(inStock) {
      inStock = this.handleProduct(inStock);
      console.log("www");
      inStock.userId = this.$store.state.userid;
      console.log(inStock.userId);

      // 添加入库单
      axios.post('/instocks/', inStock).then((response)=>{
        console.log("1");
        let flag = response.data;
        if(flag){
          this.$modal.msgSuccess("添加成功！");
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

    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const inStockIds = row.inStockId || this.ids;
      this.$modal.confirm('是否确认删除入库单编号为"' + inStockIds + '"的数据项？').then(function() {

      }).then(() => {
        axios.delete('/instocks/'+inStockIds).then((response)=>{
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
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/post/export', {
        ...this.queryParams
      }, `post_${new Date().getTime()}.xlsx`)
    },
    // 处理详情操作
    handleDetail(row){
      axios.get("http://localhost:8181/instockitems/instock/"+row.inStockId).then((response)=>{

        this.itemList = response.data;
        this.detail = true;
      })
    },
    // 新增单项目
    addProduct() {
      this.myForm.inStockItems.push({
        inStockId:"",
        productName:"",
        inStockQuantity:"",
        inStockPrice:""
      });
    },
    // 删除单项目
    removeProduct(item) {
      var index = this.myForm.inStockItems.indexOf(item);
      if (index !== -1) {
        this.myForm.inStockItems.splice(index, 1)
      }
    },
    // 获取全部商品
    getProducts(){
      axios.get("http://localhost:8181/products/").then((response)=>{
        console.log('我tm带着商品来啦');
        if(Array.isArray(response.data)){
          this.products = response.data;
        }else {
          this.products = [];
          this.products.push(response.data);
        }
      });
    },
    // 处理所有单项目
    handleProduct(inStock){
      let itemsList = inStock.inStockItems;
      let newList = [];

      for(let item in itemsList){
        for(let product in this.products){
              if(itemsList[item].productId===this.products[product].productId){

                  newList.push({
                    inStockId:"",
                    inStockPrice: this.products[product].price,
                    inStockQuantity: itemsList[item].inStockQuantity,
                    productId: itemsList[item].productId,
                    productName: this.products[product].productName
                  });
                  break;
              }
        }
      }
      inStock.inStockItems = newList;
      return inStock;
    }
  },
  filters:{
    timeFormater(value){
      return dayjs(value).format('YYYY-MM-DD HH:mm:ss')
    },
    categoryFormater(value){
      if (value === "purchase"){
        return "个人入库";
      }else if(value === "production"){
        return "公司入库";
      }else {
        return "其他入库";
      }
    }
  },
  watch:{
    'queryParams.inStockId':{
      handler(newValue){
        if(newValue){
          this.queryMutex = true;
        }else {
          this.queryMutex = false;
        }
      }

},
    'queryParams.inStockCategory':{
      handler(newValue){
        if(newValue){
          this.querySingle = true;
        }
      }

    },
    'queryParams.inStockStatus':{
      handler(newValue){
        if(newValue){
          this.querySingle = true;
        }
      }

    },
     querySelect:{
      handler(newValue){
        let v1 = newValue.value1;
        let v2 = newValue.value2;
        // 只有当两者都空时，才能修改入库单编号
        if(!v1 && !v2){
          this.querySingle = false;
        }
      }
     }
  }
};
</script>

<style>

</style>