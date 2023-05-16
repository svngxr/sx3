import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)
const actions = {
    submitform(context,value){
        context.commit('SUBMITFORM', value)

    },
    setUsername(context,value){
        context.commit('SETUSERNAME',value)
    },
    setUserid(context,value){
        context.commit('SETUSERID',value)
    }
}

const mutations = {
    SUBMITFORM(state,value){
        state.usertype = value
    },
    SETUSERNAME(state,value){
        state.username = value
    },
    SETUSERID(state,value){
        state.userid = value
    }
}

const state = {
    usertype:"normal",
    username:"",
    userid:""
}

export default new Vuex.Store({
    actions,
    mutations,
    state

})
