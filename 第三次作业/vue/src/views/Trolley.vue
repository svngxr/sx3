<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item label="小车编号" prop="trolleyId" >
        <el-input
          v-model="queryParams.trolleyId"
          placeholder="请输入小车编号"
          clearable
          :disabled="querySingle"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="小车批次" prop="batch">
        <el-input
            v-model="queryParams.batch"
            placeholder="请输入小车批次"
            clearable
            :disabled="queryMutex"
            @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="state">
        <el-select v-model="queryParams.state" placeholder="小车状态" clearable :disabled="queryMutex">
          <el-option
            key="1"
            label="繁忙中"
            value="busy"
          />
          <el-option
              key="2"
              label="空闲中"
              value="free"
          />
          <el-option
              key="3"
              label="已停用"
              value="cancelled"
          />
          <el-option
              key="4"
              label="充电中"
              value="charging"
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

        >停用</el-button>
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
      <el-table-column label="小车编号" align="center" prop="trolleyId" />
      <el-table-column label="载运量" align="center" prop="size"/>
      <el-table-column label="电量" align="center" prop="battery"/>
      <el-table-column label="状态" align="center" prop="state">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.state==='free'" type="success">
            空闲中
          </el-tag>
          <el-tag v-else-if="scope.row.state==='busy'" type="warning">
            繁忙中
          </el-tag>
          <el-tag v-else-if="scope.row.state==='charging'">
            充电中
          </el-tag>
          <el-tag v-else type="info">
            已停用
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="小车批次" align="center" prop="batch"/>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"

          >停用</el-button>
          <el-button
              size="mini"
              type="text"
              icon="el-icon-success"
              @click="handleStart(scope.row)"
          >启动/释放</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"

          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 添加或修改小车对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="350px" append-to-body>
      <el-form ref="myForm" :model="myForm" label-width="100px">
        <el-form-item label="初始化批次" prop="batch">
          <el-input v-model="myForm.batch" placeholder="请输入批次"></el-input>
        </el-form-item>
        <el-form-item label="运载量" prop="size">
          <el-select v-model="myForm.size" placeholder="运载量" clearable >
            <el-option
                label="小型车运载量"
                value="50"
            />
            <el-option
                label="中型车运载量"
                value="100"
            />
            <el-option
                label="大型车运载量"
                value="200"
            />
          </el-select>
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
import dayjs from "dayjs";

export default {
  name: "trolley",
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
      // 小车表格数据
      postList: [],
      // 小车项目数据
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
        trolleyId: undefined,
        batch: undefined,
        state: undefined
      },
      // 表单参数
      myForm: {
        battery:"100",
        state:"free",
        size:undefined,
        batch:undefined,


      },

    };
  },
  computed:{
    querySelect(){
      let value1 = this.queryParams.batch;
      let value2 = this.queryParams.state;
      return{value1,value2};
    }
  },
  created() {
    this.getList();
    this.getProducts();
  },
  methods: {
    /** 查询小车列表 */
    getList() {
      this.loading = true;
      axios.get("http://localhost:8181/trolleys/").then((response)=>{
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
        battery:"100",
        state:"free",
        size:undefined,
        batch:undefined,

      };
      this.itemList = [];
      this.resetForm();
    },
    resetForm(){

      this.queryParams = {
        trolleyId: undefined,
        batch: undefined,
        state: undefined
      };
    },
    /** 搜索按钮操作 */
    handleQuery() {
      if (this.queryParams.trolleyId){
        axios.get("http://localhost:8181/trolleys/"+this.queryParams.trolleyId).then((response)=>{
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
      else if(this.queryParams.batch){
        if(this.queryParams.state){
          axios.get("http://localhost:8181/trolleys/batch/"+this.queryParams.batch+"/"+this.queryParams.state)
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
          axios.get("http://localhost:8181/trolleys/batch/"+this.queryParams.batch).then((response)=>{
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
      else if(this.queryParams.state){
        axios.get("http://localhost:8181/trolleys/state/"+this.queryParams.state).then((response)=>{
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
      this.ids = selection.map(item => item.trolleyId)
      this.single = selection.length!=1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加小车";
    },
    /** 取消按钮操作 */
    handleUpdate(row) {

      this.reset();
      axios.get('/trolleys/cancelled/'+row.batch).then((response)=>{
        let flag = response.data;
        if(flag){
          this.$modal.msgSuccess("停用成功!");
        }else {
          this.$modal.msgError("停用失败!");
        }

        this.cancel();
        this.getList();
      });

    },
    /** 启动按钮操作 */
    handleStart(row) {

      this.reset();
      axios.get('/trolleys/start/'+row.batch).then((response)=>{
        let flag = response.data;
        if(flag){
          this.$modal.msgSuccess("启动成功!");
        }else {
          this.$modal.msgError("启动失败!");
        }

        this.cancel();
        this.getList();
      });

    },
    /** 提交按钮 */
    async submitForm(trolley) {
      console.log(trolley);
      console.log("www");
      // 添加小车
      axios.post('/trolleys/', trolley).then((response)=>{
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
      const trolleyIds = row.trolleyId || this.ids;
      this.$modal.confirm('是否确认删除小车编号为"' + trolleyIds + '"的数据项？').then(function() {

      }).then(() => {
        axios.delete('/trolleys/'+trolleyIds).then((response)=>{
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
      axios.get("http://localhost:8181/trolleyitems/trolley/"+row.trolleyId).then((response)=>{

        this.itemList = response.data;
        this.detail = true;
      })
    },
    // 新增单项目
    addProduct() {
      this.myForm.trolleyItems.push({
        trolleyId:"",
        productName:"",
        trolleyQuantity:"",
        trolleyPrice:""
      });
    },
    // 删除单项目
    removeProduct(item) {
      var index = this.myForm.trolleyItems.indexOf(item);
      if (index !== -1) {
        this.myForm.trolleyItems.splice(index, 1)
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
    handleProduct(trolley){
      let itemsList = trolley.trolleyItems;
      let newList = [];

      for(let item in itemsList){
        for(let product in this.products){
              if(itemsList[item].productId===this.products[product].productId){

                  newList.push({
                    trolleyId:"",
                    trolleyPrice: this.products[product].price,
                    trolleyQuantity: itemsList[item].trolleyQuantity,
                    productId: itemsList[item].productId,
                    productName: this.products[product].productName
                  });
                  break;
              }
        }
      }
      trolley.trolleyItems = newList;
      return trolley;
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
    'queryParams.trolleyId':{
      handler(newValue){
        if(newValue){
          this.queryMutex = true;
        }else {
          this.queryMutex = false;
        }
      }

},
    'queryParams.batch':{
      handler(newValue){
        if(newValue){
          this.querySingle = true;
        }
      }

    },
    'queryParams.state':{
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
        // 只有当两者都空时，才能修改小车编号
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