<template>
    <div style="weight: 100%; height: 100%; margin: 0; padding: 0;">

        <el-container style="height: 100%; " id="containerNav">

            <el-aside width="200rpx" style="background-color: #03152A;" id="asideNav">
              <el-menu
                class="el-menu-vertical-demo"
                background-color="#03152A"
                text-color="rgba(255,255,255,.7)"
                active-text-color="#020f1d"
                :collapse="isCollapse">

                    <div class="logo-name" @click="gotoBoard">
                        <p id="sysTitle" style="cursor: pointer;">
                            {{this.systemTitle}}
                            <el-button type="text" :icon="collapseIcon" @click="changeCollapse"></el-button>
                        </p>
                    </div>

                    <!-- <el-submenu index="1">
                        <template slot="title">
                            <i class="el-icon-setting"></i>
                            <span slot="title">入库管理</span>
                        </template>
                        <el-menu-item @click="gotoInStock" index="1-1">入库单管理</el-menu-item>
                    </el-submenu> -->

                    <el-submenu index="2">
                        <template slot="title">
                            <i class="el-icon-location"></i>
                            <span slot="title">库内管理</span>
                        </template>
                        <el-menu-item  index="2-1" @click="gotoProduct">商品管理</el-menu-item>
                        <el-menu-item  index="2-2" @click="gotoStock">货架管理</el-menu-item>
                    </el-submenu>

                    <el-submenu index="3">
                    <template slot="title">
                      <i class="el-icon-apple"></i>
                      <span slot="title">送货管理</span>
                    </template>
                    <el-menu-item  index="3-1" @click="gotoOutStock">送货单管理</el-menu-item>
                      <el-menu-item  index="3-2" @click="gotoOrder">订单管理</el-menu-item>
                  </el-submenu>

                    <!-- <el-submenu index="4">
                    <template slot="title">
                      <i class="el-icon-attract"></i>
                      <span slot="title">调度管理</span>
                    </template>
                    <el-menu-item index="4-1" @click="gotoTrolley">小车管理</el-menu-item>
                  </el-submenu> -->

                   <!-- <el-submenu index="5">
                    <template slot="title">
                      <i class="el-icon-circle-check"></i>
                      <span slot="title">退货管理</span>
                    </template>
                    <el-menu-item index="5-1" @click="gotoReorder">退货单</el-menu-item>
                  </el-submenu> -->

                   <el-submenu index="6" v-if="$store.state.usertype==='super'">
                    <template slot="title">
                      <i class="el-icon-user"></i>
                      <span slot="title">人员管理</span>
                    </template>
                    <el-menu-item  index="6-1" @click="gotoUser">用户管理</el-menu-item>
                    <el-menu-item  index="6-2" @click="gotoSupplier">个人商管理</el-menu-item>
                  </el-submenu>
                </el-menu>

            </el-aside>
            
            <el-container>
              <el-header>
                <navbar></navbar>
              </el-header>

                <el-main>
                    <router-view></router-view>
                </el-main>

            </el-container>

        </el-container>

    </div>
</template>

<style lang="scss">
    $top: top;
    $bottom: bottom;
    $left: left;
    $right: right;
    %w100 {
        width: 100%;
    }

    %h100 {
        height: 100%;
    }

    %cursor {
        cursor: pointer;
    }

    html, body, #app, .el-container, #asideNav, ul.el-menu {
        @extend %h100;
    }

    #sysTitle {
        height: 50px;
        line-height: 50px;
        text-align: center;
        font-size: 16px;
        color: #5e6d82;
    }

    .el-header {
        background-color: #f0f2f5;
        color: #333;
        line-height: 60px;
        // display: flex;
        // justify-content: flex-end;
    }

    .el-menu-vertical-demo:not(.el-menu--collapse) {
        width: 200px;
        min-height: 400px;
    }

    .el-menu{
        border-right:none !important;
    }

    #asideNav {
        // width: auto !important;
        text-align: left;
        color: #333;
        display: flex;
        flex-direction: column;
        border-right: solid 1px #e6e6e6;
        .el-menu-item {
            width: auto !important;
            background-color: #020f1d !important;
            border-bottom: 1px solid #020f1d;
        }
        .el-menu-item.is-active {
            background-color: #56a9ff !important;
        }
        .el-menu-item:hover {
            background-color: #375573 !important;
        }
    }

    .personal {
        display: flex;
        flex-direction: row;
    }
    
    .icon img {
        margin-#{$top}: 10px;
        -webkit-border-radius: 5px;
        -moz-border-radius: 5px;
        border-radius: 5px;
        width: 40px;
        height: 40px;
    }

</style>

<script>
import Navbar from "../components/Navbar"

  export default {

    data () {
      return {
        systemTitle: "送货管理系统",
        circleUrl: require("../assets/logo.png"), // 控制右上角头像
        isCollapse: false, // 控制导航栏展开，false为展开
        collapseIcon: "el-icon-arrow-left",
        switchValue: true,
        
        // 有关信息下拉框
        isfullScreen: true,
        dialogInfoVisible: false,
        dialogPassVisible: false,
        title: "",
        userId: "",
        
        direction: 'rtl',
      }
    },
    components:{
      Navbar,
    },
    methods: {
      changeCollapse(){
        if (this.isCollapse){
          this.isCollapse=false;
          this.collapseIcon = "el-icon-arrow-left";
          this.systemTitle="送货管理系统";
        }else {
          this.isCollapse=true;
          this.collapseIcon = "el-icon-arrow-right";
          this.systemTitle="";

        }
      },
      gotoBoard(){
        console.log("hihihihihih")
        this.$router.push("/welcome/board")
      },
      gotoHello(){
        this.$router.push("/welcome/hello")
      },
      gotoInStock(){
        console.log("waeweqejqweqwe")
        this.$router.push("/welcome/instock")
      },
      gotoOutStock(){
        console.log("waeweqejqweqwe")
        this.$router.push("/welcome/outstock")
      },
      gotoUser(){
        console.log("waeweqejqweqwe")
        this.$router.push("/welcome/user")
      },
      gotoSupplier(){
        this.$router.push("/welcome/supplier")
      },
      gotoProduct(){
        this.$router.push("/welcome/product")
      },
      gotoStock(){
        this.$router.push("/welcome/stock")
      },
      gotoOrder(){
        this.$router.push("/welcome/order")
      },
      gotoReorder(){
        this.$router.push("/welcome/reorder")
      },
      gotoTrolley(){
        this.$router.push("/welcome/trolley")
      }
    }

  }

</script>
