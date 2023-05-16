<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item label="订单编号" prop="ordersId" >
        <el-input
          v-model="queryParams.ordersId"
          placeholder="请输入订单编号"
          clearable
          :disabled="querySingle"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="支付方式" prop="paymentMethod">
        <el-select v-model="queryParams.paymentMethod" placeholder="支付方式" clearable :disabled="queryMutex">
          <el-option
              key="1"
              label="电子支付"
              value="electronic"
          />
          <el-option
              key="2"
              label="现金支付"
              value="cash"
          />
          <el-option
              key="3"
              label="其他支付"
              value="other"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="ordersStatus">
        <el-select v-model="queryParams.ordersStatus" placeholder="订单状态" clearable :disabled="queryMutex">
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
      <el-table-column label="订单编号" align="center" prop="ordersId" />
      <el-table-column label="支付方式" align="center" prop="paymentMethod">
        <template slot-scope="scope">
          {{scope.row.paymentMethod | categoryFormater}}
        </template>
      </el-table-column>
      <el-table-column label="创建者编号" align="center" prop="userId"/>
      <el-table-column label="状态" align="center" prop="ordersStatus">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.ordersStatus==='completed'" type="success">
            已完成
          </el-tag>
          <el-tag v-else-if="scope.row.ordersStatus==='uncompleted'" type="warning">
            进行中
          </el-tag>
          <el-tag v-else type="info">
            已取消
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center"  width="180">
        <template slot-scope="scope">
          {{scope.row.ordersTime | timeFormater}}
        </template>
      </el-table-column>
      <el-table-column label="收货地址" align="center" prop="shippingAddress"/>
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

    <!-- 添加或修改订单对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="800px" append-to-body>
      <el-form ref="myForm" :model="myForm" label-width="100px">
        <el-form-item label="支付方式" prop="paymentMethod">
          <el-select v-model="myForm.paymentMethod" placeholder="支付方式" clearable >
            <el-option
                label="电子支付"
                value="electronic"
            />
            <el-option
                label="现金支付"
                value="cash"
            />
            <el-option
                label="其他支付"
                value="other"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="收货地址" prop="shippingAddress">
          <el-input v-model="myForm.shippingAddress" placeholder="收货地址" ></el-input>
        </el-form-item>
        <el-form-item
            v-for="(product,index) in myForm.ordersItems"
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
            <el-col :span="7"><el-input v-model="product.quantity" placeholder="数量" ></el-input></el-col>
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

        <el-table-column label="单项编号" align="center" prop="ordersItemsId" />
        <el-table-column label="订单编号" align="center" prop="ordersId" />
        <el-table-column label="产品编号" align="center" prop="productId"/>
        <el-table-column label="数量" align="center" prop="quantity"/>
        <el-table-column label="单价" align="center" prop="price"/>



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
  name: "Order",
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
      // 订单表格数据
      postList: [],
      // 订单项目数据
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
        ordersId: undefined,
        paymentMethod: undefined,
        ordersStatus: undefined
      },
      // 表单参数
      myForm: {
        userId:"",
        ordersStatus:"uncompleted",
        paymentMethod:"",
        shippingAddress:"",

        ordersItems:[{
          ordersId:"",
          productId:"",
          quantity:"",
          price:""

        }]
      },

    };
  },
  computed:{
    querySelect(){
      let value1 = this.queryParams.paymentMethod;
      let value2 = this.queryParams.ordersStatus;
      return{value1,value2};
    }
  },
  created() {
    this.getList();
    this.getProducts();
  },
  methods: {
    /** 查询订单列表 */
    getList() {
      this.loading = true;
      axios.get("http://localhost:8181/orders/").then((response)=>{
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
        ordersStatus:"uncompleted",
        paymentMethod:"",
        shippingAddress:"",

        ordersItems:[{
          ordersId:"",
          quantity:"",
          price:""

        }]
      };
      this.itemList = [];
      this.resetForm("form");
    },
    resetForm(form){
      this.form = form;
      this.queryParams = {
        ordersId: undefined,
        paymentMethod: undefined,
        ordersStatus: undefined
      };
    },
    /** 搜索按钮操作 */
    handleQuery() {
      if (this.queryParams.ordersId){
        axios.get("http://localhost:8181/orders/"+this.queryParams.ordersId).then((response)=>{
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
      else if(this.queryParams.paymentMethod){
        if(this.queryParams.ordersStatus){
          axios.get("http://localhost:8181/orders/paymentMethod/"+this.queryParams.paymentMethod+"/"+this.queryParams.ordersStatus)
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
          axios.get("http://localhost:8181/orders/paymentMethod/"+this.queryParams.paymentMethod).then((response)=>{
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
      else if(this.queryParams.ordersStatus){
        axios.get("http://localhost:8181/orders/status/"+this.queryParams.ordersStatus).then((response)=>{
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
      this.ids = selection.map(item => item.ordersId)
      this.single = selection.length!=1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加订单";
    },
    /** 取消按钮操作 */
    handleUpdate(row) {
      let orders = {
        ordersId: row.ordersId,
        userId:row.userId,
        ordersStatus:"cancelled",
        paymentMethod:row.paymentMethod,
        ordersTime:row.ordersTime,
        shippingAddress:row.shippingAddress
      };

      this.reset();
      axios.put('/orders/', orders).then((response)=>{
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
    async submitForm(orders) {
      orders = this.handleProduct(orders);
      console.log("www");
      orders.userId = this.$store.state.userid;
      console.log(orders.userId);
      console.log(orders);

      // 添加订单
      axios.post('/orders/', orders).then((response)=>{
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
      const ordersIds = row.ordersId || this.ids;
      this.$modal.confirm('是否确认删除订单编号为"' + ordersIds + '"的数据项？').then(function() {

      }).then(() => {
        axios.delete('/orders/'+ordersIds).then((response)=>{
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
      axios.get("http://localhost:8181/ordersitems/orders/"+row.ordersId).then((response)=>{

        this.itemList = response.data;
        this.detail = true;
      })
    },
    // 新增单项目
    addProduct() {
      this.myForm.ordersItems.push({
        ordersId:"",
        quantity:"",
        price:""
      });
    },
    // 删除单项目
    removeProduct(item) {
      var index = this.myForm.ordersItems.indexOf(item);
      if (index !== -1) {
        this.myForm.ordersItems.splice(index, 1)
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
    handleProduct(orders){
      let itemsList = orders.ordersItems;
      let newList = [];

      for(let item in itemsList){
        for(let product in this.products){
              if(itemsList[item].productId===this.products[product].productId){

                  newList.push({
                    ordersId:"",
                    price: this.products[product].price,
                    quantity: itemsList[item].quantity,
                    productId: itemsList[item].productId,
                  });
                  break;
              }
        }
      }
      orders.ordersItems = newList;
      return orders;
    }
  },
  filters:{
    timeFormater(value){
      return dayjs(value).format('YYYY-MM-DD HH:mm:ss')
    },
    categoryFormater(value){
      if (value === "electronic"){
        return "电子支付";
      }else if(value === "cash"){
        return "现金支付";
      }else {
        return "其他支付";
      }
    },
  },
  watch:{
    'queryParams.ordersId':{
      handler(newValue){
        if(newValue){
          this.queryMutex = true;
        }else {
          this.queryMutex = false;
        }
      }

},
    'queryParams.paymentMethod':{
      handler(newValue){
        if(newValue){
          this.querySingle = true;
        }
      }

    },
    'queryParams.ordersStatus':{
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
        // 只有当两者都空时，才能修改订单编号
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