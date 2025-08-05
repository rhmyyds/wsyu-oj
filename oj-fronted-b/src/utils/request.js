import axios from "axios";
import { getToken, removeToken } from "./cookie";
import { ElMessage } from "element-plus";
import router from "@/router";

axios.defaults.headers["Content-Type"] = "application/json;charset=utf-8";
// 创建axios一个实例
const service = axios.create({
    baseURL: "/dev-api",
    timeout: 5000,
});

// 配置请求拦截器
service.interceptors.request.use(
  (config) => {
    if (getToken()) {
      config.headers["Authorization"] = "Bearer " + getToken();
    }
    return config;
  },
  (error) => {
    console.log(error)
    Promise.reject(error);
  }
);

// 配置响应拦截器
service.interceptors.response.use(
    (res) => {
        // 未设置状态码则默认成功状态
        const code = res.data.code;
        const msg = res.data.msg;
        if(code === 3001){
            // 登录过期的情况
            ElMessage.error(msg + ",请重新登录");
            removeToken();
            router.push('/oj/login');
            return Promise.reject(new Error(msg));
        }else if (code !== 1000) {
            ElMessage.error(msg);
            return Promise.reject(new Error(msg));
        } else {
            return Promise.resolve(res.data);
        }
    },
    (error) => {
        return Promise.reject(error);
    }
)

export default service;