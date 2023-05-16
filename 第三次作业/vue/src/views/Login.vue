<template>
  <div class="loginbody">
    <div class="logindata">
      <div class="logintext">
        <h2>送货管理系统</h2>
      </div>
      <div class="formdata">
        <el-form ref="form" :model="form" :rules="rules">
          <el-form-item prop="username">
            <el-input
                v-model="form.username"
                clearable
                placeholder="请输入账号"
            ></el-input>
          </el-form-item>
          <el-form-item prop="password">
            <el-input
                v-model="form.password"
                clearable
                placeholder="请输入密码"
                show-password
            ></el-input>
          </el-form-item>
        </el-form>
      </div>
      <div class="tool">
        <div>
          <el-checkbox v-model="form.type"
          >系统管理员</el-checkbox
          >
          <el-checkbox v-model="checked" @change="remenber"
          >记住密码</el-checkbox
          >
        </div>
        <div>
          <span class="shou" @click="forgetpas">忘记密码？</span>
        </div>
      </div>
      <div class="butt">
        <el-button type="primary" @click.native.prevent="login(form)"
        >登录</el-button
        >
        <el-button class="shou" @click="register">注册</el-button>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
axios.defaults.headers.post['Content-Type']='application/json;charset=UTF-8';
export default {
  name: "Login",
  data() {
    return {
      form: {
        password: "",
        username: "",
        type: false
      },
      checked: false,
      rules: {
        username: [
          { required: true, message: "请输入用户名", trigger: "blur" },
          { max: 10, message: "不能大于10个字符", trigger: "blur" },
        ],
        password: [
          { required: true, message: "请输入密码", trigger: "blur" },
          { max: 10, message: "不能大于10个字符", trigger: "blur" },
        ],
      },
    };
  },
  mounted() {
    if(localStorage.getItem("news")){
      this.form=JSON.parse(localStorage.getItem("news"))
      this.checked=true
    }
  },
  methods: {
    login(form){
      let user = {
        password: "",
        userName: "",
        type: ""};

      user.userName = form.username;
      user.password = form.password;

      if(form.type){
        user.type="super";
      }else {
        user.type="normal";
      }
      this.$store.dispatch("submitform",user.type)
      this.$store.dispatch("setUsername",user.userName)

      // 获取当前用户id
      axios.get("http://localhost:8181/usersName/"+this.$store.state.username).then((response)=>{
        console.log(response.data);
        this.$store.dispatch("setUserid",response.data.userId);
      });



      console.log(form);
      axios.post("http://localhost:8181/users/login",user).then((response)=>{
        let flag = response.data;
        console.log(response.data);
        if (flag){
          this.$router.push("/welcome/board")
        }else{
          this.$message({
            type: 'error',
            message: '登录信息有误！'
          });
        }

      })

    },
    remenber(data){
      this.checked=data
      if(this.checked){
        localStorage.setItem("news",JSON.stringify(this.form))
      }else{
        localStorage.removeItem("news")
      }
    },
    forgetpas() {
      this.$message({
        type:"info",
        message:"功能尚未开发额",
        showClose:true
      })
    },
    register() {
      this.$router.push("/register");
    },
  },
};
</script>

<style scoped>
.loginbody {
  width: 100%;
  height: 100%;
  background-image: url("../assets/keli.webp");
  background-size: 100% 100%;
  background-position: center center;
  overflow: auto;
  background-repeat: no-repeat;
  position: fixed;
  line-height: 100%;

}

.logintext {
  margin-bottom: 20px;
  line-height: 50px;
  text-align: center;
  font-size: 30px;
  font-weight: bolder;
  color: white;
  text-shadow: 2px 2px 4px #000000;
}

.logindata {
  width: 400px;
  height: 300px;
  transform: translate(-50%);
  margin-left: 50%;
}

.tool {
  display: flex;
  justify-content: space-between;
  color:#606266;
}

.butt {
  margin-top: 10px;
  text-align: center;
}

.shou {
  cursor: pointer;
  color: #606266;
}

</style>