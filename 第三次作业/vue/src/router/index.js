import VueRouter from 'vue-router'
import Login from "../views/Login";
import Register from "../views/Register"
import HelloWorld from "../components/HelloWorld";
import Board from "../views/Board"
import MyLayout from "../views/MyLayOut"
import Instock from "../views/Instock";
import User from "../views/User";
import Supplier from "../views/Supplier";
import Product from "../views/Product";
import Stock from "../views/Stock";
import Outstock from "../views/Outstock";
import Order from "../views/Order";
import Returnorder from "../views/Returnorder";
import Trolley from "../views/Trolley";

export default new VueRouter({
    routes:[
        {
            path:'/',
            redirect:'/login'

        }
        ,
        {
            path: '/login',
            component:Login
        }
        ,
        {
            path: '/hello',
            component:HelloWorld
        }
        ,
        {
            path: '/register',
            component:Register
        }
        ,
        {
            path: '/welcome',
            component:MyLayout,
            children:[
                {
                    path: 'board',
                    component:Board
                },
                {
                    path:'hello',
                    component:HelloWorld
                },
                {
                    path:'instock',
                    component:Instock
                },
                {
                    path:'outstock',
                    component:Outstock
                },
                {
                    path:'user',
                    component:User
                },
                {
                    path:'supplier',
                    component:Supplier
                },
                {
                    path:'product',
                    component:Product
                },
                {
                    path:'stock',
                    component:Stock
                },
                {
                    path:'order',
                    component:Order
                },
                {
                    path:'reorder',
                    component:Returnorder
                },
                {
                    path:'trolley',
                    component:Trolley
                }

            ]
        }

    ]
})

