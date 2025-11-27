import axios from "axios";
import {showMessage} from "~/util/msg";
import {aesDecrypt, aesEncrypt, generateAesKeyAndIv, rsaEncrypt} from "~/util/encryption.ts";
import {keyStore} from "~/store/keyStore.ts";
import CryptoJS from 'crypto-js';
import { request } from "node_modules/axios/index.d.cts";

axios.defaults.headers['Content-Type'] = 'application/json;charset=utf-8'
// 创建axios实例
const instance = axios.create({
    // axios中请求配置有baseURL选项，表示请求URL公共部分
    baseURL: import.meta.env.VITE_APP_BASE_API,
    // 超时
    timeout: 1000000
})
const responseInterceptors = (response) => {
    debugger
    if (response.data instanceof Object) {
        if (response.data.code === 401) {
            showMessage.error("登录过期，请重新登录！")
            window.localStorage.removeItem("token")
            window.location.href = "/login"
            return response.data
        }
    } else {
        let encodeData = CryptoJS.enc.Base64.parse(response.data).toString(CryptoJS.enc.Utf8)
        let key: string | null = '', iv: string | null = ''
        if (keyStore.getState().key2 && keyStore.getState().key3) {
            key = keyStore.getState().key2
            iv = keyStore.getState().key3
        }
        let decryptData = JSON.parse(aesDecrypt(encodeData, key as string, iv as string))
        const token = decryptData.data.token;
        if (token) {
            window.localStorage.setItem("token", 'Bearer ' + token);
        }
        return decryptData;
    }
};
const responseInterceptorsError = (error) => {
    if (error.response && error.response.status === 403) {
        showMessage.error("没有权限访问该资源，请联系管理员！")
    } else if (error.response && error.response.status === 500) {
        showMessage.error("服务器内部错误，请联系管理员！")
    } else {
        // 其他错误处理
        console.error("请求错误:", error.message);
    }
    return Promise.reject(error);
}
instance.interceptors.response.use(responseInterceptors, responseInterceptorsError);
const requestInterceptors = (request) => {
    // 只有post请求才会加解密
    let flag = request.method.toUpperCase() === "POST" || request.method.toUpperCase() === "PUT";
    if (flag) {
        let data = request.params || request.data
        let key: string | null = '', iv: string | null = ''
        if (keyStore.getState().key2 && keyStore.getState().key3) {
            key = keyStore.getState().key2
            iv = keyStore.getState().key3
        } else {
            const aesKeyAndIv = generateAesKeyAndIv()
            key = aesKeyAndIv.key
            iv = aesKeyAndIv.iv
            keyStore.setState({key2: key, key3: iv})
        }
        let encodeRequestData = ""
        if (data) {
            encodeRequestData = aesEncrypt(JSON.stringify(data), key as string, iv as string)
        }
        let encodeKeyIV = rsaEncrypt(key + iv, keyStore.getState().key1 as string)
        request.data = CryptoJS.enc.Base64.stringify(CryptoJS.enc.Utf8.parse(encodeRequestData + encodeKeyIV))
    }
    const token = window.localStorage.getItem("token");
    if (request.headers) {
        request.headers["Authorization"] = token;
    } else {
        request.headers = {"Authorization": token};
    }
    return request;
};
instance.interceptors.request.use(requestInterceptors);

export default instance;
