<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item label="送货单编号" prop="outStockId" >
        <el-input
          v-model="queryParams.outStockId"
          placeholder="请输入送货单编号"
          clearable
          :disabled="querySingle"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="送货单类型" prop="outStockCategory">
        <el-select v-model="queryParams.outStockCategory" placeholder="送货单类型" clearable :disabled="queryMutex">
          <el-option
              key="1"
              label="个人送货"
              value="purchase"
          />
          <el-option
              key="2"
              label="公司送货"
              value="production"
          />
          <el-option
              key="3"
              label="其他送货"
              value="other"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="outStockStatus">
        <el-select v-model="queryParams.outStockStatus" placeholder="送货单状态" clearable :disabled="queryMutex">
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
      <el-table-column label="送货单编号" align="center" prop="outStockId" />
      <el-table-column label="送货单类型" align="center" prop="outStockCategory">
        <template slot-scope="scope">
          {{scope.row.outStockCategory | categoryFormater}}
        </template>
      </el-table-column>
      <el-table-column label="创建者编号" align="center" prop="userId"/>
      <el-table-column label="状态" align="center" prop="outStockStatus">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.outStockStatus==='completed'" type="success">
            已完成
          </el-tag>
          <el-tag v-else-if="scope.row.outStockStatus==='uncompleted'" type="warning">
            进行中
          </el-tag>
          <el-tag v-else type="info">
            已取消
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center"  width="180">
        <template slot-scope="scope">
          {{scope.row.outStockTime | timeFormater}}
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

    <!-- 添加或修改送货单对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="800px" append-to-body>
      <el-form ref="myForm" :model="myForm" label-width="100px">
        <el-form-item label="送货单类型" prop="outStockCategory">
          <el-select v-model="myForm.outStockCategory" placeholder="送货单类型" clearable >
            <el-option
                label="个人送货"
                value="purchase"
            />
            <el-option
                label="公司送货"
                value="production"
            />
            <el-option
                label="其他送货"
                value="other"
            />
          </el-select>
        </el-form-item>
        <el-form-item
            v-for="(product,index) in myForm.outStockItems"
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
            <el-col :span="7"><el-input v-model="product.outStockQuantity" placeholder="数量" ></el-input></el-col>
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

        <el-table-column label="单项编号" align="center" prop="outStockItemId" />
        <el-table-column label="送货单编号" align="center" prop="outStockId" />
        <el-table-column label="产品编号" align="center" prop="productId"/>
        <el-table-column label="数量" align="center" prop="outStockQuantity"/>
        <el-table-column label="单价" align="center" prop="outStockPrice"/>



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
  name: "Outstock",
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
      // 送货单表格数据
      postList: [],
      // 送货单项目数据
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
        outStockId: undefined,
        outStockCategory: undefined,
        outStockStatus: undefined
      },
      // 表单参数
      myForm: {
        userId:"",
        outStockStatus:"uncompleted",
        outStockCategory:"",

        outStockItems:[{
          outStockId:"",
          productName:"",
          outStockQuantity:"",
          outStockPrice:""

        }]
      },
      // 表单校验
      rules: {
        userId: [
          { required: true, message: "岗位名称不能为空", trigger: "blur" }
        ],
        outStockId: [
          { required: true, message: "岗位编码不能为空", trigger: "blur" }
        ],
        outStockCategory: [
          { required: true, message: "岗位顺序不能为空", trigger: "blur" }
        ]
      }
    };
  },
  computed:{
    querySelect(){
      let value1 = this.queryParams.outStockCategory;
      let value2 = this.queryParams.outStockStatus;
      return{value1,value2};
    }
  },
  created() {
    this.getList();
    this.getProducts();
  },
  methods: {
    /** 查询岗位列表 */
    getList() {
      this.loading = true;
      axios.get("http://localhost:8181/outstocks/").then((response)=>{
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
        outStockStatus:"uncompleted",
        outStockCategory:"",

        outStockItems:[{
          outStockId:"",
          productName:"",
          outStockQuantity:"",
          outStockPrice:""

        }]
      };
      this.itemList = [];
      this.resetForm();
    },
    resetForm(){
      this.queryParams = {
        outStockId: undefined,
        outStockCategory: undefined,
        outStockStatus: undefined
      };
    },
    /** 搜索按钮操作 */
    handleQuery() {
      if (this.queryParams.outStockId){
        axios.get("http://localhost:8181/outstocks/"+this.queryParams.outStockId).then((response)=>{
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
      else if(this.queryParams.outStockCategory){
        if(this.queryParams.outStockStatus){
          axios.get("http://localhost:8181/outstocks/category/"+this.queryParams.outStockCategory+"/"+this.queryParams.outStockStatus)
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
          axios.get("http://localhost:8181/outstocks/category/"+this.queryParams.outStockCategory).then((response)=>{
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
      else if(this.queryParams.outStockStatus){
        axios.get("http://localhost:8181/outstocks/status/"+this.queryParams.outStockStatus).then((response)=>{
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
      this.ids = selection.map(item => item.outStockId)
      this.single = selection.length!=1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加送货单";
    },
    /** 取消按钮操作 */
    handleUpdate(row) {
      let outStock = {
        outStockId: row.outStockId,
        userId:row.userId,
        outStockStatus:"cancelled",
        outStockCategory:row.outStockCategory,
        outStockTime:row.outStockTime
      };

      this.reset();
      axios.put('/outstocks/', outStock).then((response)=>{
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
    async submitForm(outStock) {
      outStock = this.handleProduct(outStock);
      console.log("www");
      outStock.userId = this.$store.state.userid;
      console.log(outStock.userId);

      // 添加送货单
      axios.post('/outstocks/', outStock).then((response)=>{
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
      const outStockIds = row.outStockId || this.ids;
      this.$modal.confirm('是否确认删除送货单编号为"' + outStockIds + '"的数据项？').then(function() {

      }).then(() => {
        axios.delete('/outstocks/'+outStockIds).then((response)=>{
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
      axios.get("http://localhost:8181/outstockitems/outstock/"+row.outStockId).then((response)=>{

        this.itemList = response.data;
        this.detail = true;
      })
    },
    // 新增单项目
    addProduct() {
      this.myForm.outStockItems.push({
        outStockId:"",
        productName:"",
        outStockQuantity:"",
        outStockPrice:""
      });
    },
    // 删除单项目
    removeProduct(item) {
      var index = this.myForm.outStockItems.indexOf(item);
      if (index !== -1) {
        this.myForm.outStockItems.splice(index, 1)
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
    handleProduct(outStock){
      let itemsList = outStock.outStockItems;
      let newList = [];

      for(let item in itemsList){
        for(let product in this.products){
              if(itemsList[item].productId===this.products[product].productId){

                  newList.push({
                    outStockId:"",
                    outStockPrice: this.products[product].price,
                    outStockQuantity: itemsList[item].outStockQuantity,
                    productId: itemsList[item].productId,
                    productName: this.products[product].productName
                  });
                  break;
              }
        }
      }
      outStock.outStockItems = newList;
      return outStock;
    }
  },
  filters:{
    timeFormater(value){
      return dayjs(value).format('YYYY-MM-DD HH:mm:ss')
    },
    categoryFormater(value){
      if (value === "purchase"){
        return "个人送货";
      }else if(value === "production"){
        return "公司送货";
      }else {
        return "其他送货";
      }
    }
  },
  watch:{
    'queryParams.outStockId':{
      handler(newValue){
        if(newValue){
          this.queryMutex = true;
        }else {
          this.queryMutex = false;
        }
      }

},
    'queryParams.outStockCategory':{
      handler(newValue){
        if(newValue){
          this.querySingle = true;
        }
      }

    },
    'queryParams.outStockStatus':{
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
        // 只有当两者都空时，才能修改送货单编号
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